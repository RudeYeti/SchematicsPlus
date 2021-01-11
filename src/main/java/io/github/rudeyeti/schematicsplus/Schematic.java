package io.github.rudeyeti.schematicsplus;

import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.TimeUnit;

public class Schematic {
    public static String message(int id, String fileName) {
        switch (id) {
            case 0:
                return "Usage: The specified file `" + fileName + "`must be a schematic.";
            case 1:
                return "Usage: The schematic `" + fileName + "` already exists.";
            case 2:
                return "Usage: The schematic `" + fileName + "` exceeds the file size limit of `" + Config.sizeLimit / 1000000 + " MB`.";
            case 3:
                return "The schematic `" + fileName + "` has been successfully uploaded.";
        }
        return null;
    }

    public static void errorMessage(GuildMessageReceivedEvent event, String errorMessage) {
        event.getChannel().sendMessage(errorMessage).complete().delete().completeAfter(3, TimeUnit.SECONDS);
        event.getMessage().delete().queue();
    }

    public static String download(URL url, File destFolder) {
        String fileName = new File(url.getPath()).getName();
        File file = new File(destFolder, fileName);

        if (fileName.endsWith(".schematic")) {
            if (!file.exists()) {
                try {
                    URLConnection connection = url.openConnection();
                    connection.setRequestProperty("User-Agent", "SchematicsPlus");
                    connection.connect();

                    ReadableByteChannel readableByteChannel = Channels.newChannel(connection.getInputStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    int fileSize = IOUtils.toByteArray(connection.getInputStream()).length;

                    if (fileSize > Config.sizeLimit) {
                        readableByteChannel.close();
                        fileOutputStream.close();
                        file.delete();

                        return message(2, fileName);
                    }

                    fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    readableByteChannel.close();
                    fileOutputStream.close();

                    return message(3, fileName);
                } catch (IOException error) {
                    error.printStackTrace();
                }
            } else {
                return message(1, fileName);
            }
        } else {
            return message(0, fileName);
        }
        return "Usage: An unknown error occurred when attempting to download the file `" + fileName + "`.";
    }
}
