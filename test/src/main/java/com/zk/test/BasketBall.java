package com.zk.test;

import com.zk.annotation_lib.Factory;

/**
 * author: ZK.
 * date:   On 2018-10-29.
 */
@Factory(id = "BasketBall",type = Ball.class)
public class BasketBall implements Ball {
    @Override
    public void play() {
        System.out.println("da paiqiu");
    }
}
