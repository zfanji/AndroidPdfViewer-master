package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Zfanji on 2017/4/18.
 */

public class TimeUtil {
    // 返回2007-01-01到2007-03-01的一个随机日期
//    Date randomDate = randomDate("2007-01-01", "2007-03-01");
//    System.out.println(randomDate.toString());
    /**
     * 获取随机日期
     *
     * @param beginDate
     *            起始日期，格式为：yyyy-MM-dd
     * @param endDate
     *            结束日期，格式为：yyyy-MM-dd
     * @return
     */

    public static Date randomDate(String beginDate, String endDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(beginDate);// 构造开始日期
            Date end = format.parse(endDate);// 构造结束日期
            // getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = random(start.getTime(), end.getTime());

            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long random(long begin, long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        // 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }


    public static String randomDateStr(int count){
        String dateStr = "";
        while (count>0){
            Date randomDate = randomDate("2016-02-18", "2017-04-18");
            String xxx=randomDate.getTime()+" ";
            dateStr=dateStr+xxx;
            count--;
        }

        return dateStr;
    }

    public static boolean isSameDay(Date date, Date sameDate) {

        if (null == date || null == sameDate) {

            return false;

        }

        Calendar nowCalendar = Calendar.getInstance();

        nowCalendar.setTime(sameDate);

        Calendar dateCalendar = Calendar.getInstance();

        dateCalendar.setTime(date);

        if (nowCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)

                && nowCalendar.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH)

                && nowCalendar.get(Calendar.DATE) == dateCalendar.get(Calendar.DATE)) {

            return true;

        }

        return false;

    }
}
