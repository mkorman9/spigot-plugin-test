package com.github.mkorman9.spigotplugintest.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PoweroffAtTimeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final int minutes;
    private boolean cancel = false;

    public PoweroffAtTimeEvent(int minutes) {
        this.minutes = minutes;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public int getMinutes() {
        return minutes;
    }
}
