package util.bloom.CHTE.test;

import edu.NUDT.RDA.parallel.PacketTraceGenerator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * @author lgd
 * @date 2021/12/20 9:35
 */
public class TestWeibull {

    private PacketTraceGenerator pg;
    static final double scale=16;//初始值6.647;
    static final double shape=0.9;//初始值0.7;
    //static final double dropProb=0.4;
    //static final double lossProb=0.4;
    double dropProb=0;
    double lossProb=0;

    TestWeibull(double i, double j){
        long initialTS = 1;
        long seed = System.currentTimeMillis();
        Random r = new Random(seed);
        long _startID = Math.abs(r.nextInt());//开始行
        dropProb=i/2/10;
        lossProb=j/2/10;
        pg=new PacketTraceGenerator(scale,shape,dropProb,lossProb,initialTS,_startID);  //初始韦伯分布 乱丢  的
    }

    public void generatePacketTraceToFile(String totalFileName, double a, double b) throws IOException {
//        int q= (int) ((int) (a-1)*4+b);
//        String filename="/Users/codersavior/Documents/study project/HuaWei Paper/"+String.valueOf(q)+"/";
//        File file=new File(filename+totalFileName);
        FileOutputStream outputStream1=new FileOutputStream(totalFileName);
//        FileOutputStream outputStream2=new FileOutputStream(new File(receiveFileName));
        outputStream1.write(("id              "+"sendTS  "+"      receiveTs     "+"state\r\n").getBytes());

        for (int i=0;i<500000;i++){
            double[] doubles = pg.NextWeibullPacket(); //发送TS
            int state = pg.dropOrReorder();
            StringBuilder sb1=new StringBuilder();
            StringBuilder sb2=new StringBuilder();
            sb1.append((int)(doubles[0]%Integer.MAX_VALUE)+"    "+doubles[1]+"     "+doubles[2]+"     "+state+"     "+doubles[3]+"\r\n");
//            sb2.append(doubles[0]+"    "+doubles[2]+"     "+state+"\r\n");
            outputStream1.write(sb1.toString().getBytes());
//            outputStream2.write(sb2.toString().getBytes());
        }
        outputStream1.close();
//        outputStream2.close();
    }
    public double[] writeCorrectResultToFile(Double[][] result, double p, double j) throws IOException {
        int q= (int) ((int) (p-1)*6+j);
        double[] fin_result=new double[4];
        double avg=0;
        double var_avg=0;
        double count=0;
        double avg_pow=0;
        String filename="/Users/codersavior/Documents/study project/HuaWei Paper/experiment3/"+String.valueOf(q)+"/";
        File file=new File(filename+"corrcetAvg.txt");
        FileWriter out= new FileWriter(file);
        for (int i=0;i< result.length;i++){
            String a=String.valueOf(result[i][0]);
            out.write(a);
            out.write("\r\n");
            avg+=result[i][0];
            avg_pow+=result[i][0]*result[i][1];
            count+=result[i][1];
        }
        out.close();
        fin_result[0]=avg/result.length;//1000个计算周期的时延平均数
        fin_result[1]=avg_pow/count;//加权算数平均数
        for (int i=0;i< result.length;i++){
//            String a=String.valueOf(result[i][0]);
//            out.write(a);
//            out.write("\r\n");
            var_avg+=Math.pow(result[i][0]-fin_result[1],2)*result[i][1];
        }

        fin_result[2]=Math.sqrt(var_avg/count);//方差
        fin_result[3]=count/result.length;//平均有效个数
        return fin_result;

    }

    public static void main(String[] args) throws IOException {
        String total_file_correctAvg="/Users/codersavior/Documents/study project/HuaWei Paper/experiment3/";
        File file_total_file_correctAvg=new File(total_file_correctAvg+"total_file_correctAvg.txt");
        FileWriter out= new FileWriter(file_total_file_correctAvg);
        for (double i=1;i<=1;i++){
            for (double j=1;j<=1;j++){
                TestWeibull a=new TestWeibull(i,j);
                int q= (int) ((int) (i-1)*6+j);
                String filename="/Users/codersavior/Documents/study project/HuaWei Paper/experiment3/"+String.valueOf(q);
                Path path= Paths.get(filename);
                Path pathCreate= Files.createDirectories(path);
                File f=new File(filename);
                FileUtil fileUtil=new FileUtil();
                a.generatePacketTraceToFile(filename+"/total.txt",i,j);
                System.out.println("数据生成完成");
               //计算真实重排序率
                List<List<String>> lists=fileUtil.calculOutOfBorderPerHundred(filename+"/total.txt");
                //System.out.println(lists);
                System.out.println(fileUtil.calAverage(lists));

                fileUtil.writeTotal2SendFile(new File(filename+"/total.txt"),i,j);
//        //将total文件中接收方的拆分出来，并排序
                fileUtil.sortReceiveTsAndChange1To2File(new File(filename+"/total.txt"),i,j);
                //每100个包计算一下平均值
                Double[][] avgPre100=fileUtil.calculateAvgTs(new File(filename+"/total.txt"),100);
                //将平均值写入到文件"corrcetAvg.txt"中
                double[] avg=a.writeCorrectResultToFile(avgPre100,i,j);
                System.out.println(avgPre100);
                out.write(String.valueOf(avg[0])+"    "+String.valueOf(avg[1])+"    "+String.valueOf(avg[2])+"    "+String.valueOf(avg[3]));
                //第一列表示每个数据集的平均时延，第二列表示加权平均时延，第三列表示加权标准差，第四列表示平均有效数据包个数。
                out.write("\r\n");
            }

        }
        out.close();
        System.out.println("finished");
//        TestWeibull a=new TestWeibull(i, j);


    }

}
