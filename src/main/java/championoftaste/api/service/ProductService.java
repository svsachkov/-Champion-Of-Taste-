package championoftaste.api.service;

import championoftaste.exception.ApiRequestException;
import championoftaste.api.model.Product;
import championoftaste.api.repository.ProductRepository;
import championoftaste.validators.FieldsValidator;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;

import java.util.List;

/**
 * Сервис, реализующий CRUD (Create, Read, Update, Delete) операции над продуктами.
 */
@Service
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class ProductService implements CrudService<Product> {

    private final ProductRepository productRepository;
    private final FieldsValidator<Product> fieldsValidator = new FieldsValidator<>();

    @Override
    public void create(Product product) throws ApiRequestException {
        // Проверяем все поля создаваемого продукта на корректность.
        for (ConstraintViolation<Product> violation : fieldsValidator.validate(product)) {
            throw new ApiRequestException(violation.getMessage());
        }

        // Если у производителя уже есть продукт с точно таким же названием.
        if (productRepository
                .findByProducerAndName(product.getProducer(), product.getName())
                .isPresent()) {
            throw new ApiRequestException("У производителя уже есть продукт с таким названием");
        }

        productRepository.save(product);
    }

    @Override
    public List<Product> readAll() {
        return productRepository.findAll();
    }

    @Override
    public Product read(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public boolean update(Integer id, Product updated) throws ApiRequestException {
        Product product = productRepository.findById(id).orElse(null);

        // Если нашёлся продукт для обновления.
        if (product != null) {
            // Проверяем все поля продукта, в соответствии с которым, надо обновить.
            for (ConstraintViolation<Product> violation : fieldsValidator.validate(updated)) {
                throw new ApiRequestException(violation.getMessage());
            }

            Product current = productRepository
                    .findByProducerAndName(updated.getProducer(), updated.getName())
                    .orElse(null);

            // Если у производителя удалось найти продукт с точно таким же названием,
            // причем это не тот, который мы обновляем.
            if (current != null && !current.getId().equals(id)) {
                throw new ApiRequestException("У производителя уже есть продукт с таким названием");
            }

            // Сохраняем обновлённые данные.
            updated.setId(id);
            updated.setProducer(product.getProducer());
            updated.setNomination(product.getNomination());
            updated.setParameterScores(product.getParameterScores());
            updated.setScores(product.getScores());
            updated.setComments(product.getComments());
            productRepository.save(updated);

            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
