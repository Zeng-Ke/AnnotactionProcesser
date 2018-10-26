package com.zk.annotactionprocesser.shop;

import android.util.Log;

import com.zk.annotactionprocesser.Car;
import com.zk.annotation_lib.Factory;

/**
 * author: ZK.
 * date:   On 2018-10-15.
 */

@Factory(
        id = "BMW",
        type = Car.class
)
public class BMW implements Car {

    @Override
    public void drive() {
        Log.d("===============","宝马上路");
    }
}
