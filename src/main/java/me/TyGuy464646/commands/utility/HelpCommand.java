package me.TyGuy464646.commands.utility;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.commands.CommandRegistry;
import me.TyGuy464646.listeners.ButtonListener;
import me.TyGuy464646.util.embeds.EmbedColor;
import me.TyGuy464646.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static me.TyGuy464646.commands.CommandRegistry.commands;

public class HelpCommand extends Command {

    private static final int COMMANDS_PER_PAGE = 6;

    public HelpCommand(TyLord bot) {
        super(bot);
        this.name = "help";
        this.description = "Display a list of all commands and categories.";
        this.category = Category.UTILITY;

        OptionData data = new OptionData(OptionType.STRING, "category", "See commands under a specific category.");
        for (Category c : Category.values()) {
            String name = c.name.toLowerCase();
            data.addChoice(name, name);
        }

        this.args.add(data);
        this.args.add(new OptionData(OptionType.STRING, "command", "See details for a specific command"));
    }

    public void execute(SlashCommandInteractionEvent event) {
        // Create a hashmap that groups commands by category
        HashMap<Category, List<Command>> categories = new LinkedHashMap<>();
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(EmbedColor.DEFAULT.color);
        for (Category category : Category.values()) {
            categories.put(category, new ArrayList<>());
        }
        for (Command command : commands) {
            categories.get(command.category).add(command);
        }

        OptionMapping option = event.getOption("category");
        OptionMapping option2 = event.getOption("command");
        if (option != null && option2 != null) {
            event.replyEmbeds(EmbedUtils.createError("Please only give one optional argument and try again.")).queue();
        } else if (option != null) {
            // Display category command menu
            Category category = Category.valueOf(option.getAsString().toUpperCase());
            List<MessageEmbed> embeds = buildCategoryMenu(category, categories.get(category));
            if (embeds.isEmpty()) {
                // No commands for this category
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle(category.emoji + " **%s Commands**".formatted(category.name))
                        .setDescription("Coming soon...")
                        .setColor(EmbedColor.DEFAULT.color);
                event.replyEmbeds(embed.build()).queue();
                return;
            }
            // Send paginated help menu
            ReplyCallbackAction action = event.replyEmbeds(embeds.get(0));
            if (embeds.size() > 1) {
                ButtonListener.sendPaginatedMenu(event.getUser().getId(), action, embeds);
                return;
            }
            action.queue();
        } else if (option2 != null) {
            // Display command details menu
            Command cmd = CommandRegistry.commandsMap.get(option2.getAsString().toLowerCase());
            if (cmd != null) {
                embedBuilder.setTitle("Command: " + cmd.name);
                embedBuilder.setDescription(cmd.description);
                StringBuilder usages = new StringBuilder();
                if (cmd.subCommands.isEmpty()) {
                    usages.append("`").append(getUsage(cmd)).append("`");
                } else {
                    for (SubcommandData sub : cmd.subCommands) {
                        usages.append("`").append(getUsage(sub, cmd.name)).append("`\n");
                    }
                }
                embedBuilder.addField("Usage:", usages.toString(), false);
                embedBuilder.addField("Permission:", getPermission(cmd), false);
                event.replyEmbeds(embedBuilder.build()).queue();
            } else {
                // Command specified doesn't exist
                event.replyEmbeds(EmbedUtils.createError("No command called \"" + option2.getAsString() + "\" found.")).queue();
            }
        } else {
            // Display default menu
            embedBuilder.setTitle("TyLord Commands");
            categories.forEach((category, commands) -> {
                String categoryName = category.name().toLowerCase();
                String value = "`/help " + categoryName + "`";
                embedBuilder.addField(category.emoji + " " + category.name, value, true);
            });
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }

    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

    }

    /**
     * Builds a menu with all the commands in a specified category.
     * @param category The category to build a menu for.
     * @param commands A list of the command in this category.
     * @return A list of {@link MessageEmbed} objects for pagination.
     */
    public List<MessageEmbed> buildCategoryMenu(Category category, List<Command> commands) {
        List<MessageEmbed> embeds = new ArrayList<>();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(category.emoji + " **%s Commands**".formatted(category.name));
        embed.setColor(EmbedColor.DEFAULT.color);

        int counter = 0;
        for (Command cmd : commands) {
            if (cmd.subCommands.isEmpty()) {
                embed.appendDescription("`" + getUsage(cmd) + "`\n" + cmd.description + "\n\n");
                counter++;
                if (counter % COMMANDS_PER_PAGE == 0) {
                    embeds.add(embed.build());
                    embed.setDescription("");
                    counter = 0;
                }
            } else {
                for (SubcommandData sub : cmd.subCommands) {
                    embed.appendDescription("`" + getUsage(sub, cmd.name) + "`\n" + sub.getDescription() + "\n\n");
                    counter++;
                    if (counter % COMMANDS_PER_PAGE == 0) {
                        embeds.add(embed.build());
                        embed.setDescription("");
                        counter = 0;
                    }
                }
            }
        }
        if (counter != 0) embeds.add(embed.build());
        return embeds;
    }

    /**
     * Creates a string of {@link Command} usage.
     * @param cmd Command to build usage for.
     * @return String with name and args stitiched together.
     */
    public String getUsage(Command cmd) {
        StringBuilder usage = new StringBuilder("/" + cmd.name);
        if (cmd.args.isEmpty()) return usage.toString();
        for (int i = 0; i < cmd.args.size(); i++) {
            boolean isRequired = cmd.args.get(i).isRequired();
            if (isRequired) usage.append(" <");
            else usage.append(" [");

            usage.append(cmd.args.get(i).getName());
            if (isRequired) usage.append(">");
            else usage.append("]");
        }
        return usage.toString();
    }

    /**
     * Creates a string of {@link SubcommandData} usage.
     * @param cmd sub command data from a command.
     * @param commandName the name of the root command.
     * @return String with name and args stitched together.
     */
    public String getUsage(SubcommandData cmd, String commandName) {
        StringBuilder usage = new StringBuilder("/" + commandName + " " + cmd.getName());
        if (cmd.getOptions().isEmpty()) return usage.toString();
        for (OptionData arg : cmd.getOptions()) {
            boolean isRequired = arg.isRequired();
            if (isRequired) usage.append(" <");
            else usage.append(" [");

            usage.append(arg.getName());
            if (isRequired) usage.append(">");
            else usage.append("]");
        }
        return usage.toString();
    }

    /**
     * Builds a string of permissions from command.
     *
     * @param cmd the command to draw permisisons from
     * @return A string of command perms.
     */
    private String getPermission(Command cmd) {
        if (cmd.permission == null) {
            return "None";
        }
        return cmd.permission.getName();
    }
}
