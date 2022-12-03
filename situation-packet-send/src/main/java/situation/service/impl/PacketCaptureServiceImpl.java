package situation.service.impl;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.situation.entity.PacketTcp;
import com.situation.entity.inter.LinuxEntity;
import com.situation.util.ConnectToServer;
import com.situation.util.IdWorker;
import com.situation.util.ParseTcpdumpStringToPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import situation.dao.Mapper.PacketCaptureMapper;
import com.situation.entity.PacketCapture;
import situation.dao.Mapper.PacketTcpMapper;
import situation.service.PacketCaptureService;

import java.io.*;
import java.util.Date;

/**
 * @author lgd
 * @date 2021/12/24 14:35
 */
@Service
public class PacketCaptureServiceImpl implements PacketCaptureService {

    @Autowired
    private PacketCaptureMapper packetCaptureMapper;

    @Autowired
    private PacketTcpMapper packetTcpMapper;

    @Autowired
    private IdWorker idWorker;

    //抓包
    @Override
    public void capturePacket(PacketCapture packetCapture) throws IOException {

        //先将记录存到数据库
        add(packetCapture);

        //远程操作
        ConnectToServer connectToServer=new ConnectToServer();

        Connection serverConnection = connectToServer.getConnectionByCustomParam(
                LinuxEntity.remoteServerIP,
                LinuxEntity.remoteServerUsername,
                LinuxEntity.remoteServerPassword,
                LinuxEntity.remoteServerPublicKey);

        //执行抓包脚本
        execCaptureScript(serverConnection,packetCapture,connectToServer);

        serverConnection.close();

    }

    /**
     * 添加 抓包记录  记录  记录
     * @param packetCapture
     */
    @Override
    public void add(PacketCapture packetCapture) {
        if (packetCapture.getId()==null)    packetCapture.setId(idWorker.nextId());
        packetCaptureMapper.insert(packetCapture);
    }

    /**
     * 执行脚本
     * @param serverConnection
     * @param packetCapture
     * @param connectToServer
     * @throws IOException
     */
    private void execCaptureScript(Connection serverConnection, PacketCapture packetCapture, ConnectToServer connectToServer) throws IOException {
        Date startTime = packetCapture.getStart();
        Date endTime = packetCapture.getEnd();
        int port = packetCapture.getPort();
        String protocol = packetCapture.getProtocol();


        StringBuilder command =new StringBuilder();
        //连接会话
        Session connectionSession = connectToServer.getConnectionSession(serverConnection);

        command.append("sh ").append(ConnectToServer.SCRIPT_LOCATION).append("capture").append(".sh ")
                .append(startTime.getTime()/1000).append(" ")  //将日期转换成秒
                .append(endTime.getTime()/1000).append(" ")
                .append(protocol).append(" ")
                .append(port);

        connectionSession.requestPTY("bash");
        //打开shell
        connectionSession.startShell();
        //准备命令
        PrintWriter writer=new PrintWriter(connectionSession.getStdin());
        //输入命令
        writer.println("让我看到你！"+command.toString());
//        writer.println("exit");
//        SFTPv3Client sftPv3Client = new SFTPv3Client(serverConnection);
        //关闭


        writer.close();
        //打印控制台的结果
        InputStream stdout = connectionSession.getStdout();
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(stdout));

        boolean isCapture=false;
        String one="";
        String two="";
        int count=0;
        while (true){

            String readLine = bufferedReader.readLine();
            if (readLine == null) break;
            if (readLine.startsWith("listening")) isCapture=true; //下面显示的都是实时的数据包信息
            if (readLine.endsWith("captured"))  isCapture=false;
            if (isCapture) {
//                System.out.println(readLine);  //输出终端的内容
                //解析成对应数据包
                if (protocol.equalsIgnoreCase("tcp")){
                    if (count==0){
                        one=readLine;
                        count++;
                    }else if (count==1){
                        two=readLine;
                        PacketTcp packetTcp = ParseTcpdumpStringToPacket.parsToTcpPacket(one, two);
                        System.out.println("---------"+packetTcp.toString());
                        count=0;
                    }

                }
            }
        }
        //没有执行到下一步
        System.out.println("hh");
        connectionSession.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS,20000);
        connectionSession.close();


    }

    //返回终端结果流
    private InputStream execCaptureScriptReturnResultStream(Connection serverConnection, PacketCapture packetCapture, ConnectToServer connectToServer) throws IOException {
        Date startTime = packetCapture.getStart();
        Date endTime = packetCapture.getEnd();
        int port = packetCapture.getPort();
        String protocol = packetCapture.getProtocol();


        StringBuilder command =new StringBuilder();
        //连接会话
        Session connectionSession = connectToServer.getConnectionSession(serverConnection);

        command.append("sh ").append(ConnectToServer.SCRIPT_LOCATION).append("capture").append(".sh ")
                .append(startTime.getTime()/1000).append(" ")  //将日期转换成秒
                .append(endTime.getTime()/1000).append(" ")
                .append(protocol).append(" ")
                .append(port);

        connectionSession.requestPTY("bash");
        //打开shell
        connectionSession.startShell();
        //准备命令
        PrintWriter writer=new PrintWriter(connectionSession.getStdin());
        //输入命令
        writer.println(command.toString());
//        writer.println("exit");
//        SFTPv3Client sftPv3Client = new SFTPv3Client(serverConnection);
        //关闭


        writer.close();
        //打印控制台的结果
        InputStream stdout = connectionSession.getStdout();
        if (stdout!=null)
            return stdout;
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(stdout));

        while (true){
            String readLine = bufferedReader.readLine();
            if (readLine==null) break;
            System.out.println(readLine);  //输出终端的内容
        }
        //没有执行到下一步
        System.out.println("hh");
        connectionSession.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS,20000);
        connectionSession.close();

        return null;
    }
}
