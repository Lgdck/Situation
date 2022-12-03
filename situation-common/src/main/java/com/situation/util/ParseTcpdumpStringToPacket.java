package com.situation.util;

import com.situation.entity.Packet;
import com.situation.entity.PacketTcp;

import java.util.StringTokenizer;

/**
 * @author lgd
 * @date 2021/12/27 22:14
 */
public class ParseTcpdumpStringToPacket {

    //第一行包含时间戳及ip数据包信息  第二行包含ip 及tcp报文等信息
    public static PacketTcp parsToTcpPacket(String str1,String str2){
        StringTokenizer stringTokenizer=new StringTokenizer(str1);
        StringBuilder sb_ipInfo=new StringBuilder();
        PacketTcp packetTcp=new PacketTcp();
        int count=0;
        while (stringTokenizer.hasMoreElements()){
            String s = stringTokenizer.nextToken();
            if (s.trim().length()==0)   continue;
            if (count==0){
                packetTcp.setTs(s);
                count++;
            }else if (count==1){
                sb_ipInfo.append(s).append(" ");
            }
        }
        packetTcp.setIpInfo(sb_ipInfo.toString());
        count=0;
        StringBuilder sb_tcpInfo=new StringBuilder();
        stringTokenizer=new StringTokenizer(str2);
        while (stringTokenizer.hasMoreElements()){
            String s = stringTokenizer.nextToken();
            if (s.trim().length()==0)   continue;
            if (count==0){
                packetTcp.setSrcIp(s);
                count++;
            }else if (count==1){
                if (s.startsWith(">"))  continue;
                else {
                    packetTcp.setDestIp(s.substring(0,s.length()-1));
                    count++;
                }
            }else {
                sb_tcpInfo.append(s).append(" ");
            }
        }
        packetTcp.setTcpInfo(sb_tcpInfo.toString());
        return packetTcp;
    }

}
