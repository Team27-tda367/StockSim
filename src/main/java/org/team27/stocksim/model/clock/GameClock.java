package org.team27.stocksim.model.clock;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class GameClock extends Clock {

    private final ZoneId zone;
    private Instant simStart; // vilken simtid motsvarade när vi började
    private long realStartNanos; // System.nanoTime() när vi satte simStart
    private double speed; // 1.0 = realtid, 60.0 = 60x osv

    public GameClock(ZoneId zone, Instant initialSimTime, double speed) {
        this.zone = zone;
        this.simStart = initialSimTime;
        this.realStartNanos = System.nanoTime();
        this.speed = speed;
    }

    public GameClock() {
        this(ZoneId.systemDefault(), Instant.EPOCH, 1.0);
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new GameClock(zone, instant(), speed);
    }

    @Override
    public Instant instant() {
        long nanosSinceStart = System.nanoTime() - realStartNanos;
        long scaledNanos = (long) (nanosSinceStart * speed);
        return simStart.plusNanos(scaledNanos);
    }

    public void setSpeed(double newSpeed) {
        // Frys nuvarande simtid och starta om referensen
        Instant nowSim = instant();
        this.simStart = nowSim;
        this.realStartNanos = System.nanoTime();
        this.speed = newSpeed;
    }

    public double getSpeed() {
        return speed;
    }
}
