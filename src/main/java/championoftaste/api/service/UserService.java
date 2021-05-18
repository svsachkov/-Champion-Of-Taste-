package championoftaste.api.service;

import championoftaste.exception.ApiRequestException;
import championoftaste.api.UserRole;
import championoftaste.api.model.User;
import championoftaste.api.repository.UserRepository;
import championoftaste.api.request.UserRequest;
import championoftaste.validators.FieldsValidator;

import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;

import java.util.List;

/**
 * Сервис, реализующий CRUD (Create, Read, Update, Delete) операции над пользователями.
 */
@Service
@AllArgsConstructor
public class UserService implements CrudService<User>, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FieldsValidator<User> fieldsValidator = new FieldsValidator<>();

    public void register(UserRequest request) throws ApiRequestException {
        // Создаём нового пользователя.
        User user = new User(
                UserRole.ROLE_CONSUMER,
                request.getName(),
                request.getSurname(),
                request.getPatronymic(),
                request.getGender(),
                request.getAge(),
                request.getPhone(),
                request.getEmail(),
                request.getInfo(),
                request.getPassword(),
                request.getDeviceId()
        );

        create(user);
    }

    public boolean update(Integer id, UserRequest request) throws ApiRequestException {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return false;
        }

        // Создаём нового пользователя.
        User updated = new User(
                user.getRole(),
                request.getName(),
                request.getSurname(),
                request.getPatronymic(),
                request.getGender(),
                request.getAge(),
                request.getPhone(),
                request.getEmail(),
                request.getInfo(),
                request.getPassword(),
                request.getDeviceId()
        );

        return update(id, updated);
    }

    @Override
    public void create(User user) throws ApiRequestException {
        // Проверяем поля пользователя на корректность.
        for (ConstraintViolation<User> violation : fieldsValidator.validate(user)) {
            throw new ApiRequestException(violation.getMessage());
        }

        // Если пользователь с указанной электронной почтой уже зарегистрирован.
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ApiRequestException("Такой адрес электронной почты уже занят");
        }

        // Если пользователь с указанным контактным номером телефона уже зарегистрирован.
        if (userRepository.findByPhone(user.getPhone()).isPresent()) {
            throw new ApiRequestException("Такой номер телефона уже занят");
        }

        // Кодируем пароль для хранения в базе данных.
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    @Override
    public List<User> readAll() {
        return userRepository.findAll();
    }

    @Override
    public User read(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public boolean update(Integer id, User updated) throws ApiRequestException {
        User user = userRepository.findById(id).orElse(null);

        // Если пользователь для обновления нашёлся.
        if (user != null) {
            // Проверяем все поля пользователя, в соответствии с которым надо обновить данные.
            for (ConstraintViolation<User> violation : fieldsValidator.validate(updated)) {
                throw new ApiRequestException(violation.getMessage());
            }

            User current = userRepository.findByEmailOrPhone(updated.getEmail(), updated.getPhone()).orElse(null);
            if (current != null && !current.getId().equals(id)) {
                throw new ApiRequestException("Такой адрес электронной почты уже занят");
            }

            current = userRepository.findByPhone(updated.getPhone()).orElse(null);
            if (current != null && !current.getId().equals(id)) {
                throw new ApiRequestException("Такой номер телефона уже занят");
            }

            updated.setId(id);
            updated.setComments(user.getComments());
            updated.setParameterScores(user.getParameterScores());
            updated.setScores(user.getScores());
            userRepository.save(updated);

            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }

        return false;
    }

    /**
     * Получает пользователя по его имени пользователя.
     *
     * @param username имя пользователя
     * @return пользователь с указанным именем пользователя
     * @throws UsernameNotFoundException выбрасывается в случае, если пользователь не найден
     */
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrPhone(username, username).orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("Имя пользователя %s не найдено", username));
        }

        return user;
    }
}
