package util.bloom.CHTE.parse.entity.custom;

/**
 * @author lgd
 * @date 2022/11/5 16:52
 */
public class Mac {
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

    //源MAC
    private String srcMac;

    //目的MAC
    private String desMac;


    //类型
    private int macType;
}
