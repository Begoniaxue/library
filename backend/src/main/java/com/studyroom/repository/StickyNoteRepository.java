package com.studyroom.repository;

import com.studyroom.entity.StickyNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StickyNoteRepository extends JpaRepository<StickyNote, String> {
    
    List<StickyNote> findAllByOrderByCreatedAtAsc();
}
