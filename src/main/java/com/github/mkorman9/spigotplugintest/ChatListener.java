package com.github.mkorman9.spigotplugintest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;

public class ChatListener implements Listener, EventExecutor {
    private final Entrypoint entrypoint;

    public ChatListener(Entrypoint entrypoint) {
        this.entrypoint = entrypoint;
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        if (listener == this && event instanceof AsyncPlayerChatEvent) {
            if (((AsyncPlayerChatEvent) event).isCancelled()) {
                return;
            }

            this.onChat((AsyncPlayerChatEvent) event);
        }
    }

    private void onChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage().toLowerCase();

        if (msg.contains("szynka")) {
            entrypoint.getServer().getScheduler().runTaskLater(entrypoint, () -> {
                String answer = String.format("@%s: swinka :D", event.getPlayer().getDisplayName());
                Bukkit.broadcastMessage(ChatColor.AQUA + answer);
            }, 10);
        }
    }
}
