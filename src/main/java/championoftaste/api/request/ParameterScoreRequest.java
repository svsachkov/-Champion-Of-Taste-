package championoftaste.api.request;

import championoftaste.api.model.Parameter;
import championoftaste.api.model.Product;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Класс, описывающий оценку, выставляемую критерию, без её id и пользователя, который её выставил.
 */
@Getter                 // создаёт дефолтные геттеры автоматически для каждого поля класса
@AllArgsConstructor     // создает конструктор с 1 параметром для каждого поля класса
@EqualsAndHashCode      // реализует следующие методы: equals(Object other) и hashCode()
@ToString               // реализует метод toString()
public class ParameterScoreRequest {

    private final Short score;          // значение оценки одного из критерия, по которому оценивается продукт экспертом
    private final Product product;      // продукт, к которому относится данная оценка
    private final Parameter parameter;  // критерий (параметр), к которому относится данная оценка
}
