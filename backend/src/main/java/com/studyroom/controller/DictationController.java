package com.studyroom.controller;

import com.studyroom.entity.Dictation;
import com.studyroom.service.DictationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dictations")
public class DictationController {
    
    @Autowired
    private DictationService dictationService;
    
    @GetMapping
    public ResponseEntity<List<Dictation>> getAll() {
        return ResponseEntity.ok(dictationService.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Dictation> getById(@PathVariable String id) {
        return dictationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Dictation>> getByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(dictationService.getByStudentId(studentId));
    }
    
    @PostMapping
    public ResponseEntity<Dictation> add(@RequestBody Dictation dictation) {
        Dictation created = dictationService.add(dictation);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Dictation> update(@PathVariable String id, @RequestBody Dictation dictation) {
        return dictationService.update(id, dictation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (dictationService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
