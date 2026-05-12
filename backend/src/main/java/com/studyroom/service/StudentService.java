package com.studyroom.service;

import com.studyroom.entity.Student;
import com.studyroom.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    public List<Student> getAll() {
        return studentRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public Optional<Student> getById(String id) {
        return studentRepository.findById(id);
    }
    
    public List<Student> search(String name, String grade) {
        return studentRepository.search(name, grade);
    }
    
    @Transactional
    public Student add(Student student) {
        return studentRepository.save(student);
    }
    
    @Transactional
    public Optional<Student> update(String id, Student student) {
        return studentRepository.findById(id).map(existing -> {
            existing.setName(student.getName());
            existing.setGrade(student.getGrade());
            existing.setStudyStage(student.getStudyStage());
            existing.setContact(student.getContact());
            return studentRepository.save(existing);
        });
    }
    
    @Transactional
    public boolean delete(String id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
