package championoftaste.api.repository;

import championoftaste.api.model.Nomination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface NominationRepository extends JpaRepository<Nomination, Integer> {

    Optional<Nomination> findByName(String name);
}
