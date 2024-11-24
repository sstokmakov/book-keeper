package ru.tokmakov.bookkeeper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tokmakov.bookkeeper.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
