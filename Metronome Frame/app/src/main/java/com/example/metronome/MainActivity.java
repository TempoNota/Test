package com.example.metronome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    //TODO: Layout stuff
    Clock clock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        clock = new Clock();
        //TODO: Excuse to run
        clock.run();
        clock.stop();
    }
}
