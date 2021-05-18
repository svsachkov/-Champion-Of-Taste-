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
 * Класс, описывающий недостаток, который предлагается указать пользователю у продукта в данной номинации.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter                         // создаёт дефолтные геттеры автоматически для каждого поля
@Setter                         // создаёт дефолтные сеттеры автоматически для каждого поля
@EqualsAndHashCode              // реализует следующие методы: equals(Object other) и hashCode()
@NoArgsConstructor              // создаёт конструктор без параметров
@Entity(name = "disadvantage")  // POJO, представляющий данные, которые могут быть сохранены в базе данных
@Table(name = "disadvantages")  // все объекты данного класса будут храниться в таблице "disadvantages"
public class Disadvantage {

    @Id
    @SequenceGenerator(
            name = "disadvantages_sequence",
            sequenceName = "disadvantages_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "disadvantages_sequence"
    )
    @Column(name = "id")
    private Integer id;             // id недостатка

    @NotBlank(message = "Название недостатка не должно быть пустым")
    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;            // название недостатка

    @NotNull(message = "Недостаток должен обязательно относиться к какой-либо номинации")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nomination_id", referencedColumnName = "id", nullable = false)
    private Nomination nomination;  // номинация, у продуктов которой можно выделить данный недостаток
}
