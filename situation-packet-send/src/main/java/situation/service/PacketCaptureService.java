package situation.service;

import com.situation.entity.PacketCapture;

import java.io.IOException;

/**
 * @author lgd
 * @date 2021/12/21 16:41
 */
public interface PacketCaptureService {

     public void capturePacket(PacketCapture packetCapture) throws IOException;

     /**
      * 添加捕获记录
      */
     void add(PacketCapture packetCapture);
}
