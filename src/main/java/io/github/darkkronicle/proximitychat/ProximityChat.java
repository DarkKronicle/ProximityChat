package io.github.darkkronicle.proximitychat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProximityChat implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("proximitychat");

    public static final String MOD_ID = "proximitychat";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(BypassCommands::register);
        BypassHandler.getInstance().load();
    }

    public static double getMaxDistance() {
        return 30;
    }

    public static boolean shouldSend(PlayerEntity one, PlayerEntity two) {
        if (!one.getEntityWorld().equals(two.getEntityWorld())) {
            return false;
        }
        return one.getPos().subtract(two.getPos()).length() <= getMaxDistance();
    }

}
