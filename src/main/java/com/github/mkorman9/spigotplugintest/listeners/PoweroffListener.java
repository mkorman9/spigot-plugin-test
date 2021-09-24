package com.github.mkorman9.spigotplugintest.listeners;

import com.github.mkorman9.spigotplugintest.Entrypoint;
import com.github.mkorman9.spigotplugintest.events.PoweroffCancelEvent;
import com.github.mkorman9.spigotplugintest.events.PoweroffInEvent;
import com.github.mkorman9.spigotplugintest.events.PoweroffWhenEmptyEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.scheduler.BukkitTask;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

public class PoweroffListener implements Listener, EventExecutor {
    private final Entrypoint entrypoint;

    private boolean poweroffWhenEmpty = false;
    private BukkitTask poweroffAtTimeWarningTask = null;
    private BukkitTask poweroffAtTimeTask = null;

    public PoweroffListener(Entrypoint entrypoint) {
        this.entrypoint = entrypoint;
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        if (listener != this) {
            return;
        }

        if (event instanceof PoweroffWhenEmptyEvent) {
            if (((PoweroffWhenEmptyEvent) event).isCancelled()) {
                return;
            }

            this.schedulePoweroffWhenEmpty((PoweroffWhenEmptyEvent) event);
        }

        if (event instanceof PlayerQuitEvent) {
            this.handlePlayerDisconnect((PlayerQuitEvent) event);
        }

        if (event instanceof PoweroffInEvent) {
            if (((PoweroffInEvent) event).isCancelled()) {
                return;
            }

            this.schedulePoweroffIn((PoweroffInEvent) event);
        }

        if (event instanceof PoweroffCancelEvent) {
            if (((PoweroffCancelEvent) event).isCancelled()) {
                return;
            }

            this.cancelPoweroff((PoweroffCancelEvent) event);
        }
    }

    private void schedulePoweroffWhenEmpty(PoweroffWhenEmptyEvent event) {
        if (poweroffWhenEmpty) {
            return;
        }

        entrypoint.getServer().broadcastMessage("Server will shut down once all the players are disconnected");
        poweroffWhenEmpty = true;
    }

    private void schedulePoweroffIn(PoweroffInEvent event) {
        if (poweroffAtTimeWarningTask != null && !poweroffAtTimeWarningTask.isCancelled()) {
            poweroffAtTimeWarningTask.cancel();
            poweroffAtTimeWarningTask = null;
        }
        if (poweroffAtTimeTask != null && !poweroffAtTimeTask.isCancelled()) {
            poweroffAtTimeTask.cancel();
            poweroffAtTimeTask = null;
        }

        entrypoint.getServer().broadcastMessage(String.format("Server will automatically shut down in %d minutes", event.getMinutes()));

        long ticksUntilPoweroff = event.getMinutes() * 60 * 20;
        poweroffAtTimeWarningTask = entrypoint.getServer().getScheduler().runTaskLater(
                entrypoint,
                () -> entrypoint.getServer().broadcastMessage("[WARNING] Server shutting down in 30 seconds"),
                ticksUntilPoweroff - (30 * 20)
        );
        poweroffAtTimeTask = entrypoint.getServer().getScheduler().runTaskLater(
                entrypoint,
                this::executePoweroff,
                ticksUntilPoweroff
        );
    }

    private void cancelPoweroff(PoweroffCancelEvent event) {
        poweroffWhenEmpty = false;

        if (poweroffAtTimeWarningTask != null && !poweroffAtTimeWarningTask.isCancelled()) {
            poweroffAtTimeWarningTask.cancel();
            poweroffAtTimeWarningTask = null;
        }
        if (poweroffAtTimeTask != null && !poweroffAtTimeTask.isCancelled()) {
            poweroffAtTimeTask.cancel();
            poweroffAtTimeTask = null;
        }

        entrypoint.getServer().broadcastMessage("Automatic server shut down has been cancelled");
    }

    private void handlePlayerDisconnect(PlayerQuitEvent event) {
        if (!poweroffWhenEmpty) {
            return;
        }

        Collection<? extends Player> playersOnline = entrypoint.getServer().getOnlinePlayers();
        boolean isServerEmpty = playersOnline.isEmpty();
        boolean hasLastPlayerDisconnected = (playersOnline.size() == 1 &&
                playersOnline.iterator().next().getUniqueId().equals(event.getPlayer().getUniqueId()));

        if (isServerEmpty || hasLastPlayerDisconnected) {
            executePoweroff();
        }
    }

    private void executePoweroff() {
        entrypoint.getServer().getConsoleSender().sendMessage("Executing poweroff");

        try {
            Files.copy(
                    getClass().getResourceAsStream("/schedule-poweroff.sh"),
                    Paths.get("/tmp/schedule-poweroff.sh"),
                    StandardCopyOption.REPLACE_EXISTING
            );
            Runtime.getRuntime().exec("chmod +x /tmp/schedule-poweroff.sh").waitFor();

            String pid = String.valueOf(ProcessHandle.current().pid());
            Runtime.getRuntime().exec("/tmp/schedule-poweroff.sh " + pid);
            entrypoint.getServer().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
