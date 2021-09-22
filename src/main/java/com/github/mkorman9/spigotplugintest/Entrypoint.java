package com.github.mkorman9.spigotplugintest;

import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Entrypoint extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getLogger().info("Started spigot-plugin-test");

        this.getCommand("schedulepoweroff").setExecutor(new SchedulePoweroffCommand(this));

        ChatListener chatListener = new ChatListener(this);
        this.getServer().getPluginManager().registerEvents(chatListener, this);
        this.getServer().getPluginManager().registerEvent(
                AsyncPlayerChatEvent.class,
                chatListener,
                EventPriority.LOWEST,
                chatListener,
                this,
                true
        );

        PoweroffListener poweroffEventListener = new PoweroffListener(this);
        this.getServer().getPluginManager().registerEvents(poweroffEventListener, this);
        this.getServer().getPluginManager().registerEvent(
                SchedulePoweroffEvent.class,
                poweroffEventListener,
                EventPriority.HIGH,
                poweroffEventListener,
                this,
                true
        );
        this.getServer().getPluginManager().registerEvent(
                PlayerQuitEvent.class,
                poweroffEventListener,
                EventPriority.LOWEST,
                poweroffEventListener,
                this,
                true
        );
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Stopped spigot-plugin-test");
    }
}
