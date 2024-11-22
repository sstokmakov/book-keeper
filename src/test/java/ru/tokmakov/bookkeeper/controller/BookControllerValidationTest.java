package ru.tokmakov.bookkeeper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tokmakov.bookkeeper.dto.BookDto;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;
import ru.tokmakov.bookkeeper.exception.GlobalExceptionHandler;
import ru.tokmakov.bookkeeper.service.BookService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookController.class)
@Import(GlobalExceptionHandler.class)
class BookControllerValidationTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @MockitoBean
    private BookService bookService;

    @Autowired
    private MockMvc mvc;

    private BookDto bookDto;
    private BookSaveDto bookSaveDto;

    @BeforeEach
    void setUp() {
        bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("title");
        bookDto.setAuthor("author");
        bookDto.setGenre("genre");

        bookSaveDto = new BookSaveDto();
        bookSaveDto.setTitle("title");
        bookSaveDto.setAuthor("author");
        bookSaveDto.setGenre("genre");
    }

    @Test
    void saveBookShouldReturnCreatedBookDto() throws Exception {
        Mockito.when(bookService.saveBook(Mockito.any(BookSaveDto.class))).thenReturn(bookDto);

        mvc.perform(post("/books")
                        .content(mapper.writeValueAsString(bookSaveDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookDto.getId().intValue())))
                .andExpect(jsonPath("$.title", is(bookDto.getTitle())))
                .andExpect(jsonPath("$.author", is(bookDto.getAuthor())))
                .andExpect(jsonPath("$.genre", is(bookDto.getGenre())));
    }


    @ParameterizedTest
    @ValueSource(strings = {"title", "author", "genre"})
    void saveBookWithoutRequiredFieldShouldReturnBadRequest(String missingField) throws Exception {
        BookSaveDto bookSaveDto = createBookSaveDtoWithoutField(missingField);

        Mockito.when(bookService.saveBook(Mockito.any(BookSaveDto.class))).thenReturn(bookDto);

        mvc.perform(post("/books")
                        .content(mapper.writeValueAsString(bookSaveDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    private BookSaveDto createBookSaveDtoWithoutField(String missingField) {
        BookSaveDto bookSaveDto = new BookSaveDto();

        if (!"title".equals(missingField)) bookSaveDto.setTitle("title");
        if (!"author".equals(missingField)) bookSaveDto.setAuthor("author");
        if (!"genre".equals(missingField)) bookSaveDto.setGenre("genre");

        return bookSaveDto;
    }

    @Test
    void saveBookWithoutBodyShouldReturnMethodArgumentNotValidException() throws Exception {
        Mockito.when(bookService.saveBook(Mockito.any(BookSaveDto.class))).thenReturn(bookDto);

        mvc.perform(post("/books")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}