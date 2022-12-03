package util.bloom.CHTE.test;

import edu.harvard.syrah.prp.Log;
import util.async.java.util.concurrent.ConcurrentHashMap;
import util.bloom.CHTE.RDATable;
import util.bloom.CHTE.SimpleRDAMeasurementPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class NewPairRDA {
    static Log log = new Log(NewPairRDA.class);
    private SimpleRDAMeasurementPoint _hostA;
    private SimpleRDAMeasurementPoint _hostB;
    private int hashFuncNum = 2;
    private List<String> receiveList = new ArrayList<>();
    private List<String> sendList = new ArrayList<>();
    List<Double> list;//存放平均时延
    List<Double> list1;//存放有效个数
    List<Integer> list2;//存放接收端插入的个数
    int count = 0;
    int start = 0;
    int start1=0;
    int end = 1;

    public int parseSendText(int i, SimpleRDAMeasurementPoint _host) {
        List<String> sendList_i = this.sendList.subList(this.start1, this.sendList.size());
        int number = 0;
        while (sendList_i.size()!=0) {
            String readrow = sendList_i.get(number);//sendList_i中的第i行
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
                } else if (count == 2) {
                    state = Integer.parseInt(val);
                }
                count++;
            }
            if (ts_send >= i * 1000 && ts_send <= i * 1000 + 1000 - 1) {
                number++;
                _host.packetIncoming(id, ts_send);

                continue;
            } else if (ts_send > i * 1000 + 1000 - 1) {
                break;
            }
        }
        this.start1 += number;
        return number;

    }

    public int parseReceiveText(int i, SimpleRDAMeasurementPoint _host, int start) throws IOException {
        List<String> receiveList_i = this.receiveList.subList(this.start, this.receiveList.size());
        int number = 0;
        while (receiveList_i.size() != 0) {
            String readrow = receiveList_i.get(number);//sendList_i中的第i行
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
                } else if (count == 2) {
                    state = Integer.parseInt(val);
                }
                count++;
            }
            if (ts_send >= i * 1000 && ts_send <= i * 1000 + 1000 - 1) {
                number++;
                _host.packetIncoming(id, ts_send);

                continue;
            } else if (ts_send > i * 1000 + 1000 - 1) {
                break;
            }
        }
        this.start += number;
        return number;

    }

    public int parseText(String FileName, SimpleRDAMeasurementPoint _host, int start, int end) {

        Scanner sc;
        int number = 0;
        try {
            sc = new Scanner(new File(FileName));
            while (sc.hasNext()) {//&&lines<=TotalPacketsCount
                String str = sc.nextLine();
                long[] id = {1};
                double[] TS = {1.0};
                StringTokenizer st = new StringTokenizer(str);
                int count = 0;
                while (st.hasMoreElements()) {
                    String src = st.nextToken();
                    if (count == 0) {
                        id[0] = Long.parseLong(src);
                    }
                    if (count == 1) {
                        TS[0] = Double.parseDouble(src);
                    }
                    count++;
                }

                //record the packets
                /**
                 * 发送方将该数据包的相关信息保存到本地的RDA当中去
                 * 本地的RDA中不存在id相同的，也就是说每个桶中的数据包的个数为1
                 */
                if (TS[0] >= start && TS[0] <= end) {
                    number++;
                    _host.packetIncoming(id[0], TS[0]);
                    //sc.nextLine();
                    continue;
                } else if (TS[0] > end) {
                    break;
                }

            }
            //System.out.println("RDA count: " + _host.SenderTSTable.size());
//            RDATableEntry[] hashTable = _host.sender.hashTable;
//            int insert_count = 0;
//            for (RDATableEntry entry : hashTable) {
//                //System.out.println("id "+entry.getKeySum()+" ts"+entry.getTS()+" count"+entry.getCounter());
//                insert_count += entry.getCounter();
//            }
            //System.out.println("number"+number+"insert_count"+insert_count);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return number;
    }

    public void parseText(String FileName, SimpleRDAMeasurementPoint _host, int numbers) {
        //该函数的主要功能是缓存，发送方和接收方数据包id和时间戳。
        Scanner sc;
        int number = 0;
        try {
            sc = new Scanner(new File(FileName));
            while (sc.hasNext()) {//&&lines<=TotalPacketsCount
                if (number < numbers) {
                    String str = sc.nextLine();
                    long[] id = {1};
                    double[] TS = {1.0};
                    StringTokenizer st = new StringTokenizer(str);
                    int count = 0;
                    while (st.hasMoreElements()) {
                        String src = st.nextToken();
                        if (count == 0) {
                            id[0] = Long.parseLong(src);
                        }
                        if (count == 1) {
                            TS[0] = Double.parseDouble(src);
                        }
                        count++;
                    }

                    //record the packets
                    /**
                     * 发送方将该数据包的相关信息保存到本地的RDA当中去
                     * 本地的RDA中不存在id相同的，也就是说每个桶中的数据包的个数为1
                     */

                    _host.packetIncoming(id[0], TS[0]);
                    number++;
                } else {
                    break;
                }
            }
            System.out.println("RDA count: " + _host.SenderTSTable.size());
            System.out.println("number为：" + number);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void begin(List<Double> list, List<Double> list1) {
        /**
         *   SenderItems: 代表的是丢包数
         *   ReceiverItems：代表的是重传数
         */
        RDATable receiver = _hostB.sender;
        HashSet<Long> SenderItems = new HashSet<Long>();
        HashSet<Long> ReceiverItems = new HashSet<Long>();
        /*
            在这一步中对两个RDA结构做差。
         */
        boolean decoded = _hostA.decodeSet(receiver, SenderItems, ReceiverItems);
//        log.main("decoded: " + decoded + ", " + SenderItems.size() + ", " + ReceiverItems.size());
        //下面的循环用于数据集恢复
        while (!SenderItems.isEmpty() || !ReceiverItems.isEmpty()) {
            if (!SenderItems.isEmpty()) {
                _hostA.repairDirect(SenderItems, receiver, ReceiverItems, null);
            }
            if (!ReceiverItems.isEmpty()) {
                ConcurrentHashMap<Long, Double> ReceiverTSTable = _hostB.SenderTSTable;
                _hostA.repairDirectNew(SenderItems, receiver, ReceiverItems, ReceiverTSTable);
            }
        }

        if (SenderItems.isEmpty() && ReceiverItems.isEmpty()) {
            //
            double computeStatistics = computeStatistics(receiver)[0];
            double count = computeStatistics(receiver)[1];
            this.list.add(computeStatistics);
            this.list1.add(count);
        }
//        System.out.println("--------------");
    }

    public double[] computeStatistics(RDATable receiver) {
        double avg = _hostA.getAverage(receiver)[0];
        double count = _hostA.getAverage(receiver)[1];
        return new double[]{avg, count};
    }

    public void writeResultToFile(Object[] result) throws IOException {
        File file = new File("RDAResult.txt");
        FileWriter out = new FileWriter(file);
        for (int i = 0; i < result.length; i++) {
            out.write(result[i].toString());
            out.write("\r\n");
        }
        out.close();
    }

    public List<String> getFileToList(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        List<String> res = new ArrayList<>();
        while (scanner.hasNext()) {
            res.add(scanner.nextLine());
        }

        return res;
    }


    public static void main(String[] args) throws IOException {
        double[] avg = new double[36];
        File file = new File("RDAavg.txt");
        FileWriter out = new FileWriter(file);
        for (int i = 0; i < 36; i++) {
            String basic = "/Users/codersavior/Documents/study project/HuaWei Paper/experiment3/";
            String nu = String.valueOf(i + 1);
            String fileA = basic + nu + "/Send.txt";
            String fileB = basic + nu + "/sortReceive.txt";

            NewPairRDA rda = new NewPairRDA();

            rda.sendList = rda.getFileToList(new File(fileA));
            rda.receiveList = rda.getFileToList(new File(fileB));

//            rda._hostA = new SimpleRDAMeasurementPoint(2 * 32, 100, rda.hashFuncNum);
//            rda._hostB = new SimpleRDAMeasurementPoint(2 * 32, 100, rda.hashFuncNum);
            rda._hostA = new SimpleRDAMeasurementPoint(150, 100, rda.hashFuncNum);
            rda._hostB = new SimpleRDAMeasurementPoint( 150, 100, rda.hashFuncNum);
            rda.list = new ArrayList<>();
            rda.list1 = new ArrayList<>();
            rda.list2 = new ArrayList<>();
//            while (rda.count < 1000) {
//
//            }
            double temp_sum = 0;
            //为每个数据集创建一个RDAdetail的文件
            String aa = "RDA";
            String bb = String.valueOf(i + 1);
            File file1 = new File(aa + bb + "detail.txt");
            FileWriter out1 = null;
//            try {
//                out1 = new FileWriter(file1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            double sum_count=0;
            double sum_useful_count=0;
            double sum_pow=0;
            double result[][]=new double [1000][2];
            for (int j = 0; j < 1000; j++) {
//                rda.start = j * 1000;
//                rda.end = rda.start + 1000 - 1;
//            int a=parseText(fileA, _hostA, start, end);
//                int b=rda.parseText(fileB, rda._hostB, rda.start, rda.end);
                int a = rda.parseSendText(j, rda._hostA);
                int b = rda.parseReceiveText(j, rda._hostB, rda.start);
                rda.begin(rda.list, rda.list1);
                rda.list2.add(b);
                rda._hostA.clear();
                rda._hostB.clear();
//                rda.count++;
                //list中存储平均时延，list1中存储有效数据包个数
                temp_sum += rda.list.get(j);
//                out1.write(String.valueOf(rda.list.get(j))+"    "+String.valueOf(rda.list2.get(j))+"    "+String.valueOf(rda.list1.get(j)/2)+"\r\n");//显示每100个包的平均时延
                //out1.write("\r");
                //out1.write(String.valueOf(rda.list2.get(j)));//一共插入了多少个包
//                RDATableEntry[] hashTable = rda._hostB.sender.hashTable;
//                int insert_count = 0;
//                for (RDATableEntry entry : hashTable) {
//                    //System.out.println("id "+entry.getKeySum()+" ts"+entry.getTS()+" count"+entry.getCounter());
//                    insert_count += entry.getCounter();
//                }
//                out1.write(String.valueOf(insert_count));//每一次接收方插入多少个
                //out1.write("\r");
                //out1.write(String.valueOf(rda.list1.get(j)/2));//有效的个数有多少个
                //out1.write("\r\n");
                sum_useful_count+=rda.list1.get(j)/2;
                sum_count+=rda.list2.get(j);
                sum_pow+=rda.list.get(j)*rda.list1.get(j)/2;
                result[j][0]=rda.list.get(j);
                result[j][1]=rda.list1.get(j)/2;
            }
//            out1.close();
            double avg_pow=sum_pow/sum_useful_count;//加权平均数
            double var_pow=0;
            for(int k=0;k<1000;k++){
                var_pow+=Math.pow(result[k][0]-avg_pow,2)*result[k][1];
            }
            avg[i] = temp_sum / rda.list.size();
            String a = String.valueOf(avg[i]);
            //第一列表示平均时延，第二列表示加权平均时延，第三列表示
            out.write(a+"    "+String.valueOf(avg_pow)+"    "+String.valueOf(Math.sqrt(var_pow/sum_useful_count))+"    "+String.valueOf(sum_useful_count/1000));//在RDAavg.txt中写入均值和利用率
            out.write("\r\n");


//            Object[] array = rda.list.toArray();
//            System.out.println(Arrays.toString(array));
//            rda.writeResultToFile(array);
        }
        out.close();
        System.out.println(avg);
    }
}
