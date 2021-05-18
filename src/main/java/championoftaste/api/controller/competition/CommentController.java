package championoftaste.api.controller.competition;

import championoftaste.api.model.Comment;
import championoftaste.api.request.CommentRequest;
import championoftaste.api.service.CommentService;
import championoftaste.exception.ApiRequestException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/comments")
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_CONSUMER')")
    public ResponseEntity<?> createAll(@RequestBody List<CommentRequest> commentRequests) {
        try {
            commentService.create(commentRequests);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Комментарии успешно добавлены", HttpStatus.CREATED);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<Comment>> read() {
        final List<Comment> comments = commentService.readAll();

        return (comments != null && !comments.isEmpty())
                ? new ResponseEntity<>(comments, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Comment> read(@PathVariable Integer commentId) {
        final Comment comment = commentService.read(commentId);

        return comment != null
                ? new ResponseEntity<>(comment, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
