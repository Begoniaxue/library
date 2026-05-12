package com.studyroom.service;

import com.studyroom.dto.StickyNoteDTO;
import com.studyroom.entity.StickyNote;
import com.studyroom.repository.StickyNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class StickyNoteService {
    
    @Autowired
    private StickyNoteRepository stickyNoteRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private final ConcurrentHashMap<String, ReentrantLock> noteLocks = new ConcurrentHashMap<>();
    
    public List<StickyNote> getAll() {
        return stickyNoteRepository.findAllByOrderByCreatedAtAsc();
    }
    
    @Transactional
    public StickyNote create(double x, double y, String content) {
        StickyNote note = new StickyNote();
        note.setX(x);
        note.setY(y);
        note.setContent(content);
        StickyNote saved = stickyNoteRepository.save(note);
        
        StickyNoteDTO dto = new StickyNoteDTO(saved.getId(), saved.getX(), saved.getY(), saved.getContent());
        messagingTemplate.convertAndSend("/topic/sticky-notes/create", dto);
        
        return saved;
    }
    
    @Transactional
    public Optional<StickyNote> updatePosition(String id, double x, double y) {
        ReentrantLock lock = noteLocks.computeIfAbsent(id, k -> new ReentrantLock());
        lock.lock();
        try {
            return stickyNoteRepository.findById(id).map(existing -> {
                existing.setX(x);
                existing.setY(y);
                StickyNote updated = stickyNoteRepository.save(existing);
                
                StickyNoteDTO dto = new StickyNoteDTO(updated.getId(), updated.getX(), updated.getY());
                messagingTemplate.convertAndSend("/topic/sticky-notes/move", dto);
                
                return updated;
            });
        } finally {
            lock.unlock();
        }
    }
    
    @Transactional
    public Optional<StickyNote> updateContent(String id, String content) {
        return stickyNoteRepository.findById(id).map(existing -> {
            existing.setContent(content);
            StickyNote updated = stickyNoteRepository.save(existing);
            
            StickyNoteDTO dto = new StickyNoteDTO(updated.getId(), updated.getX(), updated.getY(), updated.getContent());
            messagingTemplate.convertAndSend("/topic/sticky-notes/update", dto);
            
            return updated;
        });
    }
    
    @Transactional
    public boolean delete(String id) {
        if (stickyNoteRepository.existsById(id)) {
            stickyNoteRepository.deleteById(id);
            messagingTemplate.convertAndSend("/topic/sticky-notes/delete", id);
            noteLocks.remove(id);
            return true;
        }
        return false;
    }
}
