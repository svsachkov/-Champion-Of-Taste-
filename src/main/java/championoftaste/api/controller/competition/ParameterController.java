package championoftaste.api.controller.competition;

import championoftaste.api.model.Parameter;
import championoftaste.api.model.ParameterScore;
import championoftaste.api.service.ParameterService;
import championoftaste.exception.ApiRequestException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/parameters")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class ParameterController {

    private final ParameterService parameterService;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody Parameter parameter) {
        try {
            parameterService.create(parameter);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_MODIFIED);
        }

        return new ResponseEntity<>("Критерий оценивания успешно создан", HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<Parameter>> read() {
        final List<Parameter> parameters = parameterService.readAll();

        return (parameters != null && !parameters.isEmpty())
                ? new ResponseEntity<>(parameters, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{parameterId}")
    public ResponseEntity<Parameter> read(@PathVariable("parameterId") Integer parameterId) {
        final Parameter parameter = parameterService.read(parameterId);

        return parameter != null
                ? new ResponseEntity<>(parameter, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{parameterId}")
    public ResponseEntity<?> update(@PathVariable("parameterId") Integer parameterId, @RequestBody Parameter parameter) {
        try {
            return parameterService.update(parameterId, parameter)
                    ? new ResponseEntity<>("Критерий оценивания успешно обновлен", HttpStatus.OK)
                    : new ResponseEntity<>("Не удалось найти критерий оценивания", HttpStatus.NOT_FOUND);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{parameterId}")
    public ResponseEntity<?> delete(@PathVariable("parameterId") Integer parameterId) {
        return parameterService.delete(parameterId)
                ? new ResponseEntity<>("Критерий оценивания успешно удален", HttpStatus.OK)
                : new ResponseEntity<>("Не удалось найти критерий оценивания", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{parameterId}/parameter-scores")
    public ResponseEntity<List<ParameterScore>> getParameterScores(@PathVariable("parameterId") Integer parameterId) {
        final Parameter parameter = parameterService.read(parameterId);

        if (parameter == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<ParameterScore> parameterScores = parameter.getScores();

        return (parameterScores != null && !parameterScores.isEmpty())
                ? new ResponseEntity<>(parameterScores, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
