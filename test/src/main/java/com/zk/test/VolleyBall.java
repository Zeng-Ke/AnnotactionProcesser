package com.zk.test;


import com.zk.annotation_lib.Factory;

/**
 * author: ZK.
 * date:   On 2018-10-29.
 */
@Factory(id = "VolleyBall",type = Ball.class)
public class VolleyBall implements Ball {
    @Override
    public void play() {
        System.out.println("da paiqiu");
    }
}
