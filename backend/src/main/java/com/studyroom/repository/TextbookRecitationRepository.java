package com.studyroom.repository;

import com.studyroom.entity.TextbookRecitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextbookRecitationRepository extends JpaRepository<TextbookRecitation, String> {
    
    @Query("SELECT t FROM TextbookRecitation t WHERE t.subject = :subject AND t.grade = :grade ORDER BY t.id")
    List<TextbookRecitation> findBySubjectAndGrade(@Param("subject") String subject, @Param("grade") String grade);
}
