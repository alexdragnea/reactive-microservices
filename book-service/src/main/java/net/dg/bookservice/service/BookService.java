package net.dg.bookservice.service;

import net.dg.bookservice.entity.Book;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookService {

	Flux<Book> findAllBooks();

	Mono<Book> findBookById(String bookId);

	Mono<Book> createBook(Book book);

	Mono<Book> deleteBook(String bookId);

	Mono<Book> updateBook(Book book, String bookId);

	Mono<Book> getBookWithRatings(String bookId);

}
