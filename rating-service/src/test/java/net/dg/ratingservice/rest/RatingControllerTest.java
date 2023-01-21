package net.dg.ratingservice.rest;

import net.dg.ratingservice.entity.Rating;
import net.dg.ratingservice.repository.RatingRepository;
import net.dg.ratingservice.service.RatingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest
class RatingControllerTest {

	@Autowired
	private WebTestClient client;

	@MockBean
	private RatingRepository ratingRepository;

	@MockBean
	private RatingService ratingService;

	@Test
	void getAllRatings() {

		Mockito.when(ratingService.findAllRatings())
				.thenReturn(Flux.just(new Rating("1", "1", 5), new Rating("1", "2", 4)));

		client.get().uri("/rating").exchange().expectStatus().isOk().expectBody().jsonPath("$.[0].bookId")
				.isEqualTo("1").jsonPath("$.[0].stars").isEqualTo(5).jsonPath("$.[1].bookId").isEqualTo("2")
				.jsonPath("$.[1].stars").isEqualTo(4);
	}

	@Test
	void getBookById() {
		Rating data = new Rating("1", "2", 4);
		Mockito.when(ratingService.findRatingById(any())).thenReturn(Mono.just(data));

		client.get().uri("/rating/" + data.getId()).exchange().expectStatus().isOk().expectBody().jsonPath("$.bookId")
				.isEqualTo("2").jsonPath("$.stars").isEqualTo(4);
	}

	@Test
	void saveBook() {
		Rating data = new Rating(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 2);
		Mockito.when(ratingService.createRating(any(Rating.class))).thenReturn(Mono.just(data));

		client.post().uri("/rating").body(Mono.just(data), Rating.class).exchange().expectStatus().isCreated();
	}

	@Test
	void deleteBookById() {
		Rating data = new Rating("1", "1", 5);
		Mockito.when(ratingService.deleteRating(data.getId())).thenReturn(Mono.just(data));
		client.delete().uri("/rating/" + data.getId()).exchange().expectStatus().isOk();
	}

	@Test
	void updateBookById() {
		Rating data = new Rating("1", "1", 5);
		Rating updatedData = new Rating("2", "2", 4);

		Mockito.when(ratingService.updateRating(data, data.getId())).thenReturn(Mono.just(updatedData));

		this.client.put().uri("/rating/" + data.getId()).body(Mono.just(data), Rating.class).exchange().expectStatus()
				.isOk();
	}

}