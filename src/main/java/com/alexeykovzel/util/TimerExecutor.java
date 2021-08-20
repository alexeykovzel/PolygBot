package com.alexeykovzel.util;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerExecutor implements AutoCloseable{
    private static volatile TimerExecutor instance;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private TimerExecutor() {
    }

    /**
     * @return Instance of the executor
     */
    public static TimerExecutor getInstance() {
        final TimerExecutor currentInstance;
        if (instance == null) {
            synchronized (TimerExecutor.class) {
                if (instance == null) {
                    instance = new TimerExecutor();
                }
                currentInstance = instance;
            }
        } else {
            currentInstance = instance;
        }

        return currentInstance;
    }

    /**
     * Add a new CustomTimerTask to be executed
     *
     * @param task       Task to execute
     * @param targetHour Hour to execute it
     * @param targetMin  Minute to execute it
     * @param targetSec  Second to execute it
     */
    public static void startExecutionEveryDayAt(CustomTimerTask task, int targetHour, int targetMin, int targetSec) {
        final Runnable taskWrapper = () -> {
            try {
                task.execute();
                task.reduceTimes();
                startExecutionEveryDayAt(task, targetHour, targetMin, targetSec);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        if (task.getTimes() != 0) {
            final long delay = computeNextDelay(targetHour, targetMin, targetSec);
            executorService.schedule(taskWrapper, delay, TimeUnit.SECONDS);
        }
    }

    /**
     * Find out next daily execution
     *
     * @param targetHour Target hour
     * @param targetMin  Target minute
     * @param targetSec  Target second
     * @return time in second to wait
     */
    private static long computeNextDelay(int targetHour, int targetMin, int targetSec) {
        final LocalDateTime localNow = LocalDateTime.now(Clock.systemDefaultZone());
        LocalDateTime localNextTarget = localNow.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);

        while (localNow.compareTo(localNextTarget.minusSeconds(1)) > 0) {
            localNextTarget = localNextTarget.plusDays(1);
        }

        final Duration duration = Duration.between(localNow, localNextTarget);
        return duration.getSeconds();
    }

    @Override
    public void close() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
