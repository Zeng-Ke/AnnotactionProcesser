package com.zk.annotactionprocesser.shop;

import android.util.Log;

import com.zk.annotactionprocesser.Car;
import com.zk.annotation_lib.Factory;

/**
 * author: ZK.
 * date:   On 2018-10-15.
 */

@Factory(
        id = "Benz",
        type = Car.class
)
public class Benz implements Car {

    @Override
    public void drive() {
        Log.d("===============","奔驰上路");
    }
}
