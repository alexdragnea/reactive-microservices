package net.dg.ratingservice.service;

import net.dg.ratingservice.entity.Rating;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RatingService {

    Flux<Rating> findRatingsByBookId(String bookId);

    Flux<Rating> findAllRatings();

    Mono<Rating> findRatingById(String ratingId);

    Mono<Rating> createRating(Rating rating);

    Mono<Rating> deleteRating(String ratingId);

    Mono<Rating> updateRating(Rating rating, String ratingId);

}
