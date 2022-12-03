package util.bloom.CHTE.parse.entity.custom;

import org.springframework.stereotype.Service;
import util.bloom.CHTE.parse.Util.IPUtils;
import util.bloom.CHTE.parse.entity.NBStructure;
import util.bloom.CHTE.parse.entity.PacketHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

/**
 * @author lgd
 * @date 2022/11/5 17:07
 */
@Service
public class ParseAll {
    private static int initialS=0;
    private static int initialMS=0;
    //mac层
    static byte[] globalHeaderBuffer = new byte[24];

    // pcap packet header: 16 bytes
    //时间戳  数据包长度 等
    static byte[] packetHeaderBuffer = new byte[16];

    //报文信息
    static byte[] packetDataBuffer;

    static Map<Integer ,String> map=new HashMap<>();
//    @Autowired
//    private  TotalEntiyMapper mapper;

    public static List<util.bloom.CHTE.parse.entity.custom.TotalEntiy> parse(File file){
//        byte[] packetHeaderBuffer = new byte[16];
        FileInputStream in=null;
        List<util.bloom.CHTE.parse.entity.custom.TotalEntiy> res=new ArrayList<>();
        map.put(6,"TCP");
        try {

            in=new FileInputStream(file);
            //这一部分暂时没用
            if (in.read(globalHeaderBuffer)!=24){
                System.out.println("坏的pcap");
            }
            int idx=0;  //用于对标第一个的时间戳  即第一个算的时候得出距离1970-0-0的偏差  后面的都减去这个值
            while (in.read(packetHeaderBuffer)>0){
                NBStructure nb=new NBStructure();
//                解析数据包头  获得包长度
                PacketHeader packetHeader = parsePacketHeader(packetHeaderBuffer,nb,idx++);
                packetDataBuffer = new byte[packetHeader.getCapLen()];
                if (in.read(packetDataBuffer) != packetHeader.getCapLen()) {
                    System.out.println("不匹配的数据长度");
                    return null;
                }
//                if(packetHeader.getCapLen()>1500)   continue;
                byte[] macHeaderBuffer =Arrays.copyOfRange(packetDataBuffer ,0, 14);
                Mac mac = parseMACHeader(macHeaderBuffer);

                int ipHeaderLen = (packetDataBuffer[14+0] - 64 ) * 4;

                byte[] ipHeaderBuffer = Arrays.copyOfRange(packetDataBuffer ,14+0, 14 +0+ ipHeaderLen);
                util.bloom.CHTE.parse.entity.custom.Ip ip = parseIpHeader(ipHeaderBuffer);


                int tcpHeaderLen  = ((packetDataBuffer[14+ipHeaderLen+12+0] & 0xf0) >> 4) * 4;
                byte[] tcpHeaderBuffer = Arrays.copyOfRange(packetDataBuffer, 14 + ipHeaderLen+0, 14 + ipHeaderLen + tcpHeaderLen+0);

                util.bloom.CHTE.parse.entity.custom.Transmission t=parseTCPHeader(tcpHeaderBuffer);

                util.bloom.CHTE.parse.entity.custom.TotalEntiy entiy=new util.bloom.CHTE.parse.entity.custom.TotalEntiy();
                entiy.setMacType(mac.getMacType());
                entiy.setSrcMac(mac.getSrcMac());
                entiy.setDesMac(mac.getDesMac());
                entiy.setVersion(ip.getVersion());
                entiy.setIpHeaderLen(ip.getIpHeaderLen());
                entiy.setDistinguishService(ip.getDistinguishService());
                entiy.setTotalLen(ip.getTotalLen());
                entiy.setIdentify(ip.getIdentify());
                entiy.setBiaozhi(ip.getBiaozhi());
                entiy.setFragmentOffset(ip.getFragmentOffset());
                entiy.setTtl(ip.getTtl());
                entiy.setProtocol(ip.getProtocol());
                entiy.setIpChecksum(ip.getIpChecksum());
                entiy.setSrcIp(ip.getSrcIp());
                entiy.setDesIp(ip.getDesIp());
                entiy.setSrcPort(t.getSrcPort());
                entiy.setDesPort(t.getDesPort());
                entiy.setSeq(t.getSeq());
                entiy.setAck(t.getAck());
                entiy.setTcpHeaderLen(t.getTcpHeaderLen());
                entiy.setwindowSize(t.getWindow());
                entiy.setTcpChecksum(t.getTcpChecksum());
                entiy.setUrgentPoint(t.getUrgentPoint());
                res.add(entiy);
                // 解析数据包数据  IP+TCP
//                parsePacketData(packetDataBuffer,nb);
//                out.write((mess+"\n").getBytes());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
    private static util.bloom.CHTE.parse.entity.custom.Ip parseIpHeader(byte[] ipHeaderBuffer){
        byte[] totalLenBuffer = Arrays.copyOfRange(ipHeaderBuffer, 2, 4);
        byte[] identifyBuffer = Arrays.copyOfRange(ipHeaderBuffer, 4, 6);
        byte[] srcIPBuffer = Arrays.copyOfRange(ipHeaderBuffer, 12, 16);
        byte[] dstIPBuffer = Arrays.copyOfRange(ipHeaderBuffer, 16, 20);
        byte[] fragmentOffsetBuffer  =  Arrays.copyOfRange(ipHeaderBuffer,6,8);
        byte[] ipChecksumBuffer  =  Arrays.copyOfRange(ipHeaderBuffer,10,12);


        int totalLen = util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(totalLenBuffer, 2);
        int identify= util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(identifyBuffer,2);
        int protocol = util.bloom.CHTE.parse.Util.DataUtils.byteToInt(ipHeaderBuffer[9]);
        String srcIP = IPUtils.int2IPv4(util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(srcIPBuffer, 4));
        String dstIP = IPUtils.int2IPv4(util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(dstIPBuffer, 4));
        int version = util.bloom.CHTE.parse.Util.DataUtils.byteToInt(ipHeaderBuffer[0]) >>>4;
        int ipHeaderLen = (util.bloom.CHTE.parse.Util.DataUtils.byteToInt(ipHeaderBuffer[0]) & 15) * version;
        int distinguishService = util.bloom.CHTE.parse.Util.DataUtils.byteToInt(ipHeaderBuffer[1]);
        int biaozhi = util.bloom.CHTE.parse.Util.DataUtils.byteToInt(ipHeaderBuffer[6]) >>> 4;
        int fragment = util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(fragmentOffsetBuffer, 2)&4096;
        int ttl = util.bloom.CHTE.parse.Util.DataUtils.byteToInt(ipHeaderBuffer[8]);
        int ipChecksum = util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(ipChecksumBuffer, 2);
        util.bloom.CHTE.parse.entity.custom.Ip ip=new util.bloom.CHTE.parse.entity.custom.Ip();
        ip.setVersion(version);
        ip.setIpHeaderLen(ipHeaderLen);
        ip.setDistinguishService(distinguishService);
        ip.setTotalLen(totalLen);
        ip.setIdentify(identify);
        ip.setBiaozhi(biaozhi);
        ip.setFragmentOffset(fragment);
        ip.setTtl(ttl);
        ip.setProtocol(map.get(protocol));
        ip.setIpChecksum(ipChecksum);
        ip.setSrcIp(srcIP);
        ip.setDesIp(dstIP);
        return ip;
    }
    private static Mac parseMACHeader(byte[] macHeaderBuffer){

        Mac mac =new Mac();

        byte[] srcMacBuffer = Arrays.copyOfRange(macHeaderBuffer, 0, 6);
        byte[] desMacBuffer = Arrays.copyOfRange(macHeaderBuffer, 6, 12);
        byte[] macTypeBuffer =Arrays.copyOfRange(macHeaderBuffer,12,14);
        String srcMac= IPUtils.toMac(srcMacBuffer);
        String desMac=IPUtils.toMac(desMacBuffer);
        int macType = util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(macTypeBuffer,2);

        mac.setSrcMac(srcMac);
        mac.setDesMac(desMac);
        mac.setMacType(macType);

        return mac;
    }

//    private static Object parseIPHeader(){
//
//
//
//    }
    private static util.bloom.CHTE.parse.entity.custom.Transmission parseTCPHeader(byte[] tcpHeaderBuffer) {

        util.bloom.CHTE.parse.entity.custom.Transmission t=new util.bloom.CHTE.parse.entity.custom.Transmission();

        // sport and dport
        byte[] srcPortBuffer = Arrays.copyOfRange(tcpHeaderBuffer, 0, 2);
        byte[] dstPortBuffer = Arrays.copyOfRange(tcpHeaderBuffer, 2, 4);
        byte[] synBuffer = Arrays.copyOfRange(tcpHeaderBuffer, 4, 8);
        byte[] ackBuffer = Arrays.copyOfRange(tcpHeaderBuffer, 8, 12);
        byte []tcpHeaderLenBuffer = Arrays.copyOfRange(tcpHeaderBuffer, 12, 13); //前一半
        byte[] windowBuffer = Arrays.copyOfRange(tcpHeaderBuffer, 14, 16); //前一半
        byte[] checkSumBuffer=Arrays.copyOfRange(tcpHeaderBuffer,16,18);
        byte[] urgentPointBuffer=Arrays.copyOfRange(tcpHeaderBuffer,18,20);

        int srcPort = util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(srcPortBuffer, 2);
        int dstPort = util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(dstPortBuffer, 2);
        long seqmess = util.bloom.CHTE.parse.Util.DataUtils.byteArray2long(synBuffer, 4);
        long ackmess = util.bloom.CHTE.parse.Util.DataUtils.byteArray2long(ackBuffer, 4);
        int tcpHeaderLen = util.bloom.CHTE.parse.Util.DataUtils.byteToInt(tcpHeaderLenBuffer[0]) >>>4;

        int window= util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(windowBuffer,2);

        int checkSum= util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(checkSumBuffer,2);
        int urgentPoint= util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(urgentPointBuffer,2);

        t.setSrcPort(srcPort);
        t.setDesPort(dstPort);
        t.setSeq(seqmess);
        t.setAck(ackmess);
        t.setTcpHeaderLen(tcpHeaderLen);
        t.setWindow(window);
        t.setTcpChecksum(checkSum);
        t.setUrgentPoint(urgentPoint);
        return t;
    }


    private static PacketHeader parsePacketHeader(byte[] dataHeaderBuffer,NBStructure nb,int idx){

        byte[] timeSBuffer = Arrays.copyOfRange(dataHeaderBuffer, 0, 4);
        byte[] timeMsBuffer = Arrays.copyOfRange(dataHeaderBuffer, 4, 8);
        byte[] capLenBuffer = Arrays.copyOfRange(dataHeaderBuffer, 8, 12);
        byte[] lenBuffer = Arrays.copyOfRange(dataHeaderBuffer, 12, 16);

        PacketHeader packetHeader = new PacketHeader();

        util.bloom.CHTE.parse.Util.DataUtils.reverseByteArray(timeSBuffer);
        util.bloom.CHTE.parse.Util.DataUtils.reverseByteArray(timeMsBuffer);
        util.bloom.CHTE.parse.Util.DataUtils.reverseByteArray(capLenBuffer);
        util.bloom.CHTE.parse.Util.DataUtils.reverseByteArray(lenBuffer);

        int timeS = util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(timeSBuffer, 4);
        int timeMs = util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(timeMsBuffer, 4);
        //System.out.println(timeS+"   "+timeMs);  //看距今多少s
        int capLen = util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(capLenBuffer, 4);
        int len = util.bloom.CHTE.parse.Util.DataUtils.byteArray2Int(lenBuffer, 4);
        //这里取决于每一个机器的时钟
        if(idx==0){
            initialS=timeS;
            initialMS=timeMs;
        }
        //用不到时间戳了
        packetHeader.setTimeS(timeS-initialS);  //因为截止时间是到格林时间1970-1-1的0:0:0
        packetHeader.setTimeMs(timeMs-initialMS);
        NumberFormat instance = NumberFormat.getInstance();
        instance.setMaximumFractionDigits(20); //保留20位小数
        instance.setGroupingUsed(false); //取消科学计数法
        //String TS=new StringBuilder().append(packetHeader.getTimeS()).append(".").append(packetHeader.getTimeMs()).toString();
        String TS = instance.format((packetHeader.getTimeS() + packetHeader.getTimeMs() / 1000000.0) / 1.0);
        //System.out.println("---------------"+TS);  //将s和ms 拼接成不用科学计数法表示的形式

        packetHeader.setCapLen(capLen);
        packetHeader.setLen(len);
        //packetHeader.setTS(TS);

//        System.out.println(packetHeader);
//        TS=String.valueOf(Double.parseDouble(TS)+0.112935d);  ///   新的  删
        nb.settS(TS);
        return packetHeader;
    }
}
