package championoftaste.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
public class ApiException {

    private final String message;           // сообщение, содержащее информацию об ошибке
    private final HttpStatus httpStatus;    // статут ответа на запрос
    private final ZonedDateTime timestamp;  // время, когда произошла ошибка
}
