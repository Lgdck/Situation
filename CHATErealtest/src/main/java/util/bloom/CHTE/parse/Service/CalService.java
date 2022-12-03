package util.bloom.CHTE.parse.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;

/**
 * @author lgd
 * @date 2022/7/24 10:27
 */
public class CalService {

    class Node{
        String id;
        double ts;

        public Node(String id, double ts) {
            this.id = id;
            this.ts = ts;
        }
    }
    static List<String>sendList=new ArrayList<>();
    static List<String>serverList=new ArrayList<>();
    static List<Double>offset1List =new ArrayList<>();
    static List<Double>offset2List =new ArrayList<>();
    static Map<String,List<double[]>>sendTS=new HashMap<>();
    static Map<String,List<double[]>>serverTS=new HashMap<>();
    static Map<String,List<Integer>>serverTotal=new HashMap<>();
    static Map<String,List<Integer>>sendTotal=new HashMap<>();
    //计算两个文件的 时延  包含时钟
    public static void calTwoFile2(File file1, File file2,File offset) throws IOException {
        saveToSendList(file1);
        saveToServerList(file2);
        saveToOffeset1List(offset);
        int sendIdx=0,serverIdx=0,inialSendSize=sendList.size(),idx1=0;
        double time=1;
        NumberFormat instance = NumberFormat.getInstance();
        instance.setMaximumFractionDigits(10); //保留20位小数
        instance.setGroupingUsed(false); //取消科学计数法
        FileOutputStream out=new FileOutputStream("E:\\work\\cheliang\\tcp\\cal-1-tcp.txt");
        while (time<sendTS.get(sendList.get(sendList.size()-1)).get(0)[0]){
            int same=0;
            double sumTs=0;
            long sumTotal=0;
            sendIdx=0;
            serverIdx=0;

            //取发送方的 窗口数据  0~sendIdx
            while (sendIdx<sendList.size() && sendTS.get(sendList.get(sendIdx)).get(0)[0]<=time){
                sendIdx++;
                idx1++;
            }
            //发送方的
            List<String> tempSendList=sendList.subList(0,sendIdx);
            int tempServerIdx=0;  //tempServerIdx是0~1刻度   serverIdx是0~2刻度
            //接收方窗口多1s
            while (serverIdx<serverList.size() && serverTS.get(serverList.get(serverIdx)).get(0)[0]<=time+1){
                //用于 截取接收方1s
                if (serverTS.get(serverList.get(serverIdx)).get(0)[0]<=time){
                    tempServerIdx++;
                }
                String id=serverList.get(serverIdx);
                if (tempSendList.contains(id)){
                    //发送方的窗口含有
                    same++;
                    //原来的
//                    sumTs+=Math.abs(serverTS.get(id).get(0)-sendTS.get(id).get(0));
                    //加上时钟
                    sumTs+=serverTS.get(id).get(0)[1]-(sendTS.get(id).get(0)[1]+(offset1List.size()>0?offset1List.get(idx1*offset1List.size()/inialSendSize):0));
                    //不加时钟
//                    sumTs+=serverTS.get(id).get(0)[1]-(sendTS.get(id).get(0)[1]);
//                    System.out.println(idx1*offset1List.size()/inialSendSize);
//                    System.out.println(idx1  +"   "+ offsetList.size()  +   "     "+inialSendSize );
//                    sumTotal+=sendTotal.get(id).get(0);
                }
                serverIdx++;

            }
            //更新map
            for (int i=0;i<sendIdx;i++){
                String id=sendList.get(i);
                List<double[]> list = sendTS.get(sendList.get(i));
                List<Integer> list1 = sendTotal.get(sendList.get(i));
                if (list.size()>1)  list.remove(0);
                if (list1.size()>1) list1.remove(0);
                sendTS.put(id,list);
                sendTotal.put(id,list1);
            }
            for (int i=0;i<tempServerIdx;i++){
                String id=serverList.get(i);
                List<double[]> list = serverTS.get(serverList.get(i));
                List<Integer> list1 = serverTotal.get(serverList.get(i));
                if (list.size()>1)  list.remove(0);
                if (list1.size()>1) list1.remove(0);
                serverTS.put(id,list);
                serverTotal.put(id,list1);
            }


            if (sendIdx<sendList.size())
                sendList=sendList.subList(sendIdx,sendList.size());
            if (tempServerIdx<serverList.size())
                serverList=serverList.subList(tempServerIdx,serverList.size());

            StringBuilder sb=new StringBuilder();
            if (same>0) {
//                sb.append(time).append(",");
                sb.append(instance.format(sumTs / same)).append(",").append(sumTotal / same).append(",").append(same).append("\n");
            }
            time+=1;
            out.write(sb.toString().getBytes());
        }


    }
    //这里是 发送 接收 分别同步 到另一个服务器
    public static void calTwoFile3(File file1, File file2,File offset1,File offset2) throws IOException {
        saveToSendList(file1);
        saveToServerList(file2);
        saveToOffeset1List(offset1);
        saveToOffeset2List(offset2);
        int sendIdx=0,serverIdx=0,inialSendSize=sendList.size(),inialServerSize=serverList.size(),idx1=0,idx2=0;
        double time=1;
        NumberFormat instance = NumberFormat.getInstance();
        instance.setMaximumFractionDigits(10); //保留20位小数
        instance.setGroupingUsed(false); //取消科学计数法
        FileOutputStream out=new FileOutputStream("E:\\work\\cheliang\\ftp\\cal-ftp-new-1.txt");
        while (time<sendTS.get(sendList.get(sendList.size()-1)).get(0)[0]){
            int same=0;
            double sumTs=0;
            long sumTotal=0;
            sendIdx=0;
            serverIdx=0;

            while (sendIdx<sendList.size() && sendTS.get(sendList.get(sendIdx)).get(0)[0]<=time){
                sendIdx++;
                idx1++;
            }
            List<String> tempSendList=sendList.subList(0,sendIdx);
            int tempServerIdx=0;
            while (serverIdx<serverList.size() && serverTS.get(serverList.get(serverIdx)).get(0)[0]<=time+1){
                if (serverTS.get(serverList.get(serverIdx)).get(0)[0]<=time){
                    tempServerIdx++;
                    idx2++;
                }
                String id=serverList.get(serverIdx);
                if (tempSendList.contains(id)){
                    same++;
                    //原来的
//                    sumTs+=Math.abs(serverTS.get(id).get(0)-sendTS.get(id).get(0));
                    //加上时钟
                    sumTs+=(serverTS.get(id).get(0)[1]+offset2List.get(idx2*offset2List.size()/inialServerSize))-(sendTS.get(id).get(0)[1]+offset1List.get(idx1*offset1List.size()/inialSendSize));
//                    System.out.println(idx1*offset1List.size()/inialSendSize);
//                    System.out.println(idx1  +"   "+ offsetList.size()  +   "     "+inialSendSize );
                    sumTotal+=sendTotal.get(id).get(0);
                }
                serverIdx++;

            }
            //更新map
            for (int i=0;i<sendIdx;i++){
                String id=sendList.get(i);
                List<double[]> list = sendTS.get(sendList.get(i));
                List<Integer> list1 = sendTotal.get(sendList.get(i));
                if (list.size()>1)  list.remove(0);
                if (list1.size()>1) list1.remove(0);
                sendTS.put(id,list);
                sendTotal.put(id,list1);
            }
            for (int i=0;i<tempServerIdx;i++){
                String id=serverList.get(i);
                List<double[]> list = serverTS.get(serverList.get(i));
                List<Integer> list1 = serverTotal.get(serverList.get(i));
                if (list.size()>1)  list.remove(0);
                if (list1.size()>1) list1.remove(0);
                serverTS.put(id,list);
                serverTotal.put(id,list1);
            }


            if (sendIdx<sendList.size())
                sendList=sendList.subList(sendIdx,sendList.size());
            if (tempServerIdx<serverList.size())
                serverList=serverList.subList(tempServerIdx,serverList.size());

            StringBuilder sb=new StringBuilder();
            if (same>0) {
//                sb.append(time).append(",");
                sb.append(instance.format(sumTs / same)).append(",").append(sumTotal / same).append(",").append(same).append("\n");
            }
            time+=1;
            out.write(sb.toString().getBytes());
        }


    }
    //添加时钟
    public static void addOffsetToFile(File file1, File file2,File offset1,File offset2) throws IOException {
        saveToSendList(file1);
        saveToServerList(file2);
        saveToOffeset1List(offset1);
        saveToOffeset2List(offset2);
        int sendIdx = 0, serverIdx = 0, inialSendSize = sendList.size(), inialServerSize = serverList.size(), idx1 = 0, idx2 = 0;
        double time = 1;
        NumberFormat instance = NumberFormat.getInstance();
        instance.setMaximumFractionDigits(10); //保留20位小数
        instance.setGroupingUsed(false); //取消科学计数法
        FileOutputStream out1=new FileOutputStream("E:\\work\\cheliang\\ftp\\ftp-src-off.txt");
        FileOutputStream out2=new FileOutputStream("E:\\work\\cheliang\\ftp\\ftp-des-off.txt");
        while (time<sendTS.get(sendList.get(sendList.size()-1)).get(0)[0]){
            int same=0;
            double sumTs=0;
            long sumTotal=0;
            sendIdx=0;
            serverIdx=0;

            while (sendIdx<sendList.size() && sendTS.get(sendList.get(sendIdx)).get(0)[0]<=time){
                sendIdx++;
                idx1++;
                String id=sendList.get(sendIdx);
                StringBuilder sb=new StringBuilder();
                sb.append(id).append(",").append(instance.format(sendTS.get(id).get(0)[1]+offset1List.get(idx1*offset1List.size()/inialSendSize))).append("\n");
                out1.write(sb.toString().getBytes());
            }


            if (sendIdx<sendList.size())
                sendList=sendList.subList(sendIdx,sendList.size());
            time+=1;
        }
        while (time<serverTS.get(serverList.get(serverList.size()-1)).get(0)[0]){
            int same=0;
            double sumTs=0;
            long sumTotal=0;
            sendIdx=0;
            serverIdx=0;

            while (sendIdx<serverList.size() && serverTS.get(serverList.get(sendIdx)).get(0)[0]<=time){
                sendIdx++;
                idx2++;
                String id=serverList.get(sendIdx);
                StringBuilder sb=new StringBuilder();
                sb.append(id).append(",").append(instance.format(serverTS.get(id).get(0)[1]+offset2List.get(idx1*offset2List.size()/inialSendSize))).append("\n");
                out2.write(sb.toString().getBytes());
            }


            if (sendIdx<serverList.size())
                serverList=serverList.subList(sendIdx,serverList.size());
            time+=1;
        }

    }

    private static void saveToOffeset1List(File offset) throws FileNotFoundException {
        if(offset==null)    return;
        Scanner sc=new Scanner(offset);
        while (sc.hasNext()){
            String s = sc.nextLine();
            StringTokenizer tokenizer = new StringTokenizer(s);
            offset1List.add(Double.parseDouble(tokenizer.nextToken())/1000000);
        }
    }
    private static void saveToOffeset2List(File offset) throws FileNotFoundException {

        Scanner sc=new Scanner(offset);
        while (sc.hasNext()){
            String s = sc.nextLine();
            StringTokenizer tokenizer = new StringTokenizer(s);
            offset2List.add(Double.parseDouble(tokenizer.nextToken())/1000000);
        }
    }

    public static void saveToServerList(File file) throws FileNotFoundException {
        Scanner sc2=new Scanner(file);
        while (sc2.hasNext()){
            String s = sc2.nextLine();
            StringTokenizer tokenizer = new StringTokenizer(s);
            while (tokenizer.hasMoreElements()){
                String id=tokenizer.nextToken();
                double ts=Double.parseDouble(tokenizer.nextToken());
                double trueTs=Double.parseDouble(tokenizer.nextToken());
//                int total=Integer.parseInt(tokenizer.nextToken());
                serverList.add(id);
                List<double[]> list = serverTS.getOrDefault(id, new ArrayList<>());
                list.add(new double[]{ts,trueTs});
                serverTS.put(id,list);
                List<Integer> list1 = serverTotal.getOrDefault(id, new ArrayList<>());
//                list1.add(total);
                serverTotal.put(id,list1);
                break;
            }
        }
    }
    public static void saveToSendList(File file) throws FileNotFoundException {
        Scanner sc2=new Scanner(file);
        while (sc2.hasNext()){
            String s = sc2.nextLine();
            StringTokenizer tokenizer = new StringTokenizer(s);
            while (tokenizer.hasMoreElements()){
                String id=tokenizer.nextToken();
                double ts=Double.parseDouble(tokenizer.nextToken());
                double trueTs=Double.parseDouble(tokenizer.nextToken());
//                int total=Integer.parseInt(tokenizer.nextToken());
                sendList.add(id);
                List<double[]> list = sendTS.getOrDefault(id, new ArrayList<>());
                list.add(new double[]{ts,trueTs});
                sendTS.put(id,list);
                List<Integer> list1 = sendTotal.getOrDefault(id, new ArrayList<>());
//                list1.add(total);
                sendTotal.put(id,list1);
                break;
            }
        }
    }
    //计算
    public static void calTwoFile(File file1, File file2) throws IOException {
        int cnt1=0,cnt2=0,same=0;
        double time=0.1;
        double sumTs=0;
        double sumTotalLen=0;
        BigDecimal sumT=new BigDecimal(0);
        BigDecimal sumTotal=new BigDecimal(0);
        BigDecimal time1=new BigDecimal(1);
        BigDecimal jiange=new BigDecimal(1);
        BigDecimal kuoda=new BigDecimal(0.5);
        BigDecimal offset=new BigDecimal(0);
        BigDecimal zero=new BigDecimal(0);

        Scanner sc1=new Scanner(file1);
        Scanner sc2=new Scanner(file2);
        Set<String> set=new HashSet<>();
        Map<String,BigDecimal> src=new HashMap<>();
        Map<String,BigDecimal> des=new HashMap<>();
        FileOutputStream out=new FileOutputStream("E:\\\\work\\\\cheliang\\\\ftp\\\\cal1-Nooff.txt");
        while (sc1.hasNext() || sc2.hasNext()){
//            double tempTime=time-0.1;
            BigDecimal tempTime=time1.subtract(jiange);
            String srcLasId="";
            BigDecimal srcLastTs=new BigDecimal(0);
            if (time1.compareTo(new BigDecimal(86.2))>0){
//                System.out.println("hhh");
            }
            while (sc1.hasNext()&&tempTime.compareTo(time1)<=0){
                String s = sc1.nextLine();
                StringTokenizer tokenizer=new StringTokenizer(s);
                while (tokenizer.hasMoreElements()){
                    String id = tokenizer.nextToken();

//                    tempTime=Double.parseDouble(tokenizer.nextToken());
                    tempTime=new BigDecimal(tokenizer.nextToken());
                    if(tempTime.compareTo(time1)<=0){
                        set.add(id);
                        src.put(id,tempTime);
                    }else{
                        srcLasId=id;
                        srcLastTs=tempTime;
                    }
                    break;
                }
            }
            tempTime=time1.subtract(jiange);
            while (sc2.hasNext() && tempTime.compareTo(time1)<=0){
                String s=sc2.nextLine();
                StringTokenizer tokenizer=new StringTokenizer(s);
                while (tokenizer.hasMoreElements()){
                    String id = tokenizer.nextToken();
//                    tempTime=Double.parseDouble(tokenizer.nextToken());
                    tempTime=new BigDecimal(tokenizer.nextToken());
                    if(set.contains(id)){
                        same++;
                        sumTotal=sumTotal.add(new BigDecimal(Integer.parseInt(tokenizer.nextToken())));
                        if(offset.compareTo(zero)==0)   offset=(tempTime.subtract(src.get(id)));
                        else if (offset.compareTo(zero)<0)  sumT=sumT.add(tempTime.subtract(src.get(id)).add(offset.abs()));
                        else sumT=sumT.add(tempTime.subtract(src.get(id)));
                    }else
                        cnt2++;
                    break;
                }

            }
//            System.out.println(sumTs);
            StringBuilder sb=new StringBuilder();
            if (same>0) {
//                sb.append(time1.doubleValue()).append("    ");
                sb.append(sumT.divide(new BigDecimal(same), 7, RoundingMode.DOWN).doubleValue()).append(",").append(sumTotal.divide(new BigDecimal(same), 7, RoundingMode.DOWN).doubleValue()).append(",").append(same).append("\n");

            }else{
                System.out.println(time1.doubleValue());
            }

            out.write(sb.toString().getBytes());
            time1=time1.add(jiange);
            same=0;
            sumT=new BigDecimal(0);
            sumTotal=new BigDecimal(0);
            Map<String,BigDecimal>temp=new HashMap<>();
            set.clear();
            for (Map.Entry<String, BigDecimal> entry : src.entrySet()) {
                if (entry.getValue().compareTo(time1)>0)   {
                    temp.put(entry.getKey(),entry.getValue());
                    set.add(entry.getKey());
                }
            }
            src=temp;
            set.add(srcLasId);
            src.put(srcLasId,srcLastTs);
        }
    }
}
