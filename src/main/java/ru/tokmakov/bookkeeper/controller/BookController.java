package ru.tokmakov.bookkeeper.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.tokmakov.bookkeeper.dto.BookDto;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;
import ru.tokmakov.bookkeeper.service.BookService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookDto> findAllBooks() {
        return bookService.findAllBooks();
    }

    @GetMapping("/{bookId}")
    public BookDto findBookById(@PathVariable Long bookId) {
        return bookService.findBookById(bookId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto saveBook(@NotNull @Validated @RequestBody BookSaveDto bookSaveDto) {
        log.info("POST /books - Creating new book. Request data: {}", bookSaveDto);

        BookDto bookDto = bookService.saveBook(bookSaveDto);

        log.info("POST /books - Book created successfully. Saved book: {}", bookDto);
        return bookDto;
    }

    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id, @RequestBody BookSaveDto book) {
        return bookService.updateBook(id, book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}