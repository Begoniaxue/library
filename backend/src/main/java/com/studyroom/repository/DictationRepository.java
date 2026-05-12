package com.studyroom.repository;

import com.studyroom.entity.Dictation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictationRepository extends JpaRepository<Dictation, String> {
    
    List<Dictation> findByStudentIdOrderByCheckDateDesc(String studentId);
    
    List<Dictation> findAllByOrderByCheckDateDesc();
}
