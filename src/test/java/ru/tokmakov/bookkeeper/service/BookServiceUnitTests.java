package ru.tokmakov.bookkeeper.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tokmakov.bookkeeper.dto.BookDto;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;
import ru.tokmakov.bookkeeper.exception.NotFoundException;
import ru.tokmakov.bookkeeper.model.Book;
import ru.tokmakov.bookkeeper.repository.BookRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class BookServiceUnitTests {
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private BookSaveDto bookSaveDto;
    private Book book;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        bookSaveDto = new BookSaveDto();
        bookSaveDto.setTitle("title");
        bookSaveDto.setAuthor("author");
        bookSaveDto.setGenre("genre");

        book = new Book();
        book.setId(1L);
        book.setTitle("title");
        book.setAuthor("author");
        book.setGenre("genre");

        bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("title");
        bookDto.setAuthor("author");
        bookDto.setGenre("genre");
    }

    @Test
    void saveBookCorrectShouldReturnBookDto() {
        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);

        BookDto actualBookDto = bookService.saveBook(bookSaveDto);

        Assertions.assertEquals(bookDto.getId(), actualBookDto.getId());
        Assertions.assertEquals(bookDto.getTitle(), actualBookDto.getTitle());
        Assertions.assertEquals(bookDto.getAuthor(), actualBookDto.getAuthor());
        Assertions.assertEquals(bookDto.getGenre(), actualBookDto.getGenre());

        Mockito.verify(bookRepository).save(Mockito.any(Book.class));
    }

    @Test
    void findAllBooks() {
    }

    @Test
    void findBookByIdShouldReturnBookDto() {
        Long id = 1L;
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        BookDto actualBookDto = bookService.findBookById(id);

        Assertions.assertEquals(bookDto.getId(), actualBookDto.getId());
        Assertions.assertEquals(bookDto.getTitle(), actualBookDto.getTitle());
        Assertions.assertEquals(bookDto.getAuthor(), actualBookDto.getAuthor());
        Assertions.assertEquals(bookDto.getGenre(), actualBookDto.getGenre());

        Mockito.verify(bookRepository).findById(Mockito.anyLong());
    }

    @Test
    void findBookByIdNotFoundShouldThrowException() {
        Long id = 1L;

        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findBookById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Book with id " + id + " not found");

        Mockito.verify(bookRepository).findById(id);
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