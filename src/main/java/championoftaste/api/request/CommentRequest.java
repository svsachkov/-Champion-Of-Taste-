package championoftaste.api.request;

import championoftaste.api.model.Product;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Класс, описывающий комментарий без указания его id и пользователя, который оставил комментарий.
 */
@Getter                 // создаёт дефолтные геттеры автоматически для каждого поля класса
@AllArgsConstructor     // создает конструктор с 1 параметром для каждого поля класса
@EqualsAndHashCode      // реализует следующие методы: equals(Object other) и hashCode()
@ToString               // реализует метод toString()
public class CommentRequest {

    private final String text;      // текст комментария
    private final Product product;  // продукт, к которому относится данный комментарий
}
