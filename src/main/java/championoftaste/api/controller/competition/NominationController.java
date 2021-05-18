package championoftaste.api.controller.competition;

import championoftaste.api.AuthorizedUser;
import championoftaste.api.model.*;
import championoftaste.api.service.NominationService;
import championoftaste.api.service.UserService;
import championoftaste.exception.ApiRequestException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/nominations")
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class NominationController {

    private final NominationService nominationService;
    private final UserService userService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> create(@RequestBody Nomination nomination) {
        try {
            nominationService.create(nomination);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Номинация успешно создана", HttpStatus.CREATED);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_CONSUMER', 'ROLE_EXPERT', 'ROLE_ADMIN')")
    public ResponseEntity<List<Nomination>> read() {
        final List<Nomination> nominations = nominationService.readAll();

        return (nominations != null && !nominations.isEmpty())
                ? new ResponseEntity<>(nominations, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{nominationId}")
    @PreAuthorize("hasAnyRole('ROLE_CONSUMER', 'ROLE_EXPERT', 'ROLE_ADMIN')")
    public ResponseEntity<Nomination> read(@PathVariable Integer nominationId) {
        final Nomination nomination = nominationService.read(nominationId);

        return nomination != null
                ? new ResponseEntity<>(nomination, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{nominationId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> update(@PathVariable Integer nominationId, @RequestBody Nomination nomination) {
        try {
            return nominationService.update(nominationId, nomination)
                    ? new ResponseEntity<>("Номинация успешно обновлена", HttpStatus.OK)
                    : new ResponseEntity<>("Не удалось найти номинацию", HttpStatus.NOT_FOUND);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{nominationId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable(name = "nominationId") Integer nominationId) {
        return nominationService.delete(nominationId)
                ? new ResponseEntity<>("Номинация успешно удалена", HttpStatus.OK)
                : new ResponseEntity<>("Не удалось найти номинацию", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{nominationId}/products")
    @PreAuthorize("hasAnyRole('ROLE_CONSUMER', 'ROLE_EXPERT', 'ROLE_ADMIN')")
    public ResponseEntity<List<Product>> readProducts(@PathVariable Integer nominationId) {
        final Nomination nomination = nominationService.read(nominationId);

        if (nomination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<Product> products = nomination.getProducts();

        if (products == null || products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<Score> scores = products.get(0).getScores();

        if (scores != null && !scores.isEmpty()) {
            final User user = userService.loadUserByUsername(AuthorizedUser.getUsername());

            if (user == null) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            for (Score score : scores) {
                if (score.getUser().getId().equals(user.getId())) {
                    return new ResponseEntity<>(products, HttpStatus.LOCKED);
                }
            }
        }

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{nominationId}/parameters")
    @PreAuthorize("hasAnyRole('ROLE_EXPERT', 'ROLE_ADMIN')")
    public ResponseEntity<List<Parameter>> readParameters(@PathVariable Integer nominationId) {
        final Nomination nomination = nominationService.read(nominationId);

        if (nomination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<Parameter> parameters = nomination.getParameters();

        return (parameters != null && !parameters.isEmpty())
                ? new ResponseEntity<>(parameters, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{nominationId}/disadvantages")
    @PreAuthorize("hasAnyRole('ROLE_CONSUMER', 'ROLE_ADMIN')")
    public ResponseEntity<List<Disadvantage>> readDisadvantages(@PathVariable Integer nominationId) {
        final Nomination nomination = nominationService.read(nominationId);

        if (nomination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<Disadvantage> disadvantages = nomination.getDisadvantages();

        return (disadvantages != null && !disadvantages.isEmpty())
                ? new ResponseEntity<>(disadvantages, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{nominationId}/activate")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> activate(@PathVariable Integer nominationId) {
        final Nomination nomination = nominationService.read(nominationId);

        if (nomination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        nomination.setActive(true);

        try {
            return nominationService.update(nominationId, nomination)
                    ? new ResponseEntity<>("Номинация теперь активна", HttpStatus.OK)
                    : new ResponseEntity<>("Не удалось найти номинацию", HttpStatus.NOT_FOUND);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{nominationId}/deactivate")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deactivate(@PathVariable Integer nominationId) {
        final Nomination nomination = nominationService.read(nominationId);

        if (nomination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        nomination.setActive(false);

        try {
            return nominationService.update(nominationId, nomination)
                    ? new ResponseEntity<>("Номинация теперь неактивна", HttpStatus.OK)
                    : new ResponseEntity<>("Не удалось найти номинацию", HttpStatus.NOT_FOUND);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{nominationId}/finish")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> finish(@PathVariable Integer nominationId) {
        final Nomination nomination = nominationService.read(nominationId);

        if (nomination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        nomination.setFinished(true);

        try {
            return nominationService.update(nominationId, nomination)
                    ? new ResponseEntity<>("Номинация завершена", HttpStatus.OK)
                    : new ResponseEntity<>("Не удалось найти номинацию", HttpStatus.NOT_FOUND);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{nominationId}/start")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> start(@PathVariable Integer nominationId) {
        final Nomination nomination = nominationService.read(nominationId);

        if (nomination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        nomination.setFinished(false);

        try {
            return nominationService.update(nominationId, nomination)
                    ? new ResponseEntity<>("Номинация снова запущена", HttpStatus.OK)
                    : new ResponseEntity<>("Не удалось найти номинацию", HttpStatus.NOT_FOUND);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{nominationId}/number-of-voters")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Integer> countVoters(@PathVariable Integer nominationId) {
        final Nomination nomination = nominationService.read(nominationId);

        if (nomination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<Product> products = nomination.getProducts();

        if (products == null || products.isEmpty()) {
            return new ResponseEntity<>(0, HttpStatus.OK);
        }

        final List<Score> scores = products.get(0).getScores();

        if (scores == null || scores.isEmpty()) {
            return new ResponseEntity<>(0, HttpStatus.OK);
        }

        return new ResponseEntity<>(scores.size(), HttpStatus.OK);
    }
}
