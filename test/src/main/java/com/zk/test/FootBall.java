package com.zk.test;

import com.zk.annotation_lib.Factory;

/**
 * author: ZK.
 * date:   On 2018-10-29.
 */
@Factory(id = "FootBall",type = Ball.class)
public class FootBall implements Ball {
    @Override
    public void play() {
        System.out.println("ti zuqiu");
    }
}
