package championoftaste.validators;

import lombok.NoArgsConstructor;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import java.util.Set;

/**
 * Класс, служащий для проверки полей класса на корректность (валидность).
 *
 * @param <T> класс, объект которого будет проверяться
 */
@NoArgsConstructor
public class FieldsValidator<T> {

    /**
     * Проверяет поля объекта на корректность (валидность), в соответствии с аннотациями указанными над ними.
     *
     * @param entity объект (сущность), чьи поля надо проверить
     * @return Set ошибок, которые были выявлены в резальтате проверки
     */
    public Set<ConstraintViolation<T>> validate(T entity) {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(entity);
    }
}
