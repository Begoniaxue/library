package com.studyroom.service;

import com.studyroom.entity.Dictation;
import com.studyroom.repository.DictationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DictationService {
    
    @Autowired
    private DictationRepository dictationRepository;
    
    public List<Dictation> getAll() {
        return dictationRepository.findAllByOrderByCheckDateDesc();
    }
    
    public Optional<Dictation> getById(String id) {
        return dictationRepository.findById(id);
    }
    
    public List<Dictation> getByStudentId(String studentId) {
        return dictationRepository.findByStudentIdOrderByCheckDateDesc(studentId);
    }
    
    @Transactional
    public Dictation add(Dictation dictation) {
        if (dictation.getCorrectCount() != null && dictation.getTotalCount() != null && dictation.getTotalCount() > 0) {
            dictation.setAccuracy((double) dictation.getCorrectCount() / dictation.getTotalCount() * 100);
        }
        return dictationRepository.save(dictation);
    }
    
    @Transactional
    public Optional<Dictation> update(String id, Dictation dictation) {
        return dictationRepository.findById(id).map(existing -> {
            existing.setStudentId(dictation.getStudentId());
            existing.setSubject(dictation.getSubject());
            existing.setContent(dictation.getContent());
            existing.setCorrectCount(dictation.getCorrectCount());
            existing.setTotalCount(dictation.getTotalCount());
            existing.setErrors(dictation.getErrors());
            existing.setCheckDate(dictation.getCheckDate());
            
            if (dictation.getCorrectCount() != null && dictation.getTotalCount() != null && dictation.getTotalCount() > 0) {
                existing.setAccuracy((double) dictation.getCorrectCount() / dictation.getTotalCount() * 100);
            }
            
            return dictationRepository.save(existing);
        });
    }
    
    @Transactional
    public boolean delete(String id) {
        if (dictationRepository.existsById(id)) {
            dictationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
