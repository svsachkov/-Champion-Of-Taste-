package championoftaste.api.service;

import championoftaste.api.model.Producer;
import championoftaste.api.repository.ProducerRepository;
import championoftaste.exception.ApiRequestException;
import championoftaste.validators.FieldsValidator;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;

import java.util.List;

/**
 * Сервис, реализующий CRUD (Create, Read, Update, Delete) операции над производителями продуктов.
 */
@Service
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class ProducerService implements CrudService<Producer> {

    private final ProducerRepository producerRepository;
    private final FieldsValidator<Producer> fieldsValidator = new FieldsValidator<>();

    @Override
    public void create(Producer producer) throws ApiRequestException {
        // Проверяем все поля создаваемого производителя на корректность.
        for (ConstraintViolation<Producer> violation : fieldsValidator.validate(producer)) {
            throw new ApiRequestException(violation.getMessage());
        }

        // Если уже существует производитель точно с таким же названием.
        if (producerRepository.findByName(producer.getName()).isPresent()) {
            throw new ApiRequestException("Производитель с таким названием уже существует");
        }

        producerRepository.save(producer);
    }

    @Override
    public List<Producer> readAll() {
        return producerRepository.findAll();
    }

    @Override
    public Producer read(Integer id) {
        return producerRepository.findById(id).orElse(null);
    }

    @Override
    public boolean update(Integer id, Producer updated) throws ApiRequestException {
        Producer producer = producerRepository.findById(id).orElse(null);

        // Если нашёлся производитель для обновления.
        if (producer != null) {
            // Проверяем все поля производителя, в соответствии с которым, надо обновить.
            for (ConstraintViolation<Producer> violation : fieldsValidator.validate(updated)) {
                throw new ApiRequestException(violation.getMessage());
            }

            Producer current = producerRepository
                    .findByName(updated.getName())
                    .orElse(null);

            // Если удалось найти производителя с точно таким же названием, причем не того, которого мы обновляем.
            if (current != null && !current.getId().equals(id)) {
                throw new ApiRequestException("Производитель с таким названием уже существует");
            }

            // Сохраняем обновлённые данные.
            updated.setId(id);
            updated.setProducts(producer.getProducts());
            producerRepository.save(updated);

            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (producerRepository.existsById(id)) {
            producerRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
