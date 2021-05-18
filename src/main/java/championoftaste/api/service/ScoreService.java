package championoftaste.api.service;

import championoftaste.api.UserRole;
import championoftaste.api.model.Product;
import championoftaste.api.model.Score;
import championoftaste.api.request.ScoreRequest;
import championoftaste.api.model.User;
import championoftaste.api.repository.ScoreRepository;
import championoftaste.api.AuthorizedUser;
import championoftaste.exception.ApiRequestException;
import championoftaste.validators.FieldsValidator;

import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис, реализующий CRUD (Create, Read, Update, Delete) операции над оценками пользователей.
 */
@Service
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class ScoreService implements CrudService<Score> {

    private final ScoreRepository scoreRepository;
    private final FieldsValidator<Score> fieldsValidator = new FieldsValidator<>();
    private final UserService userService;

    private Score createScoreFromScoreRequest(ScoreRequest scoreRequest) {
        User user;
        try {
            user = userService.loadUserByUsername(AuthorizedUser.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new ApiRequestException(e.getMessage());
        }

        return new Score(
                scoreRequest.getScore(),
                scoreRequest.getProduct(),
                user,
                user.getRole() == UserRole.ROLE_EXPERT
        );
    }

    public void create(ScoreRequest scoreRequest) throws ApiRequestException {
        create(createScoreFromScoreRequest(scoreRequest));
    }

    public void create(List<ScoreRequest> scoreRequests) throws ApiRequestException {
        List<Score> scores = new ArrayList<>();

        for (ScoreRequest request : scoreRequests) {
            scores.add(createScoreFromScoreRequest(request));
        }

        createAll(scores);
    }

    public void createAll(List<Score> scores) throws ApiRequestException {
        for (Score score : scores) {
            // Проверяем все поля создаваемой оценки пользователя на корректность.
            for (ConstraintViolation<Score> violation : fieldsValidator.validate(score)) {
                throw new ApiRequestException(violation.getMessage());
            }

            // Если пользователь уже выставлял оценку данному продукту.
            if (scoreRepository
                    .findByProductAndUser(score.getProduct(), score.getUser())
                    .isPresent()) {
                throw new ApiRequestException("Вы уже выставили оценку данному продукту");
            }
        }

        scoreRepository.saveAll(scores);
    }

    public boolean update(Integer id, ScoreRequest scoreRequest) throws ApiRequestException {
        Score score = scoreRepository.findById(id).orElse(null);

        if (score == null) {
            return false;
        }

        return update(id, createScoreFromScoreRequest(scoreRequest));
    }

    @Override
    public void create(Score score) throws ApiRequestException {
        // Проверяем все поля создаваемой оценки пользователя на корректность.
        for (ConstraintViolation<Score> violation : fieldsValidator.validate(score)) {
            throw new ApiRequestException(violation.getMessage());
        }

        // Если пользователь уже выставлял оценку данному продукту.
        if (scoreRepository
                .findByProductAndUser(score.getProduct(), score.getUser())
                .isPresent()) {
            throw new ApiRequestException("Вы уже выставили оценку данному продукту");
        }

        scoreRepository.save(score);
    }

    @Override
    public List<Score> readAll() {
        return scoreRepository.findAll();
    }

    @Override
    public Score read(Integer id) {
        return scoreRepository.findById(id).orElse(null);
    }

    @Override
    public boolean update(Integer id, Score updated) throws ApiRequestException {
        Score score = scoreRepository.findById(id).orElse(null);

        // Если нашлась оценка пользователя для обновления.
        if (score != null) {
            // Проверяем все поля оценки пользователя, в соответствии с которой, надо обновить.
            for (ConstraintViolation<Score> violation : fieldsValidator.validate(updated)) {
                throw new ApiRequestException(violation.getMessage());
            }

            Score current = scoreRepository
                    .findByProductAndUser(updated.getProduct(), updated.getUser())
                    .orElse(null);

            // Если мы пытаемся обновить нашу оценку на ту, которая уже есть в базе,
            // причем эта не та, которую мы обновляем.
            if (current != null && !current.getId().equals(id)) {
                throw new ApiRequestException("Такая оценка уже выставлена");
            }

            // Сохраняем обновлённые данные.
            updated.setId(id);
            updated.setProduct(score.getProduct());
            updated.setUser(score.getUser());
            scoreRepository.save(updated);

            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (scoreRepository.existsById(id)) {
            scoreRepository.deleteById(id);
            return true;
        }

        return false;
    }

    public List<Score> getScoresByProduct(Product product) throws ApiRequestException {
        if (product == null) {
            throw new ApiRequestException("Не удалось найти продукт");
        }

        return scoreRepository.findAllByProduct(product).orElse(null);
    }
}
