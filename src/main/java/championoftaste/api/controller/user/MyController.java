package championoftaste.api.controller.user;

import championoftaste.api.AuthorizedUser;
import championoftaste.api.model.User;
import championoftaste.api.request.UserRequest;
import championoftaste.api.service.UserService;
import championoftaste.exception.ApiRequestException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/me")
@PreAuthorize("hasAnyRole('ROLE_CONSUMER', 'ROLE_EXPERT', 'ROLE_ADMIN')")
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class MyController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<User> read() {
        try {
            String username = AuthorizedUser.getUsername();
            if (username == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            final User user = userService.loadUserByUsername(username);

            return new ResponseEntity<>(user, HttpStatus.OK);

        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping()
    public ResponseEntity<?> update(@RequestBody UserRequest request) {
        try {
            String username = AuthorizedUser.getUsername();
            if (username == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            final User user = userService.loadUserByUsername(username);

            try {
                final boolean updated = userService.update(user.getId(), request);

                return updated
                        ? new ResponseEntity<>(HttpStatus.OK)
                        : new ResponseEntity<>("Вас не удалось найти", HttpStatus.NOT_FOUND);
            } catch (ApiRequestException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_MODIFIED);
            }
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping()
    public ResponseEntity<?> delete() {
        try {
            String username = AuthorizedUser.getUsername();
            if (username == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            final User user = userService.loadUserByUsername(username);

            final boolean deleted = userService.delete(user.getId());

            return deleted
                    ? new ResponseEntity<>(HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
