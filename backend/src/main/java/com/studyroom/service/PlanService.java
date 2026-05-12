package com.studyroom.service;

import com.studyroom.dto.PlanDTO;
import com.studyroom.entity.Plan;
import com.studyroom.entity.PlanTask;
import com.studyroom.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanService {
    
    @Autowired
    private PlanRepository planRepository;
    
    public List<Plan> getAll() {
        return planRepository.findAllByOrderByWeekStartDesc();
    }
    
    public Optional<Plan> getById(String id) {
        return planRepository.findById(id);
    }
    
    public List<Plan> getByStudentId(String studentId) {
        return planRepository.findByStudentIdOrderByWeekStartDesc(studentId);
    }
    
    public Optional<Plan> getByStudentAndWeek(String studentId, LocalDate weekStart) {
        return planRepository.findByStudentIdAndWeekStart(studentId, weekStart);
    }
    
    @Transactional
    public Plan add(PlanDTO dto) {
        Plan plan = new Plan();
        plan.setStudentId(dto.getStudentId());
        plan.setWeekStart(dto.getWeekStart());
        plan.setWeekEnd(dto.getWeekEnd());
        
        if (dto.getTasks() != null) {
            List<PlanTask> tasks = dto.getTasks().stream().map(taskDTO -> {
                PlanTask task = new PlanTask();
                task.setDay(taskDTO.getDay());
                task.setContent(taskDTO.getContent());
                task.setKnowledge(taskDTO.getKnowledge());
                task.setCompleted(taskDTO.getCompleted() != null ? taskDTO.getCompleted() : false);
                task.setPlan(plan);
                return task;
            }).collect(Collectors.toList());
            plan.setTasks(tasks);
        }
        
        return planRepository.save(plan);
    }
    
    @Transactional
    public Optional<Plan> update(String id, PlanDTO dto) {
        return planRepository.findById(id).map(existing -> {
            existing.setStudentId(dto.getStudentId());
            existing.setWeekStart(dto.getWeekStart());
            existing.setWeekEnd(dto.getWeekEnd());
            
            existing.getTasks().clear();
            if (dto.getTasks() != null) {
                List<PlanTask> tasks = dto.getTasks().stream().map(taskDTO -> {
                    PlanTask task = new PlanTask();
                    task.setDay(taskDTO.getDay());
                    task.setContent(taskDTO.getContent());
                    task.setKnowledge(taskDTO.getKnowledge());
                    task.setCompleted(taskDTO.getCompleted() != null ? taskDTO.getCompleted() : false);
                    task.setPlan(existing);
                    return task;
                }).collect(Collectors.toList());
                existing.getTasks().addAll(tasks);
            }
            
            return planRepository.save(existing);
        });
    }
    
    @Transactional
    public boolean delete(String id) {
        if (planRepository.existsById(id)) {
            planRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
