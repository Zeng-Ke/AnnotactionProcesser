package com.zk.annotactionprocesser.shop;

import com.zk.annotactionprocesser.Car;
import com.zk.annotation_lib.Factory;

/**
 * author: ZK.
 * date:   On 2018-10-29.
 */


@Factory(
        id = "Porsche",
        type = Car.class
)
public class Porsche implements Car {
    @Override
    public void drive() {

    }
}
