package championoftaste.api.repository;

import championoftaste.api.model.Nomination;
import championoftaste.api.model.Parameter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ParameterRepository extends JpaRepository<Parameter, Integer> {

    Optional<Parameter> findByNominationAndName(Nomination nomination, String name);
}
