package com.studyroom.repository;

import com.studyroom.entity.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeRepository extends JpaRepository<Knowledge, String> {
    
    List<Knowledge> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT k FROM Knowledge k WHERE " +
           "(:grade IS NULL OR :grade = '' OR k.grade = :grade) AND " +
           "(:subject IS NULL OR :subject = '' OR k.subject = :subject) " +
           "ORDER BY k.isImportant DESC, k.createdAt DESC")
    List<Knowledge> findByGradeAndSubject(@Param("grade") String grade, @Param("subject") String subject);
    
    List<Knowledge> findByGradeOrderByIsImportantDesc(String grade);
    
    List<Knowledge> findBySubjectOrderByIsImportantDesc(String subject);
}
