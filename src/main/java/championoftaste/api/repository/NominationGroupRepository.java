package championoftaste.api.repository;

import championoftaste.api.model.NominationGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface NominationGroupRepository extends JpaRepository<NominationGroup, Integer> {

    Optional<NominationGroup> findByName(String name);
}
