package com.studyroom.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.studyroom.dto.BlogDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BlogService {

    private static final Logger logger = LoggerFactory.getLogger(BlogService.class);
    private static final String JSON_FILE_NAME = "blogs.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String jsonFilePath;

    @PostConstruct
    public void init() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        
        String userDir = System.getProperty("user.dir");
        jsonFilePath = userDir + File.separator + JSON_FILE_NAME;
        logger.info("Blog JSON file path: {}", jsonFilePath);
        
        File file = new File(jsonFilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                objectMapper.writeValue(file, new ArrayList<BlogDTO>());
                logger.info("Created new blogs.json file");
            } catch (IOException e) {
                logger.error("Failed to create blogs.json file", e);
                throw new RuntimeException("Failed to initialize blog storage", e);
            }
        }
    }

    public List<BlogDTO> getAll() {
        logger.info("Getting all blogs");
        List<BlogDTO> blogs = readBlogsFromFile();
        return blogs.stream()
                .sorted(Comparator.comparing(BlogDTO::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public Optional<BlogDTO> getById(String id) {
        logger.info("Getting blog by id: {}", id);
        List<BlogDTO> blogs = readBlogsFromFile();
        return blogs.stream()
                .filter(blog -> blog.getId().equals(id))
                .findFirst();
    }

    public BlogDTO add(BlogDTO blog) {
        logger.info("Adding new blog: {}", blog.getTitle());
        List<BlogDTO> blogs = readBlogsFromFile();
        BlogDTO newBlog = new BlogDTO();
        newBlog.setId(UUID.randomUUID().toString());
        newBlog.setTitle(blog.getTitle());
        newBlog.setContent(blog.getContent());
        newBlog.setCreatedAt(LocalDateTime.now().format(formatter));
        blogs.add(newBlog);
        writeBlogsToFile(blogs);
        logger.info("Blog added successfully with id: {}", newBlog.getId());
        return newBlog;
    }

    public boolean delete(String id) {
        logger.info("Deleting blog by id: {}", id);
        List<BlogDTO> blogs = readBlogsFromFile();
        boolean removed = blogs.removeIf(blog -> blog.getId().equals(id));
        if (removed) {
            writeBlogsToFile(blogs);
            logger.info("Blog deleted successfully: {}", id);
        }
        return removed;
    }

    private List<BlogDTO> readBlogsFromFile() {
        try {
            File file = new File(jsonFilePath);
            if (!file.exists() || file.length() == 0) {
                logger.debug("File does not exist or is empty, returning empty list");
                return new ArrayList<>();
            }
            List<BlogDTO> blogs = objectMapper.readValue(file, new TypeReference<List<BlogDTO>>() {});
            logger.debug("Read {} blogs from file", blogs.size());
            return blogs;
        } catch (IOException e) {
            logger.error("Failed to read blogs from file", e);
            throw new RuntimeException("Failed to read blogs", e);
        }
    }

    private void writeBlogsToFile(List<BlogDTO> blogs) {
        try {
            File file = new File(jsonFilePath);
            objectMapper.writeValue(file, blogs);
            logger.debug("Written {} blogs to file", blogs.size());
        } catch (IOException e) {
            logger.error("Failed to write blogs to file", e);
            throw new RuntimeException("Failed to save blog", e);
        }
    }
}
