package xyz.darke.survivalflight;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.Map;

public class SurvivalFlight implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("SurvivalFlight");



    @Override
    public void onInitializeServer() {
        System.out.println("Hello Fabric world!");

        PlayerFlightHandler.setPlayerData(loadPlayerData());

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            PlayerDataIO.writePlayerData(PlayerFlightHandler.getPlayerData());
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            if (server.getTicks() % 20 == 0) {
                if (server.getCurrentPlayerCount() > 0) {
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                         PlayerFlightHandler.update(player);
                    }
                }
            }
        });

        SurvivalFlightCommand.register();

    }

    private Map<String, PlayerData> loadPlayerData() {
        PlayerDataIO.setupPluginFolder();
        return PlayerDataIO.readPlayerData();
    }
}