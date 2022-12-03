package com.situation.entity;

import java.util.Date;

/**
 * @author lgd
 * @date 2021/12/24 14:25
 */
public class CaptureEntity {

    private Date start;

    private Date end;

    private String port;

    private String protocol;

    public static final String fromat="yyyy-MM-dd hh:mm:ss";

//    public CaptureEntity(Date startTime, Date endTime, String port, String protocol) {
//
//        this.startTime = startTime;
//        this.endTime = endTime;
//        this.port = port;
//        this.protocol = protocol;
//    }


    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public static String getFromat() {
        return fromat;
    }
}
