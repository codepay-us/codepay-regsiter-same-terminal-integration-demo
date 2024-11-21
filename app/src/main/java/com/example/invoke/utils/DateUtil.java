package com.example.invoke.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtil {
    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public DateUtil() {
    }

    public static Date str2Date(String str) {
        return str2Date(str, (String) null);
    }

    public static Date str2Date(String str, String format) {
        if (str != null && str.length() != 0) {
            if (format == null || format.length() == 0) {
                format = "yyyy-MM-dd HH:mm:ss";
            }

            Date date = null;

            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                date = sdf.parse(str);
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            return date;
        } else {
            return null;
        }
    }

    public static Calendar str2Calendar(String str) {
        return str2Calendar(str, (String) null);
    }

    public static Calendar str2Calendar(String str, String format) {
        Date date = str2Date(str, format);
        if (date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            return c;
        }
    }

    public static String date2Str(Calendar c) {
        return date2Str((Calendar) c, (String) null);
    }

    public static String date2Str(Calendar c, String format) {
        return c == null ? null : date2Str(c.getTime(), format);
    }

    public static String date2Str(Date d) {
        return date2Str((Date) d, (String) null);
    }

    public static String date2Str(Date d, String format) {
        if (d == null) {
            return null;
        } else {
            if (format == null || format.length() == 0) {
                format = "yyyy-MM-dd HH:mm:ss";
            }

            SimpleDateFormat sdf = new SimpleDateFormat(format);
            String s = sdf.format(d);
            return s;
        }
    }

    public static String getCurDateStr() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.get(1) + "-" + (c.get(2) + 1) + "-" + c.get(5) + " " + c.get(11) + ":" + c.get(12) + ":" + c.get(13);
    }

    public static String getCurDateStr(String format) {
        Calendar c = Calendar.getInstance();
        return date2Str(c, format);
    }

    public static String getMillon(long time) {
        return (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")).format(time);
    }

    public static String getDay(long time) {
        return (new SimpleDateFormat("yyyy-MM-dd")).format(time);
    }

    public static String getSMillon(long time) {
        return (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS")).format(time);
    }

    public static Date StringToDate(String dateStr, String formatStr) {
        DateFormat sdf = new SimpleDateFormat(formatStr);
        Date date = null;

        try {
            date = sdf.parse(dateStr);
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return date;
    }

    public static String converTime(long timestamp) {
        long currentSeconds = System.currentTimeMillis() / 1000L;
        long timeGap = currentSeconds - timestamp;
        String timeStr = null;
        if (timeGap > 86400L) {
            timeStr = timeGap / 86400L + "天前";
        } else if (timeGap > 3600L) {
            timeStr = timeGap / 3600L + "小时前";
        } else if (timeGap > 60L) {
            timeStr = timeGap / 60L + "分钟前";
        } else {
            timeStr = "刚刚";
        }

        return timeStr;
    }

    public static String getStandardTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        Date date = new Date(timestamp * 1000L);
        sdf.format(date);
        return sdf.format(date);
    }

    public static String currentDatetime() {
        return datetimeFormat.format(now());
    }

    public static String formatDatetime(Date date) {
        return datetimeFormat.format(date);
    }

    public static String currentTime() {
        return timeFormat.format(now());
    }

    public static String formatTime(Date date) {
        return timeFormat.format(date);
    }

    public static Date now() {
        return new Date();
    }

    public static Calendar calendar() {
        Calendar cal = GregorianCalendar.getInstance(Locale.CHINESE);
        cal.setFirstDayOfWeek(2);
        return cal;
    }

    public static long millis() {
        return System.currentTimeMillis();
    }

    public static int month() {
        return calendar().get(2) + 1;
    }

    public static int dayOfMonth() {
        return calendar().get(5);
    }

    public static int dayOfWeek() {
        return calendar().get(7);
    }

    public static int dayOfYear() {
        return calendar().get(6);
    }

    public static boolean isBefore(Date src, Date dst) {
        return src.before(dst);
    }

    public static boolean isAfter(Date src, Date dst) {
        return src.after(dst);
    }

    public static boolean isEqual(Date date1, Date date2) {
        return date1.compareTo(date2) == 0;
    }

    public static boolean between(Date beginDate, Date endDate, Date src) {
        return beginDate.before(src) && endDate.after(src);
    }

    public static Date lastDayOfMonth() {
        Calendar cal = calendar();
        cal.set(5, 0);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        cal.set(2, cal.get(2) + 1);
        cal.set(14, -1);
        return cal.getTime();
    }

    public static Date firstDayOfMonth() {
        Calendar cal = calendar();
        cal.set(5, 1);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTime();
    }

    private static Date weekDay(int week) {
        Calendar cal = calendar();
        cal.set(7, week);
        return cal.getTime();
    }

    public static Date friday() {
        return weekDay(6);
    }

    public static Date saturday() {
        return weekDay(7);
    }

    public static Date sunday() {
        return weekDay(1);
    }

    public static Date parseDatetime(String datetime) throws ParseException {
        return datetimeFormat.parse(datetime);
    }

    public static Date parseDate(String date) throws ParseException {
        return dateFormat.parse(date);
    }

    public static Date parseTime(String time) throws ParseException {
        return timeFormat.parse(time);
    }

    public static Date parseDatetime(String datetime, String pattern) throws ParseException {
        SimpleDateFormat format = (SimpleDateFormat) datetimeFormat.clone();
        format.applyPattern(pattern);
        return format.parse(datetime);
    }

    public static String parseSecond(int second) {
        if (second >= 60) {
            return second / 60 + "分";
        } else if (second >= 3600) {
            return second / 60 * 60 + "时";
        } else {
            return second >= 86400 ? second / 60 * 60 + "天" : second + "秒";
        }
    }

    public static int compareDate(String begin, String end) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        try {
            Date beginDate = df.parse(begin);
            Date endDate = df.parse(end);
            if (beginDate.getTime() < endDate.getTime()) {
                return 1;
            } else {
                return beginDate.getTime() > endDate.getTime() ? -1 : 0;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取当前年
     *
     * @return
     */
    public static String getYear() {
        DateFormat format = new SimpleDateFormat("yyyy");
        return format.format(new Date());
    }

    /**
     * 获取当前月
     *
     * @return
     */
    public static String getMonth() {
        DateFormat format = new SimpleDateFormat("MM");
        return format.format(new Date());
    }

    /**
     * 获取今天几号
     *
     * @return
     */
    public static String getToday() {
        DateFormat format = new SimpleDateFormat("dd");
        return format.format(new Date());
    }

    /**
     * 设置日期
     */
    public static Date setDate(Date date, int year, int month, int day) {
        date.setYear(year - 1900);
        date.setMonth(month);
        date.setDate(day);
        return date;
    }

    /**
     * 设置时间
     */
    public static Date setTime(Date date, int hour, int minute, int second) {
        date.setHours(hour);
        date.setMinutes(minute);
        date.setSeconds(second);
        return date;
    }

    /**
     * 前一天
     */
    public static Date getPreviousDay(Date date) {
        Date previous = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        previous = calendar.getTime();
        return previous;
    }

    /**
     * 后一天
     */
    public static Date getNextDay(Date date) {
        Date next = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        next = calendar.getTime();
        return next;
    }

    public int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(1);
    }

    public int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(2) + 1;
    }

    public int getWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(3);
    }

    public int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(5);
    }

    public long getDayDiff(Date begin, Date end) {
        long day = 1L;
        if (end.getTime() < begin.getTime()) {
            day = -1L;
        } else if (end.getTime() == begin.getTime()) {
            day = 1L;
        } else {
            day += (end.getTime() - begin.getTime()) / 86400000L;
        }

        return day;
    }

}
