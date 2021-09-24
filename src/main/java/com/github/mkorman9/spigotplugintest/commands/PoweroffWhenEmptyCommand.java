package com.github.mkorman9.spigotplugintest.commands;

import com.github.mkorman9.spigotplugintest.Entrypoint;
import com.github.mkorman9.spigotplugintest.events.PoweroffWhenEmptyEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PoweroffWhenEmptyCommand implements CommandExecutor {
    private final Entrypoint entrypoint;

    public PoweroffWhenEmptyCommand(Entrypoint entrypoint) {
        this.entrypoint = entrypoint;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        entrypoint.getServer().getPluginManager().callEvent(new PoweroffWhenEmptyEvent());
        return true;
    }
}
