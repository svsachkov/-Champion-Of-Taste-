package championoftaste.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, описывающий группу номинаций.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter                                 // создаёт дефолтные геттеры автоматически для каждого поля
@Setter                                 // создаёт дефолтные сеттеры автоматически для каждого поля
@EqualsAndHashCode                      // реализует следующие методы: equals(Object other) и hashCode()
@NoArgsConstructor                      // создаёт конструктор без параметров
@Entity(name = "nominationGroup")       // POJO, представляющий данные, которые могут быть сохранены в базе данных
@Table(name = "nominations_groups")     // все объекты данного класса будут храниться в таблице "nominations_groups"
public class NominationGroup {

    @Id
    @SequenceGenerator(
            name = "nominations_groups_sequence",
            sequenceName = "nominations_groups_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "nominations_groups_sequence"
    )
    @Column(name = "id")
    private Integer id;                 // id группы номинаций

    @NotBlank(message = "Название группы номинаций не может быть пустым")
    @Column(name = "name", columnDefinition = "TEXT", nullable = false, unique = true)
    private String name;                // название группы номинаций

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;            // URL фота группы номинаций

    @NotNull(message = "Не указан статус 'активности' группы номинаций")
    @Column(name = "is_active", nullable = false)
    private boolean isActive = false;   // доступна ли группа номинаций для голосования (true - доступна, false - нет)

    @NotNull(message = "Не указан статус 'завершенности' группы номинаций")
    @Column(name = "is_finished", nullable = false)
    private boolean isFinished = false; // завершено ли голосование в группе номинаций (true - завершено, false - нет)

    @JsonIgnore
    @OneToMany(
            // TODO: разобраться с удалением
            cascade = CascadeType.ALL,      // каскадировать все операции на связанные сущности
            fetch = FetchType.LAZY,         // данные ассоциации должны загружаться только по требованию
            mappedBy = "nominationGroup",   // поле, которому принадлежит отношение (на обратной стороне)
            orphanRemoval = true            // следует ли каскадировать операцию удаления для этих объектов
    )
    private List<Nomination> nominations =
            new ArrayList<>();          // список номинаций, представленных в группе

    /**
     * Добавляет номинацию в группу.
     *
     * @param nomination номинация для добавления
     */
    public void addNomination(Nomination nomination) {
        if (!this.nominations.contains(nomination)) {
            this.nominations.add(nomination);
            nomination.setNominationGroup(this);
        }
    }

    /**
     * Удаляет номинацию из группы.
     *
     * @param nomination номинация для удаления
     */
    public void removeNomination(Nomination nomination) {
        if (this.nominations.contains(nomination)) {
            this.nominations.remove(nomination);
            nomination.setNominationGroup(null);
        }
    }
}
