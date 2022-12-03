package util.bloom.CHTE.parse.Util;


/**
 * @author lgd
 * @date 2022/3/19 19:45
 */
public class IPUtils {

    public static String int2IPv4(int intIp) {
        StringBuilder sb = new StringBuilder();
        sb.append(intIp >>> 24);
        sb.append(".");
        sb.append((intIp >>> 16) & 0xff);
        sb.append(".");
        sb.append((intIp >>> 8) & 0xff);
        sb.append(".");
        sb.append(intIp & 0xff);
        return sb.toString();
    }

    public static String toMac(byte[] data){
        StringBuilder sb=new StringBuilder();
        sb.append(Integer.toHexString(DataUtils.byteToInt(data[0])));
        sb.append(":");
        sb.append(Integer.toHexString(DataUtils.byteToInt(data[1])));
        sb.append(":");
        sb.append(Integer.toHexString(DataUtils.byteToInt(data[2])));
        sb.append(":");
        sb.append(Integer.toHexString(DataUtils.byteToInt(data[3])));
        sb.append(":");
        sb.append(Integer.toHexString(DataUtils.byteToInt(data[4])));
        sb.append(":");
        sb.append(Integer.toHexString(DataUtils.byteToInt(data[5])));
        return sb.toString();
    }

}
