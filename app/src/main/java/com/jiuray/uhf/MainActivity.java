package com.jiuray.uhf;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jiuray.uhf.command.UhfCommandHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///////////////////////////////////
        UhfCommandHelper helper = new UhfCommandHelper();
        helper.reset() ;
        helper.getFirwaremVersion() ;
        helper.inventory(255) ;
        helper.getOutPower() ;

        //////////////////////////////////
    }
}
