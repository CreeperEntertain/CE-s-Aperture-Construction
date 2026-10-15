package net.centertain.ceac;

import cc.modlabs.box3d.global.Box3D;
import com.mojang.logging.LogUtils;
import net.centertain.ceac.block.ModBlocks;
import net.centertain.ceac.block_entity.ModBlockEntities;
import net.centertain.ceac.item.ModCreativeModeTabs;
import net.centertain.ceac.item.ModItems;
import net.centertain.ceac.material.ModMaterials;
import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.particle.ModParticles;
import net.centertain.ceac.sound.ModSounds;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CeacMod.MOD_ID)
public class CeacMod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "ceac";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public CeacMod(FMLJavaModLoadingContext context) {
        prepareBox3DNativeLibrary();

        IEventBus modEventBus = context.getModEventBus();

        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMaterials.register(modEventBus);
        ModParticles.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private static void prepareBox3DNativeLibrary() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);

        boolean x64 = arch.equals("amd64") || arch.equals("x86_64") || arch.equals("x64");
        boolean arm64 = arch.equals("aarch64") || arch.equals("arm64");

        final String platform;
        final String libraryName;

        if (os.startsWith("windows") && x64) {
            platform = "windows-x86_64";
            libraryName = "jniBox3D.dll";
        } else if (os.startsWith("linux") && x64) {
            platform = "linux-x86_64";
            libraryName = "libjniBox3D.so";
        } else if (os.contains("mac") || os.contains("darwin")) {
            if (x64)
                platform = "macosx-x86_64";
            else if (arm64)
                platform = "macosx-arm64";
            else {
                LOGGER.warn("Cannot prepare Box3D native library: unsupported macOS architecture {}", arch);
                return;
            }

            libraryName = "libjniBox3D.dylib";
        } else {
            LOGGER.warn("Skipping Box3D native path setup on unsupported platform {} / {}", os, arch);
            return;
        }

        ClassLoader box3dClassLoader = Box3D.class.getClassLoader();

        if (box3dClassLoader == null)
            throw new IllegalStateException(
                    "Box3D was loaded by the bootstrap class loader; cannot configure its JavaCPP loader"
            );

        String[] resourcePaths = {
                "/cc/modlabs/box3d/" + platform + "/" + libraryName,
                "/" + platform + "/" + libraryName
        };

        URL resource = null;

        for (String resourcePath : resourcePaths) {
            resource = Box3D.class.getResource(resourcePath);
            if (resource == null)
                resource = box3dClassLoader.getResource(resourcePath.substring(1));
            if (resource != null)
                break;
        }

        if (resource == null)
            throw new IllegalStateException(
                    "Could not resolve Box3D native resource for "
                    + platform + "/" + libraryName
                    + " from the Box3D class loader"
            );

        try {
            Path nativeDirectory = Files.createTempDirectory("ceac-box3d-" + ProcessHandle.current().pid() + "-");

            Path nativeFile = nativeDirectory.resolve(libraryName);

            try (InputStream input = resource.openStream()) {
                Files.copy(input, nativeFile);
            }

            String previousLibraryPath = System.getProperty("java.library.path", "");
            String updatedLibraryPath = nativeDirectory.toAbsolutePath() + File.pathSeparator + previousLibraryPath;

            System.setProperty("java.library.path", updatedLibraryPath);
            System.setProperty("org.bytedeco.javacpp.pathsFirst", "true");

            Class<?> javaCppLoader = Class.forName("org.bytedeco.javacpp.Loader", false, box3dClassLoader);
            Field pathsFirstField = javaCppLoader.getDeclaredField("pathsFirst");
            if (!pathsFirstField.trySetAccessible())
                throw new IllegalStateException("Cannot access JavaCPP Loader.pathsFirst");

            pathsFirstField.setBoolean(null, true);

            Field foundLibrariesField = javaCppLoader.getDeclaredField("foundLibraries");
            if (!foundLibrariesField.trySetAccessible())
                throw new IllegalStateException("Cannot access JavaCPP Loader.foundLibraries");

            @SuppressWarnings("unchecked")
            Map<String, URL[]> foundLibraries = (Map<String, URL[]>) foundLibrariesField.get(null);

            foundLibraries.keySet().removeIf(key -> key.toLowerCase(Locale.ROOT).contains("box3d"));

            LOGGER.info(
                    "Prepared Box3D native library from {} at {}; JavaCPP filesystem paths are prioritized",
                    resource,
                    nativeFile
            );
        } catch (IOException | ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "Failed to prepare the Box3D native library for " + platform + "/" + libraryName, exception
            );
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetworking::register);
        event.enqueueWork(Profiler::init);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
