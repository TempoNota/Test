package com.example.jared.tuner;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView output;
    private Switch onOffSwitch;

    private Tuner tuner;

    private Thread frequencyUpdateThread;

    private boolean isRunning = false;

    //TODO: Unobtrusive threading solution
    //Theory: The handler can make it possible to evaluate frequency on another thread,
    // and pass it to the UIThread to quickly change the view

    private Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            if(message.what == Tuner.FREQUENCY_MESSAGE) {
                output.setText(String.format("~%08.2f Hz", message.getData().getDouble("frequency")));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tuner = new Tuner(uiHandler);
        resetThread();

        output = (TextView) findViewById(R.id.hertzTV);

        onOffSwitch = (Switch) findViewById(R.id.switchButton);
        onOffSwitch.setOnCheckedChangeListener(switchListener);
    }

    private Runnable viewUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            isRunning = true;

            while(isRunning) {
                tuner.handleFrequencyState();
            }
        }
    };



    private void stop() {
        isRunning = false;
    }

    private void resetThread() {
        frequencyUpdateThread = new Thread(viewUpdateRunnable, "frequencyUpdateThread");
    }

    private Switch.OnCheckedChangeListener switchListener =
    new Switch.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                tuner.start();
                //TODO: See if this sleep works
                try {
                    Thread.currentThread().sleep(1024);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    //https://www.ibm.com/developerworks/library/j-jtp05236/
                    //https://www.yegor256.com/2015/10/20/interrupted-exception.html
                    throw new RuntimeException();
                }
                frequencyUpdateThread.start();

            } else {
                stop();
                tuner.stop();
                try {
                    frequencyUpdateThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    //https://www.ibm.com/developerworks/library/j-jtp05236/
                    //https://www.yegor256.com/2015/10/20/interrupted-exception.html
                    throw new RuntimeException();
                }
                resetThread();
            }
        }
    };
}
