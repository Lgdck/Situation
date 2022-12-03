package util.bloom.CHTE;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class PairRDAHostNew {
    private SimpleRDAMeasurementPoint _hostA;
    private SimpleRDAMeasurementPoint _hostB;
    private List<String> receiveList = new ArrayList<>();
    private List<String> sendList = new ArrayList<>();
    private int hashFuncNum=2;

    public void PairRDAHost() {
        for(int i=0;i<100;i++) {
            int result[]=readList(i);
            //初始化发送方和接收方的RDA
            _hostA = new SimpleRDAMeasurementPoint(128, 100, hashFuncNum);
            _hostB = new SimpleRDAMeasurementPoint(128, result[1]-result[0], hashFuncNum);
            //将发送方和接收方插入到初始化好的结构中
            insertTable(_hostA,i*100,(i+1)*100);
            insertTable(_hostB,result[0],result[1]);
//            countDelay();
        }
    }

    private void insertTable(SimpleRDAMeasurementPoint host, int start, int end) {
        List<String> sendList_i = sendList.subList(start,end );
        for (int j = 0; j <end-start; j++) {
            String readrow = sendList_i.get(j);//sendList_i中的第i行
            StringTokenizer st = new StringTokenizer(readrow);
            long id = 0;
            double ts = 0;
            //double ts_receive = 0;
            int state = 0;
            int count = 0;
            while (st.hasMoreElements()) {
                String val = st.nextToken();
                if (count == 0) {
                    id = Long.parseLong(val);
                } else if (count == 1) {
                    ts = Double.parseDouble(val);
                } else if (count == 2) {
                    state = Integer.parseInt(val);
                }
                count++;
            }
            host.packetIncoming(id,ts);

        }
    }



    public  int[] readList(int round){
        int start=0;
        int end=0;
        int temp=0;
        int[] result=new int[2];
        while(receiveList!=null){
            String readrow=sendList.get(temp);
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
            if(ts_send<1000*round){
                start++;
                end++;
                continue;
            }else if(ts_send<(round+1)*1000) {
                temp++;
                end++;
            }else
                break;
        }
        result[0]=start;result[1]=end;
        return result;
    }
    //把txt'写到list中
    public List<String> readFileTXT(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        List<String> res = new ArrayList<>();
        while (scanner.hasNext()) {
            res.add(scanner.nextLine());
        }
        return res;
    }


    public static void main(String[] args) throws FileNotFoundException {
        PairRDAHostNew test =new PairRDAHostNew();
        //读取记录存到sendList和receiveList中
        test.sendList=test.readFileTXT(new File("/Users/codersavior/Documents/study project/HuaWei Paper/基于态势感知的网络时延测量方法/网络时延精确计算/RDA2/send.txt"));
        test.receiveList=test.readFileTXT(new File("/Users/codersavior/Documents/study project/HuaWei Paper/基于态势感知的网络时延测量方法/网络时延精确计算/RDA2/sortReceive.txt"));
        test.PairRDAHost();
    }


}
