package ru.tokmakov.bookkeeper.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.tokmakov.bookkeeper.dto.BookDto;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BookServiceIntegrationTest {
    @Autowired
    private BookController bookController;

    private BookSaveDto bookSaveDto;

    @BeforeEach
    void setUp() {
        bookSaveDto = new BookSaveDto();
        bookSaveDto.setTitle("Test Book");
        bookSaveDto.setAuthor("Test Author");
        bookSaveDto.setGenre("Test Genre");
    }

    @Test
    @Transactional
    void saveBookShouldReturnCorrectBookDto() {
        BookDto savedBookDto = bookController.saveBook(bookSaveDto);

        assertThat(savedBookDto.getTitle()).isEqualTo(bookSaveDto.getTitle());
        assertThat(savedBookDto.getAuthor()).isEqualTo(bookSaveDto.getAuthor());
        assertThat(savedBookDto.getGenre()).isEqualTo(bookSaveDto.getGenre());
    }

    @Test
    @Transactional
    void saveBookShouldAssignIdToSavedBook() {
        BookDto savedBookDto = bookController.saveBook(bookSaveDto);

        assertThat(savedBookDto.getId()).isNotNull();
    }
}