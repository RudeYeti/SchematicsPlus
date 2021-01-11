package io.github.rudeyeti.schematicsplus;

import org.bukkit.configuration.Configuration;

public class Config {

    public static Configuration config;
    public static String channelId;

    public static void updateConfig() {
        config = SchematicsPlus.plugin.getConfig();
        channelId = config.getString("channel-id");
    }

    private static String message(String option, String message) {
        return "The " + option + " value in the configuration must be " + message;
    }

    public static boolean validateConfig() {
        if (!(config.get("channel-id") instanceof String)) {
            SchematicsPlus.logger.warning(message("channel-id", "enclosed in quotes."));
        } else if (config.get("channel-id").equals("##################")) {
            SchematicsPlus.logger.warning(message("channel-id", "modified from ##################."));
        } else {
            return true;
        }
        return false;
    }
}