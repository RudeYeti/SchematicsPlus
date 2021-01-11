package io.github.rudeyeti.schematicsplus;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.configuration.Configuration;

public class Config {

    public static Configuration config;
    public static String channelId;
    public static int sizeLimit;

    public static void updateConfig() {
        config = SchematicsPlus.plugin.getConfig();
        channelId = config.getString("channel-id");
        sizeLimit = Integer.parseInt(config.getString("size-limit")) * 1000000;
    }

    private static String message(String option, String message) {
        return "The " + option + " value in the configuration must be " + message;
    }

    public static boolean validateConfig() {
        if (!(config.get("channel-id") instanceof String)) {
            SchematicsPlus.logger.warning(message("channel-id", "enclosed in quotes."));
        } else if (config.get("channel-id").equals("##################")) {
            SchematicsPlus.logger.warning(message("channel-id", "modified from ##################."));
        } else if (!(config.get("size-limit") instanceof String)) {
            SchematicsPlus.logger.warning(message("size-limit", "enclosed in quotes."));
        } else if (!NumberUtils.isDigits(config.getString("size-limit"))) {
            SchematicsPlus.logger.warning(message("size-limit", "must be a number."));
        } else {
            return true;
        }
        return false;
    }
}