package me.TyGuy464646.data.cache.moderation;

/**
 * POJO object that stores information about a user ban.
 * @author TyGuy464646
 */
public class Ban {

	private long user;
	private long timestamp;
	private int days;

	public Ban() { }

	public Ban(long user, long timestamp, int days) {
		this.user = user;
		this.timestamp = timestamp;
		this.days = days;
	}

	// Getters and Setters
	public long getUser() {
		return user;
	}

	public void setUser(long user) {
		this.user = user;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}
}
