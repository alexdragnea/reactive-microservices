package net.dg.bookservice.entity;

import lombok.*;
import net.dg.bookservice.dto.Rating;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Book {

	@Id
	private String id;

	private String author;

	private String title;

	@Transient
	private List<Rating> ratings;

	public Book(String id, String author, String title) {
		this.id = id;
		this.author = author;
		this.title = title;
	}

	public Book(List<Rating> ratings) {
		this.ratings = ratings;
	}

}
