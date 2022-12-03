package com.situation.entity.inter;

/**
 * linux服务器相关信息
 * @author lgd
 * @date 2021/11/2 14:49
 */
public interface DefaultServerParam {

    //linux 发包服务器地址  用自己的虚拟机即可
    public static final String IP_VM="192.168.200.128";

    //云服务器1
    public static final String IP_TENCENT_SERVER1="49.235.199.128";

    //云服务器2 兼 frp穿透服务器  开放80端口提供穿透
    public static final String IP_TENCENT_SERVER2="212.129.237.80";

    //远程连接服务器地址的用户名和密码  已用私钥代替
    public static final String USERNAME="root";

    public static final String PASSWORD="rp220211";





}
