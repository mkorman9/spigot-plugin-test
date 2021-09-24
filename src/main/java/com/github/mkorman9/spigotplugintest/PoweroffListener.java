package com.github.mkorman9.spigotplugintest;

import com.github.mkorman9.spigotplugintest.events.PoweroffAtTimeEvent;
import com.github.mkorman9.spigotplugintest.events.PoweroffWhenEmptyEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

public class PoweroffListener implements Listener, EventExecutor {
    private final Entrypoint entrypoint;

    private boolean poweroffWhenEmpty = false;

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

        if (event instanceof PoweroffAtTimeEvent) {
            if (((PoweroffAtTimeEvent) event).isCancelled()) {
                return;
            }

            this.schedulePoweroffAtTime((PoweroffAtTimeEvent) event);
        }
    }

    private void schedulePoweroffWhenEmpty(PoweroffWhenEmptyEvent event) {
        entrypoint.getServer().broadcastMessage("Server will shut down once all the players are disconnected");
        this.poweroffWhenEmpty = true;
    }

    private void schedulePoweroffAtTime(PoweroffAtTimeEvent event) {
        entrypoint.getServer().broadcastMessage(String.format("Server will automatically shut down in %d minutes", event.getMinutes()));
        entrypoint.getServer().getScheduler().runTaskLater(entrypoint, this::executePoweroff, event.getMinutes() * 60 * 20);
    }

    private void handlePlayerDisconnect(PlayerQuitEvent event) {
        if (!poweroffWhenEmpty) {
            return;
        }

        Collection<? extends Player> playersOnline = entrypoint.getServer().getOnlinePlayers();
        if (playersOnline.isEmpty() ||
                (playersOnline.size() == 1 &&
                        playersOnline.iterator().next().getUniqueId() == event.getPlayer().getUniqueId())) {
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
