package com.studyroom.repository;

import com.studyroom.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    
    List<Student> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT s FROM Student s WHERE " +
           "(:name IS NULL OR :name = '' OR s.name LIKE %:name%) AND " +
           "(:grade IS NULL OR :grade = '' OR s.grade = :grade) " +
           "ORDER BY s.createdAt DESC")
    List<Student> search(@Param("name") String name, @Param("grade") String grade);
}
