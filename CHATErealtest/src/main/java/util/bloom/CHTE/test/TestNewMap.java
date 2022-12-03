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
    private int MaxRowHave =4;  //һ�ж��ٸ�  ������ ��Խ�� ������Խ�� ��Ϊ2ʱ 97.7%  ��Ϊ4ʱ  99.6
    private int MaxInsert=10;
    private int Mod=1024;


    public static LongHashFunction[] LongHashFunction4PosHash = new LongHashFunction[2];

    TestNewMap(){
        //��ʼ2��  hash����
        for (int i=0;i<2;i++){
            LongHashFunction4PosHash[i] = LongHashFunction.xx(i);
        }
    }

    public int hashPos(Long id,int idex){
        long hashVal = LongHashFunction4PosHash[idex].hashLong(id);
        return (int)Math.abs(hashVal%Mod);
    }
    //�ڶ���hash����
    public int hashIdentify(Long id,int idex){
//        long hashVal = LongHashFunction4PosHash[idex].hashLong(id);
        return (int)(id&63); //ȡ��6λ
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
                //��
                insertFirst(mapSender,id,row);
                senderTsTable.put(id,ts_send);
            }else if (state ==1){
                //��
                insertFirst(mapReceiver,id,row);
                receiverTsTable.put(id,ts_receive);
            }else {
                //����
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
     * ���һ��id
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
    //��һ�δ�
    public void insertFirst(Map<Integer, Queue<Integer>> map,Long id,int row){
        Queue<Integer> rowQueue = map.get(row);  //ÿһ�д�������ݰ��ı�ʶ
        int cur_identify=hashIdentify(id,1); //��ǰ���ݰ��ı�ʶ
        if (rowQueue!=null){
            if (rowQueue.size()<MaxRowHave){ //��һ��û��
                rowQueue.offer(cur_identify); //�õڶ���hash����
                map.put(row,rowQueue);
            }else {
                //��һ������
                int poll_Inentify = rowQueue.poll();  //����(�߳�)  �Ƚ��ȳ�
                int newRow=row ^ hashPos((long)poll_Inentify,0);  //�µ���
                //������������
                rowQueue.offer(cur_identify);
                map.put(row,rowQueue);
                insertOut(map,poll_Inentify,newRow,1); //���뵽����
            }
        }else {
            //ֱ�Ӳ���
            rowQueue=new LinkedList<>();
            rowQueue.offer(cur_identify);
            map.put(row,rowQueue);

        }

    }
    //���߳��İ��ٴ�  ��insert���˸��߳�����
    public void insertOut(Map<Integer, Queue<Integer>> map,int identify,int row,int outTime){
        //�ж��߳�����
        if (outTime>=MaxInsert){
            System.out.println("���ݰ���ʶ: "+identify + " �Ѵﵽ����߳����� ");
            return;
        }
        Queue<Integer> rowQueue = map.get(row);

        if (rowQueue!=null){
            if (rowQueue.size()<MaxRowHave){
                //û��
                rowQueue.offer(identify);
                map.put(row,rowQueue);
            }else {
                //����
                //�߳�һ��
                int poll = rowQueue.poll();
                //��������
                int newRow=row ^ hashPos((long)poll,1);
                //���������
                rowQueue.offer(identify);
                map.put(row,rowQueue);
                //���뵽����  �߳�����+1
                insertOut(map,poll,newRow,outTime+1);
            }
        }else {
            //ֱ�Ӳ���
            rowQueue=new LinkedList<>();
            rowQueue.offer(identify);
            map.put(row,rowQueue);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("����");
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
