package net.alphaatom.dongerircd.cmd;

import net.alphaatom.dongerircd.DongerIRCd;
import net.alphaatom.dongerircd.IRCServer;
import net.alphaatom.dongerircd.chusers.Channel;
import net.alphaatom.dongerircd.exceptions.ChannelNotFoundException;

public class Topic {
	
	/**
	 * Handles all topic requests
	 * 
	 * @param rawL Entire raw line
	 * @param uuid Users identifier
	 * @param inst Instance of the server
	 */
	public Topic(String[] rawL, String uuid, DongerIRCd inst) {
		if (rawL[1].startsWith("#")) {
			Channel ch = null;
			try {
				ch = inst.getChannel(rawL[1], inst);
			} catch (ChannelNotFoundException cnfex) {
				
			}
			net.alphaatom.dongerircd.chusers.User user = inst.getClientByUniqueID(uuid).getUser();
			if (!ch.userIsOp(user) && ch.getChannelModes().contains("t")) {
				//dont let non-ops change topic if +t is set
				return;
			}
			ch.setChannelTopic(IRCServer.joinUpArrayFrom(rawL, 2, ' ').trim());
			for (net.alphaatom.dongerircd.chusers.User u : ch.getUsers()) {
				//set a new topic and tell everyone about it
				CommandHandler cmd = inst.getClientByUniqueID(u.getUniqueID()).getCommandHandler();
				String umask = user.getNick() + "!" + user.getUsername() + "@" + user.getHostname();
				cmd.sendRawWithoutPref(":" + umask + " TOPIC " + ch.getChannelName() + " " + ch.getChannelTopic());
			}
			
		}
	}

}
