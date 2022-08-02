package me.TyGuy464646.data.cache;

import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 * POJO object that stores config data for a guild.
 *
 * @author TyGuy464646
 */
public class Config {

	private long guild;

	private Long premium;

	public Config() {
	}

	public Config(long guild) {
		this.guild = guild;
		this.premium = null;
	}

	// Getters and Setters
	public long getGuild() {
		return guild;
	}

	public void setGuild(long guild) {
		this.guild = guild;
	}

	public Long getPremium() {
		return premium;
	}

	public void setPremium(Long premium) {
		this.premium = premium;
	}
}
