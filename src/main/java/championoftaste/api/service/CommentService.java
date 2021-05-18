package championoftaste.api.service;

import championoftaste.api.model.Comment;
import championoftaste.api.request.CommentRequest;
import championoftaste.api.model.User;
import championoftaste.api.repository.CommentRepository;
import championoftaste.api.AuthorizedUser;
import championoftaste.exception.ApiRequestException;
import championoftaste.validators.FieldsValidator;

import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис, реализующий CRUD (Create, Read, Update, Delete) операции над комментариями,
 * оставленными пользователями продукту.
 */
@Service
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class CommentService implements CrudService<Comment> {

    private final CommentRepository commentRepository;
    private final FieldsValidator<Comment> fieldsValidator = new FieldsValidator<>();
    private final UserService userService;
    
    private Comment createCommentFromCommentRequest(CommentRequest commentRequest) {
        User user;
        try {
            user = userService.loadUserByUsername(AuthorizedUser.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new ApiRequestException(e.getMessage());
        }

        return new Comment(
                commentRequest.getText(),
                commentRequest.getProduct(),
                user
        );
    }

    public void create(CommentRequest commentRequest) throws ApiRequestException {
        User user;
        try {
            user = userService.loadUserByUsername(AuthorizedUser.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new ApiRequestException(e.getMessage());
        }

        Comment comment = new Comment(
                commentRequest.getText(),
                commentRequest.getProduct(),
                user
        );

        create(comment);
    }

    public void create(List<CommentRequest> commentRequests) throws ApiRequestException {
        List<Comment> comments = new ArrayList<>();

        for (CommentRequest request : commentRequests) {
            comments.add(createCommentFromCommentRequest(request));
        }

        createAll(comments);
    }

    public boolean update(Integer id, CommentRequest commentRequest) throws ApiRequestException {
        Comment comment = commentRepository.findById(id).orElse(null);

        if (comment == null) {
            return false;
        }

        User user;
        try {
            user = userService.loadUserByUsername(AuthorizedUser.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new ApiRequestException(e.getMessage());
        }

        Comment updated = new Comment(
                commentRequest.getText(),
                commentRequest.getProduct(),
                user
        );

        return update(id, updated);
    }

    public void createAll(List<Comment> comments) {
        for (Comment comment : comments) {
            // Проверяем все поля создаваемого комментария на корректность.
            for (ConstraintViolation<Comment> violation : fieldsValidator.validate(comment)) {
                throw new ApiRequestException(violation.getMessage());
            }

            // Если уже существует точно такой же комментарий, оставленный тем же пользователем тому же продукту.
            if (commentRepository
                    .findByTextAndProductAndUser(comment.getText(), comment.getProduct(), comment.getUser())
                    .isPresent()) {
                throw new ApiRequestException("Вы уже оставляли точно такой же комментарий данному продукту");
            }
        }

        commentRepository.saveAll(comments);
    }

    @Override
    public void create(Comment comment) throws ApiRequestException {
        // Проверяем все поля создаваемого комментария на корректность.
        for (ConstraintViolation<Comment> violation : fieldsValidator.validate(comment)) {
            throw new ApiRequestException(violation.getMessage());
        }

        // Если уже существует точно такой же комментарий, оставленный тем же пользователем тому же продукту.
        if (commentRepository
                .findByTextAndProductAndUser(comment.getText(), comment.getProduct(), comment.getUser())
                .isPresent()) {
            throw new ApiRequestException("Вы уже оставляли точно такой же комментарий данному продукту");
        }

        commentRepository.save(comment);
    }

    @Override
    public List<Comment> readAll() {
        return commentRepository.findAll();
    }

    @Override
    public Comment read(Integer id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public boolean update(Integer id, Comment updated) throws ApiRequestException {
        Comment comment = commentRepository.findById(id).orElse(null);

        // Если нашёлся комментарий для обновления.
        if (comment != null) {
            // Проверяем все поля комментария, в соответствии с которым, надо обновить.
            for (ConstraintViolation<Comment> violation : fieldsValidator.validate(updated)) {
                throw new ApiRequestException(violation.getMessage());
            }

            Comment current = commentRepository
                    .findByTextAndProductAndUser(updated.getText(), updated.getProduct(), updated.getUser())
                    .orElse(null);

            // Если удалось найти точно такой же комментарий, который был оставлен тем же пользователем
            // и тому же продукту, что и обновлённый, причем это не комментарий, который мы хотим обновить.
            if (current != null && !current.getId().equals(id)) {
                throw new ApiRequestException("Такой комментарий уже существует");
            }

            // Сохраняем обновлённые данные.
            updated.setId(id);
            updated.setProduct(comment.getProduct());
            updated.setUser(comment.getUser());
            commentRepository.save(updated);

            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
