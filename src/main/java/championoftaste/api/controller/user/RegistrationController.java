package championoftaste.api.controller.user;

import championoftaste.api.service.UserService;
import championoftaste.api.request.UserRequest;
import championoftaste.exception.ApiRequestException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/registration")
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class RegistrationController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody UserRequest request) {
        try {
            userService.register(request);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Пользователь успешно зарегестрирован", HttpStatus.CREATED);
    }
}
