package util.bloom.CHTE.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author lgd
 * @date 2022/1/14 16:23
 */
public class File_Util_random {

    public void writeTotal2SendFile(File file, double i, double j) throws IOException {
        Scanner sc = new Scanner(file);
        int q= (int) ((int) (i-1)*6+j);
        String filename="/Users/codersavior/Documents/study project/HuaWei Paper/experiment4/"+String.valueOf(q)+"/";
        FileOutputStream outputStream = new FileOutputStream(new File(filename+"Send.txt"));
        while (sc.hasNext()) {
            String s = sc.nextLine();
            if (s.startsWith("id")) continue;
            StringTokenizer st = new StringTokenizer(s);
            long id = 0;
            double ts_receive = 0;
            int state = 0;
            int count = 0;
            StringBuilder sb = new StringBuilder();
            while (st.hasMoreElements()) {
                String val = st.nextToken();
                if (count == 0) {
                    id = Long.parseLong(val);
                    sb.append(id).append("   ");
                } else if (count == 1) {
                    ts_receive = Double.parseDouble(val);
                    sb.append(ts_receive).append("   ");
                } else if (count == 3) {
                    state = Integer.parseInt(val);
                    if (state == 1)
                        break;

                    sb.append(state);
                }
                count++;
            }
            sb.append("\n");
            if (state!=1)
                outputStream.write(sb.toString().getBytes());
        }

        outputStream.close();
    }
    //排序接受方
    public void sortReceiveTsAndChange1To2File(File file, double i, double j) throws IOException {
        Scanner sc = new Scanner(file);
        int q= (int) ((int) (i-1)*6+j);
        String filename="/Users/codersavior/Documents/study project/HuaWei Paper/experiment4/"+String.valueOf(q)+"/";
        Map<Double, List<String>> map = new TreeMap<>();
        while (sc.hasNext()) {
            String s = sc.nextLine();
            if (s.startsWith("id")) continue;
            StringTokenizer st = new StringTokenizer(s);
            long id = 0;
            double ts_receive = 0;
            int state = 0;
            int count = 0;
            StringBuilder sb = new StringBuilder();
            while (st.hasMoreElements()) {
                String val = st.nextToken();
                if (count == 0) {
                    id = Long.parseLong(val);
                    sb.append(id).append("   ");
                } else if (count == 2) {
                    ts_receive = Double.parseDouble(val);
                    sb.append(ts_receive).append("   ");
                } else if (count == 3) {
                    state = Integer.parseInt(val);
                    if (state == 0) break;
//                    else if (state == 1) {
//                        state = 2;
//                    }
                    sb.append(state);
                }
                count++;
            }
            if (state == 0) continue;
            List<String> list = map.getOrDefault(ts_receive, new ArrayList<>());
            list.add(sb.toString());
            map.put(ts_receive, list);
        }
        FileOutputStream outputStream = new FileOutputStream(new File(filename+"sortReceive.txt"));

        for (Map.Entry<Double, List<String>> entry : map.entrySet()) {
            for (String s : entry.getValue()) {

                outputStream.write((s + "\n").getBytes());
            }
        }
        outputStream.close();
    }
    //count 是多少个一算  返回一个集合  传的文件应是total.txt  就是那个5列的文件
    public Double[][] calculateAvgTs(File file, int count) throws FileNotFoundException {
        Scanner sc=new Scanner(file);
        List<Double>res=new ArrayList<>();
        Double[][] res1=new Double[1000][2];
        int num=0; //统计累加了多少个包了
        int useful=0;//统计有用的包个数
        double totalDelay=0;  //累加的时延  只算状态1和2的
        int round=0;
        while (sc.hasNext()){
            String s = sc.nextLine();
            if (s.startsWith("id")) continue;
            StringTokenizer st = new StringTokenizer(s);
            long id = 0;
            double ts_receive = 0;
            int state = 0;
            int idx = 0;
            while (st.hasMoreElements()){
                String s1 = st.nextToken();
                if (idx==3){
                    state=Integer.parseInt(s1);
                }else if (idx==4){ //是时延那一列
                    double delay=Double.parseDouble(s1);
                    num++;
                    if (state!=0&&state!=1){//0丢1乱2正常
                        totalDelay+=delay;
                        useful++;
                    }
                    if (num==count){
                        res.add(totalDelay/useful);
                        res1[round][0]=totalDelay/useful;
                        res1[round][1]=(double)useful;
                        useful=0;
                        num=0;
                        round++;
                        totalDelay=0;
                    }
                }
                idx++;
            }
            if (round==1000)
                    break;
        }
        return res1;
    }

    //计算上面这个方法返回的集合的平均数
    public Double getListAvgTs(List<Double>list){
        return list.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
    }

//    public static void main(String[] args) throws IOException {
//        new FileUtil().sortReceiveTsAndChange1To2File(new File("/Users/codersavior/Documents/study project/HuaWei Paper/experiment3/1/total.txt"),1,1);
//    }
}
