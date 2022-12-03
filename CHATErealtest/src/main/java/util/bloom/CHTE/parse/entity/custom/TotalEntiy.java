package util.bloom.CHTE.parse.entity.custom;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lgd
 * @date 2022/11/5 16:45
 */
@Data
@TableName("totalAll")
public class TotalEntiy {

    //�汾��
    private int version;

    //�ײ�����
    private int ipHeaderLen;

    //���ַ���
    private int distinguishService;

    //�ܳ���
    private int totalLen;

    //��ʶ
    private int identify;

    //��־
    private int biaozhi;

    //Ƭƫ��
    private int fragmentOffset;

    //����ʱ��
    private int ttl;

    //Э������
    private String protocol;

    //У���
    private int ipChecksum;

    //ԭ��ַ
    private String srcIp;

    //Ŀ�ĵ�ַ
    private String desIp;



    //ԴMAC
    private String srcMac;

    //Ŀ��MAC
    private String desMac;


    //����
    private int macType;

    //Դ�˿�
    private int srcPort;

    //Ŀ�Ķ˿�
    private int desPort;

    private long seq;

    private long ack;

    //����ƫ��(tcp  �ײ�����)
    private int tcpHeaderLen;

    //����
    private int windowSize;

    //У���
    private int tcpChecksum;

    //����ָ��
    private int urgentPoint;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getIpHeaderLen() {
        return ipHeaderLen;
    }

    public void setIpHeaderLen(int ipHeaderLen) {
        this.ipHeaderLen = ipHeaderLen;
    }

    public int getDistinguishService() {
        return distinguishService;
    }

    public void setDistinguishService(int distinguishService) {
        this.distinguishService = distinguishService;
    }

    public int getTotalLen() {
        return totalLen;
    }

    public void setTotalLen(int totalLen) {
        this.totalLen = totalLen;
    }

    public int getIdentify() {
        return identify;
    }

    public void setIdentify(int identify) {
        this.identify = identify;
    }

    public int getBiaozhi() {
        return biaozhi;
    }

    public void setBiaozhi(int biaozhi) {
        this.biaozhi = biaozhi;
    }

    public int getFragmentOffset() {
        return fragmentOffset;
    }

    public void setFragmentOffset(int fragmentOffset) {
        this.fragmentOffset = fragmentOffset;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getIpChecksum() {
        return ipChecksum;
    }

    public void setIpChecksum(int ipChecksum) {
        this.ipChecksum = ipChecksum;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getDesIp() {
        return desIp;
    }

    public void setDesIp(String desIp) {
        this.desIp = desIp;
    }

    public String getSrcMac() {
        return srcMac;
    }

    public void setSrcMac(String srcMac) {
        this.srcMac = srcMac;
    }

    public String getDesMac() {
        return desMac;
    }

    public void setDesMac(String desMac) {
        this.desMac = desMac;
    }

    public int getMacType() {
        return macType;
    }

    public void setMacType(int macType) {
        this.macType = macType;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public int getDesPort() {
        return desPort;
    }

    public void setDesPort(int desPort) {
        this.desPort = desPort;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public long getAck() {
        return ack;
    }

    public void setAck(long ack) {
        this.ack = ack;
    }

    public int getTcpHeaderLen() {
        return tcpHeaderLen;
    }

    public void setTcpHeaderLen(int tcpHeaderLen) {
        this.tcpHeaderLen = tcpHeaderLen;
    }

    public int getwindowSize() {
        return windowSize;
    }

    public void setwindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public int getTcpChecksum() {
        return tcpChecksum;
    }

    public void setTcpChecksum(int tcpChecksum) {
        this.tcpChecksum = tcpChecksum;
    }

    public int getUrgentPoint() {
        return urgentPoint;
    }

    public void setUrgentPoint(int urgentPoint) {
        this.urgentPoint = urgentPoint;
    }

    public TotalEntiy() {
    }

    public TotalEntiy(int version, int ipHeaderLen, int distinguishService, int totalLen, int identify, int biaozhi, int fragmentOffset, int ttl, String protocol, int ipChecksum, String srcIp, String desIp, String srcMac, String desMac, int macType, int srcPort, int desPort, long seq, long ack, int tcpHeaderLen, int windowSize, int tcpChecksum, int urgentPoint) {
        this.version = version;
        this.ipHeaderLen = ipHeaderLen;
        this.distinguishService = distinguishService;
        this.totalLen = totalLen;
        this.identify = identify;
        this.biaozhi = biaozhi;
        this.fragmentOffset = fragmentOffset;
        this.ttl = ttl;
        this.protocol = protocol;
        this.ipChecksum = ipChecksum;
        this.srcIp = srcIp;
        this.desIp = desIp;
        this.srcMac = srcMac;
        this.desMac = desMac;
        this.macType = macType;
        this.srcPort = srcPort;
        this.desPort = desPort;
        this.seq = seq;
        this.ack = ack;
        this.tcpHeaderLen = tcpHeaderLen;
        this.windowSize = windowSize;
        this.tcpChecksum = tcpChecksum;
        this.urgentPoint = urgentPoint;
    }




}
