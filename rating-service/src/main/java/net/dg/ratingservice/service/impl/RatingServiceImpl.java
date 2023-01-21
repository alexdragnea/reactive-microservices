package net.dg.ratingservice.service.impl;

import lombok.AllArgsConstructor;
import net.dg.ratingservice.entity.Rating;
import net.dg.ratingservice.repository.RatingRepository;
import net.dg.ratingservice.service.RatingService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class RatingServiceImpl implements RatingService {

	private final RatingRepository ratingRepository;

	@Override
	public Flux<Rating> findRatingsByBookId(String bookId) {
		return ratingRepository.findRatingsByBookId(bookId);
	}

	@Override
	public Flux<Rating> findAllRatings() {
		return ratingRepository.findAll();
	}

	@Override
	public Mono<Rating> findRatingById(String ratingId) {
		return ratingRepository.findById(ratingId);
	}

	@Override
	public Mono<Rating> createRating(Rating rating) {
		return ratingRepository.save(rating);
	}

	@Override
	public Mono<Rating> deleteRating(String ratingId) {
		return ratingRepository.findById(ratingId)
				.flatMap(rating -> ratingRepository.deleteById(rating.getId()).thenReturn(rating));
	}

	@Override
	public Mono<Rating> updateRating(Rating rating, String ratingId) {
		return ratingRepository.findById(ratingId).flatMap(existingRating -> {
			existingRating.setBookId(rating.getBookId());
			existingRating.setStars(rating.getStars());
			return ratingRepository.save(existingRating);
		});
	}

}
