package com.studyroom.controller;

import com.studyroom.dto.ConstantsDTO;
import com.studyroom.dto.RecitationOptionDTO;
import com.studyroom.service.TextbookRecitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/constants")
public class ConstantsController {
    
    @Autowired
    private TextbookRecitationService textbookRecitationService;
    
    @GetMapping
    public ResponseEntity<ConstantsDTO> getConstants() {
        Map<String, ConstantsDTO.RecitationStatus> recitationStatuses = new LinkedHashMap<>();
        recitationStatuses.put("SKILLED", new ConstantsDTO.RecitationStatus("skilled", "熟练", "green"));
        recitationStatuses.put("NOT_SKILLED", new ConstantsDTO.RecitationStatus("not_skilled", "不熟", "yellow"));
        recitationStatuses.put("NOT_STARTED", new ConstantsDTO.RecitationStatus("not_started", "未完成", "red"));
        
        ConstantsDTO constants = new ConstantsDTO(
                Arrays.asList("一年级", "二年级", "三年级", "四年级", "五年级", "六年级", 
                        "七年级", "八年级", "九年级", "高一", "高二", "高三"),
                Arrays.asList("语文", "数学", "英语", "物理", "化学", "生物", "历史", "地理", "政治"),
                Arrays.asList("基础阶段", "强化阶段", "冲刺阶段"),
                recitationStatuses
        );
        
        return ResponseEntity.ok(constants);
    }
    
    @GetMapping("/recitation-options")
    public ResponseEntity<List<RecitationOptionDTO>> getRecitationOptions(
            @RequestParam String subject,
            @RequestParam String grade) {
        return ResponseEntity.ok(textbookRecitationService.getRecitationOptions(subject, grade));
    }
    
    @GetMapping("/recitation-options/{id}")
    public ResponseEntity<RecitationOptionDTO> getRecitationById(@PathVariable String id) {
        return textbookRecitationService.getRecitationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
