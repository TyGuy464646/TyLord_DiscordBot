package me.TyGuy464646.data.cache;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO object that stores data for the guild's suggestion board.
 *
 * @author TyGuy464646
 */
public class Suggestion {

	/**
	 * The ID of the guild this belongs to.
	 */
	private long guild;

	/**
	 * Ordered list of suggestion message IDs.
	 */
	private List<Long> messages;

	/**
	 * Ordered list of authors for each suggestion ID.
	 */
	private List<Long> authors;

	/**
	 * The ID of the TextChannel where suggestions are displayed.
	 */
	private Long channel;

	/**
	 * The number of the next suggestion.
	 */
	private long number;

	/**
	 * Whether responses DM the suggestion author.
	 */
	@BsonProperty("response_dm")
	private boolean responseDM;

	/**
	 * Whether suggestions are anonymous.
	 */
	@BsonProperty("is_anonymous")
	private boolean isAnonymous;

	/**
	 * For POJO object.
	 */
	public Suggestion() { }

	/**
	 * Creates a brand-new suggestion board without existing data.
	 * @param guild ID for the guild.
	 */
	public Suggestion(long guild) {
		this.guild = guild;
		this.channel = null;
		this.number = 1;
		this.responseDM = false;
		this.isAnonymous = false;
		this.messages = new ArrayList<>();
		this.authors = new ArrayList<>();
	}

	// Getters and Setters
	public long getGuild() {
		return guild;
	}

	public void setGuild(long guild) {
		this.guild = guild;
	}

	public List<Long> getMessages() {
		return messages;
	}

	public void setMessages(List<Long> messages) {
		this.messages = messages;
	}

	public List<Long> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Long> authors) {
		this.authors = authors;
	}

	public Long getChannel() {
		return channel;
	}

	public void setChannel(Long channel) {
		this.channel = channel;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public boolean isResponseDM() {
		return responseDM;
	}

	public void setResponseDM(boolean responseDM) {
		this.responseDM = responseDM;
	}

	public boolean isAnonymous() {
		return isAnonymous;
	}

	public void setAnonymous(boolean anonymous) {
		isAnonymous = anonymous;
	}
}
