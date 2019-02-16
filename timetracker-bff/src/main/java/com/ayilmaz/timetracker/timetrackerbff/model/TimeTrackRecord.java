package com.ayilmaz.timetracker.timetrackerbff.model;

import javax.validation.constraints.Email;
import java.time.OffsetDateTime;

public class TimeTrackRecord {

    @Email
    private String email;
    private OffsetDateTime startDateTime;
    private OffsetDateTime endDateTime;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OffsetDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(OffsetDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public OffsetDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(OffsetDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}
