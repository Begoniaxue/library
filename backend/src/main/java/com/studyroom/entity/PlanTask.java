package com.studyroom.entity;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "plan_tasks")
public class PlanTask {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    
    @Column(name = "week_day", nullable = false)
    private String day;
    
    @Column(nullable = false)
    private String content;
    
    private String knowledge;
    
    @Column(nullable = false)
    private Boolean completed = false;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getKnowledge() { return knowledge; }
    public void setKnowledge(String knowledge) { this.knowledge = knowledge; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
}
