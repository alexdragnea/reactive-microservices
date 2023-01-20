package net.dg.ratingservice.repository;

import net.dg.ratingservice.entity.Rating;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RatingRepository extends ReactiveMongoRepository<Rating, String> {

    Flux<Rating> findRatingsByBookId(String bookId);
}
