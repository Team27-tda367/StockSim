package org.team27.stocksim.model.clock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameClock Tests")
class GameClockTest {

    private ZoneId testZone;
    private Instant testStartTime;
    private double testSpeed;

    @BeforeEach
    void setUp() {
        testZone = ZoneId.of("Europe/Stockholm");
        testStartTime = Instant.parse("2024-01-15T10:00:00Z");
        testSpeed = 1.0;
    }

    @Test
    @DisplayName("Should create game clock with correct properties")
    void testCreateGameClock() {
        GameClock clock = new GameClock(testZone, testStartTime, testSpeed);

        assertNotNull(clock);
        assertEquals(testZone, clock.getZone());
        assertEquals(testSpeed, clock.getSpeed());
    }

    @Test
    @DisplayName("Should return instant at normal speed")
    void testInstantAtNormalSpeed() throws InterruptedException {
        GameClock clock = new GameClock(testZone, testStartTime, 1.0);

        Instant start = clock.instant();
        Thread.sleep(100); // Wait 100ms
        Instant end = clock.instant();

        assertTrue(end.isAfter(start));
        long diffMillis = end.toEpochMilli() - start.toEpochMilli();
        assertTrue(diffMillis >= 90 && diffMillis <= 150); // Allow some tolerance
    }

    @Test
    @DisplayName("Should accelerate time at 60x speed")
    void testAcceleratedSpeed() throws InterruptedException {
        GameClock clock = new GameClock(testZone, testStartTime, 60.0);

        Instant start = clock.instant();
        Thread.sleep(100); // Wait 100ms real time
        Instant end = clock.instant();

        long diffMillis = end.toEpochMilli() - start.toEpochMilli();
        // Should be approximately 100ms * 60 = 6000ms
        assertTrue(diffMillis >= 5000 && diffMillis <= 7000);
    }

    @Test
    @DisplayName("Should slow down time at 0.5x speed")
    void testSlowedSpeed() throws InterruptedException {
        GameClock clock = new GameClock(testZone, testStartTime, 0.5);

        Instant start = clock.instant();
        Thread.sleep(100); // Wait 100ms real time
        Instant end = clock.instant();

        long diffMillis = end.toEpochMilli() - start.toEpochMilli();
        // Should be approximately 100ms * 0.5 = 50ms
        assertTrue(diffMillis >= 30 && diffMillis <= 70);
    }

    @Test
    @DisplayName("Should change speed dynamically")
    void testDynamicSpeedChange() throws InterruptedException {
        GameClock clock = new GameClock(testZone, testStartTime, 1.0);

        Instant before = clock.instant();
        Thread.sleep(100);
        clock.setSpeed(60.0);
        Instant afterSpeedChange = clock.instant();
        Thread.sleep(100);
        Instant after = clock.instant();

        assertTrue(afterSpeedChange.isAfter(before));
        assertTrue(after.isAfter(afterSpeedChange));

        // After speed change, time should advance faster
        long diff1 = afterSpeedChange.toEpochMilli() - before.toEpochMilli();
        long diff2 = after.toEpochMilli() - afterSpeedChange.toEpochMilli();
        assertTrue(diff2 > diff1 * 10); // Second period should be much longer
    }

    @Test
    @DisplayName("Should create clock with different zone")
    void testWithZone() {
        GameClock clock = new GameClock(testZone, testStartTime, testSpeed);
        ZoneId newZone = ZoneId.of("America/New_York");

        GameClock newClock = (GameClock) clock.withZone(newZone);

        assertEquals(newZone, newClock.getZone());
        assertNotEquals(clock.getZone(), newClock.getZone());
    }

    @Test
    @DisplayName("Should handle zero speed")
    void testZeroSpeed() throws InterruptedException {
        GameClock clock = new GameClock(testZone, testStartTime, 0.0);

        Instant start = clock.instant();
        Thread.sleep(100);
        Instant end = clock.instant();

        // With zero speed, time should barely advance
        long diff = end.toEpochMilli() - start.toEpochMilli();
        assertTrue(diff < 10);
    }

    @Test
    @DisplayName("Should handle very high speed")
    void testVeryHighSpeed() throws InterruptedException {
        GameClock clock = new GameClock(testZone, testStartTime, 10000.0);

        Instant start = clock.instant();
        Thread.sleep(100);
        Instant end = clock.instant();

        long diffMillis = end.toEpochMilli() - start.toEpochMilli();
        // Should advance significantly
        assertTrue(diffMillis > 100000); // More than 100 seconds
    }

    @Test
    @DisplayName("Should maintain consistent time zone")
    void testConsistentTimeZone() {
        GameClock clock = new GameClock(testZone, testStartTime, testSpeed);

        ZoneId zone1 = clock.getZone();
        ZoneId zone2 = clock.getZone();

        assertEquals(zone1, zone2);
    }

    @Test
    @DisplayName("Should handle different time zones")
    void testDifferentTimeZones() {
        ZoneId[] zones = {
            ZoneId.of("UTC"),
            ZoneId.of("America/New_York"),
            ZoneId.of("Asia/Tokyo"),
            ZoneId.of("Europe/London")
        };

        for (ZoneId zone : zones) {
            GameClock clock = new GameClock(zone, testStartTime, testSpeed);
            assertEquals(zone, clock.getZone());
        }
    }

    @Test
    @DisplayName("Should start from specified initial time")
    void testInitialTime() {
        Instant specificTime = Instant.parse("2024-06-15T14:30:00Z");
        GameClock clock = new GameClock(testZone, specificTime, 1.0);

        Instant current = clock.instant();

        // Should be very close to the specified time (within 1 second)
        long diff = Math.abs(current.toEpochMilli() - specificTime.toEpochMilli());
        assertTrue(diff < 1000);
    }

    @Test
    @DisplayName("Should advance time monotonically")
    void testMonotonicTime() {
        GameClock clock = new GameClock(testZone, testStartTime, 10.0);

        Instant t1 = clock.instant();
        Instant t2 = clock.instant();
        Instant t3 = clock.instant();

        assertTrue(t2.compareTo(t1) >= 0);
        assertTrue(t3.compareTo(t2) >= 0);
    }

    @Test
    @DisplayName("Should get current speed")
    void testGetSpeed() {
        GameClock clock = new GameClock(testZone, testStartTime, 5.0);
        assertEquals(5.0, clock.getSpeed());

        clock.setSpeed(10.0);
        assertEquals(10.0, clock.getSpeed());
    }

    @Test
    @DisplayName("Should handle negative speed")
    void testNegativeSpeed() {
        // While unusual, test if clock handles it gracefully
        GameClock clock = new GameClock(testZone, testStartTime, -1.0);
        assertNotNull(clock.instant());
    }

    @Test
    @DisplayName("Should preserve instant when changing speed")
    void testPreserveInstantOnSpeedChange() throws InterruptedException {
        GameClock clock = new GameClock(testZone, testStartTime, 1.0);

        Thread.sleep(100);
        Instant beforeChange = clock.instant();
        clock.setSpeed(60.0);
        Instant afterChange = clock.instant();

        // The instant should be very close (within a few milliseconds)
        long diff = Math.abs(afterChange.toEpochMilli() - beforeChange.toEpochMilli());
        assertTrue(diff < 100); // Allow some tolerance for execution time
    }
}

