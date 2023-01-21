package net.dg.bookservice.utils;

import net.dg.bookservice.entity.Book;

import java.util.List;

public class RatingMapper {

	public static Book map(List<Book> books) {
		Book book = new Book();
		for (Book c : books) {
			if (c.getId() != null)
				book.setId(c.getId());
			if (c.getAuthor() != null)
				book.setAuthor(c.getAuthor());
			if (c.getTitle() != null)
				book.setTitle(c.getTitle());
			if (c.getRatings() != null)
				book.setRatings(c.getRatings());
		}
		return book;
	}

}
