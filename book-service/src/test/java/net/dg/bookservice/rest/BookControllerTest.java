package net.dg.bookservice.rest;

import net.dg.bookservice.dto.Rating;
import net.dg.bookservice.entity.Book;
import net.dg.bookservice.exception.CustomException;
import net.dg.bookservice.repository.BookRepository;
import net.dg.bookservice.service.BookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest
class BookControllerTest {

	@Autowired
	private WebTestClient client;

	@MockBean
	private BookRepository bookRepository;

	@MockBean
	private BookService bookService;

	@Test
	void TestGetAllBooks() {

		Mockito.when(bookService.findAllBooks())
				.thenReturn(Flux.just(new Book("1", "Laurentiu", "Spring"), new Book("1", "Laurentiu", "Java")));

		client.get().uri("/book").exchange().expectStatus().isOk().expectBody().jsonPath("$.[0].author")
				.isEqualTo("Laurentiu").jsonPath("$.[0].title").isEqualTo("Spring").jsonPath("$.[1].author")
				.isEqualTo("Laurentiu").jsonPath("$.[1].title").isEqualTo("Java");
	}

	@Test
	void TestGetBookById() {
		Book data = new Book("1", "Laurentiu", "Spring");
		Mockito.when(bookService.findBookById(any())).thenReturn(Mono.just(data));

		client.get().uri("/book/" + data.getId()).exchange().expectStatus().isOk().expectBody().jsonPath("$.author")
				.isEqualTo("Laurentiu").jsonPath("$.title").isEqualTo("Spring");
	}

	@Test
	void TestGetBookByIdShouldThrowException() {
		Mockito.when(bookService.findBookById(any())).thenThrow(CustomException.class);

		client.get().uri("/book/1").exchange().expectStatus().isBadRequest();
	}

	@Test
	void TestGetBooksAndRatingsByBookId() {
		List<Rating> ratings = Arrays.asList(new Rating("1", "1", 5), new Rating("2", "2", 4));
		Book data = new Book("1", "Laurentiu", "Spring", ratings);
		Mockito.when(bookService.getBookWithRatings(any())).thenReturn(Mono.just(data));

		client.get().uri("/book/1/with-rating").exchange().expectStatus().isOk().expectBody().jsonPath("$.author")
				.isEqualTo("Laurentiu").jsonPath("$.title").isEqualTo("Spring").jsonPath("$.ratings.[0].stars")
				.isEqualTo(5).jsonPath("$.ratings.[1].stars").isEqualTo(4);
	}

	@Test
	void TestSaveBook() {
		Book data = new Book(UUID.randomUUID().toString(), "Laurentiu", "Spring");
		Mockito.when(bookService.createBook(any(Book.class))).thenReturn(Mono.just(data));

		client.post().uri("/book").body(Mono.just(data), Book.class).exchange().expectStatus().isCreated();
	}

	@Test
	void TestDeleteBookById() {
		Book data = new Book("1", "Laurentiu", "Spring");
		Mockito.when(bookService.deleteBook(data.getId())).thenReturn(Mono.just(data));
		client.delete().uri("/book/" + data.getId()).exchange().expectStatus().isOk();
	}

	@Test
	void TestUpdateBookById() {
		Book data = new Book("1", "Laurentiu", "Spring");
		Book updatedData = new Book("1", "Alex", "Spring");

		Mockito.when(bookService.updateBook(data, data.getId())).thenReturn(Mono.just(updatedData));

		this.client.put().uri("/book/" + data.getId()).body(Mono.just(data), Book.class).exchange().expectStatus()
				.isOk();
	}

}