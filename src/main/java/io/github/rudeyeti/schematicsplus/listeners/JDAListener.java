package io.github.rudeyeti.schematicsplus.listeners;

import github.scarsz.discordsrv.dependencies.commons.io.FilenameUtils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import io.github.rudeyeti.schematicsplus.Config;
import io.github.rudeyeti.schematicsplus.SchematicsPlus;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JDAListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getGuild() == SchematicsPlus.guild && event.getChannel().getId().equals(Config.channelId) && !event.getAuthor().isBot()) {
            Plugin worldEdit = SchematicsPlus.server.getPluginManager().getPlugin("WorldEdit");
            String schematicsFolder = worldEdit.getDataFolder() + File.separator + "schematics";
            List<Message.Attachment> attachments = event.getMessage().getAttachments();

            if (!new File(schematicsFolder).exists()) {
                new File(schematicsFolder).mkdir();
            }

            try {
                // If the message is not a url, an exception will be thrown.
                URL url = new URL(event.getMessage().getContentRaw());
                String fileName = FilenameUtils.getBaseName(url.getPath()) + "." + FilenameUtils.getExtension(url.getPath());

                if (!fileName.endsWith(".schematic")) {
                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage("Usage: The specified file must be a schematic.").queue((message) -> {
                        message.delete().queueAfter(3, TimeUnit.SECONDS);
                    });
                    return;
                }

                File file = new File(schematicsFolder, fileName);

                if (file.exists()) {
                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage("Usage: The specified file already exists.").queue((message) -> {
                        message.delete().queueAfter(3, TimeUnit.SECONDS);
                    });
                    return;
                }

                URLConnection connection = url.openConnection();
                connection.setRequestProperty("User-Agent", "SchematicsPlus");
                connection.connect();

                ReadableByteChannel readableByteChannel = Channels.newChannel(connection.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                FileChannel fileChannel = fileOutputStream.getChannel();

                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                readableByteChannel.close();
                fileOutputStream.close();
                fileChannel.close();

                event.getChannel().sendMessage("The schematic `" + fileName + "` has been successfully uploaded.").queue();
            } catch (MalformedURLException error) {
                // Otherwise download an attachment if it exists.
                if (attachments.size() == 1 && attachments.get(0).getFileName().endsWith(".schematic")) {
                    String fileName = attachments.get(0).getFileName();

                    attachments.get(0).downloadToFile(new File(schematicsFolder, fileName));
                    event.getChannel().sendMessage("The schematic `" + fileName + "` has been successfully uploaded.").queue();
                } else {
                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage("Usage: The message must either contain a link or have a file attached to it.").queue((message) -> {
                        message.delete().queueAfter(3, TimeUnit.SECONDS);
                    });
                }
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
    }
}
