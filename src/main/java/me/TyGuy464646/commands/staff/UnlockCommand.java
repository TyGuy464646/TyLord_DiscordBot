package me.TyGuy464646.commands.staff;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.util.embeds.EmbedColor;
import me.TyGuy464646.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * Command that removes lock from a specified channel.
 *
 * @author TyGuy464646
 */
public class UnlockCommand extends Command {

	public UnlockCommand(TyLord bot) {
		super(bot);
		this.name = "unlock";
		this.description = "Allows @everyone to send messages in a channel.";
		this.category = Category.STAFF;
		this.args.add(new OptionData(OptionType.CHANNEL, "channel", "The channel to unlock.")
				.setChannelTypes(ChannelType.NEWS, ChannelType.TEXT));
		this.permission = Permission.MANAGE_CHANNEL;
		this.botPermission = Permission.MANAGE_CHANNEL;
	}

	public void execute(SlashCommandInteractionEvent event) {
		OptionMapping channelOption = event.getOption("channel");
		TextChannel channel;

		if (channelOption != null) channel = channelOption.getAsTextChannel();
		else channel = event.getTextChannel();

		if (channel == null) {
			event.replyEmbeds(EmbedUtils.createError("That is not a valid channel!")).setEphemeral(true).queue();
			return;
		}

		try {
			channel.upsertPermissionOverride(event.getGuild().getPublicRole()).clear(Permission.MESSAGE_SEND).queue();
			String channelString = "<#" + channel.getId() + ">";
			event.replyEmbeds(new EmbedBuilder()
					.setDescription(":unlock: " + channelString + " has been unlocked.")
					.setColor(EmbedColor.DEFAULT.color)
					.build()
			).queue();
		} catch (InsufficientPermissionException e) {
			event.replyEmbeds(EmbedUtils.createError("I am lacking some permissions required to unlock channels.")).queue();
		}
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}
}
