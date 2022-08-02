package me.TyGuy464646.commands;

import me.TyGuy464646.TyLord;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a general slash command with properties.
 *
 * @author TyGuy464646
 */
public abstract class Command {

	public TyLord bot;
	public String name;
	public String description;
	public Category category;
	public List<OptionData> args;
	public List<SubcommandData> subCommands;
	public Permission permission; // Permission user needs to execute this command
	public Permission botPermission; // Permission bot needs to execute this command

	public Command(TyLord bot) {
		this.bot = bot;
		this.args = new ArrayList<>();
		this.subCommands = new ArrayList<>();
	}

	public abstract void execute(SlashCommandInteractionEvent event);

	public abstract void autoCompleteExecute(CommandAutoCompleteInteractionEvent event);
}
