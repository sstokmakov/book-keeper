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
import org.springframework.test.web.servlet.ResultActions;
import ru.tokmakov.bookkeeper.dto.BookDto;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;
import ru.tokmakov.bookkeeper.dto.BookUpdateDto;
import ru.tokmakov.bookkeeper.exception.GlobalExceptionHandler;
import ru.tokmakov.bookkeeper.exception.NotFoundException;
import ru.tokmakov.bookkeeper.service.BookService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookController.class)
@Import(GlobalExceptionHandler.class)
class BookControllerMvcTests {
    private final ObjectMapper mapper = new ObjectMapper();

    @MockitoBean
    private BookService bookService;

    @Autowired
    private MockMvc mvc;

    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("title");
        bookDto.setAuthor("author");
        bookDto.setGenre("genre");
    }

    @Test
    void saveBookShouldReturnCreatedBookDto() throws Exception {
        BookSaveDto bookSaveDto = new BookSaveDto();
        bookSaveDto.setTitle("title");
        bookSaveDto.setAuthor("author");
        bookSaveDto.setGenre("genre");

        Mockito.when(bookService.saveBook(Mockito.any(BookSaveDto.class))).thenReturn(bookDto);

        mvc.perform(post("/books")
                        .content(mapper.writeValueAsString(bookSaveDto))
                        .contentType(MediaType.APPLICATION_JSON))
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

        ResultActions resultActions = mvc.perform(post("/books")
                .content(mapper.writeValueAsString(bookSaveDto))
                .contentType(MediaType.APPLICATION_JSON));
        checkBadRequestResponse(resultActions);
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

        ResultActions resultActions = mvc.perform(post("/books")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        checkBadRequestResponse(resultActions);
    }

    @Test
    void findBookByIdIncorrectTypeIdShouldReturnBadRequestStatus() throws Exception {
        String id = "incorrect";
        Mockito.when(bookService.saveBook(Mockito.any(BookSaveDto.class))).thenReturn(bookDto);

        ResultActions resultActions = mvc.perform(get("/books/{id}", id));
        checkBadRequestResponse(resultActions);
    }

    @Test
    void findBookByIdNotFoundShouldReturnNotFoundStatus() throws Exception {
        Long id = 1L;

        Mockito.when(bookService.findBookById(id)).thenThrow(
                new NotFoundException("Book with id " + id + " not found"));

        ResultActions resultActions = mvc.perform(get("/books/{id}", id));
        checkNotFoundResponse(resultActions);

        Mockito.verify(bookService).findBookById(id);
    }

    @Test
    void findAllBooksShouldReturnBooks() throws Exception {
        BookDto book1 = new BookDto();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        book1.setGenre("Genre 1");

        BookDto book2 = new BookDto();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        book2.setGenre("Genre 2");

        List<BookDto> books = List.of(book1, book2);

        Mockito.when(bookService.findAllBooks()).thenReturn(books);

        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Book 1"))
                .andExpect(jsonPath("$[0].author").value("Author 1"))
                .andExpect(jsonPath("$[0].genre").value("Genre 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Book 2"))
                .andExpect(jsonPath("$[1].author").value("Author 2"))
                .andExpect(jsonPath("$[1].genre").value("Genre 2"));
    }

    @Test
    void findAllBooksNoBooksShouldReturnEmptyList() throws Exception {
        Mockito.when(bookService.findAllBooks()).thenReturn(List.of());

        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void updateBookNotFoundShouldReturnNotFoundStatus() throws Exception {
        BookUpdateDto updateDto = new BookUpdateDto();
        updateDto.setAuthor("new author");
        updateDto.setGenre("new genre");
        updateDto.setTitle("new title");

        Long id = 1L;
        Mockito.when(bookService.updateBook(Mockito.eq(id), Mockito.any(BookUpdateDto.class)))
                .thenThrow(new NotFoundException("Book with id " + id + " not found"));

        ResultActions resultActions = mvc.perform(patch("/books/{id}", id)
                .content(mapper.writeValueAsString(updateDto))
                .contentType(MediaType.APPLICATION_JSON));
        checkNotFoundResponse(resultActions);

        Mockito.verify(bookService).updateBook(Mockito.eq(id), Mockito.any(BookUpdateDto.class));
    }

    @Test
    void updateBookIncorrectAuthorLengthShouldReturnBadRequestStatus() throws Exception {
        Long id = 1L;
        BookUpdateDto updateDto = new BookUpdateDto();
        updateDto.setAuthor("q");

        ResultActions resultActions = mvc.perform(patch("/books/{id}", id)
                .content(mapper.writeValueAsString(updateDto))
                .contentType(MediaType.APPLICATION_JSON));
        checkBadRequestResponse(resultActions);
    }

    @Test
    void deleteBookNotFoundShouldReturnNotFoundStatus() throws Exception {
        Long id = 1L;
        Mockito.doThrow(new NotFoundException("Book with id " + id + " not found"))
                .when(bookService).deleteBook(id);

        ResultActions resultActions = mvc.perform(delete("/books/{id}", id));
        checkNotFoundResponse(resultActions);

        Mockito.verify(bookService).deleteBook(Mockito.eq(id));
    }

    @Test
    void deleteCorrectShouldReturnNoContentStatus() throws Exception {
        Long id = 1L;

        mvc.perform(delete("/books/{id}", id))
                .andExpect(status().isNoContent());

        Mockito.verify(bookService).deleteBook(Mockito.eq(id));
    }

    private void checkBadRequestResponse(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    private void checkNotFoundResponse(ResultActions result) throws Exception {
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.reason", is("The required object was not found.")))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}