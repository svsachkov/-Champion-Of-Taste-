package championoftaste.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Класс, описывающий оценку, которую выставил пользователь (обычный потребитель или эксперт) продукту.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter                             // создаёт дефолтные геттеры автоматически для каждого поля
@Setter                             // создаёт дефолтные сеттеры автоматически для каждого поля
@EqualsAndHashCode                  // реализует следующие методы: equals(Object other) и hashCode()
@NoArgsConstructor                  // создаёт конструктор без параметров
@Entity(name = "score")             // POJO, представляющий данные, которые могут быть сохранены в базе данных
@Table(name = "scores")             // все объекты данного класса будут храниться в таблице "scores"
public class Score {

    @Id
    @SequenceGenerator(
            name = "scores_sequence",
            sequenceName = "scores_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "scores_sequence"
    )
    @Column(name = "id")
    private Integer id;                 // id оценки пользователя

    @NotNull(message = "Обязательно надо указать значение оценки")
    @Column(name = "score", nullable = false)
    private Short score;                // значение оценки, которую поставил пользователь

    @NotNull(message = "Оценка обязательно должна относиться к какому-либо продукту")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;            // продукт, к которому относится данная оценка

    @NotNull(message = "Оценка обязательно должна относиться к какому-либо пользователю")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;                  // пользователь, который выставил оценку

    @NotNull(message = "Обязательно должно быть указано является оценка экспертной или нет")
    @Column(name = "is_expert", nullable = false)
    private boolean isExpert = false;   // является ли оценка экспертной

    public Score(Short score, Product product, User user, boolean isExpert) {
        this.score = score;
        this.product = product;
        this.user = user;
        this.isExpert = isExpert;
    }
}
