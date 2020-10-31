package xyz.darke.survivalflight;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

public class PlayerFlightHandler {

    private static Map<String, PlayerData> playerData;
    private static String[] requiredAdvancements = {"minecraft:end/elytra", "minecraft:end/find_end_city"};

    public static void setPlayerData(Map<String, PlayerData> data) {
        PlayerFlightHandler.playerData = data;
    }

    public static Map<String, PlayerData> getPlayerData() {
        return playerData;
    }

    public static void update(ServerPlayerEntity player) {
        String playerKey = player.getUuidAsString();

        if (!playerData.containsKey(playerKey)) {
            playerData.put(playerKey, new PlayerData());
        }

        PlayerData sfPlayer = playerData.get(playerKey);

        // Stop early when the player should always be allowed to fly
        if (player.isSpectator() || player.isCreative()) {
            if (!player.abilities.allowFlying) {
                // don't send the update unless they can't fly, otherwise it'll interfere with spectator speed
                player.abilities.allowFlying = true;
                player.sendAbilitiesUpdate();
            }
            return;
        }

        if (sfPlayer.isSafeFallEffect() & player.isOnGround()) {
            player.removeStatusEffect(StatusEffects.SLOW_FALLING);
            sfPlayer.setSafeFallEffect(false);
        }

        if (allowedToFly(player)) {
            player.abilities.allowFlying = true;

            // Handle the case when they're already flying
            if (player.abilities.flying) {
                String playerUUID = player.getUuidAsString();
                sfPlayer.decrementFlightTime();
            }
        } else {
            player.abilities.allowFlying = false;

            // Handle the case when they're already flying
            if (player.abilities.flying) {
                player.abilities.flying = false;
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 20 * 30, 1));
                sfPlayer.setSafeFallEffect(true);
            }
        }
        player.sendAbilitiesUpdate();
    }

    public static boolean allowedToFly(ServerPlayerEntity player) {

        if (!hasRequiredAdvancements(player)) {
            return false;
        }

        String playerKey = player.getUuidAsString();
        if (playerData.get(playerKey).getFlightTimeRemaining() > 0) {
            return true;
        }

        return false;
    }

    private static boolean hasRequiredAdvancements(ServerPlayerEntity player) {
        ServerAdvancementLoader advLoader = player.getServer().getAdvancementLoader();
        for (String reqAdv : requiredAdvancements) {
            if (!player.getAdvancementTracker().getProgress(advLoader.get(new Identifier(reqAdv))).isDone()) {
                return false;
            }
        }
        return true;
    }
}
