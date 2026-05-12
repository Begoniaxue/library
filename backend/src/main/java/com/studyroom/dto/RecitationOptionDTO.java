package com.studyroom.dto;

public class RecitationOptionDTO {
    private String id;
    private String title;
    private String content;
    private String category;
    private String subject;
    private String grade;
    
    public RecitationOptionDTO() {}
    
    public RecitationOptionDTO(String id, String title, String content, String category, 
                               String subject, String grade) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.subject = subject;
        this.grade = grade;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}
