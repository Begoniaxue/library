package com.studyroom.controller;

import com.studyroom.entity.Knowledge;
import com.studyroom.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    
    @Autowired
    private KnowledgeService knowledgeService;
    
    @GetMapping
    public ResponseEntity<List<Knowledge>> getAll(
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String subject) {
        if (grade != null || subject != null) {
            return ResponseEntity.ok(knowledgeService.getByGradeAndSubject(grade, subject));
        }
        return ResponseEntity.ok(knowledgeService.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Knowledge> getById(@PathVariable String id) {
        return knowledgeService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Knowledge> add(@RequestBody Knowledge knowledge) {
        Knowledge created = knowledgeService.add(knowledge);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Knowledge> update(@PathVariable String id, @RequestBody Knowledge knowledge) {
        return knowledgeService.update(id, knowledge)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/toggle-important")
    public ResponseEntity<Knowledge> toggleImportant(@PathVariable String id) {
        return knowledgeService.toggleImportant(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (knowledgeService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
