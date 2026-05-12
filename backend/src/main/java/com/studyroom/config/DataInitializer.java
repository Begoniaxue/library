package com.studyroom.config;

import com.studyroom.entity.*;
import com.studyroom.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private KnowledgeService knowledgeService;
    
    @Autowired
    private PlanService planService;
    
    @Autowired
    private RecitationService recitationService;
    
    @Autowired
    private DictationService dictationService;
    
    @Autowired
    private HomeworkService homeworkService;
    
    @Autowired
    private TextbookRecitationService textbookRecitationService;
    
    @Override
    public void run(String... args) throws Exception {
        if (studentService.getAll().isEmpty()) {
            initStudents();
        }
        
        if (knowledgeService.getAll().isEmpty()) {
            initKnowledge();
        }
        
        if (textbookRecitationService.count() == 0) {
            initTextbookRecitations();
        }
        
        if (planService.getAll().isEmpty()) {
            initPlans();
        }
        
        if (recitationService.getAll().isEmpty()) {
            initRecitations();
        }
        
        if (dictationService.getAll().isEmpty()) {
            initDictations();
        }
        
        if (homeworkService.getAll().isEmpty()) {
            initHomework();
        }
    }
    
    private void initStudents() {
        List<Student> students = Arrays.asList(
                createStudent("张三", "七年级", "基础阶段", "13800138001"),
                createStudent("李四", "七年级", "强化阶段", "13800138002"),
                createStudent("王五", "八年级", "基础阶段", "13800138003"),
                createStudent("赵六", "九年级", "冲刺阶段", "13800138004")
        );
        students.forEach(studentService::add);
    }
    
    private Student createStudent(String name, String grade, String studyStage, String contact) {
        Student student = new Student();
        student.setName(name);
        student.setGrade(grade);
        student.setStudyStage(studyStage);
        student.setContact(contact);
        return student;
    }
    
    private void initKnowledge() {
        List<Knowledge> knowledges = Arrays.asList(
                createKnowledge("一元一次方程", "七年级", "数学", "只含有一个未知数，并且未知数的最高次数是1的整式方程", true),
                createKnowledge("整式的加减", "七年级", "数学", "同类项的合并，去括号法则", false),
                createKnowledge("古诗默写", "七年级", "语文", "《观沧海》曹操", true),
                createKnowledge("一般现在时", "七年级", "英语", "表示经常性、习惯性的动作或状态", true),
                createKnowledge("勾股定理", "八年级", "数学", "直角三角形两直角边的平方和等于斜边的平方", true),
                createKnowledge("一元二次方程", "九年级", "数学", "形如ax² + bx + c = 0 (a≠0) 的方程", true)
        );
        knowledges.forEach(knowledgeService::add);
    }
    
    private Knowledge createKnowledge(String title, String grade, String subject, String content, boolean isImportant) {
        Knowledge knowledge = new Knowledge();
        knowledge.setTitle(title);
        knowledge.setGrade(grade);
        knowledge.setSubject(subject);
        knowledge.setContent(content);
        knowledge.setIsImportant(isImportant);
        return knowledge;
    }
    
    private void initPlans() {
        List<Student> students = studentService.getAll();
        if (students.isEmpty()) return;
        
        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate sunday = monday.plusDays(6);
        
        com.studyroom.dto.PlanDTO plan1 = new com.studyroom.dto.PlanDTO();
        plan1.setStudentId(students.get(0).getId());
        plan1.setWeekStart(monday);
        plan1.setWeekEnd(sunday);
        plan1.setTasks(Arrays.asList(
                createPlanTaskDTO("周一", "完成数学练习册P15-16", "一元一次方程", true),
                createPlanTaskDTO("周二", "背诵语文古诗", "古诗默写", true),
                createPlanTaskDTO("周三", "英语单词Unit 5", "一般现在时", false),
                createPlanTaskDTO("周四", "数学错题整理", "整式的加减", false),
                createPlanTaskDTO("周五", "复习本周知识点", "", false)
        ));
        planService.add(plan1);
        
        if (students.size() > 1) {
            com.studyroom.dto.PlanDTO plan2 = new com.studyroom.dto.PlanDTO();
            plan2.setStudentId(students.get(1).getId());
            plan2.setWeekStart(monday);
            plan2.setWeekEnd(sunday);
            plan2.setTasks(Arrays.asList(
                    createPlanTaskDTO("周一", "英语语法练习", "一般现在时", true),
                    createPlanTaskDTO("周二", "数学课外题", "一元一次方程", true),
                    createPlanTaskDTO("周三", "语文阅读理解", "", true),
                    createPlanTaskDTO("周四", "复习英语单词", "", false),
                    createPlanTaskDTO("周五", "周测准备", "", false)
            ));
            planService.add(plan2);
        }
    }
    
    private com.studyroom.dto.PlanDTO.PlanTaskDTO createPlanTaskDTO(String day, String content, String knowledge, Boolean completed) {
        com.studyroom.dto.PlanDTO.PlanTaskDTO task = new com.studyroom.dto.PlanDTO.PlanTaskDTO();
        task.setDay(day);
        task.setContent(content);
        task.setKnowledge(knowledge);
        task.setCompleted(completed);
        return task;
    }
    
    private void initRecitations() {
        List<Student> students = studentService.getAll();
        if (students.isEmpty()) return;
        
        LocalDate today = LocalDate.now();
        
        Recitation r1 = createRecitation(students.get(0).getId(), "语文", "《观沧海》曹操", "skilled", today, "背诵流畅，无错误");
        Recitation r2 = createRecitation(students.get(0).getId(), "英语", "Unit 4 单词", "not_skilled", today, "部分单词发音不准确");
        if (students.size() > 1) {
            Recitation r3 = createRecitation(students.get(1).getId(), "语文", "《闻王昌龄左迁龙标遥有此寄》", "skilled", today, "");
            recitationService.add(r3);
        }
        if (students.size() > 2) {
            Recitation r4 = createRecitation(students.get(2).getId(), "语文", "《赤壁》杜牧", "not_started", today, "");
            recitationService.add(r4);
        }
        
        recitationService.add(r1);
        recitationService.add(r2);
    }
    
    private Recitation createRecitation(String studentId, String subject, String content, String status, LocalDate checkDate, String remark) {
        Recitation recitation = new Recitation();
        recitation.setStudentId(studentId);
        recitation.setSubject(subject);
        recitation.setContent(content);
        recitation.setStatus(status);
        recitation.setCheckDate(checkDate);
        recitation.setRemark(remark);
        return recitation;
    }
    
    private void initDictations() {
        List<Student> students = studentService.getAll();
        if (students.isEmpty()) return;
        
        LocalDate today = LocalDate.now();
        
        Dictation d1 = createDictation(students.get(0).getId(), "语文", "古诗词名句默写", 18, 20, "第3、8题错误", today);
        Dictation d2 = createDictation(students.get(0).getId(), "英语", "Unit 5 单词听写", 15, 20, "5个单词拼写错误", today);
        if (students.size() > 1) {
            Dictation d3 = createDictation(students.get(1).getId(), "英语", "短语听写", 8, 10, "", today);
            dictationService.add(d3);
        }
        if (students.size() > 2) {
            Dictation d4 = createDictation(students.get(2).getId(), "语文", "易错字听写", 5, 10, "多个字形错误", today);
            dictationService.add(d4);
        }
        
        dictationService.add(d1);
        dictationService.add(d2);
    }
    
    private Dictation createDictation(String studentId, String subject, String content, int correctCount, int totalCount, String errors, LocalDate checkDate) {
        Dictation dictation = new Dictation();
        dictation.setStudentId(studentId);
        dictation.setSubject(subject);
        dictation.setContent(content);
        dictation.setCorrectCount(correctCount);
        dictation.setTotalCount(totalCount);
        dictation.setErrors(errors);
        dictation.setCheckDate(checkDate);
        dictation.setAccuracy((double) correctCount / totalCount * 100);
        return dictation;
    }
    
    private void initHomework() {
        List<Student> students = studentService.getAll();
        if (students.isEmpty()) return;
        
        LocalDate today = LocalDate.now();
        
        Homework h1 = createHomework(students.get(0).getId(), "数学", "练习册P20-21", true, today);
        Homework h2 = createHomework(students.get(0).getId(), "英语", "抄写单词3遍", true, today);
        if (students.size() > 1) {
            Homework h3 = createHomework(students.get(1).getId(), "语文", "背诵课文", true, today);
            Homework h4 = createHomework(students.get(1).getId(), "数学", "错题本整理", false, today);
            homeworkService.add(h3);
            homeworkService.add(h4);
        }
        if (students.size() > 2) {
            Homework h5 = createHomework(students.get(2).getId(), "英语", "语法练习", true, today);
            homeworkService.add(h5);
        }
        if (students.size() > 3) {
            Homework h6 = createHomework(students.get(3).getId(), "数学", "模拟试卷", false, today);
            homeworkService.add(h6);
        }
        
        homeworkService.add(h1);
        homeworkService.add(h2);
    }
    
    private Homework createHomework(String studentId, String subject, String content, boolean completed, LocalDate date) {
        Homework homework = new Homework();
        homework.setStudentId(studentId);
        homework.setSubject(subject);
        homework.setContent(content);
        homework.setCompleted(completed);
        homework.setDate(date);
        return homework;
    }
    
    private void initTextbookRecitations() {
        List<TextbookRecitation> recitations = new ArrayList<>();
        
        String[] grades = {"一年级", "二年级", "三年级", "四年级", "五年级", "六年级", 
                          "七年级", "八年级", "九年级", "高一", "高二", "高三"};
        
        for (String grade : grades) {
            recitations.add(createTextbookRecitation("语文", grade, "必背古诗1", "古诗内容...", "古诗"));
            recitations.add(createTextbookRecitation("语文", grade, "必背古诗2", "古诗内容...", "古诗"));
            recitations.add(createTextbookRecitation("语文", grade, "课文片段1", "课文内容...", "课文片段"));
            recitations.add(createTextbookRecitation("语文", grade, "课文片段2", "课文内容...", "课文片段"));
            
            recitations.add(createTextbookRecitation("英语", grade, "Unit 1 单词", "英语单词...", "单词"));
            recitations.add(createTextbookRecitation("英语", grade, "Unit 2 短语", "英语短语...", "短语"));
            recitations.add(createTextbookRecitation("英语", grade, "Unit 3 对话", "英语对话...", "对话"));
            recitations.add(createTextbookRecitation("英语", grade, "Unit 4 句型", "英语句型...", "句型"));
        }
        
        textbookRecitationService.saveAll(recitations);
    }
    
    private TextbookRecitation createTextbookRecitation(String subject, String grade, String title, String content, String category) {
        TextbookRecitation recitation = new TextbookRecitation();
        recitation.setSubject(subject);
        recitation.setGrade(grade);
        recitation.setTitle(title);
        recitation.setContent(content);
        recitation.setCategory(category);
        return recitation;
    }
}
