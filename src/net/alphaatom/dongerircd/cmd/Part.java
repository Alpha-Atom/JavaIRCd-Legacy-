package net.alphaatom.dongerircd.cmd;

import net.alphaatom.dongerircd.DongerIRCd;
import net.alphaatom.dongerircd.IRCServer;
import net.alphaatom.dongerircd.chusers.Channel;
import net.alphaatom.dongerircd.chusers.Numeric;
import net.alphaatom.dongerircd.exceptions.ChannelNotFoundException;

public class Part {
	
	/**
	 * Handles all part requests
	 * 
	 * @param rawL Entire raw line
	 * @param uuid Users identifier
	 * @param inst Instance of the server
	 */
	public Part(String[] rawL, String uuid, DongerIRCd inst) {
		String channelName = rawL[1].startsWith("#") ? rawL[1] : "#" + rawL[1];
		Channel ch = null;
		net.alphaatom.dongerircd.chusers.User user = inst.getClientByUniqueID(uuid).getUser();
		//get the channel
		try {
			ch = inst.getChannel(channelName, inst);
		} catch (ChannelNotFoundException cnfex) {
			inst.getClientByUniqueID(uuid).getCommandHandler().sendRawLine(Numeric.ERR_NOSUCHCHANNEL + " " + user.getNick() + " " + rawL[1] + " :No such channel");
			return;
		}
		if (ch.getUsers().contains(user)) {
			String umask = user.getNick() + "!" + user.getUsername() + "@" + user.getHostname();
			inst.getClientByUniqueID(user.getUniqueID()).getCommandHandler().sendRawWithoutPref(":" + umask + " PART " + channelName + ((rawL.length <= 2) ? "" : (" " + IRCServer.joinUpArrayFrom(rawL, 2, ' '))));;
			//remove them from the object
			ch.removeUser(user, ch.userIsOp(user));
			for (net.alphaatom.dongerircd.chusers.User u : ch.getUsers()) {
				//let everyone know they are leaving
				CommandHandler cmd = inst.getClientByUniqueID(u.getUniqueID()).getCommandHandler();
				cmd.sendRawWithoutPref(":" + umask + " PART " + channelName + ((rawL.length <= 2) ? "" : (" " + IRCServer.joinUpArrayFrom(rawL, 2, ' '))));
			}
		}
	}

}
