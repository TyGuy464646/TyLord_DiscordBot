package me.TyGuy464646.commands.staff;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.data.GuildData;
import me.TyGuy464646.data.cache.moderation.Warning;
import me.TyGuy464646.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * {@link Command} that shows the warnings for a specified user.
 *
 * @author TyGuy464646
 */
public class WarningsCommand extends Command {

	public WarningsCommand(TyLord bot) {
		super(bot);
		this.name = "warnings";
		this.description = "Display a list of warnings for you or another user.";
		this.category = Category.STAFF;
		this.args.add(new OptionData(OptionType.USER, "user", "The user to get warnings for."));
	}

	public void execute(SlashCommandInteractionEvent event) {
		GuildData data = GuildData.get(event.getGuild());
		OptionMapping option = event.getOption("user");
		User target;

		if (option != null) target = option.getAsUser();
		else target = event.getUser();

		// Create embed template
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(EmbedColor.DEFAULT.color);

		// Check if user has no warnings
		List<Warning> warnings = data.moderationHandler.getWarnings(target.getId());
		if (warnings == null || warnings.isEmpty()) {
			embed.setAuthor(target.getAsTag() + " has no infractions", null, target.getEffectiveAvatarUrl());
			event.replyEmbeds(embed.build()).queue();
			return;
		}

		// Display warnings in an embed
		int lastSevenDays = 0;
		int lastDay = 0;
		int counter = 0;
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < warnings.size(); i++) {
			Warning w = warnings.get(i);
			String time = TimeFormat.RELATIVE.format(w.getTimestamp());
			if (counter < 10)
				content.append("`[").append(w.getId()).append("]` **").append(w.getReason()).append("** • ").append(time).append("\n");
			if (w.getTimestamp() >= System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24))
				lastDay++;
			if (w.getTimestamp() >= System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7))
				lastSevenDays++;
			counter++;
		}

		embed.setAuthor(target.getAsTag() + "'s Infractions", null, target.getEffectiveAvatarUrl());
		embed.addField("Last 24 Hours", lastDay + " warnings", true);
		embed.addField("Last 7 Days", lastSevenDays + " warnings", true);
		embed.addField("Total", warnings.size() + " warnings", true);
		embed.addField("[ID] Last 10 Warnings", content.toString(), false);
		event.replyEmbeds(embed.build()).queue();
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}
}
