package championoftaste.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Запрос аутентификации пользователя по имени пользователя и паролю.
 */
@Getter             // создаёт дефолтные геттеры автоматически для каждого поля класса
@Setter             // создаёт дефолтные сеттеры автоматически для каждого поля класса
@NoArgsConstructor  // создаёт конструктор без параметров
public class UsernameAndPasswordAuthenticationRequest {

    private String username;    // имя пользователя
    private String password;    // пароль пользователя
}
