package ru.tokmakov.bookkeeper.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookUpdateDto {
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @Size(min = 2, max = 50, message = "Author name must be between 2 and 50 characters")
    private String author;

    @Size(min = 2, max = 50, message = "Genre must be between 2 and 50 characters")
    private String genre;
}
