package io.github.darkkronicle.proximitychat;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;

public class BypassHandler {

    private final static BypassHandler INSTANCE = new BypassHandler();

    public static BypassHandler getInstance() {
        return INSTANCE;
    }

    @Getter private Whitelist whitelist;

    public boolean shouldBypass(PlayerEntity player) {
        return whitelist.isAllowed(player.getGameProfile()) || player.hasPermissionLevel(2);
    }

    public void add(PlayerEntity player) {
        whitelist.add(new WhitelistEntry(player.getGameProfile()));
        save();
    }

    public void remove(PlayerEntity player) {
        whitelist.remove(player.getGameProfile());
        save();
    }

    public File getConfigFile() {
        return new File("proximitychatwhitelist.json");
    }

    public void save() {
        try {
            whitelist.save();
        } catch (IOException e) {
            ProximityChat.LOGGER.log(Level.ERROR, "Error saving bypass list!", e);
        }
    }

    public void load() {
        whitelist = new Whitelist(getConfigFile());
        if (getConfigFile().exists()) {
            try {
                whitelist.load();
            } catch (IOException e) {
                ProximityChat.LOGGER.log(Level.ERROR, "Error loading bypass list!", e);
            }
        }
    }

}
