package com.github.mkorman9.spigotplugintest;

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

    private boolean poweroffScheduled = false;

    public PoweroffListener(Entrypoint entrypoint) {
        this.entrypoint = entrypoint;
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        if (listener == this && event instanceof SchedulePoweroffEvent) {
            if (((SchedulePoweroffEvent) event).isCancelled()) {
                return;
            }

            this.schedulePoweroff((SchedulePoweroffEvent) event);
        }

        if (listener == this && event instanceof PlayerQuitEvent) {
            this.handlePlayerDisconnect((PlayerQuitEvent) event);
        }
    }

    private void schedulePoweroff(SchedulePoweroffEvent event) {
        entrypoint.getServer().getConsoleSender().sendMessage("Poweroff has been scheduled");
        this.poweroffScheduled = true;
    }

    private void handlePlayerDisconnect(PlayerQuitEvent event) {
        if (!poweroffScheduled) {
            return;
        }

        Collection<? extends Player> playersOnline = entrypoint.getServer().getOnlinePlayers();
        if (playersOnline.isEmpty() ||
                (playersOnline.size() == 1 &&
                        playersOnline.iterator().next().getUniqueId() == event.getPlayer().getUniqueId())) {
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
}
