package championoftaste.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, описывающий номинацию конкурса.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter                         // создаёт дефолтные геттеры автоматически для каждого поля
@Setter                         // создаёт дефолтные сеттеры автоматически для каждого поля
@EqualsAndHashCode              // реализует следующие методы: equals(Object other) и hashCode()
@NoArgsConstructor              // создаёт конструктор без параметров
@Entity(name = "nomination")    // POJO, представляющий данные, которые могут быть сохранены в базе данных
@Table(name = "nominations")    // все объекты данного класса будут храниться в таблице "nominations"
public class Nomination {

    @Id
    @SequenceGenerator(
            name = "nominations_sequence",
            sequenceName = "nominations_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "nominations_sequence"
    )
    @Column(name = "id")
    private Integer id;                         // id номинации

    @NotBlank(message = "Название номинации не должно быть пустым")
    @Column(name = "name", nullable = false, columnDefinition = "TEXT", unique = true)
    private String name;                        // название номинации

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;                    // URL фота номинации

    @NotNull(message = "Не указан статус 'активности' номинации")
    @Column(name = "is_active", nullable = false)
    private boolean isActive = false;           // доступна ли номинация для голосования (true - доступна, false - нет)

    @NotNull(message = "Не указан статус 'завершенности' номинации")
    @Column(name = "is_finished", nullable = false)
    private boolean isFinished = false;         // завершено ли голосование в номинации (true - завершено, false - нет)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nomination_group_id", referencedColumnName = "id")
    private NominationGroup nominationGroup;    // группа, к которой относится данная номинация

    @JsonIgnore
    @OneToMany(
            // TODO: разобраться с удалением
            cascade = CascadeType.ALL,  // каскадировать все операции на связанные сущности
            fetch = FetchType.LAZY,     // данные ассоциации должны загружаться только по требованию
            mappedBy = "nomination",    // поле, которому принадлежит отношение (на обратной стороне)
            orphanRemoval = true        // следует ли каскадировать операцию удаления для этих объектов
    )
    private List<Product> products =
            new ArrayList<>();          // список продуктов, представленных в номинации

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,  // каскадировать все операции на связанные сущности
            fetch = FetchType.LAZY,     // данные ассоциации должны загружаться только по требованию
            mappedBy = "nomination",    // поле, которому принадлежит отношение (на обратной стороне)
            orphanRemoval = true        // следует ли каскадировать операцию удаления для этих объектов
    )
    private List<Parameter> parameters =
            new ArrayList<>();          // список параметров, по которым оценивается продукт в номинации

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,  // каскадировать все операции на связанные сущности
            fetch = FetchType.LAZY,     // данные ассоциации должны загружаться только по требованию
            mappedBy = "nomination",    // поле, которому принадлежит отношение (на обратной стороне)
            orphanRemoval = true        // следует ли каскадировать операцию удаления для этих объектов
    )
    private List<Disadvantage> disadvantages =
            new ArrayList<>();          // список недостатков, которые можно выделить у продуктов данной номинации

    /**
     * Добавляет продукт в список продуктов номинации.
     *
     * @param product продукт для добавления
     */
    public void addProduct(Product product) {
        if (!this.products.contains(product)) {
            this.products.add(product);
            product.setNomination(this);
        }
    }

    /**
     * Удаляет продукт из списка продуктов номинации.
     *
     * @param product продукт для удаления
     */
    public void removeProduct(Product product) {
        if (this.products.contains(product)) {
            this.products.remove(product);
            product.setNomination(null);
        }
    }

    /**
     * Добавляет критерий (параметр) в список критериев (параметров) оценивания продуктов в номинации.
     *
     * @param parameter критерий (параметр) для добавления
     */
    public void addParameter(Parameter parameter) {
        if (!this.parameters.contains(parameter)) {
            this.parameters.add(parameter);
            parameter.setNomination(this);
        }
    }

    /**
     * Удаляет критерий (параметр) из списка критериев (параметров) оценивания продуктов номинации.
     *
     * @param parameter критерий (параметр) для удаления
     */
    public void removeParameter(Parameter parameter) {
        if (this.parameters.contains(parameter)) {
            this.parameters.remove(parameter);
            parameter.setNomination(null);
        }
    }

    /**
     * Добавляет недостаток, который можно выделить у продукта в номинации, в список недостатков.
     *
     * @param disadvantage недостаток для добавления
     */
    public void addDisadvantage(Disadvantage disadvantage) {
        if (!this.disadvantages.contains(disadvantage)) {
            this.disadvantages.add(disadvantage);
            disadvantage.setNomination(this);
        }
    }

    /**
     * Удаляет недостаток, который можно выделить у продукта в номинации, из списка недостатков.
     *
     * @param disadvantage недостаток для удаления
     */
    public void removeDisadvantage(Disadvantage disadvantage) {
        if (this.disadvantages.contains(disadvantage)) {
            this.disadvantages.remove(disadvantage);
            disadvantage.setNomination(null);
        }
    }
}
