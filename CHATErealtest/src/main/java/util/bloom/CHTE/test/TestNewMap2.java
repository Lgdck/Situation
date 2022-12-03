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
    private int MaxRowHave = 4;  //һ�ж��ٸ�  ������ ��Խ�� ������Խ�� ��Ϊ2ʱ 97.7%  ��Ϊ4ʱ  99.6
    private int MaxInsert = 10;//�߳�����
    private int Mod_send = 1024;//���ͷ��ṹA����
    private int Mod_receive = 1024;//���շ��ṹB����
    private int x = 100;//��ʾ���Ͷ˵Ĵ�����һ�ζ�100������20
    private int k = 0;//���ý��ն˵ĳ�ʼ�����Ƿ��Ͷ˵�k��
    private List<String> receiveList = new ArrayList<>();
    private List<String> sendList = new ArrayList<>();
    //    private  int receive_start=0;//���ն˲���B��ʼ��š�
    private int receive_last_match = 0;//���ն����һ��ƥ��ı�š�
    public static LongHashFunction[] LongHashFunction4PosHash = new LongHashFunction[2];
    public int accur = 511;//����Ϊ��4λ15����5λ31����6λ,63;��7λ��127����8λ��255����9λ511��10λ1023����11λ2047��Ĭ�Ϻ�8λ
    private int memory[][] = new int[1000][4];
    private  int ac=1000000;
    TestNewMap2() {
        //��ʼ2��  hash����
        for (int i = 0; i < 2; i++) {
            LongHashFunction4PosHash[i] = LongHashFunction.xx(i);
        }
    }

    public int hashPos(Long id, int idex, int sig) {
        long hashVal = LongHashFunction4PosHash[idex].hashLong(id);
        if (sig == 1)     //�����ڽṹA�е�λ��
            return (int) Math.abs(hashVal % Mod_send);
//            return (int) Math.abs(Math.abs(hashVal) % Mod_send);
        else if (sig == 2)//�����ڽṹB�е�λ��
            return (int) Math.abs(hashVal % Mod_receive);
        return 0;
    }

    //�ڶ���hash����
    public int hashIdentify(Long id, int idex) {
//        long hashVal = LongHashFunction4PosHash[idex].hashLong(id);
        return (int) (id & accur); //ȡ��6λ
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
            String readrow = sendList_i.get(j);//sendList_i�еĵ�i��
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
            String readrow = receiveList_i.get(j);//sendList_i�еĵ�i��
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
            String readrow = sendList_i.get(j);//sendList_i�еĵ�i��
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
        int[] range=getStart(i);//��ȡ����ʼλ��

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
//                System.out.println("+++++" + id + "���ҵ���");
//                tsSum_receiver += ts_receive;
                compere_success++;
                chargeSuccess.add(receiveList_i.get(start_receiveList_i));
//                success_id.add(receiveList_i.get(start_receiveList_i));
            }
            start_receiveList_i++;
        }

        return chargeSuccess;
        //��һ��ֵ��ʾ���н��շ�start��end���ܺ�A��ȷƥ��ĸ������ڶ���ֵ��ʾ��start��������Ƶ��зֵ�֮ǰ��Ч�ĸ�����
        // ������ֵ��ʾ��start��������Ƶ��зֵ�֮ǰ��Ч��ʱ����ĺͣ����ĸ�ֵ��ʾ�зֵ���
    }
    public List<String> chargeReceiver_A_new(int i) {
        int[] range=getStart(i);//��ȡ����ʼλ��
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
                temp_sum+=7;//�������ʮ����Ҫ
            } else {
//                System.out.println("����������������" + id + "û����");
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
        //��һ��ֵ��ʾ���н��շ�start��end���ܺ�A��ȷƥ��ĸ������ڶ���ֵ��ʾ��start��������Ƶ��зֵ�֮ǰ��Ч�ĸ�����
        // ������ֵ��ʾ��start��������Ƶ��зֵ�֮ǰ��Ч��ʱ����ĺͣ����ĸ�ֵ��ʾ�зֵ���
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
     * ���һ��id
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

    //��һ�δ�
    public void insertFirst(Map<Integer, Queue<Integer>> map, Long id, int sig) {

//        System.out.println("1");

        int row = hashPos(id, 0, sig);
        Queue<Integer> rowQueue = map.get(row);  //ÿһ�д�������ݰ��ı�ʶ
        int cur_identify = hashIdentify(id, 1); //��ǰ���ݰ��ı�ʶ
        if (rowQueue != null) {
            if (rowQueue.size() < MaxRowHave) { //��һ��û��
                rowQueue.offer(cur_identify);
                map.put(row, rowQueue);
//                System.out.println("�ü�¼�����뵽�˵�" + row);
            } else {
                //��һ������
                //int poll_Inentify = rowQueue.poll();  //����(�߳�)  �Ƚ��ȳ�

                int newRow = row ^ hashPos((long) cur_identify, 0, sig);  //�µ���
                Queue<Integer> newrowQueue = map.get(newRow);  //ÿһ�д�������ݰ��ı�ʶ
                if (newrowQueue != null) {
                    if (newrowQueue.size() < MaxRowHave) {
                        newrowQueue.offer(cur_identify);
                        map.put(newRow, newrowQueue);
                        //System.out.println("�ü�¼�����뵽�˵�"+newRow);
                    } else {
                        //������������
//                        System.out.println("���������ˣ����߳���");
                        int poll_Inentify = newrowQueue.poll();  //����(�߳�)  �Ƚ��ȳ�
                        newrowQueue.offer(cur_identify);
                        map.put(row, newrowQueue);
                        insertOut(map, poll_Inentify, newRow, 1, sig); //���뵽����
                    }
                }
//                System.out.println("�ü�¼�����뵽�˵�" + newRow);
            }
        } else {
            //ֱ�Ӳ���
            rowQueue = new LinkedList<>();
            rowQueue.offer(cur_identify);
            map.put(row, rowQueue);
//            System.out.println("�ü�¼�����뵽�˵�" + row);
        }

    }

    //���߳��İ��ٴ�  ��insert���˸��߳�����
    public void insertOut(Map<Integer, Queue<Integer>> map, int identify, int row, int outTime, int sig) {
        //System.out.println("������һ���߳��¼�");
        //�ж��߳�����
        if (outTime >= MaxInsert) {
            System.out.println("���ݰ���ʶ: " + identify + " �Ѵﵽ����߳����� ");
            return;
        }
        int newRow = row ^ hashPos((long) identify, 0, sig);
        Queue<Integer> rowQueue = map.get(newRow);

        if (rowQueue != null) {
            if (rowQueue.size() < MaxRowHave) {
                //û��
                rowQueue.offer(identify);
                map.put(newRow, rowQueue);
            } else {
                //����
                //�߳�һ��
                int poll = rowQueue.poll();
                //��������
                //int newRow = row ^ hashPos((long) poll, 1,sig);
                //���������
                rowQueue.offer(identify);
                map.put(newRow, rowQueue);
                //���뵽����  �߳�����+1
                insertOut(map, poll, newRow, outTime + 1, sig);
            }
        } else {
            //ֱ�Ӳ���
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
//            out.write(String.valueOf(result[i][1]-result[i][0]+1));//���շ���ȡ�˶��ٸ�
//            out.write("\r");
//            out.write(String.valueOf(result[i][2]));//���շ���ȡ����Ч����
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
        System.out.println("����");
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

            //��һ�������ڵ������
            String file1 = "/Users/codersavior/Documents/study project/HuaWei Paper/realtest725/725/test8_send.txt";
            //�ڶ��������ڵ������
            String file2 = "/Users/codersavior/Documents/study project/HuaWei Paper/realtest725/725/test8_45.txt";
            //�����������ڵ������
//            String file3 = "/Users/codersavior/Documents/study project/HuaWei Paper/realtest721/test2/test2-44.txt";
            //���ĸ������ڵ������
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
            int receive_start = 0;//���ն˲���B��ʼ��š�
            double result[][] = new double[200][5];
            double temp_sum=0;
            double temp_sum_pow=0;
            double count_sum=0;
            double var_avg=0;
            int lala=0;
            for (int i = 0; i <195; i++) {//��ʾ���������

                //�����is�ж�������
                List<String> sendList_i=test.setRange(i,range_start);
                range_start+=sendList_i.size();
                //����CHATEͰ����
                test.setMapSender(sendList_i);
                test.parseFileSend(sendList_i,i);//��send��list��ӵ��ṹmapSend��
                List<String> result_Receiver_A = test.chargeReceiver_A_new(i);//��һ��ֵ�����ð��ĸ������ڶ���ֵ���������ݰ�ʱ����ĺ�,������ֵ���з�λ�á�
                double ts_useful_Receiver_A=test.getTSsum(result_Receiver_A);
//            System.out.println(result_Receiver_A);

                test.setMapReceiver(i, result_Receiver_A);
                test.parseFileReceive(i, result_Receiver_A);//��receive��list��ӵ��ṹmapReceive��,�ڶ��������ǿ�ʼλ�ã������������ǽ���λ��
                double[] result_Sender_B = test.chargeSender_B(i,sendList_i);//��һ��ֵ��ǰһ�ٸ�������ֵ���ڶ���ֵ��ǰһ������ֵʱ����ĺ�

//                double temp[] = {receive_start, result_Receiver_A[3], result_Receiver_A[1], result_Sender_B[0], result_Receiver_A[2], result_Sender_B[1]};
                result[i][0] = result_Receiver_A.size();
                result[i][1] = ts_useful_Receiver_A;//���շ��и���λ��
                result[i][2] = result_Sender_B[0];//��ʾ���շ���start��������Ƶ��зֵ�֮ǰ��Ч�ĸ���
                result[i][3] = result_Sender_B[1];
                if(result[i][0]==0)
                    result[i][4]=0;
                else {
                    result[i][4] = result[i][1] / result[i][0] - result[i][3] / result[i][2];//��ʾ���շ���start��������Ƶ��зֵ�֮ǰ��Ч��ʱ����ĺ�
                    lala++;
                }
//                result[i][5] = ;
//                result[i][6] = result[i][4] / result[i][2] - result[i][5] / result[i][3];
//                receive_start = (int) result_Receiver_A[3] +1;
                //����������εĻ��Ҫ���һ�»��������map
                test.mapSender.clear();
                test.mapReceiver.clear();
                temp_sum+=result[i][4];
                temp_sum_pow+=result[i][4]*result[i][0];//ÿһ���������ڵļ�Ȩ��
                count_sum+=result[i][0];
            }
            double avg_pow=temp_sum_pow/count_sum;//��Ȩƽ����
            for (int k = 0; k < 200; k++) {
                var_avg+=Math.pow(result[k][4]-avg_pow,2)*result[k][0];
            }
            //System.out.println(result);
            avg[j]=temp_sum/lala;

            String a = String.valueOf(avg[j]);
            //��һ��ƽ��ʱ�ӣ��ڶ����Ǽ�Ȩƽ��ʱ�ӣ������б�ʾ��Ȩ��������б�ʾƽ����Ч���ݰ�����
            out.write(a+"    "+String.valueOf(temp_sum_pow/count_sum)+"    "+String.valueOf(Math.sqrt(var_avg/count_sum))+"    "+String.valueOf(count_sum/lala));
            out.write("\r\n");

//            //�������ѽ��д���ļ���
            test.writeResultToFile(result,j);
        }
        out.close();
//        System.out.println(avg);
    }




}
