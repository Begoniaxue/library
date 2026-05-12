package com.studyroom.repository;

import com.studyroom.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, String> {
    
    List<Plan> findByStudentIdOrderByWeekStartDesc(String studentId);
    
    @Query("SELECT p FROM Plan p WHERE p.studentId = :studentId AND p.weekStart = :weekStart")
    Optional<Plan> findByStudentIdAndWeekStart(@Param("studentId") String studentId, @Param("weekStart") LocalDate weekStart);
    
    List<Plan> findAllByOrderByWeekStartDesc();
}
