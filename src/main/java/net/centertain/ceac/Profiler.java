package net.centertain.ceac;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class Profiler {
    private static final Map<String, Object> values = new ConcurrentHashMap<>();

    private static volatile RepeatTypes repeatType = RepeatTypes.NONE;
    private static volatile ToPrint toPrint = ToPrint.KEY_VALUE;
    private static volatile Duration repeatDuration = Duration.ofSeconds(1);
    private static volatile String keyToPrint;

    private static volatile int repeatTickDelay = 1;
    private static volatile int repeatTickCount = 0;

    private static Thread profilerThread;

    private Profiler() {}


    public enum RepeatTypes {
        NONE,
        TICK,
        DURATION
    }
    public enum ToPrint {
        VALUE,
        KEY_VALUE,
        ALL
    }


    public static void init() {
        profilerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(repeatDuration.toMillis());
                } catch (InterruptedException e) {
                    continue;
                }
                loopPrint();
            }
        });
        profilerThread.setDaemon(true);
        profilerThread.start();
    }
    private static void loopPrint() {
        if (repeatType != RepeatTypes.DURATION)
            return;
        loopPrintOut();
    }
    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber (
            modid = MOD_ID,
            bus = Mod.EventBusSubscriber.Bus.FORGE,
            value = Dist.CLIENT
    )
    private static class TickPrinter {
        @SubscribeEvent
        public static void onClientTick(final TickEvent.ClientTickEvent event) {
            if (repeatType != RepeatTypes.TICK)
                return;
            repeatTickCount++;
            if (repeatTickCount < repeatTickDelay)
                return;
            repeatTickCount = 0;
            loopPrintOut();
        }
    }
    private static void loopPrintOut() {
        switch (toPrint) {
            case VALUE -> System.out.println(values.get(keyToPrint));
            case KEY_VALUE -> System.out.println(keyToPrint + ": " + values.get(keyToPrint));
            case ALL -> printAll();
        }
    }

    public static void set(String key, Object value) {
        values.put(key, value);
    }
    @Nullable
    public static Object get(String key) {
        return values.get(key);
    }

    public static void print(@Nullable String key) {
        System.out.println(values.get(key));
    }
    public static void print(@Nullable String key, boolean printKey) {
        String keyString = printKey
                ? key + ": "
                : "";
        System.out.println(keyString + values.get(key));
    }
    public static void printAll() {
        for (var entry : values.entrySet())
            System.out.println(entry.getKey() + ": " + entry.getValue());
    }

    public static Map<String, Object> getAll() {
        return Collections.unmodifiableMap(values);
    }
    public static void clear() {
        values.clear();
    }

    public static void stopRepeatPrint() {
        repeatType = RepeatTypes.NONE;
        repeatTickCount = 0;
    }
    public static void startRepeatPrint(String key) {
        repeatType = RepeatTypes.TICK;
        repeatTickCount = 0;
        repeatTickDelay = 1;
        keyToPrint = key;
    }
    public static void startRepeatPrint(String key, Duration duration) {
        repeatType = RepeatTypes.DURATION;
        repeatDuration = duration;
        keyToPrint = key;
        profilerThread.interrupt();
    }
    public static void startRepeatPrint(String key, int ticks) {
        repeatType = RepeatTypes.TICK;
        repeatTickDelay = ticks;
        repeatTickCount = 0;
        keyToPrint = key;
    }
    public static void editRepeatPrint(String newKey) {
        keyToPrint = newKey;
    }
    public static void editRepeatPrint(Duration duration) {
        repeatType = RepeatTypes.DURATION;
        repeatDuration = duration;
        profilerThread.interrupt();
    }
    public static void editRepeatPrint(int ticks) {
        repeatType = RepeatTypes.TICK;
        repeatTickDelay = ticks;
        repeatTickCount = 0;
    }
    public static void editRepeatPrint(ToPrint whatToPrint) {
        toPrint = whatToPrint;
    }
}
