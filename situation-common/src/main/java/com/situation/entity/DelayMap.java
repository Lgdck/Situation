package com.situation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author lgd
 * @date 2022/11/28 19:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DelayMap {

    private Date time;

    private int count1;

    private double avg1;

    private int count2;


    private double avg2;

    private int avgLength;

    private double loss;
}
