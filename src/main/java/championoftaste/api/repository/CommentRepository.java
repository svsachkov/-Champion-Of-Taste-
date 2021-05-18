package championoftaste.api.repository;

import championoftaste.api.model.Comment;
import championoftaste.api.model.Product;
import championoftaste.api.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Optional<Comment> findByTextAndProductAndUser(String text, Product product, User user);
}
