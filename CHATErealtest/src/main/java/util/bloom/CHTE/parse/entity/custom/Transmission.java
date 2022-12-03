package util.bloom.CHTE.parse.entity.custom;

/**
 * @author lgd
 * @date 2022/11/5 16:53
 */
public class Transmission {
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

    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
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

    //Դ�˿�
    private int srcPort;

    //Ŀ�Ķ˿�
    private int desPort;

    private long seq;

    private long ack;

    //����ƫ��(tcp  �ײ�����)
    private int tcpHeaderLen;

    //����
    private int window;

    //У���
    private int tcpChecksum;

    //����ָ��
    private int urgentPoint;
}
