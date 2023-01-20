package net.dg.ratingservice.rest;

import lombok.AllArgsConstructor;
import net.dg.ratingservice.entity.Rating;
import net.dg.ratingservice.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.stream.Stream;

@RestController
@AllArgsConstructor
@RequestMapping("/rating")
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    Flux<Rating> getAll() {
        return ratingService.findAllRatings();
    }

    @GetMapping("/book/{id}")
    Flux<Rating> getRatingsByBookId(@PathVariable("id") String id) {
        return ratingService.findRatingsByBookId(id);
    }


    @GetMapping("/{id}")
    Mono<Rating> getById(@PathVariable("id") String id) {
        return ratingService.findRatingById(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<Rating> create(@RequestBody Rating rating) {
        return ratingService.createRating(rating);
    }

    @DeleteMapping("/{id}")
    Mono<ResponseEntity<Rating>> deleteById(@PathVariable String id) {
        return
                ratingService.deleteRating(id).map(ResponseEntity::ok)
                        .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PutMapping("/{ratingId}")
    public Mono<ResponseEntity<Rating>> updateUserById(@PathVariable String ratingId, @RequestBody Rating rating) {
        return ratingService.updateRating(rating, ratingId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Rating> streamAllRatings() {
        return ratingService
                .findAllRatings()
                .flatMap(rating -> Flux
                        .zip(Flux.interval(Duration.ofSeconds(2)),
                                Flux.fromStream(Stream.generate(() -> rating))
                        )
                        .map(Tuple2::getT2)
                );
    }

}
