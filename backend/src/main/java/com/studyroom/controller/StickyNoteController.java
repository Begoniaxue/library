package com.studyroom.controller;

import com.studyroom.dto.StickyNoteDTO;
import com.studyroom.entity.StickyNote;
import com.studyroom.service.StickyNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sticky-notes")
public class StickyNoteController {
    
    @Autowired
    private StickyNoteService stickyNoteService;
    
    @GetMapping
    public ResponseEntity<List<StickyNote>> getAll() {
        return ResponseEntity.ok(stickyNoteService.getAll());
    }
    
    @PostMapping
    public ResponseEntity<StickyNote> create(@RequestBody StickyNoteDTO dto) {
        StickyNote created = stickyNoteService.create(
            dto.getX() != null ? dto.getX() : 0,
            dto.getY() != null ? dto.getY() : 0,
            dto.getContent() != null ? dto.getContent() : ""
        );
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}/position")
    public ResponseEntity<StickyNote> updatePosition(
            @PathVariable String id,
            @RequestBody StickyNoteDTO dto) {
        return stickyNoteService.updatePosition(id, dto.getX(), dto.getY())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/content")
    public ResponseEntity<StickyNote> updateContent(
            @PathVariable String id,
            @RequestBody StickyNoteDTO dto) {
        return stickyNoteService.updateContent(id, dto.getContent())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (stickyNoteService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
