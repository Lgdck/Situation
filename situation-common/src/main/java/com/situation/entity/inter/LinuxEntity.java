package com.situation.entity.inter;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

/**
 * @author lgd
 * @date 2021/12/25 9:44
 */
public class LinuxEntity {
    public static final String remoteServerIP="212.129.237.80";
    public static final String localServerIP="192.168.211.128";
    public static final String remoteServerUsername="root";
    public static final String remoteServerPassword="root";
    public static final String localServerUsername="root";
    public static final String localServerPassword="root";
    public static File remoteServerPublicKey ;

    static {
        try {
            remoteServerPublicKey = new ClassPathResource("id_rsa").getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }







}
