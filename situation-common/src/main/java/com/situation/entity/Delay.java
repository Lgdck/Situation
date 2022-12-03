package com.situation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author lgd
 * @date 2022/11/8 11:08
 */
@Data
@Table(name = "delay")
@NoArgsConstructor
@AllArgsConstructor
public class Delay {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "`connect`")
    private String connect;

    @Column(name = "pro_type")
    private String pro_type;

    @Column(name = "send_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date send_time;

    @Column(name = "time_us")
    private Integer time_us;

    @Column(name = "`delay1`")
    private Integer delay1;

    @Column(name = "`delay2`")
    private Integer delay2;

    @Column(name = "`length`")
    private Integer length;

    @Column(name = "`interval`")
    private Integer interval;
}
