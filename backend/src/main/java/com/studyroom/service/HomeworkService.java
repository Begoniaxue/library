package com.studyroom.service;

import com.studyroom.entity.Homework;
import com.studyroom.repository.HomeworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HomeworkService {
    
    @Autowired
    private HomeworkRepository homeworkRepository;
    
    public List<Homework> getAll() {
        return homeworkRepository.findAllByOrderByDateDesc();
    }
    
    public Optional<Homework> getById(String id) {
        return homeworkRepository.findById(id);
    }
    
    public List<Homework> getByStudentId(String studentId) {
        return homeworkRepository.findByStudentIdOrderByDateDesc(studentId);
    }
    
    public List<Homework> getByDate(LocalDate date) {
        return homeworkRepository.findByDate(date);
    }
    
    @Transactional
    public Homework add(Homework homework) {
        if (homework.getCompleted() == null) {
            homework.setCompleted(false);
        }
        return homeworkRepository.save(homework);
    }
    
    @Transactional
    public Optional<Homework> update(String id, Homework homework) {
        return homeworkRepository.findById(id).map(existing -> {
            existing.setStudentId(homework.getStudentId());
            existing.setSubject(homework.getSubject());
            existing.setContent(homework.getContent());
            if (homework.getCompleted() != null) {
                existing.setCompleted(homework.getCompleted());
            }
            existing.setDate(homework.getDate());
            return homeworkRepository.save(existing);
        });
    }
    
    @Transactional
    public boolean delete(String id) {
        if (homeworkRepository.existsById(id)) {
            homeworkRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
