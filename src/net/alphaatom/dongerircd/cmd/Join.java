package net.alphaatom.dongerircd.cmd;

import net.alphaatom.dongerircd.DongerIRCd;
import net.alphaatom.dongerircd.IRCConnectionHandler;
import net.alphaatom.dongerircd.chusers.Channel;
import net.alphaatom.dongerircd.chusers.Numeric;
import net.alphaatom.dongerircd.exceptions.ChannelNotFoundException;

public class Join {
	
	/**
	 * Handles all JOIN requests 
	 * 
	 * @param rawL Entire raw line
	 * @param uuid Users identifier
	 * @param inst Instance of the server
	 */
	public Join(String[] rawL, String uuid, DongerIRCd inst) {
		//assume parameter is a channel name and ensure it starts with #
		String channelName = rawL[1].startsWith("#") ? rawL[1] : "#" + rawL[1]; 
		Channel ch;
		try {
			ch = inst.getChannel(channelName, inst);
		} catch (ChannelNotFoundException cnfex) {
			ch = new Channel(channelName, inst);
		}
		IRCConnectionHandler ircHandle = inst.getClientByUniqueID(uuid);
		CommandHandler cmd = ircHandle.getCommandHandler();
		if (ch.getUsers().contains(ircHandle.getUser())) {
			//dont let a user join a channel they are already in
			return;
		}
		if (ch.isUserBanned(ircHandle.getUser())) {
			//let a user know if they are banned, and dont let them join
			cmd.sendRawLine(Numeric.ERR_BANNEDFROMCHAN + " " + ch.getChannelName() + " :You are banned.");
			return;
		}
		cmd.sendRawWithoutPref(":" + ircHandle.getUser().getNick() + "!" + ircHandle.getUser().getUsername() + "@" + ircHandle.getUser().getHostname() + " JOIN :" + channelName);
		ch.addUser(ircHandle.getUser(), ch.getUsers().isEmpty());
		//let the user know they joined and add them to the list
		String userList = createUserList(ch);
		cmd.sendRawLine(Numeric.RPL_NAMREPLY + " " + ircHandle.getUser().getNick() + " = " + channelName + " :" + userList);
		cmd.sendRawLine(Numeric.RPL_ENDOFNAMES + " " + ircHandle.getUser().getNick() + " " + channelName + " :End of /NAMES list.");
		//send them a list of people in the channel
		if (!ch.getChannelTopic().equals("")) {
			//send them the topic, if one exists
			cmd.sendRawLine(Numeric.RPL_TOPIC + " " + ircHandle.getUser().getNick() + " " + ch.getChannelName() + " " + ch.getChannelTopic());
		}
		if (ch.getUsers().size() > 1) {
			for (net.alphaatom.dongerircd.chusers.User u : ch.getUsers()) {
				if (!u.getNick().equals(ircHandle.getUser().getNick())) {
					//let everyone else in the channel know they have joined
					inst.getClientByUniqueID(u.getUniqueID()).getCommandHandler().sendRawWithoutPref(":" + ircHandle.getUser().getNick() + "!" + ircHandle.getUser().getUsername() + "@" + ircHandle.getUser().getHostname() + " JOIN :" + channelName);
				}
			}
		}
	}
	
	/**
	 * Creates a formatted list of users to send to the client in the form of a NAMES reply
	 * 
	 * @param ch Channel to get the list from
	 * @return Formatted user list
	 */
	private String createUserList(Channel ch) {
		String ul = "";
		for (net.alphaatom.dongerircd.chusers.User u : ch.getUsers()) {
			ul = ul + " " + (ch.userIsOp(u) ? "@" : "") + u.getNick() + "!" + u.getUsername() + "@" + u.getHostname();
		}
		return ul.trim();
	}

}
