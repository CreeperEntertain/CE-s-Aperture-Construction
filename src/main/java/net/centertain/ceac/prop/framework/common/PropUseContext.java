package net.centertain.ceac.prop.framework.common;

import net.centertain.ceac.prop.framework.Prop;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("ClassCanBeRecord")
public final class PropUseContext {
    private final Level level;
    private final ServerPlayer player;
    private final Prop prop;
    private final InteractionHand hand;
    private final Vec3 location;
    private final Direction face;

    public PropUseContext(
            Level level,
            ServerPlayer player,
            Prop prop,
            InteractionHand hand,
            Vec3 location,
            Direction face
    ) {
        this.level = level;
        this.player = player;
        this.prop = prop;
        this.hand = hand;
        this.location = location;
        this.face = face;
    }

    public Level getLevel() {
        return level;
    }
    public ServerPlayer getPlayer() {
        return player;
    }
    public Prop getProp() {
        return prop;
    }
    public InteractionHand getHand() {
        return hand;
    }
    public Vec3 getLocation() {
        return location;
    }
    public Direction getFace() {
        return face;
    }
}
