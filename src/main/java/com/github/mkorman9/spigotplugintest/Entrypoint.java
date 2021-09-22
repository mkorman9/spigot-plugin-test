package com.github.mkorman9.spigotplugintest;

import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Entrypoint extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getLogger().info("Started spigot-plugin-test");

        this.getCommand("szynka").setExecutor(new ExampleCommand(this.getLogger()));

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
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Stopped spigot-plugin-test");
    }
}
