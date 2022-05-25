package io.github.darkkronicle.proximitychat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class BypassCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicatedServer) {
        dispatcher.register(
                CommandManager.literal("proximitychat")
                        .executes(context -> {
                            context.getSource().getPlayer().sendMessage(new LiteralText("Proximity Chat by DarkKronicle"), false);
                            return 1;
                        }).then(
                                CommandManager.literal("add").requires(source -> source.hasPermissionLevel(2)).then(
                                        CommandManager.argument("player", EntityArgumentType.player()).executes(context -> {
                                            try {
                                                PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                BypassHandler.getInstance().add(player);
                                                return 1;
                                            } catch (CommandSyntaxException e) {
                                                context.getSource().getPlayer().sendMessage(new LiteralText("Invalid player!"), false);
                                                return 0;
                                            }
                                        })
                                )
                        ).then(
                                CommandManager.literal("remove").requires(source -> source.hasPermissionLevel(2)).then(
                                        CommandManager.argument("player", EntityArgumentType.player()).executes(context -> {
                                            try {
                                                PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                BypassHandler.getInstance().remove(player);
                                                return 1;
                                            } catch (CommandSyntaxException e) {
                                                context.getSource().getPlayer().sendMessage(new LiteralText("Invalid player!"), false);
                                                return 0;
                                            }
                                        })
                                )
        ));
        dispatcher.register(
                CommandManager.literal("broadcast").requires(source -> {
                    try {
                        BypassHandler.getInstance().shouldBypass(source.getPlayer());
                        return true;
                    } catch (CommandSyntaxException e) {
                        return false;
                    }
                }).then(CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
                    context.getSource().getServer().getPlayerManager().broadcast(
                            new LiteralText(MessageArgumentType.getMessage(context, "message").getString()),
                            MessageType.CHAT,
                            context.getSource().getPlayer().getUuid()
                    );
                    return 1;
                }))
        );
    }


}
