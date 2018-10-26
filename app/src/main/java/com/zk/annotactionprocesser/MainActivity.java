package com.zk.annotactionprocesser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zk.annotactionprocesser.shop.Audi;
import com.zk.annotation_lib.Factory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* CarFactory carFactory = new CarFactory();
        carFactory.create("Benz").drive();*/

       create(Audi.class).drive();

    }

    public Car create(Class<? extends Car> clz) {
        try {
            return clz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


}
