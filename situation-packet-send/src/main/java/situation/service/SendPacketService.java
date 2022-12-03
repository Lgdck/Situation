package situation.service;

import com.situation.entity.Packet;

import java.io.File;
import java.io.IOException;

/**
 * @author lgd
 * @date 2021/10/29 10:53
 */

public interface SendPacketService {

    public void sendPacket(Packet packet) throws IOException;

    public void sendPacketByScript(Packet packet) throws IOException;

    public void sendPacketByScriptAndConnectionByKey(Packet packet, File publicKey) throws IOException;

    public void sendSocketParam(Packet packet) throws IOException;

    public void startCapOrClose(Packet packet) throws IOException;

}
