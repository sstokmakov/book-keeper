package ru.tokmakov.bookkeeper.service;

import ru.tokmakov.bookkeeper.dto.BookDto;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;
import ru.tokmakov.bookkeeper.dto.BookUpdateDto;

import java.util.List;

public interface BookService {
    List<BookDto> findAllBooks();

    BookDto findBookById(Long bookId);

    BookDto saveBook(BookSaveDto bookSaveDto);

    BookDto updateBook(Long bookId, BookUpdateDto bookSaveDto);

    void deleteBook(Long bookId);
}