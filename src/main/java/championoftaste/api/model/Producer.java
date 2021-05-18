package championoftaste.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, описывающий производителя продуктов.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter                     // создаёт дефолтные геттеры автоматически для каждого поля
@Setter                     // создаёт дефолтные сеттеры автоматически для каждого поля
@EqualsAndHashCode          // реализует следующие методы: equals(Object other) и hashCode()
@NoArgsConstructor          // создаёт конструктор без параметров
@Entity(name = "producer")  // POJO, представляющий данные, которые могут быть сохранены в базе данных
@Table(name = "producers")  // все объекты данного класса будут храниться в таблице "producers"
public class Producer {

    @Id
    @SequenceGenerator(
            name = "producers_sequence",
            sequenceName = "producers_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "producers_sequence"
    )
    @Column(name = "id")
    private Integer id;                 // id производителя

    @NotBlank(message = "Название производителя не может быть пустым")
    @Column(name = "name", nullable = false, columnDefinition = "TEXT", unique = true)
    private String name;                // полное наименование организации-производителя

    @Column(name = "director", columnDefinition = "TEXT")
    private String director;            // информация о начальнике предприятия

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,  // каскадировать все операции на связанные сущности
            fetch = FetchType.LAZY,     // данные ассоциации должны загружаться только по требованию
            mappedBy = "producer",      // поле, которому принадлежит отношение (на обратной стороне)
            orphanRemoval = true        // следует ли каскадировать операцию удаления для этих объектов
    )
    private List<Product> products =
            new ArrayList<>();          // список продуктов от производителя

    /**
     * Добавляет продукт в список продуктов производителя.
     *
     * @param product продукт для добавления
     */
    public void addProduct(Product product) {
        if (!this.products.contains(product)) {
            this.products.add(product);
            product.setProducer(this);
        }
    }

    /**
     * Удаляет продукт из списка продуктов производителя.
     *
     * @param product продукт для удаления
     */
    public void removeProduct(Product product) {
        if (this.products.contains(product)) {
            this.products.remove(product);
            product.setProducer(null);
        }
    }
}
