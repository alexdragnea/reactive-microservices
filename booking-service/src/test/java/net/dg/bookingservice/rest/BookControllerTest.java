package net.dg.bookingservice.rest;

import net.dg.bookingservice.entity.Book;
import net.dg.bookingservice.repository.BookRepository;
import net.dg.bookingservice.service.BookService;
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
class BookControllerTest {


    @Autowired
    private WebTestClient client;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BookService bookService;

    @Test
    void getAllBooks() {

        Mockito
                .when(bookService.findAllBooks())
                .thenReturn(Flux.just(new Book("1", "Laurentiu", "Spring"), new Book("1", "Laurentiu", "Java")));

        client
                .get()
                .uri("/book")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].author").isEqualTo("Laurentiu")
                .jsonPath("$.[0].title").isEqualTo("Spring")
                .jsonPath("$.[1].author").isEqualTo("Laurentiu")
                .jsonPath("$.[1].title").isEqualTo("Java");
    }

    @Test
    void getBookById() {
        Book data = new Book("1", "Laurentiu", "Spring");
        Mockito
                .when(bookService.findBookById(any()))
                .thenReturn(Mono.just(data));

        client
                .get()
                .uri("/book/" + data.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.author").isEqualTo("Laurentiu")
                .jsonPath("$.title").isEqualTo("Spring");
    }

    @Test
    void saveBook() {
        Book data = new Book(UUID.randomUUID().toString(), "Laurentiu", "Spring");
        Mockito
                .when(bookService.createBook(any(Book.class)))
                .thenReturn(Mono.just(data));

        client
                .post()
                .uri("/book")
                .body(Mono.just(data), Book.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void deleteBookById() {
        Book data = new Book("1", "Laurentiu", "Spring");
        Mockito
                .when(bookService.deleteBook(data.getId()))
                .thenReturn(Mono.just(data));
        client
                .delete()
                .uri("/book/" + data.getId())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateBookById() {
        Book data = new Book("1", "Laurentiu", "Spring");
        Book updatedData = new Book("1", "Alex", "Spring");


        Mockito.when(bookService.updateBook(data, data.getId()))
                .thenReturn(Mono.just(updatedData));

        this
                .client
                .put()
                .uri("/book/" + data.getId())
                .body(Mono.just(data), Book.class)
                .exchange()
                .expectStatus().isOk();
    }

}