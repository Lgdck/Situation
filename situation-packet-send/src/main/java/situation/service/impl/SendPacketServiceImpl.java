package situation.service.impl;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.situation.entity.Packet;
import com.situation.util.ConnectToServer;
import org.junit.Test;
import org.springframework.stereotype.Service;
import situation.service.SendPacketService;

import java.io.*;
import java.net.Socket;

/**
 * 发送数据包
 * @author lgd
 * @date 2021/10/29 11:16
 */
@Service
public class SendPacketServiceImpl implements SendPacketService {


    private static Socket socket;
    /**
     * 数据包发送  拼接命令(鉴于sendip的劣势 这里只能发udp包)
     * @param packet
     * @throws IOException
     */
    @Override
    public void sendPacket(Packet packet) throws IOException {
        //自定义linux连接对象  这里是写死的linux地址 以后可改成动态的
        ConnectToServer connectToServer=new ConnectToServer();  //参数自己的linux ip  账号 密码  这里测试用的自己的虚拟机

        //获取连接
        Connection connection = connectToServer.getConnectionWithPassword();
        //打开终端
        Session session = connectToServer.getConnectionSessionWithShell(connection);

        //拼接要传的linux命令
        String command="sendip -v -p ipv4 -p "+packet.getProtocol()+" -ud "+packet.getDestport() +" "+packet.getDestip();

        //准备命令
        PrintWriter writer = new PrintWriter(session.getStdin());
        //执行命令
        writer.println(command);
        writer.println("exit");
        writer.close();

        //等待，除非1.连接关闭；2.输出数据传送完毕；3.进程状态为退出；4.超时
        session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS,20000);
        session.close();

        connection.close();

    }



    /**
     * 远程执行脚本发包  这里与服务器脚本有关  这可以实现tcp包的发送
     */
    @Override
    public void sendPacketByScript(Packet packet) throws IOException {
        ConnectToServer connectToServer=new ConnectToServer();
        //用户名密码登录vm
        Connection connection = connectToServer.getConnectionWithPassword();
        //执行发包脚本
        SessionExecScript(packet, connectToServer, connection);


        connection.close();


    }
    /**
     * 传过来的公钥连接服务器 然后再执行脚本
     */
    @Override
    public void sendPacketByScriptAndConnectionByKey(Packet packet, File publicKey) throws IOException {
        ConnectToServer connectToServer=new ConnectToServer();

        Connection connection = connectToServer.getConnectionWithPublickey(publicKey);
        //执行发包脚本
        SessionExecScript(packet, connectToServer, connection);


        connection.close();

    }

    @Override
    public void sendSocketParam(Packet packet) throws IOException {
        socket= new Socket(packet.getPtpip(), Integer.parseInt(packet.getPtpport()));
        OutputStream out =socket.getOutputStream();
        StringBuilder sb=new StringBuilder();
        sb.append("UPDATE ").append(packet.getFrequenceend()).append(" ").append(packet.getSize()).append(" ").append(packet.getFrequencesyn());
        out.write(sb.toString().getBytes());
        out.flush();
        socket.shutdownOutput();
        InputStream inputStream = socket.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int offset = -1;
        byte[] buffer = new byte[1024];
        while ((offset = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, offset);
        }
        inputStream.close();
        out.close();
//        System.out.println(byteArrayOutputStream);
        byteArrayOutputStream.close();

    }

    @Override
    public void startCapOrClose(Packet packet) throws IOException {
        try {
            socket = new Socket(packet.getPtpip(), Integer.parseInt(packet.getPtpport()));
            OutputStream out = socket.getOutputStream();
            if (packet.getMessage().equals("OPEN") || packet.getMessage().equals("SHUTDOWN")) {
                out.write(packet.getMessage().getBytes());
                out.flush();
                socket.shutdownOutput();
                InputStream inputStream = socket.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int offset = -1;
                byte[] buffer = new byte[1024];
                while ((offset = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, offset);
                }
                inputStream.close();
                out.close();
                byteArrayOutputStream.close();
            }
        }catch (Exception e ){
            e.printStackTrace();
        }finally {
            socket.close();
        }
    }
//    @Test
//        public void test() throws IOException {
//        Socket socket=new Socket("212.129.237.80",8011);
//        OutputStream out = socket.getOutputStream();
//        out.write("OPEN".getBytes());
//        socket.close();
//    }
    private void SessionExecScript(Packet packet, ConnectToServer connectToServer, Connection connection) throws IOException {
        //下面直接用execCommand  就不用bash shell会话  不然报错
        Session session = connectToServer.getConnectionSession(connection);

        //执行脚本 目前写了udp脚本 和tcp 脚本  由用户传来的数据包封装的protocol决定
        /**
         * 多加个判断
         * 如果是icmp包  则需要将默认的目的端口12312置空  因为icmp包没有目的端口这一信息
         */
        if (packet.getProtocol().equalsIgnoreCase("icmp")){
            packet.setDestport("");
        }
        String command="sh "+ConnectToServer.SCRIPT_LOCATION+packet.getProtocol()+".sh"+
                " "+ packet.getCount()+" "+ packet.getFrequenceend() +
                " "+ packet.getDestip()+" "+packet.getDestport()+" "+packet.getMessage();
//        session.execCommand("mkdir /root/test1");
//        session.execCommand(ConnectToServer.SCRIPT_LOCATION+packet.getProtocol()+".sh"+
//                " "+ packet.getCount()+" "+ packet.getFrequence() +
//                " "+ packet.getDestip()+" "+packet.getDestport()+" "+packet.getMessage()); //脚本传参的顺序 1 发包个数 2 频率s 3 目的ip 4 目的端口  5信息   传几个参数就是几个 如果后面不传就使用脚本里规定的默认值

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
        InputStream is = new StreamGobbler(session.getStdout());
        BufferedReader brs = new BufferedReader(new InputStreamReader(is));
        while (true)
        {
            String line = brs.readLine();
            if (line == null)
            {
                break;
            }
            System.out.println(line);
        }
        //等待，除非1.连接关闭；2.输出数据传送完毕；3.进程状态为退出；4.超时
        session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS,20000);
        session.close();



    }




}
