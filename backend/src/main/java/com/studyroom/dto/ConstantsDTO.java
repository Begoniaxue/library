package com.studyroom.dto;

import java.util.List;
import java.util.Map;

public class ConstantsDTO {
    private List<String> grades;
    private List<String> subjects;
    private List<String> studyStages;
    private Map<String, RecitationStatus> recitationStatuses;
    
    public static class RecitationStatus {
        private String value;
        private String label;
        private String color;
        
        public RecitationStatus() {}
        
        public RecitationStatus(String value, String label, String color) {
            this.value = value;
            this.label = label;
            this.color = color;
        }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }
    
    public ConstantsDTO() {}
    
    public ConstantsDTO(List<String> grades, List<String> subjects, List<String> studyStages, 
                        Map<String, RecitationStatus> recitationStatuses) {
        this.grades = grades;
        this.subjects = subjects;
        this.studyStages = studyStages;
        this.recitationStatuses = recitationStatuses;
    }

    public List<String> getGrades() { return grades; }
    public void setGrades(List<String> grades) { this.grades = grades; }
    public List<String> getSubjects() { return subjects; }
    public void setSubjects(List<String> subjects) { this.subjects = subjects; }
    public List<String> getStudyStages() { return studyStages; }
    public void setStudyStages(List<String> studyStages) { this.studyStages = studyStages; }
    public Map<String, RecitationStatus> getRecitationStatuses() { return recitationStatuses; }
    public void setRecitationStatuses(Map<String, RecitationStatus> recitationStatuses) { 
        this.recitationStatuses = recitationStatuses; 
    }
}
