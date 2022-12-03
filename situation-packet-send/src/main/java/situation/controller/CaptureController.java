package situation.controller;

import com.situation.entity.CaptureEntity;
import com.situation.entity.PacketCapture;
import com.situation.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import situation.service.PacketCaptureService;

import java.io.IOException;

/**
 * @author lgd
 * @date 2021/12/21 16:39
 */
@CrossOrigin
@RequestMapping("/capture")
@RestController
public class CaptureController {

    @Autowired
    public PacketCaptureService packetCaptureService;

    @RequestMapping("/sh")
    public Result capturePacket(@RequestBody(required = false)PacketCapture packetCapture) throws IOException {
        if (packetCapture!=null){
            packetCaptureService.capturePacket(packetCapture);
        }
        return null;

    }

}
