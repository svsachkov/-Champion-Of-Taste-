package championoftaste.api.service;

import championoftaste.api.model.Disadvantage;
import championoftaste.api.repository.DisadvantageRepository;
import championoftaste.exception.ApiRequestException;
import championoftaste.validators.FieldsValidator;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;

import java.util.List;

/**
 * Сервис, реализующий CRUD (Create, Read, Update, Delete) операции над возможными недостатками продуктов номинации.
 */
@Service
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class DisadvantageService implements CrudService<Disadvantage> {

    private final DisadvantageRepository disadvantageRepository;
    private final FieldsValidator<Disadvantage> fieldsValidator = new FieldsValidator<>();

    @Override
    public void create(Disadvantage disadvantage) throws ApiRequestException {
        // Проверяем все поля создаваемого недостатка на корректность.
        for (ConstraintViolation<Disadvantage> violation : fieldsValidator.validate(disadvantage)) {
            throw new ApiRequestException(violation.getMessage());
        }

        // Если в номинации уже есть недостаток с таким названием.
        if (disadvantageRepository
                .findByNominationAndName(disadvantage.getNomination(), disadvantage.getName())
                .isPresent()) {
            throw new ApiRequestException("В номинации уже есть такой недостаток");
        }

        disadvantageRepository.save(disadvantage);
    }

    @Override
    public List<Disadvantage> readAll() {
        return disadvantageRepository.findAll();
    }

    @Override
    public Disadvantage read(Integer id) {
        return disadvantageRepository.findById(id).orElse(null);
    }

    @Override
    public boolean update(Integer id, Disadvantage updated) throws ApiRequestException {
        Disadvantage disadvantage = disadvantageRepository.findById(id).orElse(null);

        // Если нашёлся недостаток для обновления.
        if (disadvantage != null) {
            // Проверяем все поля недостатка, в соответствии с которым, надо обновить.
            for (ConstraintViolation<Disadvantage> violation : fieldsValidator.validate(updated)) {
                throw new ApiRequestException(violation.getMessage());
            }

            Disadvantage current = disadvantageRepository
                    .findByNominationAndName(updated.getNomination(), updated.getName())
                    .orElse(null);

            // Если в номинации удалось найти недостаток точно с таким же названием, причем это не обновляемый.
            if (current != null && !current.getId().equals(id)) {
                throw new ApiRequestException("В номинации уже есть такой недостаток");
            }

            // Сохраняем обновлённые данные.
            updated.setId(id);
            updated.setNomination(disadvantage.getNomination());
            disadvantageRepository.save(updated);

            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (disadvantageRepository.existsById(id)) {
            disadvantageRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
