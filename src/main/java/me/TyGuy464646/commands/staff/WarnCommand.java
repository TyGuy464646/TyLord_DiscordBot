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
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Objects;

/**
 * {@link Command} that adds a warning to user's account.
 *
 * @author TyGuy464646
 */
public class WarnCommand extends Command {

	public WarnCommand(TyLord bot) {
		super(bot);
		this.name = "warn";
		this.description = "Adds a warning to a user's profile.";
		this.category = Category.STAFF;
		this.args.add(new OptionData(OptionType.USER, "user", "The user to warn.", true));
		this.args.add(new OptionData(OptionType.STRING, "reason", "Reason for the warning."));
		this.permission = Permission.MODERATE_MEMBERS;
	}

	public void execute(SlashCommandInteractionEvent event) {
		// Get command and member data
		User user = Objects.requireNonNull(event.getOption("user")).getAsUser();

		GuildData data = GuildData.get(Objects.requireNonNull(event.getGuild()));
		ModerationHandler moderationHandler = data.moderationHandler;

		OptionMapping reasonOption = event.getOption("reason");
		String reason = reasonOption != null ? reasonOption.getAsString() : "Unspecified";

		Objects.requireNonNull(event.getGuild()).retrieveMember(user).queue(target -> {
			if (target == null) {
				event.replyEmbeds(EmbedUtils.createError("That user is not in this server!")).setEphemeral(true).queue();
				return;
			} else if (target.getIdLong() == event.getJDA().getSelfUser().getIdLong()) {
				event.replyEmbeds(EmbedUtils.createError("Why would I warn myself... silly human!")).setEphemeral(true).queue();
				return;
			}

			// Check that target is not the same as author
			if (target.getIdLong() == event.getUser().getIdLong()) {
				event.replyEmbeds(EmbedUtils.createError("You cannot warn yourself!")).setEphemeral(true).queue();
				return;
			}

			// Add warning for user
			moderationHandler.addWarning(reason, target.getIdLong(), event.getUser().getIdLong());
		});

		// Private message user with reason for warn
		user.openPrivateChannel().queue(privateChannel -> {
			MessageEmbed msg = moderationHandler.createCaseMessage(event.getUser().getIdLong(), "Warn", reason, EmbedColor.WARNING.color);
			privateChannel.sendMessageEmbeds(msg).queue();
		}, fail -> {
		});

		// Send confirmation message
		event.replyEmbeds(new EmbedBuilder()
				.setAuthor(user.getAsTag() + " has been warned", null, user.getEffectiveAvatarUrl())
				.setDescription("**Reason:** " + reason)
				.setColor(EmbedColor.DEFAULT.color)
				.build()
		).queue();
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}
}
