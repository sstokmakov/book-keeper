package ru.tokmakov.bookkeeper.dto;

import lombok.experimental.UtilityClass;
import ru.tokmakov.bookkeeper.model.Book;

@UtilityClass
public class BookMapper {
    public static BookDto bookToBookDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setGenre(book.getGenre());
        return bookDto;
    }

    public static Book bookSaveDtoToBook(BookSaveDto bookSaveDto) {
        Book book = new Book();

        book.setTitle(bookSaveDto.getTitle());
        book.setAuthor(bookSaveDto.getAuthor());
        book.setGenre(bookSaveDto.getGenre());

        return book;
    }
}
