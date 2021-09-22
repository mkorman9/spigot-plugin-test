package com.github.mkorman9.spigotplugintest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

public class ExampleCommand implements CommandExecutor {
    private Logger logger;

    public ExampleCommand(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        logger.info("swinka :D");
        return true;
    }
}
