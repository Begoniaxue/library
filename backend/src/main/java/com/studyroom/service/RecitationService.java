package com.studyroom.service;

import com.studyroom.entity.Recitation;
import com.studyroom.repository.RecitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecitationService {
    
    @Autowired
    private RecitationRepository recitationRepository;
    
    public List<Recitation> getAll() {
        return recitationRepository.findAllByOrderByCheckDateDesc();
    }
    
    public Optional<Recitation> getById(String id) {
        return recitationRepository.findById(id);
    }
    
    public List<Recitation> getByStudentId(String studentId) {
        return recitationRepository.findByStudentIdOrderByCheckDateDesc(studentId);
    }
    
    @Transactional
    public Recitation add(Recitation recitation) {
        return recitationRepository.save(recitation);
    }
    
    @Transactional
    public Optional<Recitation> update(String id, Recitation recitation) {
        return recitationRepository.findById(id).map(existing -> {
            existing.setStudentId(recitation.getStudentId());
            existing.setSubject(recitation.getSubject());
            existing.setContent(recitation.getContent());
            existing.setStatus(recitation.getStatus());
            existing.setCheckDate(recitation.getCheckDate());
            existing.setRemark(recitation.getRemark());
            return recitationRepository.save(existing);
        });
    }
    
    @Transactional
    public boolean delete(String id) {
        if (recitationRepository.existsById(id)) {
            recitationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
