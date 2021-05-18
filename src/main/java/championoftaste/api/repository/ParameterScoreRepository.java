package championoftaste.api.repository;

import championoftaste.api.model.Parameter;
import championoftaste.api.model.Product;
import championoftaste.api.model.ParameterScore;
import championoftaste.api.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ParameterScoreRepository extends JpaRepository<ParameterScore, Integer> {

    Optional<ParameterScore> findByProductAndParameterAndUser(Product product, Parameter parameter, User user);
}
