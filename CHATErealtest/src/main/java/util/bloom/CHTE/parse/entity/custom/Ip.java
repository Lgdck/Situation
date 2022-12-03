package util.bloom.CHTE.parse.entity.custom;

/**
 * @author lgd
 * @date 2022/11/5 16:52
 */
public class Ip {

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
}
