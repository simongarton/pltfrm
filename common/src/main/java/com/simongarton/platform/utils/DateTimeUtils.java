package com.simongarton.platform.utils;

import java.time.Instant;
import java.time.OffsetDateTime;

public class DateTimeUtils {

    public static OffsetDateTime longToOffsetDateTime(final long epoch) {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(epoch), OffsetDateTime.now().getOffset());
    }

    public static OffsetDateTime offsetDateTimeNowToSeconds() {
        return OffsetDateTime.now().withNano(0);
    }

}
