package ru.tokmakov.bookkeeper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tokmakov.bookkeeper.dto.BookDto;
import ru.tokmakov.bookkeeper.dto.BookMapper;
import ru.tokmakov.bookkeeper.dto.BookSaveDto;
import ru.tokmakov.bookkeeper.dto.BookUpdateDto;
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
        log.info("Fetching all books");

        List<Book> books = bookRepository.findAll();

        log.info("Found {} books", books.size());

        return books.stream()
                .map(BookMapper::bookToBookDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto findBookById(Long bookId) {
        log.info("Attempting to find book with id: {}", bookId);

        Book book = getBookById(bookId);

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
    public BookDto updateBook(Long bookId, BookUpdateDto bookUpdateDto) {
        log.info("Updating book with ID: {}, Update data: {}", bookId, bookUpdateDto);

        Book bookToUpdate = getBookById(bookId);
        log.info("Book found: {}", bookToUpdate);

        updateFields(bookToUpdate, bookUpdateDto);
        log.info("Fields updated for book with ID: {}", bookId);

        Book updatedBook = bookRepository.save(bookToUpdate);
        log.info("Book with ID: {} successfully updated and saved", bookId);

        BookDto result = BookMapper.bookToBookDto(updatedBook);
        log.info("Returning updated book DTO: {}", result);

        return BookMapper.bookToBookDto(updatedBook);
    }

    private void updateFields(Book bookToUpdate, BookUpdateDto newBook) {
        if (newBook.getTitle() != null)
            bookToUpdate.setTitle(newBook.getTitle());
        if (newBook.getAuthor() != null)
            bookToUpdate.setAuthor(newBook.getAuthor());
        if (newBook.getGenre() != null)
            bookToUpdate.setGenre(newBook.getGenre());
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        log.info("Attempting to delete book with ID: {}", id);

        Book book = getBookById(id);

        log.info("Book found: ID={}, Title={}, Author={}", book.getId(), book.getTitle(), book.getAuthor());

        bookRepository.deleteById(book.getId());

        log.info("Successfully deleted book with ID: {}", id);
    }

    private Book getBookById(Long bookId) {
        return bookRepository.findById(bookId).
                orElseThrow(() -> new NotFoundException("Book with id " + bookId + " not found"));
    }
}