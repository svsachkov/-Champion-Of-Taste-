package championoftaste.api.repository;

import championoftaste.api.model.Producer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ProducerRepository extends JpaRepository<Producer, Integer> {

    Optional<Producer> findByName(String name);
}
