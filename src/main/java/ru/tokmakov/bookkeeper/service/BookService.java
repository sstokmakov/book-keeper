package ru.tokmakov.bookkeeper.service;

import ru.tokmakov.bookkeeper.dto.BookDto;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;

import java.util.List;

public interface BookService {
    List<BookDto> findAllBooks();

    BookDto findBookById(Long bookId);

    BookDto saveBook(BookSaveDto book);

    BookDto updateBook(Long id, BookSaveDto book);

    void deleteBook(Long id);
}