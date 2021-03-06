package com.easygo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 崔凯 on 2016/6/4.
 */
public class DaysUtil {
    //传入两个Date模样的字符串，返回之间相差的天数
    public static int getDays(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date mDate1 = null;
        Date mDate2 = null;
        try {
            mDate1 = sdf.parse(date1);
            mDate2 = sdf.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(mDate1);
        calendar2.setTime(mDate2);
        int days = getDaysBetween(calendar1, calendar2);
        return days;
    }

    public static int getDaysBetween(Calendar d1, Calendar d2) {
        if (d1.after(d2)) {
            java.util.Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }
        int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
        int y2 = d2.get(Calendar.YEAR);
        if (d1.get(Calendar.YEAR) != y2) {
            d1 = (Calendar) d1.clone();
            do {
                days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);//得到当年的实际天数
                d1.add(Calendar.YEAR, 1);
            } while (d1.get(Calendar.YEAR) != y2);
        }
        return days;
    }

    //未完成
    //判断某个日期是否在两个日期之间((date1,date2)时间段，date3)
    /*public static boolean isDayBetween(String date1,String date2,String date3) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date mDate1 = null;
        Date mDate2 = null;
        Date mDate3 = null;
        try {
            mDate1 = sdf.parse(date1);
            mDate2 = sdf.parse(date2);
            mDate3 = sdf.parse(date3);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        Calendar calendar3 = Calendar.getInstance();
        calendar1.setTime(mDate1);
        calendar2.setTime(mDate2);
        calendar3.setTime(mDate3);
        *//*if (d1.after(d2)){
            java.util.Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }*//*
        int days = calendar2.get(Calendar.DAY_OF_YEAR) - calendar1.get(Calendar.DAY_OF_YEAR);
        int y2 = calendar2.get(Calendar.YEAR);
        if (calendar1.get(Calendar.YEAR) != y2) {
            calendar1 = (Calendar) calendar1.clone();
            do {
                days += calendar1.getActualMaximum(Calendar.DAY_OF_YEAR);//得到当年的实际天数
                calendar1.add(Calendar.YEAR, 1);
            } while (calendar1.get(Calendar.YEAR) != y2);
        }
        return false;
    }*/
}
