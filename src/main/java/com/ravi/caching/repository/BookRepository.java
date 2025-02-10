package com.ravi.caching.repository;

import com.ravi.caching.modal.Book;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Integer> {
}
