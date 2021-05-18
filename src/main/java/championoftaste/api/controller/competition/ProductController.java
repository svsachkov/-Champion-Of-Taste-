package championoftaste.api.controller.competition;

import championoftaste.api.model.Comment;
import championoftaste.api.model.ParameterScore;
import championoftaste.api.model.Product;
import championoftaste.api.model.Score;
import championoftaste.api.service.ProductService;
import championoftaste.exception.ApiRequestException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> create(@RequestBody Product product) {
        try {
            productService.create(product);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_MODIFIED);
        }

        return new ResponseEntity<>("Продукт успешно создан", HttpStatus.CREATED);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<Product>> read() {
        final List<Product> products = productService.readAll();

        return (products != null && !products.isEmpty())
                ? new ResponseEntity<>(products, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Product> read(@PathVariable("productId") Integer productId) {
        final Product product = productService.read(productId);

        return product != null
                ? new ResponseEntity<>(product, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> update(@PathVariable("productId") Integer productId, @RequestBody Product product) {
        try {
            return productService.update(productId, product)
                    ? new ResponseEntity<>("Продукт успешно обновлен", HttpStatus.OK)
                    : new ResponseEntity<>("Не удалось найти продукт", HttpStatus.NOT_FOUND);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Integer productId) {
        return productService.delete(productId)
                ? new ResponseEntity<>("Продукт успешно удален", HttpStatus.OK)
                : new ResponseEntity<>("Не удалось найти продукт", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{productId}/comments")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<Comment>> getComments(@PathVariable("productId") Integer productId) {
        final Product product = productService.read(productId);

        if (product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<Comment> comments = product.getComments();

        return (comments != null && !comments.isEmpty())
                ? new ResponseEntity<>(comments, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{productId}/scores")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<Score>> getScores(@PathVariable("productId") Integer productId) {
        final Product product = productService.read(productId);

        if (product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<Score> scores = product.getScores();

        return (scores != null && !scores.isEmpty())
                ? new ResponseEntity<>(scores, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{productId}/parameter-scores")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<ParameterScore>> getParameterScores(@PathVariable("productId") Integer productId) {
        final Product product = productService.read(productId);

        if (product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<ParameterScore> parameterScores = product.getParameterScores();

        return (parameterScores != null && !parameterScores.isEmpty())
                ? new ResponseEntity<>(parameterScores, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{productId}/scores/average")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Double> getScoresAverage(@PathVariable("productId") Integer productId) {
        final Product product = productService.read(productId);

        if (product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<Score> scores = product.getScores();

        if (scores == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Double average = 0.0;

        for (Score score : scores) {
            average += score.getScore();
        }
        
        return scores.size() == 0
                ? new ResponseEntity<>(average, HttpStatus.OK)
                : new ResponseEntity<>(average / scores.size(), HttpStatus.OK);
    }
}
