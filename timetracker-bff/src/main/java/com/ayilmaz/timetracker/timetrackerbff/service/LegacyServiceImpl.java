package com.ayilmaz.timetracker.timetrackerbff.service;

import com.ayilmaz.timetracker.timetrackerbff.TimeTrackerConstants;
import com.ayilmaz.timetracker.timetrackerbff.model.TimeTrackRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LegacyServiceImpl implements LegacyService {

    @Value("${LEGACY_SERVICE_URL}")
    private String LEGACY_SERVICE_URL;

    // GET and POST /records services use different datetime formats
    private final DateTimeFormatter toLegacyFormatter;
    private final DateTimeFormatter fromLegacyFormatter;

    private final RestTemplate restTemplate = new RestTemplate();

    private final String PATH_RECORDS = "records";

    private static class LegacyRecord {
        private String email;
        private String start;
        private String end;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }

    public LegacyServiceImpl(@Value("${legacyservice.dateformat.from}") String legacyDateTimeFormatFrom,
                             @Value("${legacyservice.dateformat.to}") String legacyDateTimeFormatTo) {

        fromLegacyFormatter = DateTimeFormatter.ofPattern(legacyDateTimeFormatFrom);
        toLegacyFormatter = DateTimeFormatter.ofPattern(legacyDateTimeFormatTo);
    }

    @Override
    public List<TimeTrackRecord> getTimeTrackRecords(String email, int offset, int length) {
        String requestUri = UriComponentsBuilder.fromHttpUrl(LEGACY_SERVICE_URL)
                .path(PATH_RECORDS)
                .queryParam(TimeTrackerConstants.PARAM_EMAIL, email)
                .queryParam(TimeTrackerConstants.PARAM_OFFSET, offset)
                .queryParam(TimeTrackerConstants.PARAM_LENGTH, length)
                .build()
                .toString();

        LegacyRecord[] legacyRecords = restTemplate.exchange(requestUri, HttpMethod.GET, HttpEntity.EMPTY,
                LegacyRecord[].class).getBody();

        return Arrays.stream(legacyRecords)
                .filter(r -> r != null)
                .map(this::convertFromLegacyRecord)
                .collect(Collectors.toList());
    }

    @Override
    public TimeTrackRecord postTimeTrackRecord(TimeTrackRecord timeTrackRecord) {
        LegacyRecord legacyRecord = convertToLegacyRecord(timeTrackRecord);

        String requestUri = UriComponentsBuilder.fromHttpUrl(LEGACY_SERVICE_URL)
                .path(PATH_RECORDS)
                .build()
                .toString();

        HttpHeaders headers = createHeadersForPostRequest();

        MultiValueMap<String, String> map = createMultiValueMapForPostRequest(legacyRecord);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(map,
                headers);

        legacyRecord = restTemplate.exchange(requestUri, HttpMethod.POST,
                requestEntity, LegacyRecord.class).getBody();

        return convertFromLegacyRecord(legacyRecord);
    }

    private MultiValueMap<String, String> createMultiValueMapForPostRequest(LegacyRecord legacyRecord) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(TimeTrackerConstants.PARAM_EMAIL, legacyRecord.getEmail());
        map.add(TimeTrackerConstants.PARAM_START, legacyRecord.getStart());
        map.add(TimeTrackerConstants.PARAM_END, legacyRecord.getEnd());
        return map;
    }

    private HttpHeaders createHeadersForPostRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private TimeTrackRecord convertFromLegacyRecord(LegacyRecord legacyRecord) {
        TimeTrackRecord record = new TimeTrackRecord();

        record.setEmail(legacyRecord.getEmail());
        record.setStartDateTime(OffsetDateTime.parse(legacyRecord.getStart(), fromLegacyFormatter));
        record.setEndDateTime(OffsetDateTime.parse(legacyRecord.getEnd(), fromLegacyFormatter));

        return record;
    }

    private LegacyRecord convertToLegacyRecord(TimeTrackRecord record) {
        LegacyRecord legacyRecord = new LegacyRecord();

        legacyRecord.setEmail(record.getEmail());
        legacyRecord.setStart(record.getStartDateTime().format(toLegacyFormatter));
        legacyRecord.setEnd(record.getEndDateTime().format(toLegacyFormatter));

        return legacyRecord;
    }
}
