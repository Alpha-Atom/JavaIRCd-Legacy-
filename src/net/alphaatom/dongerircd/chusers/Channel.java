package net.alphaatom.dongerircd.chusers;

import java.util.ArrayList;

import net.alphaatom.dongerircd.DongerIRCd;

/**
 * The {@code Channel} class holds information about a channel, for
 * example "#info" and its user list, ban list, and other channel
 * specific information.
 * 
 * @author Matt
 *
 */
public class Channel {
	
	/**
	 * Private variables accessed by getters and setters below
	 */
	private String channelName = "";
	private String channelModes = "nt";
	private String channelTopic = "";
	private long channelTopicTime;
	private long creationTime;
	private ArrayList<User> users = new ArrayList<User>();
	private ArrayList<User> operators = new ArrayList<User>();
	private ArrayList<String> banList = new ArrayList<String>();
	
	/**
	 * Constructor for a new channel
	 * 
	 * @param chanName The name of the new channel
	 * @param inst The instance of DongerIRCd it is to be created on, so as to add it to
	 * 			   the list of existing channels
	 */
	public Channel(String chanName, DongerIRCd inst) {
		this.channelName = chanName;
		inst.existingChannels.add(this);
		setCreationTime(System.currentTimeMillis()/1000);
	}
	
	/**
	 * 
	 * @return The channel name including prefix (#)
	 */
	public String getChannelName() {
		return channelName;
	}
	
	/**
	 * Sets a new channel name, likely unused as channels do not change name
	 * present for completeness
	 * 
	 * @param channelName New channel name
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	
	/**
	 * 
	 * @return The channel modes , in format "+[A-Za-z]"
	 */
	public String getChannelModes() {
		return channelModes;
	}
	
	/**
	 * Sets new channel modes, does not append so you must include {@code getChannelModes()}
	 * in the argument, format "+[A-Za-z]"
	 * 
	 * @param channelModes Modes to set
	 */
	public void setChannelModes(String channelModes) {
		this.channelModes = channelModes;
	}
	/**
	 * 
	 * @return List of all users presently in the channel. This ArrayList must be
	 * synchronised with clients by use of the JOIN, PART, KICK commands
	 */
	public ArrayList<User> getUsers() {
		return users;
	}
	
	/**
	 * Sets new list of users, again likely unused but present for completeness
	 * Must be synchronised with clients as any other method relating to user list
	 * 
	 * @param users ArrayList of users
	 */
	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}
	
	/**
	 * Adds a single user to the list of users, must be synchronised with
	 * the client using JOIN
	 * 
	 * @param u User to add
	 * @param isOp Is the user an operator in the channel
	 */
	public void addUser(User u, boolean isOp) {
		if (isOp) {
			users.add(u);
			operators.add(u);
		} else {
			users.add(u);
		}
	}
	
	/**
	 * Remove a single user from the channel, must be synchronised with
	 * the client using PART,KICK commands
	 * 
	 * @param u User to add
	 * @param isOp Is the user an operator
	 */
	public void removeUser(User u, boolean isOp) {
		if (isOp) {
			users.remove(u);
			operators.remove(u);
		} else {
			users.remove(u);
		}
	}
	
	/**
	 * Is the user a channel operator
	 * 
	 * @param u User to check
	 * @return True if the user is on the operator list
	 */
	public boolean userIsOp(User u) {
		return operators.contains(u);
	}

	/**
	 * 
	 * @return Unix time in seconds when channel was created
	 */
	public long getCreationTime() {
		return creationTime;
	}
	
	/**
	 * Set a new creation time for the channel
	 * 
	 * @param creationTime New creation time to set
	 */
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	
	public void setChannelTopic(String newTopic) {
		this.channelTopic = newTopic;
		long creTime = System.currentTimeMillis()/1000;
		this.channelTopicTime = creTime;
	}
	
	/**
	 * 
	 * @return The channel topic
	 */
	public String getChannelTopic() {
		return this.channelTopic;
	}
	
	/**
	 * 
	 * @return Time the channel topic was set
	 */
	public long getChannelTopicTime() {
		return this.channelTopicTime;
	}
	
	/**
	 * 
	 * @return Full list of banned users
	 */
	public ArrayList<String> getBanList() {
		return banList;
	}
	
	/**
	 * Set a new banlist, likely unused but present for completeness
	 * 
	 * @param banList New list of banned users
	 */
	public void setBanList(ArrayList<String> banList) {
		this.banList = banList;
	}
	
	/**
	 * Add a single hostmask to the ban list
	 * 
	 * @param u The hostmask to ban in form {@code *!*@*}
	 * @return True if the user was succesfully banned, false if the user was already banned
	 */
	public boolean banUser(String hostmask) {
		if (!banList.contains(hostmask)) {
			banList.add(hostmask);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Remove a hostmask from the ban list
	 * 
	 * @param u The hostmask to remove from the list {@code *!*@*}
	 * @return True if the user was successfully unbanned false if the user was already unbanned
	 */
	public boolean unbanUser(String hostmask) {
		if (banList.contains(hostmask)) {
			banList.remove(hostmask);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if a given user matches a nick, username or hostname ban
	 * 
	 * @param u User
	 * @return true if the user is banned, false if not
	 */
	public boolean isUserBanned(User u) {
		int match = 0;
		for (String s : banList) {
			String[] info = s.split("[!@]");
			if (!info[0].equals("*")) {
				if (info[0].equalsIgnoreCase(u.getNick())) {
					match++;
				}
			} else {
				match++;
			}
			if (!info[1].equals("*")) {
				if (info[1].equalsIgnoreCase(u.getUsername())) {
					match++;
				}
			} else {
				match++;
			}
			if (!info[2].equals("*")) {
				if (info[2].equalsIgnoreCase(u.getHostname())) {
					match++;
				}
			} else {
				match++;
			}
			if (match == 3) {
				return true;
			} else {
				match = 0;
			}
		}
		return false;
	}

}
