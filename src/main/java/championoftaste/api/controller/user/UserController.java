package championoftaste.api.controller.user;

import championoftaste.api.model.Comment;
import championoftaste.api.model.ParameterScore;
import championoftaste.api.model.Score;
import championoftaste.api.model.User;
import championoftaste.api.service.UserService;
import championoftaste.exception.ApiRequestException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody User user) {
        try {
            userService.create(user);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Пользователь успешно создан", HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<User>> read() {
        final List<User> users = userService.readAll();

        return (users != null && !users.isEmpty())
                ? new ResponseEntity<>(users, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> read(@PathVariable(name = "userId") Integer userId) {
        final User user = userService.read(userId);

        return user != null
                ? new ResponseEntity<>(user, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> update(@PathVariable(name = "userId") Integer userId, @RequestBody User user) {
        try {
            return userService.update(userId, user)
                    ? new ResponseEntity<>("Пользователь успешно обновлен", HttpStatus.OK)
                    : new ResponseEntity<>("Не удалось найти пользователя", HttpStatus.NOT_FOUND);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable(name = "userId") Integer userId) {
        return userService.delete(userId)
                ? new ResponseEntity<>("Пользователь успешно удален", HttpStatus.OK)
                : new ResponseEntity<>("Не удалось найти пользователя", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{userId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable(name = "userId") Integer userId) {
        final User user = userService.read(userId);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<Comment> comments = user.getComments();

        return (comments != null && !comments.isEmpty())
                ? new ResponseEntity<>(comments, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{userId}/scores")
    public ResponseEntity<List<Score>> getScores(@PathVariable(name = "userId") Integer userId) {
        final User user = userService.read(userId);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<Score> scores = user.getScores();

        return (scores != null && !scores.isEmpty())
                ? new ResponseEntity<>(scores, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{userId}/parameter-scores")
    public ResponseEntity<List<ParameterScore>> getParameterScores(@PathVariable(name = "userId") Integer userId) {
        final User user = userService.read(userId);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<ParameterScore> parameterScores = user.getParameterScores();

        return (parameterScores != null && !parameterScores.isEmpty())
                ? new ResponseEntity<>(parameterScores, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
