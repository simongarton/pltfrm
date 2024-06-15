package com.simongarton.platform.utils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static OffsetDateTime longToOffsetDateTime(final long epoch) {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(epoch), OffsetDateTime.now().getOffset());
    }

    public static String nowAsFileNameISOinUTC() {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss'Z'");
        return dateTimeFormatter.format(OffsetDateTime.now().withNano(0));
    }

    public static String asOffsetDateTimeString(final OffsetDateTime timestamp) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(timestamp);
    }

    public static OffsetDateTime getTimeInPacificAuckland() {
        return OffsetDateTime.now().withOffsetSameInstant(ZoneId.of("Pacific/Auckland").getRules().getOffset(Instant.now()));
    }
}
