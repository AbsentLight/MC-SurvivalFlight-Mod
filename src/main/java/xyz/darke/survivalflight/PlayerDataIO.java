package xyz.darke.survivalflight;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class PlayerDataIO {

    public static void setFilepath(String filepath) {
        PlayerDataIO.filepath = filepath;
    }

    private static String filepath = "mods/SurvivalFlight/player_data.json";

    public static Map<String, PlayerData> readPlayerData() {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder.create();

        Map<String, Object> playersRaw;
        Map<String,PlayerData> players = new HashMap<>();

        try {
            FileReader reader = new FileReader(filepath);
            playersRaw = gson.fromJson(reader, Map.class);

            for (String key : playersRaw.keySet()) {
                if (playersRaw.get(key) instanceof Map) {
                    try {
                        Map<String,Object> rawPlayerMap = (Map<String, Object>) playersRaw.get(key);
                        players.put(key, new PlayerData(
                                ((Double) rawPlayerMap.get("flightTimeRemaining")).intValue(),
                                (boolean) rawPlayerMap.get("safeFallEffect"))
                        );
                    } catch (Exception e) {
                        SurvivalFlight.LOGGER.error("Error while parsing player data");
                        e.printStackTrace();
                    }

                } else {
                    SurvivalFlight.LOGGER.error("Illegal entity while parsing player data");
                }
            }


            reader.close();
        } catch (Exception e) {
            SurvivalFlight.LOGGER.error("Failed to read from disk");
            players = new HashMap<String,PlayerData>();
            e.printStackTrace();
        }

        return players;
    }

    public static void writePlayerData(Map<String, PlayerData> players) {
        GsonBuilder builder = new GsonBuilder();
        //builder.registerTypeAdapter(PlayerData.class, new PlayerDataTypeAdapter());

        Gson gson = builder.create();

        try {
            FileWriter writer = new FileWriter(filepath);
            gson.toJson(players, writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            SurvivalFlight.LOGGER.error("Failed to write data to disk!");
            e.printStackTrace();
        }


    }

    public static void setupPluginFolder() {

        File file = new File("mods/SurvivalFlight");
        boolean folderWasCreated = file.mkdir();

        file = new File(filepath);

        if (file.exists()) {
            return;
        }

        try {
            boolean fileWasCreated = file.createNewFile();
            FileWriter writer = new FileWriter(filepath);
            writer.write("{}");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            SurvivalFlight.LOGGER.error("Failed to create player_data.json");
            e.printStackTrace();
        }
    }
}
