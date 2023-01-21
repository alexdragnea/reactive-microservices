package net.dg.bookservice.service.impl;

import net.dg.bookservice.entity.Book;
import net.dg.bookservice.repository.BookRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
class BookServiceImplTest {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private WebClient.Builder webClientBuilder;

	private BookServiceImpl bookService;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@AfterAll
	static void setup() {
		mongoDBContainer.stop();
	}

	@BeforeEach
	void setUp() {
		this.bookService = new BookServiceImpl(bookRepository, webClientBuilder);
	}

	@Test
	void getAllBooks() {
		Book data1 = new Book("1", "Josh", "Java");
		Book data2 = new Book("1", "Matt", "Spring");
		Flux<Book> books = bookRepository.saveAll(Flux.just(data1, data2));

		StepVerifier.create(books).expectNextCount(2).verifyComplete();
	}

	@Test
	void saveNewBook() {
		Book data = new Book("1", "Laurentiu", "Spring");
		bookService.createBook(data).block();

		Mono<Book> publisher = bookService.findBookById(data.getId());

		StepVerifier.create(publisher).assertNext(book -> assertEquals("Laurentiu", book.getAuthor())).verifyComplete();
	}

	@Test
	void delete() {
		Book data = new Book("1", "Laurentiu", "Spring");
		Mono<Book> publisher = bookService.createBook(data).flatMap(book -> bookService.deleteBook(book.getId()));
		StepVerifier.create(publisher).expectNextMatches(book -> book.getAuthor().equalsIgnoreCase(book.getAuthor()))
				.expectNextCount(0).verifyComplete();
	}

	@Test
	void update() throws Exception {
		Book data = new Book("1", "Laurentiu", "Spring");
		Book updatedData = new Book("1", "Alex", "Spring");

		Mono<Book> publisher = bookService.createBook(data)
				.flatMap(p -> bookService.updateBook(updatedData, p.getId()));
		StepVerifier.create(publisher).expectNextMatches(p -> p.getAuthor().equalsIgnoreCase("Alex")).verifyComplete();
	}

	@Test
	void getById() {
		Book data = new Book("1", "Laurentiu", "Spring");
		Mono<Book> publisher = bookService.createBook(data).flatMap(book -> bookService.findBookById((book.getId())));
		StepVerifier.create(publisher).expectNextMatches(book -> book.toString().equals(data.toString()))
				.verifyComplete();
	}

}