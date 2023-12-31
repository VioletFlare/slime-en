package com.github.nekolr.slime.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {

    /**
     * Format Date
     *
     * @param date Date
     * @return The date/ time format of the clock, or NULL to use the system default.
     */
    public static String format(Date date) {
        if (date == null) {
            return null;
        }
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss", TimeZone.getDefault());
    }

    /**
     * 获取之前的某个时间
     *
     * @param duration Duration
     * @return 之前的某个时间
     */
    public static Date getBeforeTime(Duration duration) {
        LocalDateTime beforeTime = LocalDateTime.now().minus(duration.toMillis(), ChronoUnit.MILLIS);
        return Date.from(beforeTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
