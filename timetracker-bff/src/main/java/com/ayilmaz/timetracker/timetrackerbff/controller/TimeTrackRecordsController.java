package com.ayilmaz.timetracker.timetrackerbff.controller;

import com.ayilmaz.timetracker.timetrackerbff.model.TimeTrackRecord;
import com.ayilmaz.timetracker.timetrackerbff.service.LegacyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@Validated
public class TimeTrackRecordsController {

    private final LegacyService legacyService;

    public TimeTrackRecordsController(LegacyService legacyService) {
        this.legacyService = legacyService;
    }

    @GetMapping
    public List<TimeTrackRecord> getTimeTrackRecords(@RequestParam @Email String email,
                                                     @RequestParam(required = false, defaultValue = "0") int offset,
                                                     @RequestParam(required = false, defaultValue = "10") int limit) {

        return legacyService.getTimeTrackRecords(email, offset, limit);
    }

    @PostMapping
    public TimeTrackRecord postTimeTrackRecord(@RequestBody @Valid TimeTrackRecord timeTrackRecord) {

        return legacyService.postTimeTrackRecord(timeTrackRecord);
    }

}
