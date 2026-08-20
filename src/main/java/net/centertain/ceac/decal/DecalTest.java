package net.centertain.ceac.decal;

import net.centertain.ceac.CeacMod;
import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.decal.network.SyncDecalPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;

import java.util.Set;
import java.util.UUID;

public class DecalTest {
    private DecalTest() {}

    public static void addTestDecal(ServerLevel level, ServerPlayer player, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        DecalManager manager = DecalCapabilities.get(chunk);

        Decal decal = new Decal(
                UUID.randomUUID(),
                pos.getCenter(),
                Direction.NORTH,
                0,
                16,
                16,
                (byte) 0,
                ResourceLocation.fromNamespaceAndPath(CeacMod.MOD_ID, "textures/decal/test.png"),
                Set.of(pos)
        );
        manager.addDecal(decal);

        ModNetworking.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncDecalPacket(decal)
        );
    }
}
