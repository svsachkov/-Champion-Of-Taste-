package championoftaste.api.model;

import championoftaste.api.UserRole;
import championoftaste.validators.Phone;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Класс, описывающий пользователя.
 */
@Getter                 // создаёт дефолтные геттеры автоматически для каждого поля
@Setter                 // создаёт дефолтные сеттеры автоматически для каждого поля
@EqualsAndHashCode      // реализует следующие методы: equals(Object other) и hashCode()
@NoArgsConstructor      // создаёт конструктор без параметров
@Entity(name = "user")  // POJO, представляющий данные, которые могут быть сохранены в базе данных
@Table(name = "users")  // все объекты данного класса будут храниться в таблице "users"
public class User implements UserDetails {

    @Id
    @SequenceGenerator(
            name = "users_sequence",
            sequenceName = "users_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "users_sequence"
    )
    @Column(name = "id")
    private Integer id;                 // id пользователя

    @NotNull(message = "У пользователя обязательно должна быть указана роль")
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;              // роль пользователя (ROLE_CONSUMER, ROLE_EXPERT, ROLE_ADMIN)

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 1, max = 35, message = "Имя пользователя должно содержать от 1 до 35 символов")
    @Column(name = "name", nullable = false)
    private String name;                // имя пользователя

    @NotBlank(message = "Фамилия пользователя не может быть пустой")
    @Size(min = 1, max = 35, message = "Фамилия пользователя должна содержать от 1 до 35 символов")
    @Column(name = "surname", nullable = false)
    private String surname;             // фамилия пользователя

    @Size(min = 1, max = 35, message = "Отчество пользователя должно содержать от 1 до 35 символов")
    @Column(name = "patronymic")
    private String patronymic;          // отчество пользователя

    @Min(value = 0, message = "Указатель на пол должен быть не меньше 0")
    @Max(value = 2, message = "Указатель на пол должен быть не больше 2")
    @Column(name = "gender", nullable = false)
    private short gender;               // пол (стандарт ISO 5218: 0 - неизвестно, 1 - мужчина, 2 - женщина)

    @Min(value = 1, message = "Возраст пользователя должен быть не меньше 1")
    @Max(value = 130, message = "Возраст пользователя должен быть не больше 130")
    @Column(name = "age", nullable = false)
    private short age;                  // возраст пользователя

    @Phone(message = "Некорректный телефонный номер")
    @NotBlank(message = "Номер телефона не может быть пустым")
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;               // контактный номер телефона пользователя

    @Email(message = "Некорректный адрес электронной почты")
    @NotBlank(message = "Адрес электронной почты не может быть пустым")
    @Column(name = "email", nullable = false, unique = true)
    private String email;               // адрес электронной почты пользователя

    @Column(name = "info")
    private String info;                // дополнительная информация о пользователе (например: откуда узнал о конкурсе)

    @NotNull(message = "Поле с паролем должно быть заполнено")
    @Column(name = "password", nullable = false)
    private String password;            // пароль пользователя

    @Column(name = "device_id")
    private String deviceId;            // Android ID устройства, с которого вошёл пользователь

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,  // каскадировать все операции на связанные сущности
            fetch = FetchType.LAZY,     // данные ассоциации должны загружаться только по требованию
            mappedBy = "user",          // поле, которому принадлежит отношение (на обратной стороне)
            orphanRemoval = true        // следует ли каскадировать операцию удаления для этих объектов
    )
    private List<Comment> comments =
            new ArrayList<>();          // список комментариев, которые были написаны данным пользователем

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,  // каскадировать все операции на связанные сущности
            fetch = FetchType.LAZY,     // данные ассоциации должны загружаться только по требованию
            mappedBy = "user",          // поле, которому принадлежит отношение (на обратной стороне)
            orphanRemoval = true        // следует ли каскадировать операцию удаления для этих объектов
    )
    private List<ParameterScore> parameterScores =
            new ArrayList<>();          // список оценок, которые были выставлены пользователем критериям оценивания

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,  // каскадировать все операции на связанные сущности
            fetch = FetchType.LAZY,     // данные ассоциации должны загружаться только по требованию
            mappedBy = "user",          // поле, которому принадлежит отношение (на обратной стороне)
            orphanRemoval = true        // следует ли каскадировать операцию удаления для этих объектов
    )
    private List<Score> scores =
            new ArrayList<>();          // список оценок, которые были выставлены пользователем продуктам

    public User(UserRole role,
                String name,
                String surname,
                String patronymic,
                short gender,
                short age,
                String phone,
                String email,
                String info,
                String password,
                String deviceId) {
        this.role = role;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.info = info;
        this.password = password;
        this.deviceId = deviceId;
    }

    /**
     * Добавляет комментарий пользователя в список его комментариев.
     *
     * @param comment комментарий для добавления
     */
    public void addComment(Comment comment) {
        if (!this.comments.contains(comment)) {
            this.comments.add(comment);
            comment.setUser(this);
        }
    }

    /**
     * Удаляет комментарий из списка комментариев пользователя.
     *
     * @param comment комментарий для удаления
     */
    public void removeComment(Comment comment) {
        if (this.comments.contains(comment)) {
            this.comments.remove(comment);
            comment.setUser(null);
        }
    }

    /**
     * Добавляет оценку, которую пользователь (эксперт) выставил определённому критерию оценивания продукта.
     *
     * @param parameterScore оценка для добавления
     */
    public void addParameterScore(ParameterScore parameterScore) {
        if (!this.parameterScores.contains(parameterScore)) {
            this.parameterScores.add(parameterScore);
            parameterScore.setUser(this);
        }
    }

    /**
     * Удаляет оценку из списка оценок пользователя, которые он выставил как эксперт определённым критериям.
     *
     * @param parameterScore оценка для удаления
     */
    public void removeParameterScore(ParameterScore parameterScore) {
        if (this.parameterScores.contains(parameterScore)) {
            this.parameterScores.remove(parameterScore);
            parameterScore.setUser(null);
        }
    }

    /**
     * Добавляет оценку, которую поставил пользователь в список его оценок.
     *
     * @param score оценка для добавления
     */
    public void addScore(Score score) {
        if (!this.scores.contains(score)) {
            this.scores.add(score);
            score.setUser(this);
        }
    }

    /**
     * Удаляет оценку из списка оценок пользователя, которые он выставил продуктам.
     *
     * @param score оценка для удаления
     */
    public void removeScore(Score score) {
        if (this.scores.contains(score)) {
            this.scores.remove(score);
            score.setUser(null);
        }
    }

    /**
     * Возвращает полномочия, предоставленные пользователю.
     *
     * @return коллекция полномочий пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Возвращает имя пользователя, используемое для аутентификации пользователя.
     *
     * @return строка, содержащая имя пользователя
     */
    @Override
    public String getUsername() {
        // В данном случае в качестве имени пользователя используется email или номер телефона пользователя.
        return (email == null || email.isBlank()) ? phone : email;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return строка, содержащая пароль пользователя
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Указывает, не истек ли срок действия учетной записи пользователя.
     *
     * @return true - не истёк, false - истёк
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, не заблокирован ли пользователь.
     *
     * @return true - не заблокирован, false - заблокирован
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, не истёк ли срок действия учётных данных пользователя.
     *
     * @return true - не истёк, false - истёк
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Указывает, доступен или недоступен пользователь.
     *
     * @return true - доступен, false - недоступен
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
