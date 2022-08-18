package me.TyGuy464646.commands.staff;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.data.GuildData;
import me.TyGuy464646.util.embeds.EmbedColor;
import me.TyGuy464646.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * {@link Command} that bans a user from the guild.
 *
 * @author TyGuy464646
 */
public class BanCommand extends Command {

	public BanCommand(TyLord bot) {
		super(bot);
		this.name = "ban";
		this.description = "Bans a user from your server.";
		this.category = Category.STAFF;
		this.args.add(new OptionData(OptionType.USER, "user", "The user to ban.", true));
		this.args.add(new OptionData(OptionType.INTEGER, "days", "Time duration for the ban in days."));
		this.args.add(new OptionData(OptionType.STRING, "reason", "Reason for the ban."));
		this.permission = Permission.BAN_MEMBERS;
		this.botPermission = Permission.BAN_MEMBERS;
	}

	public void execute(SlashCommandInteractionEvent event) {
		// Get command and member data
		User user = event.getOption("user").getAsUser();
		Member member = event.getOption("user").getAsMember();
		if (user.getIdLong() == event.getJDA().getSelfUser().getIdLong()) {
			event.replyEmbeds(EmbedUtils.createError("Did you seriously expect me to ban myself?!")).setEphemeral(true).queue();
			return;
		}

		// Check target role position
		Guild guild = event.getGuild();
		GuildData data = GuildData.get(guild);
		if (!data.moderationHandler.canTargetMember(member)) {
			event.replyEmbeds(EmbedUtils.createError("This member cannot be banned.")).setEphemeral(true).queue();
			return;
		}

		// Get command line options
		OptionMapping reasonOption = event.getOption("reason");
		String reason = reasonOption != null ? reasonOption.getAsString() : "Unspecified";
		OptionMapping daysOption = event.getOption("days");
		final boolean isTempBan;
		String duration = null;
		if (daysOption != null) {
			duration = daysOption.getAsInt() + " Days";
			isTempBan = true;
		} else isTempBan = false;

		// Start unban timer if temp ban specified
		StringBuilder content = new StringBuilder();
		content.append(user.getAsTag()).append(" has been banned");
		if (daysOption != null) {
			int days = daysOption.getAsInt();
			content.append(" for ").append(days).append(" day");
			if (days > 1) content.append("s");
			data.moderationHandler.scheduleUnban(guild, user, days);
		} else if (data.moderationHandler.hasTimedBan(user.getId())) {
			// Remove timed ban in favor or permanent ban
			data.moderationHandler.removeBan(user);
		}

		// Ban user from guild
		String finalDuration = duration;
		user.openPrivateChannel().queue(privateChannel -> {
			// Private message user with reason for ban
			MessageEmbed msg;
			if (isTempBan)
				msg = data.moderationHandler.createCaseMessage(event.getUser().getIdLong(), "Ban", reason, finalDuration, EmbedColor.ERROR.color);
			else
				msg = data.moderationHandler.createCaseMessage(event.getUser().getIdLong(), "Ban", reason, EmbedColor.WARNING.color);

			privateChannel.sendMessageEmbeds(msg).queue(
					message -> guild.ban(user, 7, reason).queue(),
					failure -> guild.ban(user, 7, reason).queue()
			);
		}, fail -> guild.ban(user, 7, reason).queue());

		// Send confirmation message
		event.replyEmbeds(new EmbedBuilder()
				.setAuthor(content.toString(), null, user.getEffectiveAvatarUrl())
				.setDescription("**Reason:** " + reason)
				.setColor(EmbedColor.DEFAULT.color)
				.build()
		).queue();
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}
}
