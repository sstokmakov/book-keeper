package ru.tokmakov.bookkeeper.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.tokmakov.bookkeeper.dto.BookDto;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;
import ru.tokmakov.bookkeeper.dto.BookUpdateDto;
import ru.tokmakov.bookkeeper.service.BookService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookDto> findAllBooks() {
        log.info("GET /books - Request received");

        List<BookDto> books = bookService.findAllBooks();

        log.debug("GET /books - Response: {} books found", books.size());
        return books;
    }

    @GetMapping("/{bookId}")
    public BookDto findBookById(@PathVariable Long bookId) {
        log.info("GET /books/{} - Request received", bookId);

        BookDto bookDto = bookService.findBookById(bookId);

        log.info("GET /books/{} - Response: {}", bookId, bookDto);
        return bookDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto saveBook(@NotNull @Validated @RequestBody BookSaveDto bookSaveDto) {
        log.info("POST /books - Creating new book. Request data: {}", bookSaveDto);

        BookDto bookDto = bookService.saveBook(bookSaveDto);

        log.info("POST /books - Book created successfully. Saved book: {}", bookDto);
        return bookDto;
    }

    @PatchMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id, @NotNull @RequestBody @Validated BookUpdateDto bookUpdateDto) {
        log.info("PATCH /books/{} - Updating book. Request data: {}", id, bookUpdateDto);

        BookDto bookDto = bookService.updateBook(id, bookUpdateDto);

        log.info("PATCH /books/{} - Updated successfully. Saved book: {}", id, bookDto);
        return bookDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        log.info("DELETE /books/{} - Deleting book. Request received", id);

        bookService.deleteBook(id);

        log.info("DELETE /books/{} - Deleted successfully", id);
    }
}