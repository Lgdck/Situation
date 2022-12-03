package util.bloom.CHTE.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class count {
    private List<String> sendList = new ArrayList<>();
    count(){

    }
    public void writeResultToFile(int[] result, int j) throws IOException {
        String ba="result-";
        String n=String.valueOf(j+1);
        String re=ba+n+".txt";
        File file = new File(re);
        FileWriter out = new FileWriter(file);
        for (int i = 0; i < result.length; i++) {
            //String a = String.valueOf(result[i][6]);
            out.write(String.valueOf(result[i])+"\n");
//            out.write("\r");
//            out.write(String.valueOf(result[i][1]-result[i][0]+1));//接收方截取了多少个
//            out.write("\r");
//            out.write(String.valueOf(result[i][2]));//接收方截取的有效个数
//            out.write("\n");
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
    public static void main(String[] args)throws IOException {
//        FileWriter out = new FileWriter(file);
        for (int j=0;j<1;j++){
        int range_start=0;
        count test=new count();
            String file1 = "/Users/codersavior/Documents/study project/HuaWei Paper/realtest721/721/test6/test6-12345/test6-send.txt";
            //第二个测量节点的数据
            String file2 = "/Users/codersavior/Documents/study project/HuaWei Paper/realtest721/721/test6/test6-12345/test6-45.txt";
            //第三个测量节点的数据
//            String file3 = "/Users/codersavior/Documents/study project/HuaWei Paper/realtest721/test2/test2-44.txt";
            //第四个测量节点的数据
            String file4 = "/Users/codersavior/Documents/study project/HuaWei Paper/realtest721/721/test6/test6-12345/test6-server.txt";
        String fileA="";
        String fileB="";
        int result[]= new int [200];
        switch (j){
            case 0:
                fileA= file1;

                break;
            case 1:
                fileA= file2;
//                fileB= file4;
                break;
            case 2:
                fileA= file4;
//                fileB= file4;
                break;
        }
        test.sendList=test.getFileToList(new File(fileA));
        for(int i=0;i<195;i++){
            List<String> sendList_i=test.setRange(i,range_start);
            range_start+=sendList_i.size();
            result[i]=sendList_i.size();
        }
            test.writeResultToFile(result,j);

    }


    }
}
