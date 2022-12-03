package com.situation.util;

import org.apache.poi.ss.formula.functions.T;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * @author lgd
 * @date 2022/11/12 15:14
 */
public class DoubleUtil {

    //保留指定位小数


    public static String convertDivideDouble(int a, int b){
        NumberFormat instance = NumberFormat.getInstance();
        instance.setMaximumIntegerDigits(3);
        return instance.format(a/(double)b);
    }
}
