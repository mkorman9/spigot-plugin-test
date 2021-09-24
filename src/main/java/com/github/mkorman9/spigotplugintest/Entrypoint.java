package com.github.mkorman9.spigotplugintest;

import com.github.mkorman9.spigotplugintest.commands.PoweroffInCommand;
import com.github.mkorman9.spigotplugintest.commands.PoweroffWhenEmptyCommand;
import com.github.mkorman9.spigotplugintest.events.PoweroffInEvent;
import com.github.mkorman9.spigotplugintest.events.PoweroffWhenEmptyEvent;
import com.github.mkorman9.spigotplugintest.listeners.ChatListener;
import com.github.mkorman9.spigotplugintest.listeners.PoweroffListener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Entrypoint extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getLogger().info("Started utility-plugin");

        this.getCommand("poweroffwhenempty").setExecutor(new PoweroffWhenEmptyCommand(this));
        this.getCommand("poweroffin").setExecutor(new PoweroffInCommand(this));

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
                PoweroffWhenEmptyEvent.class,
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
        this.getServer().getPluginManager().registerEvent(
                PoweroffInEvent.class,
                poweroffEventListener,
                EventPriority.HIGH,
                poweroffEventListener,
                this,
                true
        );
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Stopped utility-plugin");
    }
}
