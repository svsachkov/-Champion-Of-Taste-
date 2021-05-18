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
 * Класс, описывающий параметр (критерий), по которому оценивается продукт экспертом.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter                     // создаёт дефолтные геттеры автоматически для каждого поля
@Setter                     // создаёт дефолтные сеттеры автоматически для каждого поля
@EqualsAndHashCode          // реализует следующие методы: equals(Object other) и hashCode()
@NoArgsConstructor          // создаёт конструктор без параметров
@Entity(name = "parameter") // POJO, представляющий данные, которые могут быть сохранены в базе данных
@Table(name = "parameters") // все объекты данного класса будут храниться в таблице "parameters"
public class Parameter {

    @Id
    @SequenceGenerator(
            name = "parameters_sequence",
            sequenceName = "parameters_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "parameters_sequence"
    )
    @Column(name = "id")
    private Integer id;                 // id параметра (критерия)

    @NotBlank(message = "Название критерия не должно быть пустым")
    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;                // название параметра (критерия)

    @NotNull(message = "Критерий должен обязательно относиться к какой-либо номинации")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nomination_id", referencedColumnName = "id", nullable = false)
    private Nomination nomination;      // номинация, к которой относится данный параметр (критерий)

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,  // каскадировать все операции на связанные сущности
            fetch = FetchType.LAZY,     // данные ассоциации должны загружаться только по требованию
            mappedBy = "parameter",     // поле, которому принадлежит отношение (на обратной стороне)
            orphanRemoval = true        // следует ли каскадировать операцию удаления для этих объектов
    )
    private List<ParameterScore> scores =
            new ArrayList<>();          // список оценок, которые относятся к данному критерию

    /**
     * Добавляет оценку в список оценок, выставленных данному критерию.
     *
     * @param score оценка для добавления
     */
    public void addScore(ParameterScore score) {
        if (!this.scores.contains(score)) {
            this.scores.add(score);
            score.setParameter(this);
        }
    }

    /**
     * Удаляет оценку из списка оценок, выставленных данному критерию.
     *
     * @param score оценка для удаления
     */
    public void removeScore(ParameterScore score) {
        if (this.scores.contains(score)) {
            this.scores.remove(score);
            score.setParameter(null);
        }
    }
}
