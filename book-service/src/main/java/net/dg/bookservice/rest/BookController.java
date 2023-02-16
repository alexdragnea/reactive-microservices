package net.dg.bookservice.rest;

import lombok.AllArgsConstructor;
import net.dg.bookservice.entity.Book;
import net.dg.bookservice.service.BookService;
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
@RequestMapping("/book")
public class BookController {

	private final BookService bookService;

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
		return bookService.deleteBook(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PutMapping("/{bookId}")
	public Mono<ResponseEntity<Book>> updateUserById(@PathVariable String bookId, @RequestBody Book book) {
		return bookService.updateBook(book, bookId).map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@GetMapping("/{bookId}/with-rating")
	public Mono<Book> getBooksAndRatingsByBookId(@PathVariable("bookId") String bookId) {

		return bookService.getBookWithRatings(bookId);
	}

	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Book> streamAllBooks() {
		return bookService.findAllBooks()
				.flatMap(book -> Flux
						.zip(Flux.interval(Duration.ofSeconds(2)), Flux.fromStream(Stream.generate(() -> book)))
						.map(Tuple2::getT2));
	}

	@GetMapping("/benchmark")
	public Mono<String> benchMark() {
		return Mono.just("Hello");
	}

}
