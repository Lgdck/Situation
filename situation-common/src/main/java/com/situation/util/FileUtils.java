package com.situation.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件工具类
 * @author lgd
 * @date 2021/11/2 16:15
 */
public class FileUtils {

    public static File MultipartFileConvertToFile(MultipartFile multipartFile){
        String filename = multipartFile.getOriginalFilename();

        try {
            File file=File.createTempFile(filename,"");
            multipartFile.transferTo(file);

            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
