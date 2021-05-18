package championoftaste.api.request;

import championoftaste.api.model.Product;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter                 // создаёт дефолтные геттеры автоматически для каждого поля класса
@AllArgsConstructor     // создает конструктор с 1 параметром для каждого поля класса
@EqualsAndHashCode      // реализует следующие методы: equals(Object other) и hashCode()
@ToString               // реализует метод toString()
public class ScoreRequest {

    private final Short score;      // значение оценки, которую поставил пользователь
    private final Product product;  // продукт, которому выставляется оценка пользователем
}
