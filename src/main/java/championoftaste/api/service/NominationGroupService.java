package championoftaste.api.service;

import championoftaste.api.model.NominationGroup;
import championoftaste.api.repository.NominationGroupRepository;
import championoftaste.exception.ApiRequestException;
import championoftaste.validators.FieldsValidator;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;

import java.util.List;

/**
 * Сервис, реализующий CRUD (Create, Read, Update, Delete) операции над группами номинаций.
 */
@Service
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class NominationGroupService implements CrudService<NominationGroup> {

    private final NominationGroupRepository nominationGroupRepository;
    private final FieldsValidator<NominationGroup> fieldsValidator = new FieldsValidator<>();

    @Override
    public void create(NominationGroup nominationGroup) throws ApiRequestException {
        // Проверяем все поля создаваемой группы номинаций на корректность.
        for (ConstraintViolation<NominationGroup> violation : fieldsValidator.validate(nominationGroup)) {
            throw new ApiRequestException(violation.getMessage());
        }

        // Если уже существует группа номинаций с таким же названием.
        if (nominationGroupRepository.findByName(nominationGroup.getName()).isPresent()) {
            throw new ApiRequestException("Группа номинаций с таким названием уже существует");
        }

        nominationGroupRepository.save(nominationGroup);
    }

    @Override
    public List<NominationGroup> readAll() {
        return nominationGroupRepository.findAll();
    }

    @Override
    public NominationGroup read(Integer id) {
        return nominationGroupRepository.findById(id).orElse(null);
    }

    @Override
    public boolean update(Integer id, NominationGroup updated) throws ApiRequestException {
        NominationGroup nominationGroup = nominationGroupRepository.findById(id).orElse(null);

        if (nominationGroup != null) {
            // Проверяем все поля группы номинаций, в соответствии с которой, надо обновить.
            for (ConstraintViolation<NominationGroup> violation : fieldsValidator.validate(updated)) {
                throw new ApiRequestException(violation.getMessage());
            }

            NominationGroup current = nominationGroupRepository
                    .findByName(updated.getName())
                    .orElse(null);

            // Если нашлась группа номинаций с таким же названием, что и обновлённая, и это на та, которую обновляем.
            if (current != null && !current.getId().equals(id)) {
                throw new ApiRequestException("Группа номинаций с таким названием уже существует");
            }

            // Сохраняем изменённые данные.
            updated.setId(id);
            updated.setNominations(nominationGroup.getNominations());
            nominationGroupRepository.save(updated);

            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (nominationGroupRepository.existsById(id)) {
            nominationGroupRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
