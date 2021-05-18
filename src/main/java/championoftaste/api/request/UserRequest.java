package championoftaste.api.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Класс, описывающий пользователя полями, которые он вводит при регистрации.
 */
@Getter                 // создаёт дефолтные геттеры автоматически для каждого поля класса
@AllArgsConstructor     // создает конструктор с 1 параметром для каждого поля класса
@EqualsAndHashCode      // реализует следующие методы: equals(Object other) и hashCode()
@ToString               // реализует метод toString()
public class UserRequest {

    private final String name;          // имя пользователя
    private final String surname;       // фамилия пользователя
    private final String patronymic;    // отчество пользователя
    private final short gender;         // пол (стандарт ISO 5218: 0 - неизвестно, 1 - мужчина, 2 - женщина)
    private final short age;            // возраст пользователя
    private final String phone;         // контактный номер телефона
    private final String email;         // адрес электронной почты пользователя
    private final String info;          // дополнительная информация о пользователе
    private final String password;      // пароль пользователя
    private final String deviceId;      // Android ID устройства, с которого вошёл пользователь
}
