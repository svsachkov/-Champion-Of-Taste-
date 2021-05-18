package championoftaste.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Класс, описывающий комментарий (оставленный пользователем) к продукту.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter                     // создаёт дефолтные геттеры автоматически для каждого поля
@Setter                     // создаёт дефолтные сеттеры автоматически для каждого поля
@EqualsAndHashCode          // реализует следующие методы: equals(Object other) и hashCode()
@NoArgsConstructor          // создаёт конструктор без параметров
@Entity(name = "comment")   // POJO, представляющий данные, которые могут быть сохранены в базе данных
@Table(name = "comments")   // все объекты данного класса будут храниться в таблице "comments"
public class Comment {

    @Id
    @SequenceGenerator(
            name = "comments_sequence",
            sequenceName = "comments_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comments_sequence"
    )
    @Column(name = "id")
    private Integer id;         // id комментария

    @NotBlank(message = "Комментарий к продукту не должен быть пустым")
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;        // текст комментария

    @NotNull(message = "Комментарий обязательно должен относиться к какому-либо продукту")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;    // продукт, к которому относится данный комментарий

    @NotNull(message = "Комментарий обязательно должен относиться к какому-либо пользователю")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;          // пользователь, который оставил данный комментарий

    public Comment(String text, Product product, User user) {
        this.text = text;
        this.product = product;
        this.user = user;
    }
}
