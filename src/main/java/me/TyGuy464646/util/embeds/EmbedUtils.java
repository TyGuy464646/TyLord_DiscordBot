package me.TyGuy464646.util.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class storing helpful methods for embeds.
 *
 * @author TyGuy464646
 */
public class EmbedUtils {

	/**
	 * Emojis
	 */
	public static final String GREEN_TICK = ":white_check_mark:";
	public static final String RED_X = ":x:";

	/**
	 * Quickly creates a simple error embed.
	 *
	 * @param errorMessage message to be displayed.
	 * @return completed error {@link MessageEmbed}.
	 */
	public static @NotNull MessageEmbed createError(String errorMessage) {
		return new EmbedBuilder()
				.setColor(EmbedColor.ERROR.color)
				.setDescription(RED_X + " **Error** | `" + errorMessage + "`")
				.build();
	}

	/**
	 * Quickly creates a simple default embed.
	 *
	 * @param message message to be displayed.
	 * @return completed default {@link MessageEmbed}.
	 */
	public static @NotNull MessageEmbed createDefault(String message) {
		return new EmbedBuilder()
				.setColor(EmbedColor.DEFAULT.color)
				.setDescription("`" + message + "`")
				.build();
	}

	/**
	 * Quickly creates a simple success embed.
	 *
	 * @param message message to be displayed
	 * @return completed success {@link MessageEmbed}
	 */
	public static @NotNull MessageEmbed createSuccess(String message) {
		return new EmbedBuilder()
				.setColor(EmbedColor.SUCCESS.color)
				.setDescription(GREEN_TICK + " **Success** | `" + message + "`")
				.build();
	}
}
