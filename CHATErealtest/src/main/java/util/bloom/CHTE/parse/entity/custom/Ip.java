package util.bloom.CHTE.parse.entity.custom;

/**
 * @author lgd
 * @date 2022/11/5 16:52
 */
public class Ip {

    //版本号
    private int version;

    //首部长度
    private int ipHeaderLen;

    //区分服务
    private int distinguishService;

    //总长度
    private int totalLen;

    //标识
    private int identify;

    //标志
    private int biaozhi;

    //片偏移
    private int fragmentOffset;

    //生存时间
    private int ttl;

    //协议类型
    private String protocol;

    //校验和
    private int ipChecksum;

    //原地址
    private String srcIp;

    //目的地址
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
