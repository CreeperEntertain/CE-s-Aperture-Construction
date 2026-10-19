package net.centertain.ceac.prop.framework.server;

import net.centertain.ceac.prop.framework.Prop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PropManager implements INBTSerializable<CompoundTag> {
    private final LevelChunk chunk;
    private final Map<UUID, Prop> props = new HashMap<>();
    private final Map<UUID, Prop> readOnlyProps = Collections.unmodifiableMap(props);

    public PropManager(LevelChunk chunk) {
        this.chunk = chunk;
    }

    public void addProp(Prop prop) {
        if (props.putIfAbsent(prop.getId(), prop) != null)
            throw new IllegalArgumentException("Prop with ID " + prop.getId() + " already exists");
        chunk.setUnsaved(true);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean removeProp(UUID id) {
        if (props.remove(id) == null)
            return false;
        chunk.setUnsaved(true);
        return true;
    }

    public Prop getProp(UUID id) {
        return props.get(id);
    }
    public Map<UUID, Prop> getProps() {
        return readOnlyProps;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag root = new CompoundTag();
        ListTag list = new ListTag();
        for (Prop prop : props.values())
            list.add(prop.serializeNBT());
        root.put("Props", list);
        return root;
    }

    @Override
    public void deserializeNBT(CompoundTag root) {
        props.clear();
        ListTag list = root.getList("Props", ListTag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            Prop prop = Prop.deserializeNBT(list.getCompound(i));
            if (prop != null)
                props.putIfAbsent(prop.getId(), prop);
        }
    }
}
