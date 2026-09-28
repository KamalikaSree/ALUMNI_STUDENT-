package com.mentorconnect.controller;

import com.mentorconnect.dto.DashboardSummaryDto;
import com.mentorconnect.dto.EngagementReportDto;
import com.mentorconnect.service.ReportService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(
            ReportService service) {

        this.service = service;
    }

    @GetMapping("/dashboard")
    public DashboardSummaryDto dashboard() {

        return service.dashboard();

    }

    @GetMapping("/engagement")
    public List<EngagementReportDto> engagement() {

        return service.engagement();

    }
}