package com.ayilmaz.timetracker.timetrackerbff;

import com.ayilmaz.timetracker.timetrackerbff.model.TimeTrackRecord;
import com.ayilmaz.timetracker.timetrackerbff.service.LegacyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LegacyServiceTest {

    @Autowired
    LegacyService legacyService;

    @Autowired
    RestTemplate restTemplate;

    @Value("${LEGACY_SERVICE_URL}")
    String LEGACY_SERVICE_URL;

    @Value("${legacyservice.dateformat.from}")
    String legacyDateTimeFormat;

    private DateTimeFormatter legacyFormatter;

    private MockRestServiceServer mockServer;

    @Before
    public void init() {
        legacyFormatter = DateTimeFormatter.ofPattern(legacyDateTimeFormat);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void givenMockingIsDoneByMockRestServiceServer_whenGetIsCalled_thenReturnsMockedObject() throws Exception {

        String email = "hello@world.com";
        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime end = OffsetDateTime.now();

        int offset = 0;
        int length = 10;

        String legacyRecordsResponse = createLegacyRecordsResponse(email, start, end);

        TimeTrackRecord record = createTimeTrackRecord(email, start, end);

        mockServer.expect(ExpectedCount.once(), requestTo(createExpectedGetRequestURI(email, offset, length)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(legacyRecordsResponse)
                );

        List<TimeTrackRecord> records = legacyService.getTimeTrackRecords(email, offset, length);

        mockServer.verify();

        assertEquals(records.size(), 1);
        assertEquals(record, records.get(0));
    }

    @Test
    public void givenMockingIsDoneByMockRestServiceServer_whenPostIsCalled_thenReturnsMockedObject() throws Exception {

        String email = "hello@world.com";
        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime end = OffsetDateTime.now();

        String legacyRecordResponse = createLegacyRecordAsString(email, start, end);

        TimeTrackRecord record = createTimeTrackRecord(email, start, end);

        mockServer.expect(ExpectedCount.once(), requestTo(createExpectedPostRequestURI()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(legacyRecordResponse)
                );

        TimeTrackRecord createdRecord = legacyService.postTimeTrackRecord(record);

        mockServer.verify();

        assertEquals(record, createdRecord);
    }

    private String createLegacyRecordAsString(String email, OffsetDateTime start, OffsetDateTime end) {
        return "{\"email\":\"" + email + "\"," +
                "\"start\":\"" + start.format(legacyFormatter) + "\"," +
                "\"end\":\"" + end.format(legacyFormatter) + "\"}";
    }

    private String createLegacyRecordsResponse(String email, OffsetDateTime start, OffsetDateTime end) {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(createLegacyRecordAsString(email, start, end));
        sb.append("]");

        return sb.toString();
    }

    private TimeTrackRecord createTimeTrackRecord(String email, OffsetDateTime start, OffsetDateTime end) {
        TimeTrackRecord record = new TimeTrackRecord();
        record.setEmail(email);
        record.setStartDateTime(start);
        record.setEndDateTime(end);

        return record;
    }

    private String createExpectedGetRequestURI(String email, int offset, int length) {
        StringBuilder sb = new StringBuilder();

        sb.append(LEGACY_SERVICE_URL);
        sb.append("/records?email=");
        sb.append(email);
        sb.append("&offset=");
        sb.append(offset);
        sb.append("&length=");
        sb.append(length);

        return sb.toString();
    }

    private String createExpectedPostRequestURI() {

        return LEGACY_SERVICE_URL + "/records";
    }
}
