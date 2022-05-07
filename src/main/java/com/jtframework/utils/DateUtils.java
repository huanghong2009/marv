package com.jtframework.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2018/8/27
 */
public class DateUtils {
    //获取当天的开始时间
    public static Date getDayBegin() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    //获取当天的结束时间
    public static Date getDayEnd() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }


    //获取昨天的开始时间
    public static Date getBeginDayOfYesterday() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayBegin());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    //获取昨天的结束时间
    public static Date getEndDayOfYesterDay() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayEnd());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    //获取明天的开始时间
    public static Date getBeginDayOfTomorrow() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayBegin());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    //获取明天的结束时间
    public static Date getEndDayOfTomorrow() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayEnd());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    //两个日期相减得到的毫秒数
    public static long getDiffSecond(Date beginDate, Date endDate) {
        long date1ms = beginDate.getTime();
        long date2ms = endDate.getTime();
        return (date2ms - date1ms) / 1000;
    }


    //获取本周的开始时间
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayStartTime(cal.getTime());
    }

    //获取本周的结束时间
    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    //获取本月的开始时间
    public static Date getBeginDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        return getDayStartTime(calendar.getTime());
    }

    //获取本月的结束时间
    public static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    //获取本年的开始时间
    public static Date getBeginDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);
        return getDayStartTime(cal.getTime());
    }

    //获取本年的结束时间
    public static Date getEndDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 31);
        return getDayEndTime(cal.getTime());
    }

    //获取某个日期的开始时间
    public static Date getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Date(calendar.getTimeInMillis());
    }

    //获取某个日期的结束时间
    public static Date getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Date(calendar.getTimeInMillis());
    }

    //获取今年是哪一年
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    //获取本月是哪一月
    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

    //两个日期相减得到的天数
    public static int getDiffDays(Date beginDate, Date endDate) {
        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("getDiffDays param is null!");
        }
        long diff = (endDate.getTime() - beginDate.getTime()) / (1000 * 60 * 60 * 24);
        int days = new Long(diff).intValue();
        return days;
    }

    //两个日期相减得到的毫秒数
    public static long dateDiff(Date beginDate, Date endDate) {
        long date1ms = beginDate.getTime();
        long date2ms = endDate.getTime();
        return date2ms - date1ms;
    }


    //获取两个日期中的最大日期
    public static Date max(Date beginDate, Date endDate) {
        if (beginDate == null) {
            return endDate;
        }
        if (endDate == null) {
            return beginDate;
        }
        if (beginDate.after(endDate)) {
            return beginDate;
        }
        return endDate;
    }

    //获取两个日期中的最小日期
    public static Date min(Date beginDate, Date endDate) {
        if (beginDate == null) {
            return endDate;
        }
        if (endDate == null) {
            return beginDate;
        }
        if (beginDate.after(endDate)) {
            return endDate;
        }
        return beginDate;
    }

    //返回某月该季度的第一个月
    public static Date getFirstSeasonDate(Date date) {
        final int[] SEASON = {1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int sean = SEASON[cal.get(Calendar.MONTH)];
        cal.set(Calendar.MONTH, sean * 3 - 3);
        return cal.getTime();
    }

    //返回某个日期下几天的日期
    public static Date getNextDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + i);
        return cal.getTime();
    }

    //返回某个日期前几天的日期
    public static Date getFrontDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - i);
        return cal.getTime();
    }

    //获取某年某月到某年某月按天的切片日期集合（间隔天数的日期集合）
    public static List getTimeList(int beginYear, int beginMonth, int endYear, int endMonth, int k) {
        List list = new ArrayList();
        if (beginYear == endYear) {
            for (int j = beginMonth; j <= endMonth; j++) {
                list.add(getTimeList(beginYear, j, k));
            }
        } else {
            for (int j = beginMonth; j < 12; j++) {
                list.add(getTimeList(beginYear, j, k));
            }
            for (int i = beginYear + 1; i < endYear; i++) {
                for (int j = 0; j < 12; j++) {
                    list.add(getTimeList(i, j, k));
                }
            }
            for (int j = 0; j <= endMonth; j++) {
                list.add(getTimeList(endYear, j, k));
            }
        }
        return list;
    }

    //获取某年某月按天切片日期集合（某个月间隔多少天的日期集合）
    public static List getTimeList(int beginYear, int beginMonth, int k) {
        List list = new ArrayList();
        Calendar begincal = new GregorianCalendar(beginYear, beginMonth, 1);
        int max = begincal.getActualMaximum(Calendar.DATE);
        for (int i = 1; i < max; i = i + k) {
            list.add(begincal.getTime());
            begincal.add(Calendar.DATE, k);
        }
        begincal = new GregorianCalendar(beginYear, beginMonth, max);
        list.add(begincal.getTime());
        return list;
    }

    //获取某年某月的第一天日期
    public static Date getStartMonthDate(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getTime();
    }

    //获取某年某月的最后一天日期
    public static Date getEndMonthDate(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(year, month - 1, day);
        return calendar.getTime();
    }


    public static String getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0L;

        try {
            Date date = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / 86400000L;
        } catch (Exception var7) {
            return "";
        }

        return day + "";
    }

    /**
     * 获取两个日期相减的秒数
     * @param dateStartStr
     * @param dateEndStr
     * @return
     */
    public static long getDiffSecond(String dateStartStr, String dateEndStr) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long num = 0L;

        try {
            Date dateStart = myFormatter.parse(dateStartStr);
            Date dateEnd = myFormatter.parse(dateEndStr);
            num =  dateEnd.getTime() - dateStart.getTime();
            return num /1000 ;
        } catch (Exception var7) {
            return 0L;
        }
    }

    public static String getMonthDays(String str) {
        String nm = formatDate(addMonth(strToDate(str)), "yyyy-MM-dd");
        return getTwoDay(nm, str);
    }

    public static String formatDate(Date date, String format) {
        if (date == null) {
            date = new Date();
        }

        if (format == null || "".equalsIgnoreCase(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static String getWeek(String sdate) {
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return (new SimpleDateFormat("EEEE")).format(c.getTime());
    }

    public static String getWeekNow() {
        Map<String, String> map = new HashMap();
        map.put("星期一", "1");
        map.put("星期二", "2");
        map.put("星期三", "3");
        map.put("星期四", "4");
        map.put("星期五", "5");
        map.put("星期六", "6");
        map.put("星期日", "7");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String week = sdf.format(new Date());
        return (String) map.get(week);
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }


    public static Date strToDate(String strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static Date addMonth(Date date) {
        if (date == null) {
            date = new Date();
        }

        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        cd.add(2, 1);
        return cd.getTime();
    }

    public static long getDays(String date1, String date2) {
        if (date1 != null && !date1.equals("")) {
            if (date2 != null && !date2.equals("")) {
                SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                Date mydate = null;

                try {
                    date = myFormatter.parse(date1);
                    mydate = myFormatter.parse(date2);
                } catch (Exception var7) {
                    ;
                }

                long day = (date.getTime() - mydate.getTime()) / 86400000L;
                return day;
            } else {
                return 0L;
            }
        } else {
            return 0L;
        }
    }

    public static String getAddDay(String date, int i) {
        Date tempDate = strToDate(date);
        Calendar cd = Calendar.getInstance();
        cd.setTime(tempDate);
        cd.add(5, i);
        Date tempDate1 = cd.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(tempDate1);
    }

    public static String getDefaultDay() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(5, 1);
        lastDate.add(2, 1);
        lastDate.add(5, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public String getPreviousMonthFirst() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(5, 1);
        lastDate.add(2, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public String getFirstDayOfMonth() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(5, 1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public String getCurrentWeekday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    public static String getCurrentMonday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    public static String getNextWeekday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus + 6 + 1);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = df1.format(monday);
        return preMonday;
    }

    public static String getNowTime(String dateformat) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);
        String hehe = dateFormat.format(now);
        return hehe;
    }

    private static int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        int dayOfWeek = cd.get(7) - 1;
        return dayOfWeek == 1 ? 0 : 1 - dayOfWeek;
    }

    public static String getMondayOFWeek() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    public static String getNextMonday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus + 7);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    public static String getNextSunday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus + 7 + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    public static String getPreviousMonthEnd() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(2, -1);
        lastDate.set(5, 1);
        lastDate.roll(5, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public static String getNextMonthFirst() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(2, 1);
        lastDate.set(5, 1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public static String getNextMonthEnd() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(2, 1);
        lastDate.set(5, 1);
        lastDate.roll(5, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public static String getNextYearEnd() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(1, 1);
        lastDate.set(6, 1);
        lastDate.roll(6, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public static String getNextYearFirst() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(1, 1);
        lastDate.set(6, 1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    private static int getMaxYear() {
        Calendar cd = Calendar.getInstance();
        cd.set(6, 1);
        cd.roll(6, -1);
        int MaxYear = cd.get(6);
        return MaxYear;
    }

    private static int getYearPlus() {
        Calendar cd = Calendar.getInstance();
        int yearOfNumber = cd.get(6);
        cd.set(6, 1);
        cd.roll(6, -1);
        int MaxYear = cd.get(6);
        return yearOfNumber == 1 ? -MaxYear : 1 - yearOfNumber;
    }

    public static String getCurrentYearFirst() {
        int yearPlus = getYearPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, yearPlus);
        Date yearDay = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preYearDay = df.format(yearDay);
        return preYearDay;
    }

    public static String getCurrentYearEnd() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String years = dateFormat.format(date);
        return years + "-12-31";
    }

    public static String getPreviousYearFirst() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String years = dateFormat.format(date);
        int years_value = Integer.parseInt(years);
        --years_value;
        return years_value + "-1-1";
    }

    public static String getThisSeasonTime(int month) {
        int[][] array = new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};
        int season = 1;
        if (month >= 1 && month <= 3) {
            season = 1;
        }

        if (month >= 4 && month <= 6) {
            season = 2;
        }

        if (month >= 7 && month <= 9) {
            season = 3;
        }

        if (month >= 10 && month <= 12) {
            season = 4;
        }

        int start_month = array[season - 1][0];
        int end_month = array[season - 1][2];
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String years = dateFormat.format(date);
        int years_value = Integer.parseInt(years);
        int start_days = 1;
        int end_days = getLastDayOfMonth(years_value, end_month);
        String seasonDate = years_value + "-" + start_month + "-" + start_days + ";" + years_value + "-" + end_month + "-" + end_days;
        return seasonDate;
    }

    private static int getLastDayOfMonth(int year, int month) {
        if (month != 1 && month != 3 && month != 5 && month != 7 && month != 8 && month != 10 && month != 12) {
            if (month != 4 && month != 6 && month != 9 && month != 11) {
                if (month == 2) {
                    return isLeapYear(year) ? 29 : 28;
                } else {
                    return 0;
                }
            } else {
                return 30;
            }
        } else {
            return 31;
        }
    }

    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    public static boolean compareMonth(int year1, int month1, int year, int month) {
        if (year1 > year) {
            return true;
        } else {
            return year1 == year && month1 >= month;
        }
    }

    public static boolean compareDate(String d1, String d2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date1 = df.parse(d1);
            Date date2 = df.parse(d2);
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(date1);
            c2.setTime(date2);
            return c2.before(c1);
        } catch (ParseException var7) {
            return false;
        }
    }

    public static String decreaseMonth(String d1) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date1 = df.parse(d1);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date1);
            c1.add(2, -1);
            return df.format(c1.getTime());
        } catch (ParseException var4) {
            return "";
        }
    }


    /**
     * 指定时间 + 天
     *
     * @param date
     * @param day
     * @return
     */
    public static Date addDay(Date date, int day) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        c1.add(Calendar.DATE, day);
        return c1.getTime();
    }

    /**
     * 现在 + 天
     *
     * @param day
     * @return
     */
    public static Date addDay(int day) {
        return addDay(new Date(), day);
    }

    public static String addDay(String d1, int day) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date1 = df.parse(d1);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date1);
            c1.add(Calendar.DATE, day);
            return df.format(c1.getTime());
        } catch (ParseException var5) {
            return "";
        }
    }

    public static String addHour(String d1, int hour) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date1 = df.parse(d1);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date1);
            c1.add(11, hour);
            return df.format(c1.getTime());
        } catch (ParseException var5) {
            return "";
        }
    }

    public static String addMin(String d1, int min) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date1 = df.parse(d1);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date1);
            c1.add(12, min);
            return df.format(c1.getTime());
        } catch (ParseException var5) {
            return "";
        }
    }

    public static List getDateList(int year, int month) {
        List list = new ArrayList();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String strDate = df.format(date);
        String thisDate = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(26);
        String thisDate1 = decreaseMonth(thisDate);
        String thisDate2 = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(25);
        if (compareDate(thisDate1, strDate)) {
            return null;
        } else {
            long days;
            int i;
            if (compareDate(strDate, thisDate2)) {
                days = getDays(thisDate2, thisDate1);

                for (i = 0; (long) i < days - 1L; ++i) {
                    list.add(addDay(thisDate1, i));
                }
            } else if (compareDate(strDate, thisDate1)) {
                days = getDays(strDate, thisDate1);

                for (i = 0; (long) i < days; ++i) {
                    list.add(addDay(thisDate1, i));
                }
            }

            return list;
        }
    }

    public static List getDateListInDate(List list, String sdate) {
        if (list != null && list.size() != 0) {
            List upList = new ArrayList();
            List downList = new ArrayList();
            List aList = new ArrayList();
            String tstr = sdate;

            while (true) {
                tstr = getSubDateUp(list, tstr);
                if ("".equalsIgnoreCase(tstr)) {
                    tstr = sdate;

                    while (true) {
                        tstr = getSubDateDown(list, tstr);
                        if ("".equalsIgnoreCase(tstr)) {
                            int i;
                            String tempStr;
                            for (i = upList.size() - 1; i >= 0; --i) {
                                tempStr = (String) upList.get(i);
                                aList.add(tempStr);
                            }

                            aList.add(sdate);

                            for (i = 0; i < downList.size(); ++i) {
                                tempStr = (String) downList.get(i);
                                aList.add(tempStr);
                            }

                            return aList;
                        }

                        downList.add(tstr);
                    }
                }

                upList.add(tstr);
            }
        } else {
            return null;
        }
    }

    public static String getSubDateDown(List list, String sdate) {
        if (list != null && list.size() != 0) {
            String sdate1 = addDay(sdate, 1);

            for (int i = 0; i < list.size(); ++i) {
                String tempStr = (String) list.get(i);
                if (sdate1.equalsIgnoreCase(tempStr)) {
                    return tempStr;
                }
            }

            return "";
        } else {
            return "";
        }
    }

    public static String getSubDateUp(List list, String sdate) {
        if (list != null && list.size() != 0) {
            String sdate1 = addDay(sdate, -1);

            for (int i = 0; i < list.size(); ++i) {
                String tempStr = (String) list.get(i);
                if (sdate1.equalsIgnoreCase(tempStr)) {
                    return tempStr;
                }
            }

            return "";
        } else {
            return "";
        }
    }

    public static List getWeekInYear(String year) {
        List list = new ArrayList();
        int iyear = Integer.parseInt(year);
        int[] days = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (iyear % 4 == 0 && iyear % 100 != 0 || iyear % 400 == 0) {
            days[2] = 29;
        }

        for (int i = 1; i < days.length; ++i) {
            for (int j = 1; j <= days[i]; ++j) {
                Date date = new Date(iyear - 1900, i - 1, j);
                int week = date.getDay();
                if (week == 0 || week == 6) {
                    list.add(formatDate(date, "yyyy-MM-dd"));
                }
            }
        }

        return list;
    }

    public static int getMaxDay(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(1, year);
        cal.set(2, month - 1);
        int maxDate = cal.getActualMaximum(5);
        return maxDate;
    }

    /**
     * 获得今天剩余秒数
     *
     * @return
     */
    public static int getToDayLastSeconds() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        // 得到今天 晚上的最后一刻 最后时间
        String last = sdf2.format(new Date()) + " 23:59:59";

        try {
            // 转换为今天
            Date latDate = sdf.parse(last);
            // 得到的毫秒 除以1000转换 为秒
            return (int) (latDate.getTime() - System.currentTimeMillis()) / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new Exception("获取剩余秒数异常");
        }
    }

    /**
     * 获得当天是周几
     */
    public static String getWeekDay() {
        String[] weekDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }


    /**
     * 日期转cron表达式
     *
     * @param time
     * @return
     */
    public static String getCron(String time) throws ParseException {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatTimeStr = null;
        if (time != null) {
            try {
                formatTimeStr = sdf.format(sdf2.parse(time));
            } catch (ParseException e) {
                throw e;
            }
        }
        return formatTimeStr;
    }
}
