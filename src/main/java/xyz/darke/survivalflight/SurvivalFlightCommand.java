package xyz.darke.survivalflight;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class SurvivalFlightCommand {
    public static void register() {

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LiteralCommandNode<ServerCommandSource> sfNode = CommandManager
                    .literal("sf")
                    .executes(SurvivalFlightCommand::executeBase)
                    .build();

            // Set
            LiteralCommandNode<ServerCommandSource> sfSetNode = CommandManager
                    .literal("set")
                    .requires(source -> source.hasPermissionLevel(1))
                    .then(RequiredArgumentBuilder.argument("duration", IntegerArgumentType.integer(0, 1000000)))
                    .build();
            ArgumentCommandNode<ServerCommandSource, Integer> sfSetDurationNode = CommandManager
                    .argument("duration", IntegerArgumentType.integer(0, 1000000))
                    .then(CommandManager.argument("player", EntityArgumentType.player()))
                    .executes(SurvivalFlightCommand::executeSet)
                    .build();
            ArgumentCommandNode<ServerCommandSource, EntitySelector> sfSetPlayerNode = CommandManager
                    .argument("player", EntityArgumentType.player())
                    .executes(SurvivalFlightCommand::executeSet)
                    .build();

            // Add
            LiteralCommandNode<ServerCommandSource> sfAddNode = CommandManager
                    .literal("add")
                    .requires(source -> source.hasPermissionLevel(1))
                    .then(RequiredArgumentBuilder.argument("duration", IntegerArgumentType.integer(0, 1000000)))
                    .build();
            ArgumentCommandNode<ServerCommandSource, Integer> sfAddDurationNode = CommandManager
                    .argument("duration", IntegerArgumentType.integer(0, 1000000))
                    .then(CommandManager.argument("player", EntityArgumentType.player()))
                    .executes(SurvivalFlightCommand::executeAdd)
                    .build();
            ArgumentCommandNode<ServerCommandSource, EntitySelector> sfAddPlayerNode = CommandManager
                    .argument("player", EntityArgumentType.player())
                    .executes(SurvivalFlightCommand::executeAdd)
                    .build();


            dispatcher.getRoot().addChild(sfNode);
            sfNode.addChild(sfSetNode);
            sfSetNode.addChild(sfSetDurationNode);
            sfSetDurationNode.addChild(sfSetPlayerNode);
            sfNode.addChild(sfAddNode);
            sfAddNode.addChild(sfAddDurationNode);
            sfAddDurationNode.addChild(sfAddPlayerNode);

        });
    }

    public static int executeBase(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        PlayerData playerData = PlayerFlightHandler.getPlayerData().get(context.getSource().getPlayer().getUuidAsString());
        int rawSecs = playerData.getFlightTimeRemaining();
        int hours = rawSecs / 3600;
        int mins  = (rawSecs % 3600) / 60;
        int secs  = (rawSecs % 3600) % 60;

        String feedback = String.format("You have %dh%dm%ds of flight time remaining",
                hours,
                mins,
                secs
        );
        context.getSource().sendFeedback(new LiteralText(feedback), true);
        return 1;
    }

    public static int executeSet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Integer duration = IntegerArgumentType.getInteger(context, "duration");
        ServerPlayerEntity target = null;
        try {
            target = EntityArgumentType.getPlayer(context, "player");
        } catch (Exception e) {

        }
        String feedback = "";
        String targetUUID;
        if (target == null) {
            targetUUID = context.getSource().getPlayer().getUuidAsString();
            PlayerFlightHandler.getPlayerData().get(targetUUID).setFlightTimeRemaining(duration);
            feedback = String.format("%s set their flight duration to %d",
                    context.getSource().getName(),
                    duration
            );
        } else {
            targetUUID = target.getUuidAsString();
            PlayerFlightHandler.getPlayerData().get(targetUUID).setFlightTimeRemaining(duration);
            feedback = String.format("%s set %s's flight duration to %d",
                    context.getSource().getName(),
                    target.getName().asString(),
                    duration
            );
        }

        context.getSource().sendFeedback(new LiteralText(feedback), true);
        return 1;
    }

    public static int executeAdd(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Integer duration = IntegerArgumentType.getInteger(context, "duration");
        ServerPlayerEntity target = null;
        try {
            target = EntityArgumentType.getPlayer(context, "player");
        } catch (Exception e) {

        }
        String feedback = "";
        String targetUUID;
        if (target == null) {
            targetUUID = context.getSource().getPlayer().getUuidAsString();
            PlayerData targetData = PlayerFlightHandler.getPlayerData().get(targetUUID);
            targetData.addFlightTimeRemaining(duration);
            feedback = String.format("%s added %d to their flight duration (now %d)",
                    context.getSource().getName(),
                    duration,
                    targetData.getFlightTimeRemaining()
            );
        } else {
            targetUUID = target.getUuidAsString();
            PlayerData targetData = PlayerFlightHandler.getPlayerData().get(targetUUID);
            targetData.addFlightTimeRemaining(duration);
            feedback = String.format("%s added %d to %s's flight duration (now %d)",
                    context.getSource().getName(),
                    duration,
                    target.getName().asString(),
                    targetData.getFlightTimeRemaining()

            );
        }

        context.getSource().sendFeedback(new LiteralText(feedback), true);
        return 1;
    }
}
