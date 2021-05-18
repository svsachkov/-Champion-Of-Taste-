package championoftaste.api.controller.competition;

import championoftaste.api.model.Score;
import championoftaste.api.request.ScoreRequest;
import championoftaste.api.service.ScoreService;
import championoftaste.exception.ApiRequestException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/scores")
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class ScoreController {

    private final ScoreService scoreService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_CONSUMER', 'ROLE_EXPERT')")
    public ResponseEntity<?> createAll(@RequestBody List<ScoreRequest> scoreRequests) {
        try {
            scoreService.create(scoreRequests);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Оценки успешно выставлена", HttpStatus.CREATED);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<Score>> read() {
        final List<Score> scores = scoreService.readAll();

        return (scores != null && !scores.isEmpty())
                ? new ResponseEntity<>(scores, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{scoreId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Score> read(@PathVariable Integer scoreId) {
        final Score score = scoreService.read(scoreId);

        return score != null
                ? new ResponseEntity<>(score, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
