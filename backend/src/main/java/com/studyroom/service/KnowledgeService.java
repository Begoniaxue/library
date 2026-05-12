package com.studyroom.service;

import com.studyroom.entity.Knowledge;
import com.studyroom.repository.KnowledgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class KnowledgeService {
    
    @Autowired
    private KnowledgeRepository knowledgeRepository;
    
    public List<Knowledge> getAll() {
        return knowledgeRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public Optional<Knowledge> getById(String id) {
        return knowledgeRepository.findById(id);
    }
    
    public List<Knowledge> getByGradeAndSubject(String grade, String subject) {
        if ((grade == null || grade.isEmpty()) && (subject == null || subject.isEmpty())) {
            return getAll();
        }
        return knowledgeRepository.findByGradeAndSubject(grade, subject);
    }
    
    @Transactional
    public Knowledge add(Knowledge knowledge) {
        if (knowledge.getIsImportant() == null) {
            knowledge.setIsImportant(false);
        }
        return knowledgeRepository.save(knowledge);
    }
    
    @Transactional
    public Optional<Knowledge> update(String id, Knowledge knowledge) {
        return knowledgeRepository.findById(id).map(existing -> {
            existing.setTitle(knowledge.getTitle());
            existing.setGrade(knowledge.getGrade());
            existing.setSubject(knowledge.getSubject());
            existing.setContent(knowledge.getContent());
            if (knowledge.getIsImportant() != null) {
                existing.setIsImportant(knowledge.getIsImportant());
            }
            return knowledgeRepository.save(existing);
        });
    }
    
    @Transactional
    public Optional<Knowledge> toggleImportant(String id) {
        return knowledgeRepository.findById(id).map(existing -> {
            existing.setIsImportant(!existing.getIsImportant());
            return knowledgeRepository.save(existing);
        });
    }
    
    @Transactional
    public boolean delete(String id) {
        if (knowledgeRepository.existsById(id)) {
            knowledgeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
