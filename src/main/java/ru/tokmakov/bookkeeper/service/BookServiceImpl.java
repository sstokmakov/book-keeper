package ru.tokmakov.bookkeeper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tokmakov.bookkeeper.dto.BookDto;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;
import ru.tokmakov.bookkeeper.repository.BookRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    public List<BookDto> findAllBooks() {
        return List.of();
    }

    @Override
    public BookDto findBookById(Long bookId) {
        return null;
    }

    @Override
    public BookDto saveBook(BookSaveDto book) {
        return null;
    }

    @Override
    public BookDto updateBook(Long id, BookSaveDto book) {
        return null;
    }

    @Override
    public void deleteBook(Long id) {

    }
}
