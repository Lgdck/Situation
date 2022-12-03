package util.bloom.CHTE.parse;


import java.io.File;
import java.io.IOException;

import static util.bloom.CHTE.parse.Service.CalService.*;
/**
 * @author lgd
 * @date 2022/3/19 18:50
 */
public class ParseWirshark {
    public static void main(String[] args) throws IOException {
        //1   解析wireshark -> txt   发送端 接收端都要执行
        /*
        File pcapFile = new File("E:\\work\\cheliang\\tcp\\b-1.pcap");
        FileOutputStream out=new FileOutputStream("E:\\work\\cheliang\\tcp\\b.txt");
        ParseService service=new ParseService();
        service.parseFile(pcapFile,out);
        */


        //2   对齐两个file   即  使两个txt的 第一个对齐的为基准
/*
        File pcapFile = new File("E:\\work\\cheliang\\ftp\\server1.txt");
        FileOutputStream out=new FileOutputStream("E:\\work\\cheliang\\ftp\\server1-xiuzeng.txt");
        ParseService service=new ParseService();
        service.chuliFileTimeCha(pcapFile,out,728249440);

*/
        //3   计算发送端 接收端的时延  包含时钟偏移

        File src=new File("E:\\work\\cheliang\\tcp\\a.txt");
        File des=new File("E:\\work\\cheliang\\tcp\\b.txt");
        File offset1=new File("E:\\work\\cheliang\\tcp\\offset.txt");
//        File offset2=new File("E:\\work\\cheliang\\ftp\\offset2.txt");
        calTwoFile2(src,des,offset1);
//        CalService.calTwoFile3(des,src,offset1,offset2);
//        CalService.calTwoFile2(src,des,offset1);
//        CalService.addOffsetToFile(des,src,offset1,offset2);
          //存数据库
//        File file =new File("E:\\work\\5g\\5g_main\\situation-web-vision\\src\\main\\java\\lgd\\1.pcap");
//        ParseAll.parse(file);

        //File totalFile=new File("E:\\work\\rda\\1.txt");
        //DevideFile.DevideFile(totalFile);
    }


}
