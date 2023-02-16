package net.dg.ratingservice.service.impl;

import net.dg.ratingservice.entity.Rating;
import net.dg.ratingservice.repository.RatingRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
class RatingServiceImplTest {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Autowired
	private RatingRepository ratingRepository;

	private RatingServiceImpl ratingService;

	@BeforeEach
	void setUp() {
		this.ratingService = new RatingServiceImpl(ratingRepository);
	}

	@AfterAll
	static void setup() {
		mongoDBContainer.stop();
	}

	@Test
	void getAllRatings() {
		Rating data1 = new Rating("1", "1", 5);
		Rating data2 = new Rating("2", "2", 4);
		Flux<Rating> publisher = ratingRepository.saveAll(Flux.just(data1, data2));

		StepVerifier.create(publisher).expectNextCount(2).verifyComplete();
	}

	@Test
	void saveNewRating() {
		Rating data = new Rating("1", "1", 5);
		ratingService.createRating(data).block();

		Mono<Rating> publisher = ratingService.findRatingById(data.getId());

		StepVerifier.create(publisher).assertNext(rating -> assertEquals(5, rating.getStars())).verifyComplete();
	}

	@Test
	void delete() {
		Rating data = new Rating("1", "1", 5);
		Mono<Rating> publisher = ratingService.createRating(data)
				.flatMap(rating -> ratingService.deleteRating(rating.getId()));
		StepVerifier.create(publisher).expectNextMatches(rating -> rating.getStars() == 5).expectNextCount(0)
				.verifyComplete();
	}

	@Test
	void update() throws Exception {
		Rating data = new Rating("1", "1", 5);
		Rating updatedData = new Rating("2", "2", 4);

		Mono<Rating> publisher = ratingService.createRating(data)
				.flatMap(rating -> ratingService.updateRating(updatedData, rating.getId()));
		StepVerifier.create(publisher).expectNextMatches(rating -> rating.getStars() == 4).verifyComplete();
	}

	@Test
	void getById() {
		Rating data = new Rating("1", "1", 5);
		Mono<Rating> publisher = ratingService.createRating(data)
				.flatMap(rating -> ratingService.findRatingById((rating.getId())));
		StepVerifier.create(publisher).expectNextMatches(rating -> rating.getStars() == 5).verifyComplete();
	}

}