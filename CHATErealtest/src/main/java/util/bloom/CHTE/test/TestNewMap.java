package util.bloom.CHTE.test;

import util.bloom.Apache.Hash.hashing.LongHashFunction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author lgd
 * @date 2021/12/23 15:14
 */
public class TestNewMap {

    private Map<Integer, Queue<Integer>> mapSender = new HashMap<>();
    private Map<Integer, Queue<Integer>> mapReceiver = new HashMap<>();
    private Map<Long,Double>senderTsTable=new HashMap<>();
    private Map<Long,Double>receiverTsTable=new HashMap<>();
    private int MaxRowHave =4;  //一行多少个  经试验 列越大 利用率越高 列为2时 97.7%  列为4时  99.6
    private int MaxInsert=10;
    private int Mod=1024;


    public static LongHashFunction[] LongHashFunction4PosHash = new LongHashFunction[2];

    TestNewMap(){
        //初始2个  hash函数
        for (int i=0;i<2;i++){
            LongHashFunction4PosHash[i] = LongHashFunction.xx(i);
        }
    }

    public int hashPos(Long id,int idex){
        long hashVal = LongHashFunction4PosHash[idex].hashLong(id);
        return (int)Math.abs(hashVal%Mod);
    }
    //第二个hash函数
    public int hashIdentify(Long id,int idex){
//        long hashVal = LongHashFunction4PosHash[idex].hashLong(id);
        return (int)(id&63); //取低6位
    }
    void parseFileSend() throws FileNotFoundException {
        int count1=0;
        Scanner sc = new Scanner(new File("E:\\work\\rdaWriteFile\\send1111.txt"));
        while (sc.hasNext() && count1<1024*4) {

            String readrow = sc.nextLine();
            StringTokenizer st = new StringTokenizer(readrow);
            long id = 0;
            double ts_send = 0;
            double ts_receive = 0;
            int state = 0;
            int count = 0;
            while (st.hasMoreElements()) {
                String val = st.nextToken();
                if (count == 0) {
                    id = Long.parseLong(val);
                } else if (count == 1) {
                    ts_send = Double.parseDouble(val);
                } else if (count == 2) {
                    ts_receive = Double.parseDouble(val);
                } else if (count == 3) {
                    state = Integer.parseInt(val);
                }
                count++;
            }


            int row=hashPos(id,0);
            if (state ==0){
                //丢
                insertFirst(mapSender,id,row);
                senderTsTable.put(id,ts_send);
            }else if (state ==1){
                //乱
                insertFirst(mapReceiver,id,row);
                receiverTsTable.put(id,ts_receive);
            }else {
                //正常
                insertFirst(mapSender,id,row);
                insertFirst(mapReceiver,id,row);
                senderTsTable.put(id,ts_send);
                receiverTsTable.put(id,ts_receive);
            }
            count1++;
        }
    }
    public double caculateUse(Map<Integer, Queue<Integer>> map){
        Collection<Queue<Integer>> values = map.values();
        int sum=0;
        for (Queue<Integer> value : values) {
            sum+=value.size();
        }
        return sum/(double)(senderTsTable.values().size());
    }
    /**
     * 检测一个id
     * @param id
     */
    public void check(Long id){
        int row1 = hashPos(id, 0);
        Queue<Integer> send = mapSender.get(row1);
        int identify=(int)(id&63);
        if (send!=null){
            if (send.contains(identify)){
                System.out.println(row1 +" get");
                return;
            }

        }
        int row2=row1 ^ hashPos((long)identify,0);
        Queue<Integer> send2 = mapSender.get(row2);
        if (send2!=null){
            if (send2.contains(identify)){
                System.out.println(row2 +" 2get");
            }
        }
    }
    //第一次存
    public void insertFirst(Map<Integer, Queue<Integer>> map,Long id,int row){
        Queue<Integer> rowQueue = map.get(row);  //每一行存的是数据包的标识
        int cur_identify=hashIdentify(id,1); //当前数据包的标识
        if (rowQueue!=null){
            if (rowQueue.size()<MaxRowHave){ //这一行没满
                rowQueue.offer(cur_identify); //用第二个hash函数
                map.put(row,rowQueue);
            }else {
                //这一行满了
                int poll_Inentify = rowQueue.poll();  //出队(踢出)  先进先出
                int newRow=row ^ hashPos((long)poll_Inentify,0);  //新的行
                //插入这个传入的
                rowQueue.offer(cur_identify);
                map.put(row,rowQueue);
                insertOut(map,poll_Inentify,newRow,1); //插入到新行
            }
        }else {
            //直接插入
            rowQueue=new LinkedList<>();
            rowQueue.offer(cur_identify);
            map.put(row,rowQueue);

        }

    }
    //被踢出的包再存  比insert多了个踢出次数
    public void insertOut(Map<Integer, Queue<Integer>> map,int identify,int row,int outTime){
        //判断踢出次数
        if (outTime>=MaxInsert){
            System.out.println("数据包标识: "+identify + " 已达到最大踢出次数 ");
            return;
        }
        Queue<Integer> rowQueue = map.get(row);

        if (rowQueue!=null){
            if (rowQueue.size()<MaxRowHave){
                //没满
                rowQueue.offer(identify);
                map.put(row,rowQueue);
            }else {
                //满了
                //踢出一个
                int poll = rowQueue.poll();
                //计算新行
                int newRow=row ^ hashPos((long)poll,1);
                //将这个插入
                rowQueue.offer(identify);
                map.put(row,rowQueue);
                //插入到新行  踢出次数+1
                insertOut(map,poll,newRow,outTime+1);
            }
        }else {
            //直接插入
            rowQueue=new LinkedList<>();
            rowQueue.offer(identify);
            map.put(row,rowQueue);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("哈哈");
        TestNewMap test=new TestNewMap();
        //test.parseFile();
//        test.check((long)1100025919);  //2
//        test.check((long)1100025931);  //2
//        test.check((long)1100025921);  //1
//        test.check((long)1100025930);  //0
        System.out.println(test.caculateUse(test.mapSender));
        System.out.println("finish");
    }
}
