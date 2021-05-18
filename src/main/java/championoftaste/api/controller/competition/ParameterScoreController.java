package championoftaste.api.controller.competition;

import championoftaste.api.model.ParameterScore;
import championoftaste.api.model.Score;
import championoftaste.api.request.ParameterScoreRequest;
import championoftaste.api.service.ParameterScoreService;
import championoftaste.exception.ApiRequestException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/parameter-scores")
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class ParameterScoreController {

    private final ParameterScoreService parameterScoreService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('ROLE_EXPERT')")
    public ResponseEntity<?> createAll(@RequestBody List<ParameterScoreRequest> parameterScoreRequests) {
        try {
            parameterScoreService.create(parameterScoreRequests);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Оценка успешно выставлена", HttpStatus.CREATED);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<ParameterScore>> read() {
        final List<ParameterScore> scores = parameterScoreService.readAll();

        return (scores != null && !scores.isEmpty())
                ? new ResponseEntity<>(scores, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{scoreId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ParameterScore> read(@PathVariable Integer scoreId) {
        final ParameterScore score = parameterScoreService.read(scoreId);

        return score != null
                ? new ResponseEntity<>(score, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
