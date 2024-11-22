package ru.tokmakov.bookkeeper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tokmakov.bookkeeper.dto.BookDto;
import ru.tokmakov.bookkeeper.dto.BookMapper;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;
import ru.tokmakov.bookkeeper.exception.NotFoundException;
import ru.tokmakov.bookkeeper.model.Book;
import ru.tokmakov.bookkeeper.repository.BookRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAllBooks() {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto findBookById(Long bookId) {
        log.info("Attempting to find book with id: {}", bookId);

        Book book = bookRepository.findById(bookId).
                orElseThrow(() -> new NotFoundException("Book with id " + bookId + " not found"));

        BookDto bookDto = BookMapper.bookToBookDto(book);
        log.info("Successfully found book: {}", bookDto);

        return bookDto;
    }

    @Override
    @Transactional
    public BookDto saveBook(BookSaveDto bookSaveDto) {
        log.info("Received request to save a book with details: {}", bookSaveDto);

        Book book = BookMapper.bookSaveDtoToBook(bookSaveDto);
        log.debug("Converted BookSaveDto to Book: {}", book);

        BookDto savedBook = BookMapper.bookToBookDto(bookRepository.save(book));
        log.info("Successfully saved book with ID: {}", savedBook.getId());

        return savedBook;
    }

    @Override
    @Transactional
    public BookDto updateBook(Long bookId, BookSaveDto book) {
        return null;
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {

    }
}