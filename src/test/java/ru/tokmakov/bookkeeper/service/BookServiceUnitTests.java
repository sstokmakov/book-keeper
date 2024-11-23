package ru.tokmakov.bookkeeper.service;

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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

        assertThat(actualBookDto).isEqualTo(bookDto);
        Mockito.verify(bookRepository).save(Mockito.any(Book.class));
    }

    @Test
    void findAllBooksShouldReturnPagedBooks() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        book1.setGenre("Genre 1");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        book2.setGenre("Genre 2");

        List<Book> books = List.of(book1, book2);

        Mockito.when(bookRepository.findAll()).thenReturn(books);

        List<BookDto> actualBooks = bookService.findAllBooks();

        assertThat(actualBooks).hasSize(2)
                .extracting(BookDto::getTitle).containsExactly("Book 1", "Book 2");
        Mockito.verify(bookRepository).findAll();
    }

    @Test
    void findBookByIdShouldReturnBookDto() {
        Long id = 1L;
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        BookDto actualBookDto = bookService.findBookById(id);

        assertThat(actualBookDto).isEqualTo(bookDto);
        Mockito.verify(bookRepository).findById(id);
    }

    @Test
    void findBookByIdNotFoundShouldThrowNotFoundException() {
        Long id = 1L;
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findBookById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Book with id " + id + " not found");

        Mockito.verify(bookRepository).findById(id);
    }

    @Test
    void deleteBookCorrectShouldInvokeDeleteMethod() {
        Long id = 1L;
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        bookService.deleteBook(id);

        Mockito.verify(bookRepository).findById(id);
        Mockito.verify(bookRepository).deleteById(book.getId());
    }
}