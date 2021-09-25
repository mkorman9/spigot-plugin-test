package com.github.mkorman9.spigotplugintest.commands;

import com.github.mkorman9.spigotplugintest.Entrypoint;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VillagersCommand implements CommandExecutor {
    private static final int CHUNKS_RADIUS = 8;

    private final Entrypoint entrypoint;

    public VillagersCommand(Entrypoint entrypoint) {
        this.entrypoint = entrypoint;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            entrypoint.getServer().getConsoleSender().sendMessage("This command can only be executed by a player");
            return true;
        }

        Player player = (Player) sender;
        Chunk playerChunk = player.getLocation().getChunk();
        World world = player.getWorld();

        Map<Villager.Profession, Integer> villagersCountByProfession = new HashMap<>();
        int totalCount = 0;

        for (int i = playerChunk.getX() - CHUNKS_RADIUS; i < playerChunk.getX() + CHUNKS_RADIUS; i++) {
            for (int j = playerChunk.getZ() - CHUNKS_RADIUS; j < playerChunk.getZ() + CHUNKS_RADIUS; j++) {
                Chunk chunk = world.getChunkAt(i, j);

                List<Entity> allEntities = Arrays.asList(chunk.getEntities());
                List<Villager> villagers = allEntities.stream()
                        .filter(entity -> entity.getType() == EntityType.VILLAGER)
                        .map(entity -> (Villager) entity)
                        .collect(Collectors.toList());

                for (Villager villager : villagers) {
                    Villager.Profession profession = villager.getProfession();

                    if (!villagersCountByProfession.containsKey(profession)) {
                        villagersCountByProfession.put(profession, 0);
                    }

                    villagersCountByProfession.put(profession, villagersCountByProfession.get(profession) + 1);
                    totalCount += 1;
                }
            }
        }

        player.sendMessage(String.format("TOTAL Villagers: %d", totalCount));
        for (Map.Entry<Villager.Profession, Integer> entry : villagersCountByProfession.entrySet()) {
            player.sendMessage(String.format("%s: %d", entry.getKey().name(), entry.getValue()));
        }

        return true;
    }
}
