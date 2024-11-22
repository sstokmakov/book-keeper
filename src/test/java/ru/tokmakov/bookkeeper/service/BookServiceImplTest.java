package ru.tokmakov.bookkeeper.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.tokmakov.bookkeeper.dto.BookDto;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;
import ru.tokmakov.bookkeeper.model.Book;
import ru.tokmakov.bookkeeper.repository.BookRepository;

class BookServiceImplTest {
    @Test
    void saveBookCorrectShouldReturnBookDto() {
        BookSaveDto bookSaveDto = new BookSaveDto();
        bookSaveDto.setTitle("title");
        bookSaveDto.setAuthor("author");
        bookSaveDto.setGenre("genre");

        BookDto expectedBookDto = new BookDto();
        expectedBookDto.setId(1L);
        expectedBookDto.setTitle("title");
        expectedBookDto.setAuthor("author");
        expectedBookDto.setGenre("genre");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("title");
        book.setAuthor("author");
        book.setGenre("genre");

        BookRepository bookRepository = Mockito.mock(BookRepository.class);
        BookServiceImpl bookService = new BookServiceImpl(bookRepository);

        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);

        BookDto actualBookDto = bookService.saveBook(bookSaveDto);

        Assertions.assertEquals(expectedBookDto.getId(), actualBookDto.getId());
        Assertions.assertEquals(expectedBookDto.getTitle(), actualBookDto.getTitle());
        Assertions.assertEquals(expectedBookDto.getAuthor(), actualBookDto.getAuthor());
        Assertions.assertEquals(expectedBookDto.getGenre(), actualBookDto.getGenre());

        Mockito.verify(bookRepository).save(Mockito.any(Book.class));
    }

    @Test
    void findAllBooks() {
    }

    @Test
    void findBookById() {
    }

    @Test
    void saveBook() {
    }

    @Test
    void updateBook() {
    }

    @Test
    void deleteBook() {
    }
}