package com.studyroom.dto;

import java.time.LocalDate;
import java.util.List;

public class PlanDTO {
    private String studentId;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private List<PlanTaskDTO> tasks;
    
    public static class PlanTaskDTO {
        private String day;
        private String content;
        private String knowledge;
        private Boolean completed;

        public String getDay() { return day; }
        public void setDay(String day) { this.day = day; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getKnowledge() { return knowledge; }
        public void setKnowledge(String knowledge) { this.knowledge = knowledge; }
        public Boolean getCompleted() { return completed; }
        public void setCompleted(Boolean completed) { this.completed = completed; }
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public LocalDate getWeekStart() { return weekStart; }
    public void setWeekStart(LocalDate weekStart) { this.weekStart = weekStart; }
    public LocalDate getWeekEnd() { return weekEnd; }
    public void setWeekEnd(LocalDate weekEnd) { this.weekEnd = weekEnd; }
    public List<PlanTaskDTO> getTasks() { return tasks; }
    public void setTasks(List<PlanTaskDTO> tasks) { this.tasks = tasks; }
}
