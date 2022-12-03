package com.situation.entity;

import com.situation.entity.inter.DefaultPacketParam;

import java.io.Serializable;

/**
 * 数据包载体
 * @author lgd
 * @date 2021/10/27 21:04
 */
public class Packet implements Serializable {


    private String destip=DefaultPacketParam.PTP_IP;

    private String ptpip=DefaultPacketParam.PTP_IP;

    private String ptpport=DefaultPacketParam.PTP_PORT;

    private String protocol= DefaultPacketParam.PROTOCOL;

    private String sourceport;

    private String frequenceend=DefaultPacketParam.FREQUENT;

    private String frequencesyn=DefaultPacketParam.FREQUENT;

    private String size=DefaultPacketParam.SIZE;

    private String destport =DefaultPacketParam.DEST_PORT;

    private String frequence=DefaultPacketParam.FREQUENT;

    private String message=DefaultPacketParam.MESSAGE;

    private String count=DefaultPacketParam.COUNT;


    public String getPtpip() {
        return ptpip;
    }

    public void setPtpip(String ptpip) {
        this.ptpip = ptpip;
    }

    public String getPtpport() {
        return ptpport;
    }

    public void setPtpport(String ptpport) {
        this.ptpport = ptpport;
    }

    public String getFrequenceend() {
        return frequenceend;
    }

    public void setFrequenceend(String frequenceend) {
        this.frequenceend = frequenceend;
    }

    public String getFrequencesyn() {
        return frequencesyn;
    }

    public void setFrequencesyn(String frequencesyn) {
        this.frequencesyn = frequencesyn;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDestip() {
        return destip;
    }

    public void setDestip(String destip) {
        this.destip = destip;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getSourceport() {
        return sourceport;
    }

    public void setSourceport(String sourceport) {
        this.sourceport = sourceport;
    }

    public String getDestport() {
        return destport;
    }

    public void setDestport(String destport) {
        this.destport = destport;
    }

    public String getFrequence() {
        return frequence;
    }

    public void setFrequence(String frequence) {
        this.frequence = frequence;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
