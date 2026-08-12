package com.umc.halo.domain.calendar.controller;

import com.umc.halo.domain.calendar.controller.docs.CalendarControllerDocs;
import com.umc.halo.domain.calendar.dto.CalendarDailyResDTO;
import com.umc.halo.domain.calendar.dto.CalendarMonthlyResDTO;
import com.umc.halo.domain.calendar.exception.code.CalendarSuccessCode;
import com.umc.halo.domain.calendar.service.CalendarService;
import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/calendar")
public class CalendarController implements CalendarControllerDocs {

    private final CalendarService calendarService;

    // 월간
    @GetMapping
    public ApiResponse<CalendarMonthlyResDTO.MonthlyInfo> getMonthly(
            @AuthenticationPrincipal Long memberId, @RequestParam int year, @RequestParam @Min(value = 1, message = "1 이상이어야 합니다.") @Max(value = 12, message = "12 이하여야 합니다.") int month
    ) {
        BaseSuccessCode code = CalendarSuccessCode.MAIN_SUCCESS;
        return ApiResponse.onSuccess(code, calendarService.getMonthly(memberId, year, month));
    }

    // 일별
    @GetMapping("/{date}")
    public ApiResponse<CalendarDailyResDTO.DailyInfo> getDaily(
            @AuthenticationPrincipal Long memberId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        BaseSuccessCode code = CalendarSuccessCode.DAILY_SUCCESS;
        return ApiResponse.onSuccess(code, calendarService.getDaily(memberId, date));
    }
}