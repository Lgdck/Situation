package com.situation.entity;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.Serializable;

/**
 * 前端传的结果封装
 * @author lgd
 * @date 2021/10/29 10:53
 */
public class TotalMessage implements Serializable {

    //包信息的封装
    private Packet packet;

    //执行命令
    private String command;



    public TotalMessage() {
    }



    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
