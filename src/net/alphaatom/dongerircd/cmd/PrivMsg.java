package net.alphaatom.dongerircd.cmd;

import net.alphaatom.dongerircd.DongerIRCd;
import net.alphaatom.dongerircd.IRCConnectionHandler;
import net.alphaatom.dongerircd.IRCServer;
import net.alphaatom.dongerircd.chusers.Channel;
import net.alphaatom.dongerircd.chusers.Numeric;
import net.alphaatom.dongerircd.exceptions.ChannelNotFoundException;

public class PrivMsg {
	
	/**
	 * Handles all privmsg requests
	 * 
	 * @param rawL Entire raw line
	 * @param uuid Users identifier
	 * @param inst Instance of the server
	 */
	public PrivMsg(String[] rawL, String uuid, DongerIRCd inst) {
		if (rawL[1].startsWith("#")) {
			//For channels follow this logic
			Channel ch = null;
			net.alphaatom.dongerircd.chusers.User user = inst.getClientByUniqueID(uuid).getUser();
			try {
				ch = inst.getChannel(rawL[1], inst);
			} catch (ChannelNotFoundException cnfex) {
				inst.getClientByUniqueID(uuid).getCommandHandler().sendRawLine(Numeric.ERR_NOSUCHNICK + " " + user.getNick() + " " + rawL[1] + " :No such nick/channel");
				return;
			}
			if (ch != null) {
				if (!ch.getUsers().contains(user) && ch.getChannelModes().contains("n")) {
					//Obey the +n mode which all channels have set, the channel must contiain the user
					inst.getClientByUniqueID(uuid).getCommandHandler().sendRawLine(Numeric.ERR_CANNOTSENDTOCHAN + " " + ch.getChannelName() + " :You are not in the channel");
					return;
				}
				if (ch.isUserBanned(user)) {
					//Banned users cannot talk
					inst.getClientByUniqueID(uuid).getCommandHandler().sendRawLine(Numeric.ERR_CANNOTSENDTOCHAN + " " + user.getNick() + " " + ch.getChannelName() + " :You are banned from " + ch.getChannelName());
					return;
				}
				String umask = user.getNick() + "!" + user.getUsername() + "@" + user.getHostname();
				for (net.alphaatom.dongerircd.chusers.User u : ch.getUsers()) {
					if (!u.getNick().equals(user.getNick())) {
						//send their message to everyone if allowed
						CommandHandler cmd = inst.getClientByUniqueID(u.getUniqueID()).getCommandHandler();
						cmd.sendRawWithoutPref(":" + umask + " PRIVMSG " + rawL[1] + " " + IRCServer.joinUpArrayFrom(rawL, 2, ' '));
					}
				}
			}
		} else {
			//For private messages
			for (IRCConnectionHandler irc : inst.connectedClients) {
				if (rawL[1].equalsIgnoreCase(irc.getUser().getNick())) {
					//send message to the recipient
					net.alphaatom.dongerircd.chusers.User user = inst.getClientByUniqueID(uuid).getUser();
					String umask = user.getNick() + "!" + user.getUsername() + "@" + user.getHostname();
					CommandHandler cmd = inst.getClientByUniqueID(irc.getUser().getUniqueID()).getCommandHandler();
					cmd.sendRawWithoutPref(":" + umask + " PRIVMSG " + rawL[1] + " " + IRCServer.joinUpArrayFrom(rawL, 2, ' '));
					return;
				}
			}
			//if we cant find them, tell the client
			inst.getClientByUniqueID(uuid).getCommandHandler().sendRawLine(Numeric.ERR_NOSUCHNICK + " " + inst.getClientByUniqueID(uuid).getUser().getNick() + " " + rawL[1] + " :No such nick/channel");
		}
	}
	
}
