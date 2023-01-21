package net.dg.bookservice.service.impl;

import lombok.AllArgsConstructor;
import net.dg.bookservice.dto.Rating;
import net.dg.bookservice.entity.Book;
import net.dg.bookservice.exception.CustomException;
import net.dg.bookservice.repository.BookRepository;
import net.dg.bookservice.service.BookService;
import net.dg.bookservice.utils.RatingMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final WebClient.Builder webClientBuilder;


    @Override
    public Flux<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Mono<Book> findBookById(String bookId) {
        return bookRepository.findById(bookId).switchIfEmpty(Mono.error(new CustomException("Book not found")));
    }

    @Override
    public Mono<Book> createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Mono<Book> deleteBook(String bookId) {
        return bookRepository.findById(bookId)
                .flatMap(book -> bookRepository.deleteById(bookId).thenReturn(book));
    }

    @Override
    public Mono<Book> updateBook(Book book, String bookId) {
        return bookRepository.findById(bookId)
                .flatMap(existingBook -> {
                    existingBook.setTitle(book.getTitle());
                    existingBook.setAuthor(book.getAuthor());
                    return bookRepository.save(existingBook);
                });
    }

    @Override
    public Mono<Book> getBookWithRatings(String bookId) {
        Flux<Rating> ratings = webClientBuilder.build().get().uri("http://localhost:8881/api/rating/book/" + bookId).retrieve().bodyToFlux(Rating.class);
        return ratings
                .collectList()
                .map(b -> new Book(b))
                .mergeWith(bookRepository.findById(bookId))
                .collectList()
                .map(RatingMapper::map);
    }

}
