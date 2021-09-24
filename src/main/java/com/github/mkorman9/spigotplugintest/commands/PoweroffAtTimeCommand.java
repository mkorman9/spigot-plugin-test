package com.github.mkorman9.spigotplugintest.commands;

import com.github.mkorman9.spigotplugintest.Entrypoint;
import com.github.mkorman9.spigotplugintest.events.PoweroffAtTimeEvent;
import com.github.mkorman9.spigotplugintest.events.PoweroffWhenEmptyEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PoweroffAtTimeCommand implements CommandExecutor {
    private final Entrypoint entrypoint;

    public PoweroffAtTimeCommand(Entrypoint entrypoint) {
        this.entrypoint = entrypoint;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOp()) {
                return false;
            }
        }

        int minutes = parseArgs(args);
        if (minutes < 0) {
            return false;
        }

        entrypoint.getServer().getPluginManager().callEvent(new PoweroffAtTimeEvent(minutes));
        return true;
    }

    private int parseArgs(String[] args) {
        if (args.length < 1) {
            return -1;
        }

        try {
            int minutes = Integer.parseInt(args[0]);

            if (minutes <= 0) {
                return -1;
            }

            return minutes;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
