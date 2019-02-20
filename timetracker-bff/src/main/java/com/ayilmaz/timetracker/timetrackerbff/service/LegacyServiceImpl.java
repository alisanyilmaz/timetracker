package com.ayilmaz.timetracker.timetrackerbff.service;

import com.ayilmaz.timetracker.timetrackerbff.TimeTrackerConstants;
import com.ayilmaz.timetracker.timetrackerbff.model.TimeTrackRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LegacyServiceImpl implements LegacyService {

    private final static Logger LOGGER = LoggerFactory.getLogger(LegacyServiceImpl.class.getName());

    @Value("${LEGACY_SERVICE_URL}")
    private String LEGACY_SERVICE_URL;

    // GET and POST /records services use different datetime formats
    private final DateTimeFormatter toLegacyFormatter;
    private final DateTimeFormatter fromLegacyFormatter;

    private final RestTemplate restTemplate;

    private final static String PATH_RECORDS = "records";

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

    public LegacyServiceImpl(RestTemplate restTemplate,
                             @Value("${legacyservice.dateformat.from}") String legacyDateTimeFormatFrom,
                             @Value("${legacyservice.dateformat.to}") String legacyDateTimeFormatTo) {

        this.restTemplate = restTemplate;
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

        // sensitive info should be hidden in a real-world app
        LOGGER.info("Legacy service URL call [GET]: " + requestUri);

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

        // sensitive info should be hidden in a real-world app
        LOGGER.info("Legacy service URL call [POST]: " + requestUri);

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
        legacyRecord.setStart(record.getStartDateTime()
                .withOffsetSameInstant(ZoneOffset.UTC)
                .format(toLegacyFormatter));

        legacyRecord.setEnd(record.getEndDateTime().withOffsetSameInstant(ZoneOffset.UTC).format(toLegacyFormatter));

        return legacyRecord;
    }
}
