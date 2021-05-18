package championoftaste.api.controller.competition;

import championoftaste.api.model.Disadvantage;
import championoftaste.api.service.DisadvantageService;
import championoftaste.exception.ApiRequestException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/disadvantages")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class DisadvantageController {

    private final DisadvantageService disadvantageService;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody Disadvantage disadvantage) {
        try {
            disadvantageService.create(disadvantage);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_MODIFIED);
        }

        return new ResponseEntity<>("Возможный недостаток успешно создан", HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<Disadvantage>> read() {
        final List<Disadvantage> disadvantages = disadvantageService.readAll();

        return (disadvantages != null && !disadvantages.isEmpty())
                ? new ResponseEntity<>(disadvantages, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{disadvantageId}")
    public ResponseEntity<Disadvantage> read(@PathVariable Integer disadvantageId) {
        final Disadvantage disadvantage = disadvantageService.read(disadvantageId);

        return disadvantage != null
                ? new ResponseEntity<>(disadvantage, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{disadvantageId}")
    public ResponseEntity<?> update(@PathVariable Integer disadvantageId, @RequestBody Disadvantage disadvantage) {
        try {
            return disadvantageService.update(disadvantageId, disadvantage)
                    ? new ResponseEntity<>("Возможный недостаток успешно обновлен", HttpStatus.OK)
                    : new ResponseEntity<>("Не удалось найти возможный недостаток", HttpStatus.NOT_FOUND);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{disadvantageId}")
    public ResponseEntity<?> delete(@PathVariable Integer disadvantageId) {
        return disadvantageService.delete(disadvantageId)
                ? new ResponseEntity<>("Возможный недостаток успешно удален", HttpStatus.OK)
                : new ResponseEntity<>("Не удалось найти возможный недостаток", HttpStatus.NOT_FOUND);
    }
}
