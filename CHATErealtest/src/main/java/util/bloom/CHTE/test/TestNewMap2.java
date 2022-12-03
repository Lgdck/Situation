package util.bloom.CHTE.test;

import util.bloom.Apache.Hash.hashing.LongHashFunction;

import java.io.*;
import java.util.*;
import java.util.List;

/**
 * @author lgd
 * @date 2021/12/23 15:14
 */
public class TestNewMap2 {

    private Map<Integer, Queue<Integer>> mapSender = new HashMap<>();
    private Map<Integer, Queue<Integer>> mapReceiver = new HashMap<>();
    private Map<Long, Double> senderTsTable = new HashMap<>();
    private Map<Long, Double> receiverTsTable = new HashMap<>();
    private int MaxRowHave = 4;  //一行多少个  经试验 列越大 利用率越高 列为2时 97.7%  列为4时  99.6
    private int MaxInsert = 10;//踢出次数
    private int Mod_send = 1024;//发送方结构A的行
    private int Mod_receive = 1024;//接收方结构B的行
    private int x = 100;//表示发送端的窗口是一次读100个包，20
    private int k = 0;//设置接收端的初始窗口是发送端的k倍
    private List<String> receiveList = new ArrayList<>();
    private List<String> sendList = new ArrayList<>();
    //    private  int receive_start=0;//接收端插入B起始编号。
    private int receive_last_match = 0;//接收端最后一个匹配的编号。
    public static LongHashFunction[] LongHashFunction4PosHash = new LongHashFunction[2];
    public int accur = 511;//精度为后4位15，后5位31，后6位,63;后7位，127；后8位，255；后9位511后10位1023，后11位2047。默认后8位
    private int memory[][] = new int[1000][4];
    private  int ac=1000000;
    TestNewMap2() {
        //初始2个  hash函数
        for (int i = 0; i < 2; i++) {
            LongHashFunction4PosHash[i] = LongHashFunction.xx(i);
        }
    }

    public int hashPos(Long id, int idex, int sig) {
        long hashVal = LongHashFunction4PosHash[idex].hashLong(id);
        if (sig == 1)     //返回在结构A中的位置
            return (int) Math.abs(hashVal % Mod_send);
//            return (int) Math.abs(Math.abs(hashVal) % Mod_send);
        else if (sig == 2)//返回在结构B中的位置
            return (int) Math.abs(hashVal % Mod_receive);
        return 0;
    }

    //第二个hash函数
    public int hashIdentify(Long id, int idex) {
//        long hashVal = LongHashFunction4PosHash[idex].hashLong(id);
        return (int) (id & accur); //取低6位
//        return (int)(id%ac);
    }

    public List<String> getFileToList(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        List<String> res = new ArrayList<>();
        while (scanner.hasNext()) {
            res.add(scanner.nextLine());
        }
        return res;
    }

    void parseFileSend(List<String> sendList,int i) {
        List<String> sendList_i = sendList;

        for (int j = 0; j < sendList_i.size(); j++) {
            String readrow = sendList_i.get(j);//sendList_i中的第i行
            StringTokenizer st = new StringTokenizer(readrow);
            long id = 0;
            double ts_send = 0;
            //double ts_receive = 0;
            int state = 0;
            int count = 0;
            while (st.hasMoreElements()) {
                String val = st.nextToken();
                if (count == 0) {
                    id = Long.parseLong(val);
                } else if (count == 1) {
                    ts_send = Double.parseDouble(val);
                }
                count++;
            }
            insertFirst(mapSender, id, 1);
            //senderTsTable.put(id, ts_send);
        }
//        System.out.println(mapSender.values());
        int temp_sum = 0;
        for (Queue<Integer> value : mapSender.values()) {
            temp_sum += value.size();
        }
//        System.out.println(temp_sum);
//        System.out.println(Mod_send);

        memory[i][0] = temp_sum;
        memory[i][1] = Mod_send;

    }

    void parseFileReceive(int i, List<String> receiveList_i) {

        for (int j = 0; j < receiveList_i.size(); j++) {
            String readrow = receiveList_i.get(j);//sendList_i中的第i行
            StringTokenizer st = new StringTokenizer(readrow);
            long id = 0;
            //double ts_send = 0;
            double ts_receive = 0;
            int state = 0;
            int count = 0;
            while (st.hasMoreElements()) {
                String val = st.nextToken();
                if (count == 0) {
                    id = Long.parseLong(val);
                } else if (count == 1) {
                    ts_receive = Double.parseDouble(val);
                }
                count++;
            }
//            int row = hashPos(id, 0,2);
            insertFirst(mapReceiver, id, 2);
//            receiverTsTable.put(id, ts_receive);
        }
        int temp_sum = 0;
        for (Queue<Integer> value : mapReceiver.values()) {
            temp_sum += value.size();
        }
//        System.out.println(temp_sum);
//        System.out.println(Mod_receive);
        memory[i][2] = temp_sum;
        memory[i][3] = Mod_receive;

    }

    public double[] chargeSender_B(int i, List<String> sendList_i) {

        double count_send_useful = 0;
        double count_send_exuseful = 0;
        double tsSum_send_useful = 0;
        double checkresult[] = new double[100000];
        for (int j = 0; j < sendList_i.size(); j++) {
            String readrow = sendList_i.get(j);//sendList_i中的第i行
            StringTokenizer st = new StringTokenizer(readrow);
            long id = 0;
            double ts_send = 0;
            //double ts_receive = 0;
            //int state = 0;
            int count = 0;
            while (st.hasMoreElements()) {
                String val = st.nextToken();
                if (count == 0) {
                    id = Long.parseLong(val);
                } else if (count == 1) {
                    ts_send = Double.parseDouble(val);
                }
                count++;
            }

            if (checkSender_B(id)) {
                count_send_useful++;
                tsSum_send_useful += ts_send;
                checkresult[j]=1;
            }else
                checkresult[j]=0;
        }
        return new double[]{count_send_useful, tsSum_send_useful};
    }
    public int[] getStart(int i){
        int start=0;
        int end=0;
        int row=0;
        int useful=0;
        while (receiveList.size()!=0){
            String readrow=receiveList.get(row);
            StringTokenizer st = new StringTokenizer(readrow);
            long id = 0;
            //double ts_send = 0;
            double ts_receive = 0;
            int state = 0;
            int count = 0;
            while (st.hasMoreElements()) {
                String val = st.nextToken();
                if (count == 1) {
                    ts_receive = Double.parseDouble(val);
                }
                count++;
            }
            if(ts_receive<i*1)
                row++;
            else if(ts_receive>=i*1&&ts_receive<=(i+1)*1){
                end=row+1;
                useful++;
                row++;
            }else if(ts_receive>(i+1)*1)
                break;
        }
        start=end-useful;
        return new int[]{start,end};
    }
    public List<String> chargeReceiver_A(int i) {
        int[] range=getStart(i);//截取的起始位置

        List<String> chargeSuccess=new ArrayList<>();
//        if (start + (k + 1) * x > receiveList.size()) {
//            end = receiveList.size();
//        } else
//            end = start + (k + 1) * x;
        List<String> receiveList_i = receiveList.subList(range[0], range[1]);
        int start_receiveList_i=0;
//        double count_receiver_useful = 0;
//        double tsSum_receiver_useful = 0;
        double temp_sum = 0;
        double max_temp_sum = 0;
        int max_pos=0;
        int compere_success=0;
        ArrayList<String> success_id=new ArrayList<>();
//        double tsSum_receiver = 0;
        while(start_receiveList_i<receiveList_i.size()) {
            String readrow=receiveList_i.get(start_receiveList_i);
            StringTokenizer st = new StringTokenizer(readrow);
            long id = 0;
            //double ts_send = 0;
            double ts_receive = 0;
            int state = 0;
            int count = 0;
            while (st.hasMoreElements()) {
                String val = st.nextToken();
                if (count == 0) {
                    id = Long.parseLong(val);
                }
                count++;
            }
            if (this.checkReceive_A(id)) {
//                System.out.println("+++++" + id + "被找到了");
//                tsSum_receiver += ts_receive;
                compere_success++;
                chargeSuccess.add(receiveList_i.get(start_receiveList_i));
//                success_id.add(receiveList_i.get(start_receiveList_i));
            }
            start_receiveList_i++;
        }

        return chargeSuccess;
        //第一个值表示所有接收方start到end中能和A正确匹配的个数，第二个值表示从start到我们设计的切分点之前有效的个数，
        // 第三个值表示从start到我们设计的切分点之前有效的时间戳的和，第四个值表示切分点编号
    }
    public List<String> chargeReceiver_A_new(int i) {
        int[] range=getStart(i);//截取的起始位置
        List<String> chargeSuccess=new ArrayList<>();

        List<String> receiveList_i = receiveList.subList(range[0], range[1]);
        int start_receiveList_i=0;
//        double count_receiver_useful = 0;
//        double tsSum_receiver_useful = 0;
        double temp_sum = 0;
        double max_temp_sum = 0;
        int max_pos=0;
        int compere_success=0;
        ArrayList<String> success_id=new ArrayList<>();
//        double tsSum_receiver = 0;
        while(start_receiveList_i<receiveList_i.size()) {
            String readrow=receiveList_i.get(start_receiveList_i);
            StringTokenizer st = new StringTokenizer(readrow);
            long id = 0;
            int count = 0;
            while (st.hasMoreElements()) {
                String val = st.nextToken();
                if (count == 0) {
                    id = Long.parseLong(val);
                }
                count++;
            }
            if (this.checkReceive_A(id)) {

                compere_success++;

                success_id.add(receiveList_i.get(start_receiveList_i));
                temp_sum+=7;//增益参数十分重要
            } else {
//                System.out.println("————————" + id + "没到了");
                temp_sum -= 0.1;
            }
            if (temp_sum > max_temp_sum) {
                max_temp_sum = temp_sum;
                max_pos=compere_success;
            }
            start_receiveList_i++;
        }
        for (int m=0;m<max_pos;m++){
            chargeSuccess.add(success_id.get(m));
        }
        return chargeSuccess;
        //第一个值表示所有接收方start到end中能和A正确匹配的个数，第二个值表示从start到我们设计的切分点之前有效的个数，
        // 第三个值表示从start到我们设计的切分点之前有效的时间戳的和，第四个值表示切分点编号
    }

    public double caculateUse(Map<Integer, Queue<Integer>> map) {
        Collection<Queue<Integer>> values = map.values();
        int sum = 0;
        for (Queue<Integer> value : values) {
            sum += value.size();
        }
        return sum / (double) (senderTsTable.values().size());
    }

    /**
     * 检测一个id
     *
     * @param id
     */
    public boolean checkSender_B(Long id) {
        int row1 = hashPos(id, 0, 2);
        Queue<Integer> receive = mapReceiver.get(row1);
        int identify = (int) (id & accur);
//        int identify= (int )(id%ac);
        if (receive != null) {
            if (receive.contains(identify)) {
//                System.out.println(row1 + " get");
                return true;
            }

        }
        int row2 = row1 ^ hashPos((long) identify, 0, 2);
        Queue<Integer> receive2 = mapReceiver.get(row2);
        if (receive2 != null) {
            if (receive2.contains(identify)) {
//                System.out.println(row2 + " 2get");
                return true;
            }
        }
        return false;
    }

    public boolean checkReceive_A(Long id) {
        int identify = (int) (id & accur);
//        int identify= (int )(id%ac);
        int row1 = hashPos(id, 0, 1);
        Queue<Integer> send = mapSender.get(row1);

        if (send != null) {
            if (send.contains(identify)) {
//                System.out.println(row1 + " get");
                return true;
            }

        }
        int row2 = row1 ^ hashPos((long) identify, 0, 1);
        Queue<Integer> send2 = mapSender.get(row2);
        if (send2 != null) {
            if (send2.contains(identify)) {
//                System.out.println(row2 + " 2get");
                return true;
            }
        }
        return false;
    }

    //第一次存
    public void insertFirst(Map<Integer, Queue<Integer>> map, Long id, int sig) {

//        System.out.println("1");

        int row = hashPos(id, 0, sig);
        Queue<Integer> rowQueue = map.get(row);  //每一行存的是数据包的标识
        int cur_identify = hashIdentify(id, 1); //当前数据包的标识
        if (rowQueue != null) {
            if (rowQueue.size() < MaxRowHave) { //这一行没满
                rowQueue.offer(cur_identify);
                map.put(row, rowQueue);
//                System.out.println("该记录被插入到了第" + row);
            } else {
                //这一行满了
                //int poll_Inentify = rowQueue.poll();  //出队(踢出)  先进先出

                int newRow = row ^ hashPos((long) cur_identify, 0, sig);  //新的行
                Queue<Integer> newrowQueue = map.get(newRow);  //每一行存的是数据包的标识
                if (newrowQueue != null) {
                    if (newrowQueue.size() < MaxRowHave) {
                        newrowQueue.offer(cur_identify);
                        map.put(newRow, newrowQueue);
                        //System.out.println("该记录被插入到了第"+newRow);
                    } else {
                        //插入这个传入的
//                        System.out.println("两个都满了，该踢出了");
                        int poll_Inentify = newrowQueue.poll();  //出队(踢出)  先进先出
                        newrowQueue.offer(cur_identify);
                        map.put(row, newrowQueue);
                        insertOut(map, poll_Inentify, newRow, 1, sig); //插入到新行
                    }
                }
//                System.out.println("该记录被插入到了第" + newRow);
            }
        } else {
            //直接插入
            rowQueue = new LinkedList<>();
            rowQueue.offer(cur_identify);
            map.put(row, rowQueue);
//            System.out.println("该记录被插入到了第" + row);
        }

    }

    //被踢出的包再存  比insert多了个踢出次数
    public void insertOut(Map<Integer, Queue<Integer>> map, int identify, int row, int outTime, int sig) {
        //System.out.println("发生了一次踢出事件");
        //判断踢出次数
        if (outTime >= MaxInsert) {
            System.out.println("数据包标识: " + identify + " 已达到最大踢出次数 ");
            return;
        }
        int newRow = row ^ hashPos((long) identify, 0, sig);
        Queue<Integer> rowQueue = map.get(newRow);

        if (rowQueue != null) {
            if (rowQueue.size() < MaxRowHave) {
                //没满
                rowQueue.offer(identify);
                map.put(newRow, rowQueue);
            } else {
                //满了
                //踢出一个
                int poll = rowQueue.poll();
                //计算新行
                //int newRow = row ^ hashPos((long) poll, 1,sig);
                //将这个插入
                rowQueue.offer(identify);
                map.put(newRow, rowQueue);
                //插入到新行  踢出次数+1
                insertOut(map, poll, newRow, outTime + 1, sig);
            }
        } else {
            //直接插入
            rowQueue = new LinkedList<>();
            rowQueue.offer(identify);
            map.put(newRow, rowQueue);
        }
    }

    void setMapSender(List<String> i) {

        int temp = i.size() / (MaxRowHave-1);
        int a = Integer.toBinaryString(temp).length();
        if (Math.pow(2, a) < temp)
            Mod_send = (int) Math.pow(2, a + 1);
        else
            Mod_send = (int) Math.pow(2, a);
    }

    void setMapReceiver(int i, List<String> receiveList_i) {
//        int total=receiveList_i.size();
//        int temp = (int) total / 3;
//        int a = Integer.toBinaryString(temp).length();
//        if (Math.pow(2, a) < temp)
//            Mod_receive = (int) Math.pow(2, a + 1);
//        else
//            Mod_receive = (int) Math.pow(2, a);

        Mod_receive=Mod_send;
    }

    public void writeResultToFile(double[][] result, int j) throws IOException {
        String ba="result";
        String n=String.valueOf(j+1);
        String re=ba+n+".txt";
        File file = new File(re);
        FileWriter out = new FileWriter(file);
        for (int i = 0; i < result.length; i++) {
            //String a = String.valueOf(result[i][6]);
            out.write(String.valueOf(result[i][4])+"    "+String.valueOf(result[i][0])+"\n");
//            out.write("\r");
//            out.write(String.valueOf(result[i][1]-result[i][0]+1));//接收方截取了多少个
//            out.write("\r");
//            out.write(String.valueOf(result[i][2]));//接收方截取的有效个数
//            out.write("\n");
        }
        out.close();
    }
    public double getTSsum(List<String> result_receiver_a) {
        double ts=0;
        int row=0;
        while (row<result_receiver_a.size()){
            String readrow=result_receiver_a.get(row);
            StringTokenizer st = new StringTokenizer(readrow);
            long id = 0;
            //double ts_send = 0;
            double ts_receive = 0;
            int state = 0;
            int count = 0;
            while (st.hasMoreElements()) {
                String val = st.nextToken();
                if (count == 0) {
                    id = Long.parseLong(val);
                } else if (count == 1) {
                    ts_receive = Double.parseDouble(val);
                }
                count++;
            }
            ts+=ts_receive;
            row++;
        }
        return ts;
    }
    public List<String> setRange(int i, int range_start) {
        List<String> sendList_i = sendList.subList(range_start, sendList.size());
        int number=0;
        while(sendList_i.size()!=0){
            String readRow=sendList_i.get(number);
            StringTokenizer st=new StringTokenizer(readRow);
            long id=0;
            double ts_send=0;
            int state = 0;
            int count = 0;
            while (st.hasMoreElements()){
                String val=st.nextToken();
                if(count==0){
                    id=Long.parseLong(val);
                }else if(count==1){
                    ts_send=Double.parseDouble(val);
                }
                count++;
            }
            if (ts_send>=i*1&&ts_send<=i*1+1){
                number++;
            }  else if(ts_send>i*1+1)
                break;
        }
        sendList_i=sendList_i.subList(0,number);
        return sendList_i;

    }
    public static void main(String[] args) throws IOException {
        System.out.println("哈哈");
        File file=new File("CHTErealtestavg.txt");
        FileWriter out = new FileWriter(file);
        double[] avg=new double[36];
        for (int j = 0; j < 3; j++) {
//            System.out.println(j);
            TestNewMap2 test = new TestNewMap2();
//            String basic = "/Users/codersavior/Documents/study project/HuaWei Paper/experiment3/";
//            String basic = "/Users/codersavior/Documents/study project/HuaWei Paper/realtest/";
//            String nu = String.valueOf(j + 1);
//            String fileA = basic + nu + "/Send.txt";
//            String fileB = basic + nu + "/sortReceive.txt";

            //第一个测量节点的数据
            String file1 = "/Users/codersavior/Documents/study project/HuaWei Paper/realtest725/725/test8_send.txt";
            //第二个测量节点的数据
            String file2 = "/Users/codersavior/Documents/study project/HuaWei Paper/realtest725/725/test8_45.txt";
            //第三个测量节点的数据
//            String file3 = "/Users/codersavior/Documents/study project/HuaWei Paper/realtest721/test2/test2-44.txt";
            //第四个测量节点的数据
            String file4 = "/Users/codersavior/Documents/study project/HuaWei Paper/realtest725/725/test8_server.txt";
            String fileA="";
            String fileB="";
             switch (j){
                 case 0:
                     fileA= file1;
                     fileB= file2;
                     break;
                 case 1:
                     fileA= file2;
                     fileB= file4;
                     break;
                 case 2:
                     fileA= file1;
                     fileB= file4;
                     break;

             }

            test.sendList = test.getFileToList(new File(fileA));
            test.receiveList = test.getFileToList(new File(fileB));
            int range_start=0;
            int max_effect = 1;
            int receive_start = 0;//接收端插入B起始编号。
            double result[][] = new double[200][5];
            double temp_sum=0;
            double temp_sum_pow=0;
            double count_sum=0;
            double var_avg=0;
            int lala=0;
            for (int i = 0; i <195; i++) {//表示处理多少秒

                //计算第is有多少数据
                List<String> sendList_i=test.setRange(i,range_start);
                range_start+=sendList_i.size();
                //设置CHATE桶数量
                test.setMapSender(sendList_i);
                test.parseFileSend(sendList_i,i);//把send中list添加到结构mapSend中
                List<String> result_Receiver_A = test.chargeReceiver_A_new(i);//第一个值是有用包的个数，第二个值是有用数据包时间戳的和,第三个值是切分位置。
                double ts_useful_Receiver_A=test.getTSsum(result_Receiver_A);
//            System.out.println(result_Receiver_A);

                test.setMapReceiver(i, result_Receiver_A);
                test.parseFileReceive(i, result_Receiver_A);//把receive中list添加到结构mapReceive中,第二个参数是开始位置，第三个参数是结束位置
                double[] result_Sender_B = test.chargeSender_B(i,sendList_i);//第一个值是前一百个的有用值，第二个值是前一百有用值时间戳的和

//                double temp[] = {receive_start, result_Receiver_A[3], result_Receiver_A[1], result_Sender_B[0], result_Receiver_A[2], result_Sender_B[1]};
                result[i][0] = result_Receiver_A.size();
                result[i][1] = ts_useful_Receiver_A;//接收方切个点位置
                result[i][2] = result_Sender_B[0];//表示接收方从start到我们设计的切分点之前有效的个数
                result[i][3] = result_Sender_B[1];
                if(result[i][0]==0)
                    result[i][4]=0;
                else {
                    result[i][4] = result[i][1] / result[i][0] - result[i][3] / result[i][2];//表示接收方从start到我们设计的切分点之前有效的时间戳的和
                    lala++;
                }
//                result[i][5] = ;
//                result[i][6] = result[i][4] / result[i][2] - result[i][5] / result[i][3];
//                receive_start = (int) result_Receiver_A[3] +1;
                //运行完了这次的活动需要清空一下缓存和两个map
                test.mapSender.clear();
                test.mapReceiver.clear();
                temp_sum+=result[i][4];
                temp_sum_pow+=result[i][4]*result[i][0];//每一个计算周期的加权和
                count_sum+=result[i][0];
            }
            double avg_pow=temp_sum_pow/count_sum;//加权平均数
            for (int k = 0; k < 200; k++) {
                var_avg+=Math.pow(result[k][4]-avg_pow,2)*result[k][0];
            }
            //System.out.println(result);
            avg[j]=temp_sum/lala;

            String a = String.valueOf(avg[j]);
            //第一列平均时延，第二列是加权平均时延，第三列表示加权方差，第四列表示平均有效数据包个数
            out.write(a+"    "+String.valueOf(temp_sum_pow/count_sum)+"    "+String.valueOf(Math.sqrt(var_avg/count_sum))+"    "+String.valueOf(count_sum/lala));
            out.write("\r\n");

//            //接下来把结果写到文件里
            test.writeResultToFile(result,j);
        }
        out.close();
//        System.out.println(avg);
    }




}
