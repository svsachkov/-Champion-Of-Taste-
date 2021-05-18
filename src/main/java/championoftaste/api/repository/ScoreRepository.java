package championoftaste.api.repository;

import championoftaste.api.model.Product;
import championoftaste.api.model.Score;
import championoftaste.api.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ScoreRepository extends JpaRepository<Score, Integer> {

    Optional<Score> findByProductAndUser(Product product, User user);

    Optional<List<Score>> findAllByProduct(Product product);
}
