package situation.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.situation.entity.*;
import com.situation.util.DataUtil;
import com.situation.util.FileUtils;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import situation.service.DelayService;
import situation.service.SendPacketService;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 数据包发送
 *
 * @author lgd
 * @date 2021/10/28 14:53
 */

@RestController
@RequestMapping("/send")
@CrossOrigin(origins = "*",maxAge = 3600)
@Slf4j
public class SendController {

    @Autowired
    private SendPacketService sendPacketService;

    private File privateKey;

    @Autowired
    private DelayService delayService;


    /**
     * 数据包发送 接手前端json形式的packer对象
     *
     * @param totalMessage
     * @return
     */
    @PostMapping("/send")
    public Result send(@RequestBody(required = false) TotalMessage totalMessage) throws IOException {

        Packet packet = totalMessage.getPacket();
        try {
            sendPacketService.sendPacketByScript(packet);

        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, Status.ERROR, "发送失败");
        }
        return new Result(true, Status.SUCCESS, "发送成功");
    }


    /**
     * 远程执行脚本  由封装实体的里的packet 里的 protocol 属性决定脚本名(事先在服务端写好的)
     * 私钥前端可以用整个表单传过来  不传就用用户名密码登录
     *
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("/sh")
    public Result execScript(@RequestBody(required = false) TotalMessage totalMessage) throws IOException {
//        TotalMessage totalMessage = JSON.parseObject(jsonMessage, TotalMessage.class);
        Packet packet = totalMessage.getPacket();
        try {
            if (privateKey != null) sendPacketService.sendPacketByScriptAndConnectionByKey(packet, privateKey);
            else sendPacketService.sendPacketByScript(packet);

        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, Status.ERROR, "执行脚本失败");
        }
        return new Result(true, Status.SUCCESS, "执行脚本成功");
    }

    @RequestMapping("/socket")
    public Result execSocket(@RequestBody(required = false) TotalMessage totalMessage) throws IOException {
//        TotalMessage totalMessage = JSON.parseObject(jsonMessage, TotalMessage.class);
        Packet packet = totalMessage.getPacket();
        log.info("packet={}", packet);
        try {
//            if (privateKey!=null)   sendPacketService.sendPacketByScriptAndConnectionByKey(packet, privateKey);
//            else sendPacketService.sendPacketByScript(packet);
            sendPacketService.sendSocketParam(packet);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, Status.ERROR, "操作失败");
        }
        return new Result(true, Status.SUCCESS, "操作成功");
    }

    @RequestMapping("/opt")
    public Result execOpenOrShut(@RequestBody(required = false) TotalMessage totalMessage) {
        Packet packet = totalMessage.getPacket();
        try {
            sendPacketService.startCapOrClose(packet);
        } catch (Exception e) {
            e.printStackTrace();
            ;
            return new Result(false, Status.ERROR, "操作失败");
        }
        return new Result(true, Status.SUCCESS, "操作成功");
    }

    @RequestMapping(value = "/delay/{id}")
    public Result<Map<String, Object>> selectNewData(@PathVariable Integer id) {

        try {
            List<Delay> delays = delayService.selectNewData(id);
            PredictMap predict = delayService.selectNewPredict();
            Map<String, Object> map = new HashMap<>();
            List<Integer> delay = new ArrayList<>();
            List<Integer> interval = new ArrayList<>();
            List<Integer> length = new ArrayList<>();
            List<String> protocolList=new ArrayList<>();
            StringBuilder dateCurGroupFirst=new StringBuilder();
            long sum = 0;
            int cnt = 0;
            long lengthSum=0;
            boolean first=true;
            for (Delay delay1 : delays) {
                delay.add(delay1.getDelay1());
                interval.add(delay1.getInterval());
                length.add(delay1.getLength());
                protocolList.add(delay1.getPro_type());
                if (first)  {
                    dateCurGroupFirst.append(DataUtil.convertDate(delay1.getSend_time()));
//                    System.out.println(DataUtil.convertDate(delay1.getSend_time())+"-----------");
//                    dateCurGroupFirst.append(DataUtil.convertDate(delay1.getSend_time()));
//                    String s = DoubleUtil.convertDivideDouble(delay1.getTime_us(), 1000000);
//                    if (s.length()>2){
//                        dateCurGroupFirst.append(".");
//                        dateCurGroupFirst.append(s.substring(2));
//                    }
                }
                first=false;
                if (delay1.getDelay1() > 0) {
                    //只计算时延为正数的包
                    lengthSum+=delay1.getLength();
                    sum += delay1.getDelay1();
                    cnt++;
                }
            }
            if(delay.size()>0) {
                map.put("maxGroupDelay", delay.stream().mapToInt(Integer::intValue).max().getAsInt());
                map.put("minGroupDelay", delay.stream().mapToInt(Integer::intValue).min().getAsInt());
            }
            double averageDelay=cnt > 0 ? sum / (double)cnt : 0;

            double fangcha=0d;
            for (Delay delay1 : delays) {
                fangcha+=Math.pow(delay1.getDelay1()-averageDelay,2);
            }

            map.put("DateFirst",dateCurGroupFirst.toString());
            map.put("ProtocolType",protocolList);
            map.put("AllDelay", delay);
            map.put("AllInterval", interval);
            map.put("AllLength", length);
            map.put("count", delays.size());
            map.put("nextId", cnt > 0 ? delays.get(delays.size() - 1).getId() : id);
            map.put("AverageDelay", averageDelay);
            map.put("Fangcha",fangcha);
            map.put("Biaozhun",Math.sqrt(fangcha));
            map.put("LengthAverage",cnt>0 ? lengthSum/(double)cnt:lengthSum);
            map.put("predict",predict);
            return new Result<>(true, Status.SUCCESS, "查询成功", map);

//            String s = JSON.toJSONString(map);
//            System.out.println(delays.size());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(false, Status.ERROR, "查询失败");
        }
    }

    public Map<String, Object> dealMethod(Integer id){
        List<Delay> delays = delayService.selectNewData(id);

        PredictMap predict = delayService.selectNewPredict();

        Map<String, Object> map = new HashMap<>();
        List<Integer> delayOne = new ArrayList<>();
        List<Integer> delayTwo = new ArrayList<>();
        List<Integer> interval = new ArrayList<>();
        List<Integer> length = new ArrayList<>();
        List<String> protocolList=new ArrayList<>();
        StringBuilder dateCurGroupFirst=new StringBuilder();
        long sum1 = 0 ,sum2=0;
        int cnt1 = 0,cnt2=0;
        long lengthSum=0;
        boolean first=true;
        for (Delay delay1 : delays) {
            interval.add(delay1.getInterval());
            length.add(delay1.getLength());
            protocolList.add(delay1.getPro_type());
            if (first)  {
                dateCurGroupFirst.append(DataUtil.convertDate(delay1.getSend_time()));
//                    System.out.println(DataUtil.convertDate(delay1.getSend_time())+"-----------");
//                    dateCurGroupFirst.append(DataUtil.convertDate(delay1.getSend_time()));
//                    String s = DoubleUtil.convertDivideDouble(delay1.getTime_us(), 1000000);
//                    if (s.length()>2){
//                        dateCurGroupFirst.append(".");
//                        dateCurGroupFirst.append(s.substring(2));
//                    }
            }
            first=false;
            if (delay1.getDelay1() > 0) {
                //只计算时延为正数的包
                delayOne.add(delay1.getDelay1());
                lengthSum+=delay1.getLength();
                sum1 += delay1.getDelay1();
                cnt1++;
            }
            /*
            if (delay1.getDelay2()>0){
                delayTwo.add(delay1.getDelay2());
                sum2+=delay1.getDelay2();
                cnt2++;
            }*/
            sum2+=0;
            cnt2++;
        }

        double averageDelayOne=cnt1 > 0 ? sum1 / (double)cnt1 : 0;
        double averageDelayTwo=cnt2 > 0 ? sum2 / (double)cnt2 : 0;

        double fangchaOne=0d;
        double fangchaTwo=0d;
        for (Delay delay1 : delays) {
            fangchaOne+=Math.pow(delay1.getDelay1()-averageDelayOne,2);
            //fangchaTwo+=Math.pow(delay1.getDelay2()-averageDelayTwo,2);
        }
        if(delayOne.size()>0) {
            map.put("maxGroupDelayOne", delayOne.stream().mapToInt(Integer::intValue).max().getAsInt());
            map.put("minGroupDelayOne", delayOne.stream().mapToInt(Integer::intValue).min().getAsInt());
        }
        /*
        if(delayTwo.size()>0) {
            map.put("maxGroupDelayTwo", delayTwo.stream().mapToInt(Integer::intValue).max().getAsInt());
            map.put("minGroupDelayTwo", delayTwo.stream().mapToInt(Integer::intValue).min().getAsInt());
        }*/
        map.put("DateFirst",dateCurGroupFirst.toString());
        map.put("ProtocolType",protocolList);
        map.put("AllDelayOne", delayOne);
        map.put("AllDelayTwo", delayTwo);
        map.put("AllInterval", interval);
        map.put("AverageLength", length.stream().mapToInt(Integer::intValue).average());
        map.put("count", delays.size());
        map.put("nextId", cnt1 > 0 ? delays.get(delays.size() - 1).getId() : id);
        map.put("AverageDelayOne", averageDelayOne);
        map.put("AverageDelayTwo", averageDelayTwo);
        map.put("FangChaOne",fangchaOne);
        map.put("FangChaTwo",fangchaTwo);
        map.put("BiaozhunOne",Math.sqrt(fangchaOne));
        map.put("BiaozhunTwo",Math.sqrt(fangchaTwo));
        map.put("LengthAverage",cnt1>0 ? lengthSum/(double)cnt1:lengthSum);
        map.put("predict",predict);
        return map;
    }

    @RequestMapping(value = "/delayRda")
    public Result<List> selectDelay(@RequestBody(required = false)  Date date) {
        try {

//            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//            String s1 = simpleDateFormat.format(new Date((String)date.get("date")));
//            String s1 = simpleDateFormat.format(date);
            List delay = delayService.selectNewAll();
            return new Result<>(true, Status.SUCCESS, "查询成功", delay);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(false, Status.ERROR, "查询失败");
        }
    }
    /*
    @Test
    public void test() throws ParseException {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date("Mon Nov 28 22:38:21 CST 2022");

        System.out.println(simpleDateFormat.format(date));
    }*/
    @RequestMapping(value = "/delay1/{id}")
    public Result<Map<String, Object>> selectNewDataTwo(@PathVariable Integer id) {
        try {
            Map<String, Object> map = dealMethod(id);
            return new Result<>(true, Status.SUCCESS, "查询成功", map);

//            String s = JSON.toJSONString(map);
//            System.out.println(delays.size());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(false, Status.ERROR, "查询失败");
        }
    }


    @RequestMapping(value = "/delay/all")
    public Result<Map> selectAll() {
        List<Delay> delays = delayService.selectAll();

        return new Result(true ,Status.SUCCESS, "查询成功",delays);
    }
    @RequestMapping(value = "/delay/one/{id}")
    public Result<Map> selectOne(@PathVariable Integer id) {
        Delay delay = delayService.selectId(id);

        return new Result(true ,Status.SUCCESS, "查询成功",delay);
    }
    /**
     * 接收文件示例  服务器 私钥
     */
    @PostMapping("/upload")
    public Result uploadKey(@RequestParam("keyfile") MultipartFile multipartFile) {
        if (multipartFile != null) {
            File file = FileUtils.MultipartFileConvertToFile(multipartFile);
            this.privateKey = file;
            Result res = new Result(true, Status.SUCCESS, "私钥上传成功");
            return res;
//            return new Result(true,Status.SUCCESS,"私钥上传成功");
        }

        return new Result(false, Status.ERROR, "私钥上传失败");
    }
}
