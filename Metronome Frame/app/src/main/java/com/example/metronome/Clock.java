package com.example.metronome;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Clock implements Runnable {

    private ScheduledExecutorService timingExecutor = Executors.newScheduledThreadPool(1);
    private Runnable services = new Runnable() {
        @Override
        public void run() {
            //TODO: All appropriate tasks for the pulses (i.e. view change, sound, update a counter, etc.)

            //NOTE: Make these very quick and simple
        }
    };

    private int beatsPerMinute;
    private int beatsPerMeasure;

    //TODO: Add instance variables for views and clock-ish such

    public Clock(/*TODO: Pass through appropriate layout views and clock-ish such*/) {
        super();

        //TODO: INSTANTIATE VARIABLES!!!
    }

    @Override
    public void run() {
        timingExecutor.scheduleAtFixedRate(services, 0, this.getPeriod(), TimeUnit.MILLISECONDS);
    }

    public void stop() {
        timingExecutor.shutdown();
    }

    public long getPeriod() {
        return 0; //TODO: Your math(s)y stuff for the timing of a pulse in MILLISECONDS
    }

}
