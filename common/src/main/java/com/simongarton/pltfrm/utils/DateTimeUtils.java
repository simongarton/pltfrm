package com.simongarton.pltfrm.utils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static final String PACIFIC_AUCKLAND = "Pacific/Auckland";

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

    public static OffsetDateTime inPacificAuckland(final OffsetDateTime timestamp) {
        return timestamp.withOffsetSameInstant(ZoneId.of(PACIFIC_AUCKLAND).getRules().getOffset(Instant.now()));
    }
}
