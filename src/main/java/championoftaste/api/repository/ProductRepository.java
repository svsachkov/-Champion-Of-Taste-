package championoftaste.api.repository;

import championoftaste.api.model.Producer;
import championoftaste.api.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findByProducerAndName(Producer producer, String name);

    //TODO: удалить
//    @Query(value = "SELECT * FROM PRODUCTS WHERE IS_FINALIST = true", nativeQuery = true)
//    Optional<List<Product>> findByFinalistIsTrue();
}
