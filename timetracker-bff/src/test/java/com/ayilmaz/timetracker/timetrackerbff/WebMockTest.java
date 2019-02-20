package com.ayilmaz.timetracker.timetrackerbff;

import com.ayilmaz.timetracker.timetrackerbff.controller.TimeTrackRecordsController;
import com.ayilmaz.timetracker.timetrackerbff.model.TimeTrackRecord;
import com.ayilmaz.timetracker.timetrackerbff.service.LegacyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(TimeTrackRecordsController.class)
public class WebMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LegacyService service;

    @Test
    public void getTimeTrackRecords_ShouldReturnData_WithStatusOk() throws Exception {

        TimeTrackRecord record = new TimeTrackRecord();
        record.setEmail("hello@world.com");
        record.setStartDateTime(OffsetDateTime.now());
        record.setEndDateTime(OffsetDateTime.now());

        when(service.getTimeTrackRecords("hello@world.com", 0, 10)).thenReturn(Arrays.asList(record));

        this.mockMvc.perform(get("/api/records?email=hello@world.com&offset=0&length=10")).andDo(print()).andExpect
                (status().isOk())
                .andExpect(content().string(containsString("hello@world.com")));
    }

    @Test
    public void getTimeTrackRecords_InvalidEmail_ShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/api/records?email=helloworld")).andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void getTimeTrackRecords_NullEmail_ShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/api/records")).andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void postTimeTrackRecord_ShouldReturnData_WithStatusCreated() throws Exception {

        String requestBody = "{" +
                "\"email\":\"hello@world.com\"," +
                "\"startDateTime\":\"2019-02-16T09:00:00.000+0000\"," +
                "\"endDateTime\":\"2019-02-16T10:00:00.000+0000\"" +
                "}";

        TimeTrackRecord record = new TimeTrackRecord();
        record.setEmail("hello@world.com");
        record.setStartDateTime(OffsetDateTime.now());
        record.setEndDateTime(OffsetDateTime.now());

        when(service.postTimeTrackRecord(any(TimeTrackRecord.class))).thenReturn(record);

        this.mockMvc.perform(post("/api/records").content(requestBody).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print()).andExpect(status().isCreated())
                .andExpect(content().string(containsString("hello@world.com")));
    }

    @Test
    public void postTimeTrackRecord_InvalidEmail_ShouldReturn_WithStatusBadRequest() throws Exception {

        String requestBody = "{" +
                "\"email\":\"hello\"," +
                "\"startDateTime\":\"2019-02-16T09:00:00.000+0000\"," +
                "\"endDateTime\":\"2019-02-16T10:00:00.000+0000\"" +
                "}";

        this.mockMvc.perform(post("/api/records").content(requestBody).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print()).andExpect(status().isBadRequest());
    }

}
