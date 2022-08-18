package me.TyGuy464646.commands.staff;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.data.GuildData;
import me.TyGuy464646.handlers.ModerationHandler;
import me.TyGuy464646.util.embeds.EmbedColor;
import me.TyGuy464646.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * {@link Command} that kicks a user from the guild.
 *
 * @author TyGuy464646
 */
public class KickCommand extends Command {

	public KickCommand(TyLord bot) {
		super(bot);
		this.name = "kick";
		this.description = "Kicks a user from your server.";
		this.category = Category.STAFF;
		this.args.add(new OptionData(OptionType.USER, "user", "The user to kick", true));
		this.args.add(new OptionData(OptionType.STRING, "reason", "Reason for the kick"));
		this.permission = Permission.KICK_MEMBERS;
		this.botPermission = Permission.KICK_MEMBERS;
	}

	public void execute(SlashCommandInteractionEvent event) {
		// Get command and member data
		User user = event.getOption("user").getAsUser();
		Member target = event.getOption("user").getAsMember();
		if (target == null) {
			event.replyEmbeds(EmbedUtils.createError("That user is not in this server!")).setEphemeral(true).queue();
			return;
		} else if (target.getIdLong() == event.getJDA().getSelfUser().getIdLong()) {
			event.replyEmbeds(EmbedUtils.createError("Do you seriously expect me to kick myself?!")).setEphemeral(true).queue();
			return;
		}

		// Check target role position
		ModerationHandler moderationHandler = GuildData.get(event.getGuild()).moderationHandler;
		if (!moderationHandler.canTargetMember(target)) {
			event.replyEmbeds(EmbedUtils.createError("This member cannot be kicked.")).setEphemeral(true).queue();
			return;
		}

		// Kick user from guild
		OptionMapping reasonOption = event.getOption("reason");
		String reason = reasonOption != null ? reasonOption.getAsString() : "Unspecified";
		user.openPrivateChannel().queue(privateChannel -> {
			// Private message user with reason for kick
			MessageEmbed msg = moderationHandler.createCaseMessage(event.getUser().getIdLong(), "Kick", reason, EmbedColor.WARNING.color);
			privateChannel.sendMessageEmbeds(msg).queue(
					message -> target.kick(reason).queue(),
					failure -> target.kick(reason).queue()
			);
		}, fail -> target.kick(reason).queue());

		// Send confirmation message
		event.replyEmbeds(new EmbedBuilder()
				.setAuthor(user.getAsTag() + " has been kicked", null, user.getEffectiveAvatarUrl())
				.setDescription("**Reason:** " + reason)
				.setColor(EmbedColor.DEFAULT.color)
				.build()
		).queue();
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}
}
