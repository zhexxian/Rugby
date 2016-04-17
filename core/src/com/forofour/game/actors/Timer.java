package com.forofour.game.actors;

import com.badlogic.gdx.Gdx;

/**
 * Stopwatch to help keep track of in game time
 *  as well as for use in addtion of scores
 */
public class Timer {

    private boolean done = false;

    private int gameDuration = 60; // Default timing
    private long endTime = gameDuration * 1000;

    private final long nanosPerMilli = 1000000;
    private long startTime = 0;
    private long stopTime = 0;
    private boolean running = false;
    private boolean paused = false;
    private long startPause = 0;

    long elapsed;

    public Timer(){
    }
    public Timer(int gameDuration){
        this.gameDuration = gameDuration;
        this.endTime = gameDuration * 1000;
    }

    public void setGameDuration(int duration) {
        this.gameDuration = duration;
        this.endTime = duration * 1000;
    }

    public boolean isRunning() {
        return running;
    }

    // Start measuring
    public void start() {
        if(paused) {
            startTime += System.nanoTime() -  startPause;
            paused = false;
        }
        else if(!running) {
            this.startTime = System.nanoTime();
            this.running = true;
            this.paused = false;
            this.startPause = 0;
        }
        else {
//            Gdx.app.log("Timer", getElapsed());
        }
    }

    public void pause() {
        if(!paused) {
            paused = true;
            startPause = System.nanoTime();
        }
    }

    // Stop measuring
    public void stop() {
        this.stopTime = System.nanoTime();
        this.paused = true;
        this.done = true;
    }

    // Reset
    public void reset() {
        this.startTime = 0;
        this.stopTime = 0;
        this.running = false;

        this.startPause = 0;
        this.paused = false;

        this.done = false;
    }

    public void update() {
        if (running && paused) {
            elapsed = (System.nanoTime() - startTime) + (System.nanoTime() -  startPause);
        }
        else if(running && !paused) {
            elapsed = (System.nanoTime() - startTime);
        }
        else {
            elapsed = (stopTime - startTime);
        }

//        Gdx.app.log("Timer",elapsed + " " + endTime*nanosPerMilli);

        if(elapsed >= endTime * nanosPerMilli){
            stop();
            Gdx.app.log("Done", "True");
        }
    }

    // Get elapsed milliseconds
    public long getElapsedMilliseconds() {
        return elapsed / nanosPerMilli;
    }
    // Get elapsed milliseconds

    public long getElapsedSeconds() {
        return getElapsedMilliseconds()/1000;
    }

    public boolean isDone(){
        return done;
    }

    public void setElapsedMilliseconds(long targetElapsed){
        if(running && !paused)
            startTime = System.nanoTime() - targetElapsed;
    }

    // Get formatted elapsed time
    public String getElapsed() {
        String timeFormatted = "";
        timeFormatted = formatTime(getElapsedMilliseconds());
        return timeFormatted;
    }

    // Helper method splits time into minutes, seconds, hundredths, and formats
    public static String formatTime(final long millis) {
        int minutesComponent = (int)(millis / (1000 * 60));
        int secondsComponent = (int)((millis / 1000) % 60);
        int hundredthsComponent = (int)((millis / 10) % 100);
        String paddedMinutes = String.format("%02d", minutesComponent);
        String paddedSeconds = String.format("%02d", secondsComponent);
        String paddedHundredths = String.format("%02d", hundredthsComponent);
        String formattedTime;
        if ((millis>0)&&(millis<3600000)) {
            formattedTime = paddedMinutes+":"+paddedSeconds;
//            formattedTime = paddedMinutes+":"+paddedSeconds+":"+paddedHundredths;
        }
        else {
            formattedTime = 59+":"+59;
//            formattedTime = 59+":"+59+":"+99;
        }
        return formattedTime;
    }
}
