package me.TyGuy464646.commands.utility;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class AvatarCommand extends Command {

	public AvatarCommand(TyLord bot) {
		super(bot);
		this.name = "avatar";
		this.description = "Display your avatar or someone else's avatar.";
		this.category = Category.UTILITY;
		this.args.add(new OptionData(OptionType.USER, "user", "See another user's avatar."));
	}

	public void execute(SlashCommandInteractionEvent event) {
		// Get user
		OptionMapping option = event.getOption("user");
		User user;
		if (option != null) user = option.getAsUser();
		else user = event.getUser();

		// Create and send embed
		String avatarUrl = user.getEffectiveAvatarUrl() + "?size=1024";
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(EmbedColor.DEFAULT.color)
				.setAuthor(user.getAsTag(), null, avatarUrl)
				.setTitle("Avatar link", avatarUrl)
				.setImage(avatarUrl);
		event.replyEmbeds(embed.build()).queue();
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {
	}
}
