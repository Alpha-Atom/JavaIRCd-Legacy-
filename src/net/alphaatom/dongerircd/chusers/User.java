package net.alphaatom.dongerircd.chusers;

/**
 * The {@code User} class holds information about each user connected
 * to the IRC server, they are identifiable by their {@code UUID} and
 * the instance of the server holds a method to do this
 * 
 * @author Matt
 *
 */
public class User {
	
	/**
	 * Private variables used below in getters and setters
	 */
	private String nick = "";
	private String hostname = "";
	private byte mode = 0;
	private String realname = "";
	private String username = "";
	private String uniqueID = "";
	
	/**
	 * Constructor for a new user, takes only two parameters, the rest
	 * is filled out when the server receives a valid USER command
	 * 
	 * @param hostname Clients hostname
	 * @param uuid Clients UUID, generated in the {@code IRCConnnectionHandler} class
	 */
	public User(String hostname, String uuid) {
		this.setHostname(hostname);
		this.setUniqueID(uuid);
	}

	/**
	 * The client can be identified by other IRC users by this as it is unique
	 * across the server, however this should not be used as an identifier
	 * in the code.
	 * 
	 * @return The users current nickname
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * Sets a new nickname, this must be synchronised with the all affected clients
	 * (includes any user in the same channels as this user) by use of the appropriate 
	 * NICK commands
	 * 
	 * @param nick Nickname to set
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * Returns the users hostname, identifies the connection a user is 
	 * connected from however is not guaranteed to be unique and therefore
	 * should not be used as an identifier
	 * 
	 * @return Users hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Sets a new hostname, is likely to be unused except in the case
	 * of IRC oper spoofing but is present anyway for completeness
	 * 
	 * @param hostname New hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * 
	 * @return The bytes that identify any usermodes set
	 */
	public byte getMode() {
		return mode;
	}

	/**
	 * Sets a new usermode, this method does not append and therefore a call to
	 * {@code getMode()} must be made in the argument
	 * 
	 * @param mode Usermodes to set
	 */
	public void setMode(byte mode) {
		this.mode = mode;
	}

	/**
	 * Users real name, if present will be listed in WHOIS requests
	 * 
	 * @return Users realname
	 */
	public String getRealname() {
		return realname;
	}

	/**
	 * Set a new real name for the user
	 * 
	 * @param realname Real name to set
	 */
	public void setRealname(String realname) {
		this.realname = realname;
	}

	/**
	 * Usernames are used in the full hostmask for the user when client requires the format:<br>
	 * {@code nick!username@hostname}
	 * 
	 * @return The clients username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Set a new username for the user
	 * 
	 * @param username Username to be set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Return the unique identifier for this user,
	 * preferred method of identifying a User
	 * 
	 * @return Users UUID
	 */
	public String getUniqueID() {
		return uniqueID;
	}

	/**
	 * Set a new unique ID for the user. Very unlikely to be used outside
	 * of the constructor but is present for completeness all the same.                           
	 * 
	 * @param uniqueID
	 */
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

}
