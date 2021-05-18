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
 * Класс, описывающий продукт.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter                     // создаёт дефолтные геттеры автоматически для каждого поля
@Setter                     // создаёт дефолтные сеттеры автоматически для каждого поля
@EqualsAndHashCode          // реализует следующие методы: equals(Object other) и hashCode()
@NoArgsConstructor          // создаёт конструктор без параметров
@Entity(name = "product")   // POJO, представляющий данные, которые могут быть сохранены в базе данных
@Table(name = "products")   // все объекты данного класса будут храниться в таблице "products"
public class Product {

    @Id
    @SequenceGenerator(
            name = "products_sequence",
            sequenceName = "products_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "products_sequence"
    )
    @Column(name = "id")
    private Integer id;                 // id продукта

    @NotBlank(message = "Название продукта не должно быть пустым")
    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;                // название продукта

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;            // URL фота продукта

    @NotNull(message = "Продукт должен обязательно относиться к какому-либо производителю")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producer_id", referencedColumnName = "id", nullable = false)
    private Producer producer;          // производитель продукта

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nomination_id", referencedColumnName = "id")
    private Nomination nomination;      // номинация, в которой представлен данный продукт

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,  // каскадировать все операции на связанные сущности
            fetch = FetchType.LAZY,     // данные ассоциации должны загружаться только по требованию
            mappedBy = "product",       // поле, которому принадлежит отношение (на обратной стороне)
            orphanRemoval = true        // следует ли каскадировать операцию удаления для этих объектов
    )
    private List<ParameterScore> parameterScores =
            new ArrayList<>();          // список оценок экспертов критериям, которые относятся к данному продукту

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,  // каскадировать все операции на связанные сущности
            fetch = FetchType.LAZY,     // данные ассоциации должны загружаться только по требованию
            mappedBy = "product",       // поле, которому принадлежит отношение (на обратной стороне)
            orphanRemoval = true        // следует ли каскадировать операцию удаления для этих объектов
    )
    private List<Score> scores =
            new ArrayList<>();          // список оценок обычных потребителей, которые относятся к данному продукту

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,  // каскадировать все операции на связанные сущности
            fetch = FetchType.LAZY,     // данные ассоциации должны загружаться только по требованию
            mappedBy = "product",       // поле, которому принадлежит отношение (на обратной стороне)
            orphanRemoval = true        // следует ли каскадировать операцию удаления для этих объектов
    )
    private List<Comment> comments =
            new ArrayList<>();          // список комментариев, которые были написаны пользователями данному продукту

    /**
     * Добавляет оценку, выставленную одному из критериев продукта экспертом, в список оценок продукта.
     *
     * @param parameterScore оценка для добавления
     */
    public void addParameterScore(ParameterScore parameterScore) {
        if (!this.parameterScores.contains(parameterScore)) {
            this.parameterScores.add(parameterScore);
            parameterScore.setProduct(this);
        }
    }

    /**
     * Удаляет оценку, выставленную одному из критериев продукта экспертом, из списка оценок продукта.
     *
     * @param parameterScore оценка для удаления
     */
    public void removeParameterScore(ParameterScore parameterScore) {
        if (this.parameterScores.contains(parameterScore)) {
            this.parameterScores.remove(parameterScore);
            parameterScore.setProduct(null);
        }
    }

    /**
     * Добавляет оценку, выставленную обычным потребителем продукту, в список оценок продукта.
     *
     * @param score оценка для добавления
     */
    public void addConsumerScore(Score score) {
        if (!this.scores.contains(score)) {
            this.scores.add(score);
            score.setProduct(this);
        }
    }

    /**
     * Удаляет оценку, выставленную обычным потребителем продукту, из списка оценок продукта.
     *
     * @param score оценка для удаления
     */
    public void removeConsumerScore(Score score) {
        if (this.scores.contains(score)) {
            this.scores.remove(score);
            score.setProduct(null);
        }
    }

    /**
     * Добавляет комментарий, написанный пользователем данному продукту, в список комментариев продукта.
     *
     * @param comment комментарий для добавления
     */
    public void addComment(Comment comment) {
        if (!this.comments.contains(comment)) {
            this.comments.add(comment);
            comment.setProduct(this);
        }
    }

    /**
     * Удаляет комментарий, написанный пользователм данному продукту из списка комментариев продукта.
     *
     * @param comment комментарий для удаления
     */
    public void removeComment(Comment comment) {
        if (this.comments.contains(comment)) {
            this.comments.remove(comment);
            comment.setProduct(null);
        }
    }
}
