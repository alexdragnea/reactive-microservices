package net.dg.bookingservice.rest;

import lombok.AllArgsConstructor;
import net.dg.bookingservice.dto.Rating;
import net.dg.bookingservice.entity.Book;
import net.dg.bookingservice.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.stream.Stream;

@RestController
@AllArgsConstructor
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    private WebClient.Builder webClientBuilder;

    @GetMapping
    Flux<Book> getAll() {
        return bookService.findAllBooks();
    }


    @GetMapping("/{id}")
    Mono<Book> getById(@PathVariable("id") String id) {
        return bookService.findBookById(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<Book> create(@RequestBody Book book) {
        return bookService.createBook(book);
    }

    @DeleteMapping("/{id}")
    Mono<ResponseEntity<Book>> deleteById(@PathVariable String id) {
        return
                bookService.deleteBook(id).map(ResponseEntity::ok)
                        .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PutMapping("/{bookId}")
    public Mono<ResponseEntity<Book>> updateUserById(@PathVariable String bookId, @RequestBody Book book) {
        return bookService.updateBook(book, bookId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Book> streamAllBooks() {
        return bookService
                .findAllBooks()
                .flatMap(book -> Flux
                        .zip(Flux.interval(Duration.ofSeconds(2)),
                                Flux.fromStream(Stream.generate(() -> book))
                        )
                        .map(Tuple2::getT2)
                );
    }

    @GetMapping("/{id}/with-rating")
    public Mono<Book> findByIdWithAccounts(@PathVariable("id") String id) {
        Flux<Rating> ratings = webClientBuilder.build().get().uri("http://localhost:8881/api/rating/book/" + id).retrieve().bodyToFlux(Rating.class);

        Mono<Book> book = bookService.findBookById(id);


        return book;
//        return accounts
//                .collectList()
//                .map(a -> new Customer(a))
//                .mergeWith(repository.findById(id))
//                .collectList()
//                .map(CustomerMapper::map);
    }


}
