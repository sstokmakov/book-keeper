package ru.tokmakov.bookkeeper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.tokmakov.bookkeeper.controller.BookController;
import ru.tokmakov.bookkeeper.dto.BookDto;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;
import ru.tokmakov.bookkeeper.dto.BookUpdateDto;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class BookIntegrationTests {
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

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
    void saveBookShouldReturnCorrectBookDto() throws Exception {
        mvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookSaveDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title", is(bookSaveDto.getTitle())))
                .andExpect(jsonPath("$.author", is(bookSaveDto.getAuthor())))
                .andExpect(jsonPath("$.genre", is(bookSaveDto.getGenre())));
    }

    @Test
    @Transactional
    void getSavedBookByIdShouldReturnCorrectBookDto() throws Exception {
        BookDto bookDto = bookController.saveBook(bookSaveDto);

        mvc.perform(get("/books/{id}", bookDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(bookSaveDto.getTitle())))
                .andExpect(jsonPath("$.author", is(bookSaveDto.getAuthor())))
                .andExpect(jsonPath("$.genre", is(bookSaveDto.getGenre())));
    }

    @Test
    @Transactional
    void getBookNotFoundShouldReturnNotFoundStatus() throws Exception {
        Long id = 999L;
        mvc.perform(get("/books/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.reason", is("The required object was not found.")))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @Transactional
    void findAllBooksShouldReturnAllBooks() throws Exception {
        for (int i = 0; i < 2; i++) {
            bookController.saveBook(bookSaveDto);
        }

        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[1].id").exists());
    }

    @Test
    @Transactional
    void findAllBooksShouldReturnEmptyList() throws Exception {
        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Transactional
    void updateBookShouldReturnCorrectBookDto() throws Exception {
        BookDto bookDto = bookController.saveBook(bookSaveDto);
        BookUpdateDto updateDto = new BookUpdateDto();
        updateDto.setAuthor("new author");

        mvc.perform(patch("/books/{id}", bookDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author", is(updateDto.getAuthor())))
                .andExpect(jsonPath("$.title", is(bookSaveDto.getTitle())))
                .andExpect(jsonPath("$.genre", is(bookSaveDto.getGenre())));
    }

    @Test
    @Transactional
    void updateAllFieldsShouldReturnCorrectBookDto() throws Exception {
        BookDto bookDto = bookController.saveBook(bookSaveDto);
        BookUpdateDto updateDto = new BookUpdateDto();
        updateDto.setAuthor("new author");
        updateDto.setTitle("new title");
        updateDto.setGenre("new genre");

        mvc.perform(patch("/books/{id}", bookDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author", is(updateDto.getAuthor())))
                .andExpect(jsonPath("$.title", is(updateDto.getTitle())))
                .andExpect(jsonPath("$.genre", is(updateDto.getGenre())));
    }

    @Test
    @Transactional
    void updateBookWithoutFieldsShouldReturnCorrectBookDto() throws Exception {
        BookDto bookDto = bookController.saveBook(bookSaveDto);
        BookUpdateDto updateDto = new BookUpdateDto();

        mvc.perform(patch("/books/{id}", bookDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author", is(bookSaveDto.getAuthor())))
                .andExpect(jsonPath("$.title", is(bookSaveDto.getTitle())))
                .andExpect(jsonPath("$.genre", is(bookSaveDto.getGenre())));
    }

    @Test
    @Transactional
    void deleteBookShouldRemoveBook() throws Exception {
        BookDto bookDto = bookController.saveBook(bookSaveDto);
        bookController.deleteBook(bookDto.getId());

        mvc.perform(get("/books/{id}", bookDto.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.reason", is("The required object was not found.")))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}