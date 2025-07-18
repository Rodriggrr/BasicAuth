package com.basicauth.player;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;

import com.basicauth.debug.LoggerStatic;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.lang.reflect.Type;
import static com.basicauth.BasicAuth.players;

// checar se esta no mapa
// se nao tiver, carregar do disco.

public class PlayerDataHandler {
    public static final String DATA_FILE = "basicauth/player_data.json";
    private static final Gson gson = new Gson();
    //private static final TypeToken<PlayerModel> PlayerModelType = new TypeToken<PlayerModel>() {};

    public PlayerDataHandler() {
        // Ensure the data file exists
        ensureDataFileExists();
    }

    private static boolean pathExists(Path path) {
        return Files.exists(path);
    }

    public static Path ensureDataFileExists() {
        Path dataPath = FabricLoader.getInstance().getConfigDir().resolve(DATA_FILE);
        if (!pathExists(dataPath)) {
            try {
                LoggerStatic.info("[BASIC AUTH] Creating player data file at: " + dataPath);
                Files.createDirectories(dataPath.getParent());
                Files.createFile(dataPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return dataPath;
    }

    public static boolean playerExists(String playerName) {
        PlayerModel player = players.get(playerName);
        if(player != null)
            return true;
        
        Path dataPath = ensureDataFileExists();
        try {
            String json = java.nio.file.Files.readString(dataPath);
            HashMap<String, PlayerModel> map = gson.fromJson(json, new TypeToken<HashMap<String, PlayerModel>>(){}.getType());
            return map != null && map.containsKey(playerName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static PlayerModel loadPlayerData(String playerName) {
        PlayerModel player = null;
        if(players != null) {
            player = players.get(playerName);
        }

        if(player != null)
                return player;
        
        Path dataPath = ensureDataFileExists();
        try {
            String json = Files.readString(dataPath);
            Type type = new TypeToken<HashMap<String, PlayerModel>>() {}.getType();
            Map<String, PlayerModel> playerMap = gson.fromJson(json, type);

            if (playerMap == null) return null;

            player = playerMap.get(playerName);
            if (player == null) return null;

            players.put(playerName, player);

            return player;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PlayerModel loadPlayerData(ServerPlayerEntity player) {
        if(player == null) {
            return null;
        }

        return loadPlayerData(player.getName().getString());
    }

    public static HashMap<String, PlayerModel> loadAllPlayerData() {
        Path dataPath = ensureDataFileExists();
        try {
            String json = Files.readString(dataPath);
            Type type = new TypeToken<HashMap<String, PlayerModel>>() {}.getType();
            HashMap<String, PlayerModel> playerMap = gson.fromJson(json, type);
            return playerMap != null ? playerMap : new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }


    public static void savePlayerData(PlayerModel playerModel) {
        if(playerModel == null) {
            return;
        }
        
        players.put(playerModel.getUsername(), playerModel);

        Path dataPath = ensureDataFileExists();
        HashMap<String, PlayerModel> map = new HashMap<>();
        try {
            String existingJson = Files.readString(dataPath);
            Type type = new TypeToken<HashMap<String, PlayerModel>>(){}.getType();
            map = gson.fromJson(existingJson, type);
            if (map == null) map = new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
        }

        map.put(playerModel.getUsername(), playerModel);

        String newJson = gson.toJson(map);

        try {
            Files.writeString(dataPath, newJson);       
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deletePlayerData(String playerName) {
        if(playerName == null || playerName.isEmpty()) {
            return false;
        }
        players.remove(playerName);

        Path dataPath = ensureDataFileExists();
        HashMap<String, PlayerModel> map = new HashMap<>();
        try {
            String existingJson = Files.readString(dataPath);
            Type type = new TypeToken<HashMap<String, PlayerModel>>(){}.getType();
            map = gson.fromJson(existingJson, type);
            if (map == null) map = new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        map.remove(playerName);

        String newJson = gson.toJson(map);

        try {
            Files.writeString(dataPath, newJson);       
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static int size() {
        Path dataPath = ensureDataFileExists();
        try {
            String json = Files.readString(dataPath);
            Type type = new TypeToken<HashMap<String, PlayerModel>>(){}.getType();
            Map<String, PlayerModel> playerMap = gson.fromJson(json, type);
            return playerMap != null ? playerMap.size() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int sizeDenied() {
        Path dataPath = ensureDataFileExists();
        try {
            String json = Files.readString(dataPath);
            Type type = new TypeToken<HashMap<String, PlayerModel>>(){}.getType();
            Map<String, PlayerModel> playerMap = gson.fromJson(json, type);
            if (playerMap == null) return 0;

            int count = 0;
            for (PlayerModel player : playerMap.values()) {
                if (!player.isAllowed()) {
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
}
