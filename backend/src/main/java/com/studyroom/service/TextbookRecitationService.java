package com.studyroom.service;

import com.studyroom.dto.RecitationOptionDTO;
import com.studyroom.entity.TextbookRecitation;
import com.studyroom.repository.TextbookRecitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TextbookRecitationService {
    
    @Autowired
    private TextbookRecitationRepository textbookRecitationRepository;
    
    public List<RecitationOptionDTO> getRecitationOptions(String subject, String grade) {
        List<TextbookRecitation> recitations = textbookRecitationRepository.findBySubjectAndGrade(subject, grade);
        return recitations.stream()
                .map(r -> new RecitationOptionDTO(
                        r.getId(),
                        r.getTitle(),
                        r.getContent(),
                        r.getCategory(),
                        r.getSubject(),
                        r.getGrade()
                ))
                .collect(Collectors.toList());
    }
    
    public Optional<RecitationOptionDTO> getRecitationById(String id) {
        return textbookRecitationRepository.findById(id)
                .map(r -> new RecitationOptionDTO(
                        r.getId(),
                        r.getTitle(),
                        r.getContent(),
                        r.getCategory(),
                        r.getSubject(),
                        r.getGrade()
                ));
    }
    
    public List<TextbookRecitation> saveAll(List<TextbookRecitation> recitations) {
        return textbookRecitationRepository.saveAll(recitations);
    }
    
    public long count() {
        return textbookRecitationRepository.count();
    }
}
