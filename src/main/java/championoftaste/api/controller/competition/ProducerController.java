package championoftaste.api.controller.competition;

import championoftaste.api.model.Producer;
import championoftaste.api.model.Product;
import championoftaste.api.service.ProducerService;
import championoftaste.exception.ApiRequestException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/producers")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class ProducerController {

    private final ProducerService producerService;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody Producer producer) {
        try {
            producerService.create(producer);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Производитель успешно создан", HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<Producer>> read() {
        final List<Producer> producers = producerService.readAll();

        return (producers != null && !producers.isEmpty())
                ? new ResponseEntity<>(producers, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{producerId}")
    public ResponseEntity<Producer> read(@PathVariable Integer producerId) {
        final Producer producer = producerService.read(producerId);

        return producer != null
                ? new ResponseEntity<>(producer, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{producerId}")
    public ResponseEntity<?> update(@PathVariable Integer producerId, @RequestBody Producer producer) {
        try {
            return producerService.update(producerId, producer)
                    ? new ResponseEntity<>("Производитель успешно обновлен", HttpStatus.OK)
                    : new ResponseEntity<>("Не удалось найти производителя", HttpStatus.NOT_FOUND);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{producerId}")
    public ResponseEntity<?> delete(@PathVariable Integer producerId) {
        return producerService.delete(producerId)
                ? new ResponseEntity<>("Производитель успешно удален", HttpStatus.OK)
                : new ResponseEntity<>("Не удалось найти производителя", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{producerId}/products")
    public ResponseEntity<List<Product>> getProducts(@PathVariable("producerId") Integer producerId) {
        final Producer producer = producerService.read(producerId);

        if (producer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<Product> products = producer.getProducts();

        return (products != null && !products.isEmpty())
                ? new ResponseEntity<>(products, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
