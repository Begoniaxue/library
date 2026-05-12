package com.studyroom.repository;

import com.studyroom.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, String> {
    
    List<Homework> findByStudentIdOrderByDateDesc(String studentId);
    
    @Query("SELECT h FROM Homework h WHERE h.date = :date ORDER BY h.createdAt DESC")
    List<Homework> findByDate(@Param("date") LocalDate date);
    
    List<Homework> findAllByOrderByDateDesc();
}
