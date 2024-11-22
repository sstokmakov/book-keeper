package ru.tokmakov.bookkeeper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class BookIntegrationTests {
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

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
        MvcResult result = mvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookSaveDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Long createdBookId = JsonPath.parse(responseContent).read("$.id", Long.class);

        mvc.perform(get("/books/{id}", createdBookId))
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
}