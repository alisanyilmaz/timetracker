package com.ayilmaz.timetracker.timetrackerbff.service;

import com.ayilmaz.timetracker.timetrackerbff.model.TimeTrackRecord;

import java.util.List;

public interface LegacyService {
    List<TimeTrackRecord> getTimeTrackRecords(String email, int offset, int limit);

    TimeTrackRecord postTimeTrackRecord(TimeTrackRecord timeTrackRecord);
}
