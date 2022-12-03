package com.situation.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DataUtil {

    public static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static Timestamp Format(String s){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(s.replaceAll("T", " ") , df);
        Date date = Date.from( localDateTime.atZone( ZoneId.systemDefault()).toInstant());
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp;
    }

    public static String convertDate(Date date){
        return simpleDateFormat.format(date);
    }


}
