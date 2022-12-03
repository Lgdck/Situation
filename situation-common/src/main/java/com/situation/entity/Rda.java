package com.situation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author lgd
 * @date 2022/11/28 16:14
 */
public class Rda {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    @Column(name = "count")
    private Integer count;

    @Column(name = "`avg_delay`")
    private Integer avg_delay;

    @Column(name = "`avg_length`")
    private Integer avg_length;

}
