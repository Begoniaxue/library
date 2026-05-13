package com.studyroom.controller;

import com.studyroom.dto.BlogDTO;
import com.studyroom.service.BlogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    @Autowired
    private BlogService blogService;

    @GetMapping
    public ResponseEntity<List<BlogDTO>> getAll() {
        logger.info("GET /api/blogs - Getting all blogs");
        try {
            List<BlogDTO> blogs = blogService.getAll();
            logger.info("GET /api/blogs - Returning {} blogs", blogs.size());
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            logger.error("GET /api/blogs - Error getting blogs", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogDTO> getById(@PathVariable String id) {
        logger.info("GET /api/blogs/{} - Getting blog by id", id);
        try {
            return blogService.getById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("GET /api/blogs/{} - Error getting blog", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<BlogDTO> add(@RequestBody BlogDTO blog) {
        logger.info("POST /api/blogs - Adding new blog: {}", blog.getTitle());
        if (blog.getTitle() == null || blog.getTitle().trim().isEmpty()) {
            logger.warn("POST /api/blogs - Title is empty");
            return ResponseEntity.badRequest().build();
        }
        try {
            BlogDTO created = blogService.add(blog);
            logger.info("POST /api/blogs - Blog created successfully with id: {}", created.getId());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("POST /api/blogs - Error adding blog", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        logger.info("DELETE /api/blogs/{} - Deleting blog", id);
        try {
            if (blogService.delete(id)) {
                logger.info("DELETE /api/blogs/{} - Blog deleted successfully", id);
                return ResponseEntity.noContent().build();
            }
            logger.warn("DELETE /api/blogs/{} - Blog not found", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("DELETE /api/blogs/{} - Error deleting blog", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
