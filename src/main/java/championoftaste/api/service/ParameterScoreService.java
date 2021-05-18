package championoftaste.api.service;

import championoftaste.api.UserRole;
import championoftaste.api.model.ParameterScore;
import championoftaste.api.model.Score;
import championoftaste.api.request.ParameterScoreRequest;
import championoftaste.api.model.User;
import championoftaste.api.repository.ParameterScoreRepository;
import championoftaste.api.AuthorizedUser;
import championoftaste.api.request.ScoreRequest;
import championoftaste.exception.ApiRequestException;
import championoftaste.validators.FieldsValidator;

import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис, реализующий CRUD (Create, Read, Update, Delete) операции над оценками критериев продуктов.
 */
@Service
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class ParameterScoreService implements CrudService<ParameterScore> {

    private final ParameterScoreRepository parameterScoreRepository;
    private final FieldsValidator<ParameterScore> fieldsValidator = new FieldsValidator<>();
    private final UserService userService;

    private ParameterScore createParameterScoreFromParameterScoreRequest(ParameterScoreRequest parameterScoreRequest) {
        User user;
        try {
            user = userService.loadUserByUsername(AuthorizedUser.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new ApiRequestException(e.getMessage());
        }

        return new ParameterScore(
                parameterScoreRequest.getScore(),
                parameterScoreRequest.getProduct(),
                parameterScoreRequest.getParameter(),
                user
        );
    }

    public void create(List<ParameterScoreRequest> parameterScoreRequests) throws ApiRequestException {
        List<ParameterScore> parameterScores = new ArrayList<>();

        for (ParameterScoreRequest request : parameterScoreRequests) {
            parameterScores.add(createParameterScoreFromParameterScoreRequest(request));
        }

        createAll(parameterScores);
    }

    public void create(ParameterScoreRequest parameterScoreRequest) throws ApiRequestException {
        User user;
        try {
            user = userService.loadUserByUsername(AuthorizedUser.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new ApiRequestException(e.getMessage());
        }

        ParameterScore parameterScore = new ParameterScore(
                parameterScoreRequest.getScore(),
                parameterScoreRequest.getProduct(),
                parameterScoreRequest.getParameter(),
                user
        );

        create(parameterScore);
    }

    public boolean update(Integer id, ParameterScoreRequest parameterScoreRequest) throws ApiRequestException {
        ParameterScore parameterScore = parameterScoreRepository.findById(id).orElse(null);

        if (parameterScore == null) {
            return false;
        }

        User user;
        try {
            user = userService.loadUserByUsername(AuthorizedUser.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new ApiRequestException(e.getMessage());
        }

        ParameterScore updated = new ParameterScore(
                parameterScoreRequest.getScore(),
                parameterScoreRequest.getProduct(),
                parameterScoreRequest.getParameter(),
                user
        );

        return update(id, updated);
    }

    public void createAll(List<ParameterScore> parameterScores) throws ApiRequestException {
        for (ParameterScore parameterScore : parameterScores) {
            // Проверяем все поля создаваемой оценки критерия на корректность.
            for (ConstraintViolation<ParameterScore> violation : fieldsValidator.validate(parameterScore)) {
                throw new ApiRequestException(violation.getMessage());
            }

            // Если уже данным пользователем была выставлена оценка данному критерию данного продукта.
            if (parameterScoreRepository
                    .findByProductAndParameterAndUser(
                            parameterScore.getProduct(),
                            parameterScore.getParameter(),
                            parameterScore.getUser()
                    )
                    .isPresent()) {
                throw new ApiRequestException("Вы уже выставили оценку данному критерию данного продукта");
            }
        }

        parameterScoreRepository.saveAll(parameterScores);
    }

    @Override
    public void create(ParameterScore parameterScore) throws ApiRequestException {
        // Проверяем все поля создаваемой оценки критерия на корректность.
        for (ConstraintViolation<ParameterScore> violation : fieldsValidator.validate(parameterScore)) {
            throw new ApiRequestException(violation.getMessage());
        }

        // Если уже данным пользователем была выставлена оценка данному критерию данного продукта.
        if (parameterScoreRepository
                .findByProductAndParameterAndUser(
                        parameterScore.getProduct(),
                        parameterScore.getParameter(),
                        parameterScore.getUser()
                )
                .isPresent()) {
            throw new ApiRequestException("Вы уже выставили оценку данному критерию данного продукта");
        }

        parameterScoreRepository.save(parameterScore);
    }

    @Override
    public List<ParameterScore> readAll() {
        return parameterScoreRepository.findAll();
    }

    @Override
    public ParameterScore read(Integer id) {
        return parameterScoreRepository.findById(id).orElse(null);
    }

    @Override
    public boolean update(Integer id, ParameterScore updated) throws ApiRequestException {
        ParameterScore parameterScore = parameterScoreRepository.findById(id).orElse(null);

        // Если нашлась оценка критерия для обновления.
        if (parameterScore != null) {
            // Проверяем все поля оценки критерия, в соответствии с которой, надо обновить.
            for (ConstraintViolation<ParameterScore> violation : fieldsValidator.validate(updated)) {
                throw new ApiRequestException(violation.getMessage());
            }

            ParameterScore current = parameterScoreRepository
                    .findByProductAndParameterAndUser(
                            updated.getProduct(),
                            updated.getParameter(),
                            updated.getUser()
                    )
                    .orElse(null);

            // Если мы пытаемся обновить оценку на ту, которая уже выставлена, причем эта не та, которую обновляем.
            if (current != null && !current.getId().equals(id)) {
                throw new ApiRequestException("Такая оценка критерию уже выставлена");
            }

            // Сохраняем обновлённые данные.
            updated.setId(id);
            updated.setProduct(parameterScore.getProduct());
            updated.setParameter(parameterScore.getParameter());
            updated.setUser(parameterScore.getUser());
            parameterScoreRepository.save(updated);

            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (parameterScoreRepository.existsById(id)) {
            parameterScoreRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
