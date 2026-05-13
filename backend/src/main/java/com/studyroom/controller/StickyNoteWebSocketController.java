package com.studyroom.controller;

import com.studyroom.dto.StickyNoteDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class StickyNoteWebSocketController {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public StickyNoteWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    @MessageMapping("/sticky-notes/move")
    public void handleMove(@Payload StickyNoteDTO dto) {
        if (dto.getId() != null && dto.getX() != null && dto.getY() != null) {
            messagingTemplate.convertAndSend("/topic/sticky-notes/move", dto);
        }
    }
    
    @MessageMapping("/sticky-notes/create")
    public void handleCreate(@Payload StickyNoteDTO dto) {
        if (dto.getId() != null) {
            messagingTemplate.convertAndSend("/topic/sticky-notes/create", dto);
        }
    }
    
    @MessageMapping("/sticky-notes/update")
    public void handleUpdate(@Payload StickyNoteDTO dto) {
        if (dto.getId() != null) {
            messagingTemplate.convertAndSend("/topic/sticky-notes/update", dto);
        }
    }
    
    @MessageMapping("/sticky-notes/delete")
    public void handleDelete(@Payload String id) {
        if (id != null && !id.isEmpty()) {
            messagingTemplate.convertAndSend("/topic/sticky-notes/delete", id);
        }
    }
}
