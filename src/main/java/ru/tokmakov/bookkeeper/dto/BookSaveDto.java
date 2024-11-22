package ru.tokmakov.bookkeeper.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class BookSaveDto {
    @NotBlank(message = "Title is mandatory")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @NotBlank(message = "Author is mandatory")
    @Size(min = 2, max = 50, message = "Author name must be between 2 and 50 characters")
    private String author;

    @NotBlank(message = "Genre is mandatory")
    @Size(min = 2, max = 50, message = "Genre must be between 2 and 50 characters")
    private String genre;
}