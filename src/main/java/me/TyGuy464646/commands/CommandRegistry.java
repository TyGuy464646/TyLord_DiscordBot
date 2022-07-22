package me.TyGuy464646.commands;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.utility.EmbedCommand;
import me.TyGuy464646.commands.utility.PingCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers, listens, and executes commands.
 *
 * @author TyGuy464646
 */
public class CommandRegistry extends ListenerAdapter {

    /**
     * CommandRegistry logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandRegistry.class);

    /**
     * List of commands in the exact order registered.
     */
    public static final List<Command> commands = new ArrayList<>();

    /**
     * Map of command names to command objects
     */
    public static final Map<String, Command> commandsMap = new HashMap<>();

    /**
     * Adds commands to a global list and registers them as event listener.
     *
     * @param bot An instance of TyLord.
     */
    public CommandRegistry(TyLord bot) {
        mapCommand(
                // Utility commands
                new PingCommand(bot),
                new EmbedCommand(bot)
        );
    }

    /**
     * Adds a {@link Command} to the static list and map.
     *
     * @param cmds a spread list of {@link Command} objects
     */
    private void mapCommand(Command... cmds) {
        for (Command cmd : cmds) {
            commandsMap.put(cmd.name, cmd);
            commands.add(cmd);
        }
    }

    /**
     * Creates a list of {@link CommandData} for all commands.
     *
     * @return a list of {@link CommandData} to be used for registration.
     */
    public static List<CommandData> unpackCommandData() {
        // Register slash commands
        List<CommandData> commandData = new ArrayList<>();
        for (Command command : commands) {
            SlashCommandData slashCommand = Commands.slash(command.name, command.description).addOptions(command.args);
            if (command.permission != null)
                slashCommand.setDefaultPermissions(DefaultMemberPermissions.enabledFor(command.permission));
            if (!command.subCommands.isEmpty())
                slashCommand.addSubcommands(command.subCommands);
            commandData.add(slashCommand);
            LOGGER.info("{} command was successfully added.", command.name);
        }
        return commandData;
    }

    /**
     * Runs whenever a slash command is run in Discord.
     *
     * @param event the event in which the slash command was ran.
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // Get command by name
        Command cmd = commandsMap.get(event.getName());
        if (cmd != null) {
            // Check for required bot permissions
            Role botRole = event.getGuild().getBotRole();
            if (cmd.botPermission != null) {
                if (!botRole.hasPermission(cmd.botPermission) && !botRole.hasPermission(Permission.ADMINISTRATOR)) {
                    String text = "I need the `" + cmd.botPermission.getName() + "` permission to execute that command.";
                    event.reply(text).setEphemeral(true).queue();
                    return;
                }
            }
            // Run command
            cmd.execute(event);
            LOGGER.info("{} command was used.", cmd.name);
        }
    }

    /**
     * Runs whenever an isAutoComplete OptionData is called
     * @param event the event in which the Auto Complete OptionData was called.
     */
    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("embed") && event.getSubcommandName().equals("remove")) {
            LOGGER.info("Doing an auto complete interaction event!");
            OptionMapping message_url = event.getOption("message_url");

            String[] messageParsed = message_url.getAsString().split("/");
            try {
                long guildID = Long.parseLong(messageParsed[4]);
                long channelID = Long.parseLong(messageParsed[5]);
                long messageID = Long.parseLong(messageParsed[6]);

                event.getGuild().getTextChannelById(channelID).retrieveMessageById(messageID).queue(message -> {
                    int numEmbeds = message.getEmbeds().size();
                    List<Choice> choices = new ArrayList<>();
                    for (int i = 0; i < numEmbeds; i++) {
                        choices.add(new Choice("Embed " + String.valueOf(i + 1), i));
                    }
                    event.replyChoices(choices).queue();
                });
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Registers slash commands as guild commands.
     * NOTE: May change to global commands on release.
     *
     * @param event executes when a guild is ready.
     */
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        // Register slash command
        event.getGuild().updateCommands().addCommands(unpackCommandData()).queue(succ -> {
        }, fail -> {
        });
        LOGGER.info("Guild commands have been updated.");
    }
}
