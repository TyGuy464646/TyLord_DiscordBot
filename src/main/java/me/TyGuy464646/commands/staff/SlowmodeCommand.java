package me.TyGuy464646.commands.staff;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Duration;
import java.time.format.DateTimeParseException;

/**
 * {@link Command} that puts a channel in slowmode with specified time;
 *
 * @author TyGuy464646
 */
public class SlowmodeCommand extends Command {

	public SlowmodeCommand(TyLord bot) {
		super(bot);
		this.name = "slowmode";
		this.description = "Sets slowmode time for a channel.";
		this.category = Category.STAFF;
		this.args.add(new OptionData(OptionType.STRING, "time", "The time to set for slowmode."));
		this.permission = Permission.MANAGE_CHANNEL;
		this.botPermission = Permission.MANAGE_CHANNEL;
	}

	public void execute(SlashCommandInteractionEvent event) {
		// Prevent slowmode in threads
		ChannelType type = event.getChannelType();
		if (type == ChannelType.GUILD_PUBLIC_THREAD || type == ChannelType.GUILD_NEWS_THREAD || type == ChannelType.GUILD_PRIVATE_THREAD) {
			event.replyEmbeds(EmbedUtils.createError("You cannot set slowmode on threads!")).setEphemeral(true).queue();
			return;
		}

		OptionMapping timeOption = event.getOption("time");
		if (timeOption != null) {
			// Retrieve time in seconds from input
			String timeString = timeOption.getAsString();
			int time;
			try {
				try {
					Duration duration = Duration.parse("PT" + timeString.replaceAll(" ", ""));
					time = (int) duration.toSeconds();
					if (time <= 0) throw new NumberFormatException();
				} catch (DateTimeParseException e) {
					time = Integer.parseInt(timeString);
					if (time <= 0) throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				// Disable slowmode
				event.getTextChannel().getManager().setSlowmode(0).queue();
				event.replyEmbeds(EmbedUtils.createDefault("Slowmode has been disabled from this channel.")).queue();
				return;
			}

			// Set slowmode timer
			if (time > TextChannel.MAX_SLOWMODE) {
				event.replyEmbeds(EmbedUtils.createError("Time should be less than or equal to 6 hours.")).setEphemeral(true).queue();
				return;
			}
			event.getTextChannel().getManager().setSlowmode(time).queue();
			event.replyEmbeds(EmbedUtils.createDefault("This channel's slowmode has been set to " + formatTime(time) + ".")).queue();
		} else {
			// Display current slowmode timer
			int totalSeconds = event.getTextChannel().getSlowmode();
			String timeString = formatTime(totalSeconds);
			event.replyEmbeds(EmbedUtils.createDefault("This channel's slowmode is " + timeString + ".")).queue();
		}
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}

	/**
	 * Formats seconds into a string 'x hours, x minutes, x seconds'.
	 * @param totalSeconds the number of seconds to convert to string.
	 * @return a formatted string.
	 */
	private String formatTime(int totalSeconds) {
		int hours = totalSeconds / 3600;
		int minutes = (totalSeconds % 3600) / 60;
		int seconds = totalSeconds % 60;
		StringBuilder time = new StringBuilder();
		if (hours > 0) {
			time.append(hours).append(" hour");
			if (hours > 1) time.append("s");
			if (minutes > 0) time.append(", ");
		}
		if (minutes > 0) {
			time.append(minutes).append( " minute");
			if (minutes > 1) time.append("s");
			if (seconds > 0) time.append(", ");
		}
		if (seconds > 0) {
			time.append(seconds).append(" second");
			if (seconds > 1) time.append("s");
		}

		return time.toString();
	}
}
