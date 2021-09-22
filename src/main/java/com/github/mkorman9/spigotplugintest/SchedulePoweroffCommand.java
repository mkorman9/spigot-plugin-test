package com.github.mkorman9.spigotplugintest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SchedulePoweroffCommand implements CommandExecutor {
    private final Entrypoint entrypoint;

    public SchedulePoweroffCommand(Entrypoint entrypoint) {
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

        entrypoint.getServer().getPluginManager().callEvent(new SchedulePoweroffEvent());
        return true;
    }
}
