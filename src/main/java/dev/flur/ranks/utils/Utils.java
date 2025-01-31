package dev.flur.ranks.utils;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.requirement.RequirementFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {
    private static FileConfiguration ranksFile;
    private static HashMap<String, String> ranksList;

    public Utils() {
        this.ranksFile = getFileConfiguration("ranks");
        ranksList = getRanks(this.ranksFile);
    }

    public HashMap<String, String> getRanks(FileConfiguration ranksFile) {
        HashMap<String, String> ranks = new HashMap<>();

        for (String key : ranksFile.getKeys(false)) {
            boolean hasName = ranksFile.getString(key + ".name") != null;
            // todo: throw exception
            if (!hasName) continue;

            String name = ranksFile.getString(key + ".name");
            ranks.putIfAbsent(name, key);
        }

        return ranks;
    }

    public static HashMap<String, String> getNext(String rank) {
        HashMap<String, String> nextRanks = new HashMap<>();
        String path = ranksList.get(rank);
        ConfigurationSection section = ranksFile.getConfigurationSection(path + ".next");

        if (section == null) return nextRanks;

        for (String key : section.getKeys(false)) {
            String name = section.getString(key);
            nextRanks.putIfAbsent(key, name);
        }

        return nextRanks;
    }

    private void generateFile(String fileName) {
        File file = new File(Ranks.getPlugin().getDataFolder(), fileName);
        if (file.exists()) return;
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();

        try (InputStream in = Ranks.getPlugin().getResource(fileName);

             OutputStream out = new FileOutputStream(file)) {

            if (in == null) return;

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getFileConfiguration(String file) {
        File ranksFile = new File(Ranks.getPlugin().getDataFolder(), file + ".yml");
        if (!ranksFile.exists()) generateFile(file + ".yml");

        return YamlConfiguration.loadConfiguration(ranksFile);
    }

    public static ArrayList<Requirement> getRequirements(String next, Player player) {
        ArrayList<Requirement> requirements = new ArrayList<>();
        String primaryGroup = Ranks.getPermissions().getPrimaryGroup(player);

        if (ranksList.containsKey(primaryGroup)) {
            String path = ranksList.get(primaryGroup) + ".next." + next + ".requirements";
            ranksFile.getStringList(path).forEach(req -> {
                requirements.add(RequirementFactory.registerRequirement(req));
            });
        }
        return requirements;
    }

    public static ArrayList<String> getRankList() {
        return new ArrayList<>(ranksList.keySet());
    }
}
