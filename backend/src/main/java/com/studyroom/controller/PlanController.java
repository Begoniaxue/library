package com.studyroom.controller;

import com.studyroom.dto.PlanDTO;
import com.studyroom.entity.Plan;
import com.studyroom.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {
    
    @Autowired
    private PlanService planService;
    
    @GetMapping
    public ResponseEntity<List<Plan>> getAll() {
        return ResponseEntity.ok(planService.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Plan> getById(@PathVariable String id) {
        return planService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Plan>> getByStudentId(
            @PathVariable String studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        if (weekStart != null) {
            return planService.getByStudentAndWeek(studentId, weekStart)
                    .map(p -> ResponseEntity.ok(Collections.singletonList(p)))
                    .orElse(ResponseEntity.ok(Collections.emptyList()));
        }
        return ResponseEntity.ok(planService.getByStudentId(studentId));
    }
    
    @PostMapping
    public ResponseEntity<Plan> add(@RequestBody PlanDTO planDTO) {
        Plan created = planService.add(planDTO);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Plan> update(@PathVariable String id, @RequestBody PlanDTO planDTO) {
        return planService.update(id, planDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (planService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
