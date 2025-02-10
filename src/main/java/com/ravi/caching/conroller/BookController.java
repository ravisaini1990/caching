package com.ravi.caching.conroller;

import com.ravi.caching.modal.Book;
import com.ravi.caching.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.rmi.NoSuchObjectException;
import java.util.List;

import static com.ravi.caching.config.EhcacheConfig.CACHE_STORE_NAME;

@RestController
@RequestMapping("api/v1")
public class BookController {

    @Autowired
    BookRepository bookRepository;

    @GetMapping("/books")
    public List<Book> findBooks() {
        return bookRepository.findAll();
    }

    @Cacheable(value = CACHE_STORE_NAME, key = "#id")
    //@Cacheable(cacheNames = "books", key = "#id")
    @GetMapping("/book/{id}")
    public Book findByBookId(@PathVariable int id) {
        return bookRepository.findById(id).orElse(null);
    }

    @PostMapping("/addBook")
    @Transactional
    public Book addBook(@RequestBody Book book) {
        return bookRepository.saveAndFlush(book);
    }

    @CachePut(cacheNames = "books", key = "#updatedBook.id")
    @PutMapping("/updateBook")
    public ResponseEntity<?> updateBook(@RequestBody Book updatedBook) {
        return bookRepository.findById(updatedBook.getId())
                .map(existingBook -> {
                    existingBook.setName(updatedBook.getName());
                    existingBook.setAuthor(updatedBook.getAuthor());
                    existingBook.setEdition(updatedBook.getEdition());
                    existingBook.setCategory(updatedBook.getCategory());
                    existingBook.setPublisher(updatedBook.getPublisher());
                    bookRepository.save(existingBook);
                    return ResponseEntity.ok(existingBook);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @CacheEvict(cacheNames = "books", key = "#id")
    @DeleteMapping("deleteBook/{id}")
    public String deleteBook(@PathVariable Integer id) throws NoSuchObjectException {
        Book updatedBook = bookRepository.findById(id).orElse(null);
        if (updatedBook == null) throw new NoSuchObjectException("Book with ID " + id + "not found");
        else
            bookRepository.delete(updatedBook);
        return "Book with ID " + id + " deleted successfully.";
    }

}
