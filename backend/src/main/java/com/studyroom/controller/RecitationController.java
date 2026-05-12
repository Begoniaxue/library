package com.studyroom.controller;

import com.studyroom.entity.Recitation;
import com.studyroom.service.RecitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recitations")
public class RecitationController {
    
    @Autowired
    private RecitationService recitationService;
    
    @GetMapping
    public ResponseEntity<List<Recitation>> getAll() {
        return ResponseEntity.ok(recitationService.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Recitation> getById(@PathVariable String id) {
        return recitationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Recitation>> getByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(recitationService.getByStudentId(studentId));
    }
    
    @PostMapping
    public ResponseEntity<Recitation> add(@RequestBody Recitation recitation) {
        Recitation created = recitationService.add(recitation);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Recitation> update(@PathVariable String id, @RequestBody Recitation recitation) {
        return recitationService.update(id, recitation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (recitationService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
