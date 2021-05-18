package championoftaste.api.service;

import championoftaste.api.model.Parameter;
import championoftaste.api.repository.ParameterRepository;
import championoftaste.exception.ApiRequestException;
import championoftaste.validators.FieldsValidator;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;

import java.util.List;

/**
 * Сервис, реализующий CRUD (Create, Read, Update, Delete) операции над критериями,
 * по которым оценивается продукт экспертом.
 */
@Service
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class ParameterService implements CrudService<Parameter> {

    private final ParameterRepository parameterRepository;
    private final FieldsValidator<Parameter> fieldsValidator = new FieldsValidator<>();

    @Override
    public void create(Parameter parameter) throws ApiRequestException {
        // Проверяем все поля создаваемого критерия оценивания продукта на корректность.
        for (ConstraintViolation<Parameter> violation : fieldsValidator.validate(parameter)) {
            throw new ApiRequestException(violation.getMessage());
        }

        // Если в номинации уже есть критерий оценивания продукта с таким названием.
        if (parameterRepository
                .findByNominationAndName(parameter.getNomination(), parameter.getName())
                .isPresent()) {
            throw new ApiRequestException("В номинации уже есть критерий оценивания с таким названием");
        }

        parameterRepository.save(parameter);
    }

    @Override
    public List<Parameter> readAll() {
        return parameterRepository.findAll();
    }

    @Override
    public Parameter read(Integer id) {
        return parameterRepository.findById(id).orElse(null);
    }

    @Override
    public boolean update(Integer id, Parameter updated) throws ApiRequestException {
        Parameter parameter = parameterRepository.findById(id).orElse(null);

        // Если нашёлся критерий для обновления.
        if (parameter != null) {
            // Проверяем все поля критерия, в соответствии с которым, надо обновить.
            for (ConstraintViolation<Parameter> violation : fieldsValidator.validate(updated)) {
                throw new ApiRequestException(violation.getMessage());
            }

            Parameter current = parameterRepository
                    .findByNominationAndName(updated.getNomination(), updated.getName())
                    .orElse(null);

            // Если в номинации удалось найти критерий оценивания точно с таким же названием, как у обновлённого,
            // причем это не тот, который мы обновляем.
            if (current != null && !current.getId().equals(id)) {
                throw new ApiRequestException("В номинации уже есть критерий оценивания с таким названием");
            }

            // Сохраняем обновлённые данные.
            updated.setId(id);
            updated.setNomination(parameter.getNomination());
            updated.setScores(parameter.getScores());
            parameterRepository.save(updated);

            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (parameterRepository.existsById(id)) {
            parameterRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
