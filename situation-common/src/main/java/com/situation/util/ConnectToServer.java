package com.situation.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.situation.entity.inter.DefaultServerParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * 获取liux连接工具类
 * @author lgd
 * @date 2021/10/29 9:53
 */
public class ConnectToServer implements Serializable {


    private String ip= DefaultServerParam.IP_VM;

    private String username=DefaultServerParam.USERNAME;

    private String password=DefaultServerParam.PASSWORD;

    //服务器脚本存放位置
    public static final  String SCRIPT_LOCATION="/usr/local/customsh/";

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static String getScriptLocation() {
        return SCRIPT_LOCATION;
    }

    public ConnectToServer(String ip, String username, String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
    }

    public ConnectToServer() {
    }

    public Connection getConnectionByCustomParam(String ip,String username,String password,File publicKey) throws IOException {
        Connection connection=new Connection(ip);
        connection.connect();

        //授权
        if (publicKey!=null){
            boolean isconnect = connection.authenticateWithPublicKey(username, publicKey, password);
            if (isconnect){
                return connection;
            }else {
                throw new RuntimeException("连接失败  私钥错误 or用户名密码无效");
            }
        }else {
            boolean isconnect = connection.authenticateWithPassword(username, password);
            if (isconnect){
                return connection;
            }else {
                throw new RuntimeException("连接失败  用户名密码无效" );
            }
        }
    }
    /**
     * 获取linux 连接  账号密码
     * @return
     * @throws IOException
     */

    public  Connection getConnectionWithPassword() throws IOException {
        Connection connection=new Connection(ip);
        connection.connect();

        //授权
        boolean isconnect = connection.authenticateWithPassword(username, password);
        if (isconnect){
            return connection;
        }else {
            throw new RuntimeException("连接失败 用户名or密码无效");
        }
    }

    /**
     * 获取linux 连接 私钥模式
     * @param publickey
     * @return
     * @throws IOException
     */
    public  Connection getConnectionWithPublickey(File publickey) throws IOException {
        Connection connection=new Connection(ip);
        connection.connect();

        boolean isconnect = connection.authenticateWithPublicKey(username, publickey, password);

        if (isconnect){
            return connection;
        }else {
            throw new RuntimeException("连接失败 用户名or密码or公钥无效");
        }

    }

    /**
     * 创建linux终端
     * @param connection
     * @return
     * @throws IOException
     */

    public Session getConnectionSessionWithShell(Connection connection) throws IOException {
        if (connection==null){
            throw new RuntimeException("连接无效");
        }

        Session session = connection.openSession();
        //shell类型
        session.requestPTY("bash");
        //打开终端
        session.startShell();

        return session;
    }

    /**
     * 创建会话
     * @param connection
     * @return
     * @throws IOException
     */
    public Session getConnectionSession(Connection connection) throws IOException {
        if (connection==null){
            throw new RuntimeException("连接无效");
        }

        Session session = connection.openSession();
        return session;
    }
}
