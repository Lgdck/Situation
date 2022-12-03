package com;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionInfo;
import ch.ethz.ssh2.Session;
import com.alibaba.fastjson.JSON;
import com.situation.entity.CaptureEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class test {

    @Test
    public void testRead() throws IOException {
        InputStream stream = new ClassPathResource("id_rsa").getInputStream();
        FileOutputStream outputStream=new FileOutputStream("D:\\IDEA\\My_project\\5g\\situation-parent\\situation-common\\src\\main\\resources\\static\\a.txt");
        byte[]buffer=new byte[stream.available()];
        while (stream.read(buffer)!=-1){
            outputStream.write(buffer);
        }
        outputStream.flush();
        outputStream.close();
        stream.close();
    }

    @Test
    public void test() throws IOException {
        Connection connection = new Connection("49.235.199.128");
        connection.connect();
        //准备私钥
        File file = new ClassPathResource("id_rsa").getFile();
        //私钥ssh授权
        boolean isconnect = connection.authenticateWithPublicKey("root", file, "wawss1314..");
//        boolean isconnect=connection.authenticateWithPassword("root","wawss1314..");
        if (isconnect){
//            System.out.println("hhh");
            //执行命令
            String command="sh /usr/local/customsh/tcp.sh";
            //打开session
            Session session = connection.openSession();
            //建立虚拟终端
            session.requestPTY("bash");
            //打开一个shell
            session.startShell();
            //准备输入命令
            PrintWriter out=new PrintWriter(session.getStdin());
            //输入待执行命令
            out.println(command);
            out.println("exit");
            //关闭
            out.close();
            //等待，除非1.连接关闭；2.输出数据传送完毕；3.进程状态为退出；4.超时
            session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS,20000);
            session.close();

            connection.close();

        }else {
            throw new RuntimeException("用户名或密码错误");
        }

//        System.out.println(connect.keyExchangeCounter);


    }

    /**
     * 远程打开脚本
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        Connection connection = new Connection("192.168.211.128");
        connection.connect();
        //私钥ssh授权
//        boolean isconnect = connection.authenticateWithPublicKey("root", file, "wawss1314..");
        boolean isconnect=connection.authenticateWithPassword("root","root");
        if (isconnect){
            //执行命令
//            String command="sendip -v -p ipv4  -p udp -ud 12312 49.235.199.128";
            //打开session
            Session session = connection.openSession();

            session.execCommand("/usr/local/customsh/udp.sh 10 1  212.129.237.80 12312");

            //等待，除非1.连接关闭；2.输出数据传送完毕；3.进程状态为退出；4.超时
            session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS,20000);

            Integer res = session.getExitStatus();
            System.out.println(res);
            session.close();

            connection.close();

        }else {
            throw new RuntimeException("用户名或密码错误");
        }

//        System.out.println(connect.keyExchangeCounter);


    }
    @Test
    public void test3(){
        Date date=new Date();
        System.out.println(date.getTime()/1000);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(CaptureEntity.fromat);

        String format = simpleDateFormat.format(date);
        long second=System.currentTimeMillis()/ 1000;

//        simpleDateFormat.
    }

}
