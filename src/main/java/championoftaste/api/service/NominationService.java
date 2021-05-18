package championoftaste.api.service;

import championoftaste.exception.ApiRequestException;
import championoftaste.api.model.Nomination;
import championoftaste.api.repository.NominationRepository;
import championoftaste.validators.FieldsValidator;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;

import java.util.List;

/**
 * Сервис, реализующий CRUD (Create, Read, Update, Delete) операции над номинациями.
 */
@Service
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class NominationService implements CrudService<Nomination> {

    private final NominationRepository nominationRepository;
    private final FieldsValidator<Nomination> fieldsValidator = new FieldsValidator<>();

    @Override
    public void create(Nomination nomination) throws ApiRequestException {
        // Проверяем все поля создаваемой номинации на корректность.
        for (ConstraintViolation<Nomination> violation : fieldsValidator.validate(nomination)) {
            throw new ApiRequestException(violation.getMessage());
        }

        // Если уже существует номинация с точно таким же названием.
        if (nominationRepository.findByName(nomination.getName()).isPresent()) {
            throw new ApiRequestException("Номинация с таким названием уже существует");
        }

        nominationRepository.save(nomination);
    }

    @Override
    public List<Nomination> readAll() {
        return nominationRepository.findAll();
    }

    @Override
    public Nomination read(Integer id) {
        return nominationRepository.findById(id).orElse(null);
    }

    @Override
    public boolean update(Integer id, Nomination updated) throws ApiRequestException {
        Nomination nomination = nominationRepository.findById(id).orElse(null);

        // Если нашлась номинация для обновления.
        if (nomination != null) {
            // Проверяем все поля номинации, в соответствии с которой, надо обновить.
            for (ConstraintViolation<Nomination> violation : fieldsValidator.validate(updated)) {
                throw new ApiRequestException(violation.getMessage());
            }

            Nomination current = nominationRepository
                    .findByName(updated.getName())
                    .orElse(null);

            // Если удалось найти номинацию с точно таким же названием, причем это не та, которую обновляем.
            if (current != null && !current.getId().equals(id)) {
                throw new ApiRequestException("Номинация с таким названием уже существует");
            }

            // Сохраняем обновлённые данные.
            updated.setId(id);
            updated.setNominationGroup(nomination.getNominationGroup());
            updated.setProducts(nomination.getProducts());
            updated.setParameters(nomination.getParameters());
            updated.setDisadvantages(nomination.getDisadvantages());
            nominationRepository.save(updated);

            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (nominationRepository.existsById(id)) {
            nominationRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
