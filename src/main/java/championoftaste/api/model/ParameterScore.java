package championoftaste.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Класс, описывающий оценку определённого параметра продукта (оценка выставляется экспертом).
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter                         // создаёт дефолтные геттеры автоматически для каждого поля
@Setter                         // создаёт дефолтные сеттеры автоматически для каждого поля
@EqualsAndHashCode              // реализует следующие методы: equals(Object other) и hashCode()
@NoArgsConstructor              // создаёт конструктор без параметров
@Entity(name = "parameterScore")   // POJO, представляющий данные, которые могут быть сохранены в базе данных
@Table(name = "parameters_scores") // все объекты данного класса будут храниться в таблице "parameters_scores"
public class ParameterScore {

    @Id
    @SequenceGenerator(
            name = "parameters_scores_sequence",
            sequenceName = "parameters_scores_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "parameters_scores_sequence"
    )
    @Column(name = "id")
    private Integer id;             // id оценки одного из критерия, по которому оценивается продукт экспертом

    @NotNull(message = "Обязательно надо указать значение оценки")
    @Column(name = "score", nullable = false)
    private Short score;            // значение оценки одного из критерия, по которому оценивается продукт экспертом

    @NotNull(message = "Оценка обязательно должна относиться к какому-либо продукту")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;        // продукт, к которому относится данная оценка

    @NotNull(message = "Оценка должна обязательно относиться к одному из критериев оценивания продукта")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter_id", referencedColumnName = "id", nullable = false)
    private Parameter parameter;    // критерий (параметр), к которому относится данная оценка

    @NotNull(message = "Оценка обязательно должна относиться к какому-либо пользователю")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;              // пользователь, который выставил оценку

    public ParameterScore(Short score, Product product, Parameter parameter, User user) {
        this.score = score;
        this.product = product;
        this.parameter = parameter;
        this.user = user;
    }
}
