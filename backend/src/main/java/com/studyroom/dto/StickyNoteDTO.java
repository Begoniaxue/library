package com.studyroom.dto;

public class StickyNoteDTO {
    
    private String id;
    private Double x;
    private Double y;
    private String content;

    public StickyNoteDTO() {}

    public StickyNoteDTO(String id, Double x, Double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public StickyNoteDTO(String id, Double x, Double y, String content) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.content = content;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }
    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
