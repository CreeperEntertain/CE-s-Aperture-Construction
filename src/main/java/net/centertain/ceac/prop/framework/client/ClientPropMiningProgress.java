package net.centertain.ceac.prop.framework.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClientPropMiningProgress {
    private static final Map<UUID, Integer> STAGES = new HashMap<>();

    private ClientPropMiningProgress() {}

    public static void setStage(UUID propId, int stage) {
        if (stage < 0 || stage > 9) {
            STAGES.remove(propId);
            return;
        }
        STAGES.put(propId, stage);
    }

    public static int getStage(UUID propId) {
        return STAGES.getOrDefault(propId, -1);
    }

    public static void clear() {
        STAGES.clear();
    }
}
