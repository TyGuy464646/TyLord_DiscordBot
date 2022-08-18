package me.TyGuy464646.commands.staff;

import me.TyGuy464646.TyLord;
import me.TyGuy464646.commands.Category;
import me.TyGuy464646.commands.Command;
import me.TyGuy464646.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * {@link Command} that changes or resets a user's nickname.
 *
 * @author TyGuy464646
 */
public class SetNickCommand extends Command {

	public SetNickCommand(TyLord bot) {
		super(bot);
		this.name = "setnick";
		this.description = "change or reset a user's nickname.";
		this.category = Category.STAFF;
		this.args.add(new OptionData(OptionType.USER, "user", "The user to set nick for.", true));
		this.args.add(new OptionData(OptionType.STRING, "nickname", "The new nickname."));
		this.permission = Permission.NICKNAME_MANAGE;
		this.botPermission = Permission.NICKNAME_MANAGE;
	}

	public void execute(SlashCommandInteractionEvent event) {
		Member target = event.getOption("user").getAsMember();
		if (target == null) {
			event.replyEmbeds(EmbedUtils.createError("That user is not in your server!")).setEphemeral(true).queue();
			return;
		}

		try {
			String content = "";
			OptionMapping nickOption = event.getOption("nickname");
			if (nickOption != null) {
				String originalName = target.getUser().getName();
				String name = nickOption.getAsString();
				target.modifyNickname(name).queue();
				content = EmbedUtils.GREEN_TICK + " **" + originalName + "**'s nick has been changed to **" + name + "**.";
			} else {
				String name = target.getUser().getName();
				target.modifyNickname(name).queue();
				content = EmbedUtils.GREEN_TICK + " **" + name + "**'s nick has been reset.";
			}

			event.replyEmbeds(EmbedUtils.createDefault(content)).queue();
		} catch (HierarchyException e) {
			event.replyEmbeds(EmbedUtils.createError("This member cannot be updated. I need my role moved higher than theirs.")).setEphemeral(true).queue();
		}
	}

	public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

	}
}
