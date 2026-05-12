package com.studyroom.repository;

import com.studyroom.entity.Recitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecitationRepository extends JpaRepository<Recitation, String> {
    
    List<Recitation> findByStudentIdOrderByCheckDateDesc(String studentId);
    
    List<Recitation> findAllByOrderByCheckDateDesc();
}
