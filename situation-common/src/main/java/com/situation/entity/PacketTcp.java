package com.situation.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author lgd
 * @date 2021/12/27 20:51
 */
@Table(name = "packet_tcp")
public class PacketTcp implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "src_ip")
    private String srcIp;

    @Column(name = "dest_ip")
    private String destIp;

    @Column(name = "ts")
    private String ts;

    @Column(name = "ip_info")
    private String ipInfo;

    @Column(name = "tcp_info")
    private String tcpInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getIpInfo() {
        return ipInfo;
    }

    public void setIpInfo(String ipInfo) {
        this.ipInfo = ipInfo;
    }

    public String getTcpInfo() {
        return tcpInfo;
    }

    public void setTcpInfo(String tcpInfo) {
        this.tcpInfo = tcpInfo;
    }

    @Override
    public String toString() {
        return "PacketTcp{" +
                "id=" + id +
                ", srcIp='" + srcIp + '\'' +
                ", destIp='" + destIp + '\'' +
                ", ts='" + ts + '\'' +
                ", ipInfo='" + ipInfo + '\'' +
                ", tcpInfo='" + tcpInfo + '\'' +
                '}';
    }
}
