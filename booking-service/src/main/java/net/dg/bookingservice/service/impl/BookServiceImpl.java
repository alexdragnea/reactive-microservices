package net.dg.bookingservice.service.impl;

import lombok.AllArgsConstructor;
import net.dg.bookingservice.entity.Book;
import net.dg.bookingservice.repository.BookRepository;
import net.dg.bookingservice.service.BookService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;


    @Override
    public Flux<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Mono<Book> findBookById(String bookId) {
        return bookRepository.findById(bookId);
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

}
