package com.studyroom.controller;

import com.studyroom.entity.Homework;
import com.studyroom.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/homeworks")
public class HomeworkController {
    
    @Autowired
    private HomeworkService homeworkService;
    
    @GetMapping
    public ResponseEntity<List<Homework>> getAll() {
        return ResponseEntity.ok(homeworkService.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Homework> getById(@PathVariable String id) {
        return homeworkService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Homework>> getByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(homeworkService.getByStudentId(studentId));
    }
    
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Homework>> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(homeworkService.getByDate(date));
    }
    
    @PostMapping
    public ResponseEntity<Homework> add(@RequestBody Homework homework) {
        Homework created = homeworkService.add(homework);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Homework> update(@PathVariable String id, @RequestBody Homework homework) {
        return homeworkService.update(id, homework)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (homeworkService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
