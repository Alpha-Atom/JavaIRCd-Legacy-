package net.alphaatom.dongerircd.cmd;

import net.alphaatom.dongerircd.DongerIRCd;
import net.alphaatom.dongerircd.IRCConnectionHandler;
import net.alphaatom.dongerircd.IRCServer;
import net.alphaatom.dongerircd.chusers.Channel;
import net.alphaatom.dongerircd.chusers.Numeric;
import net.alphaatom.dongerircd.exceptions.ChannelNotFoundException;

public class Kick {
	
	/**
	 * Handles all kick requests
	 * 
	 * @param rawL Entire raw line
	 * @param uuid Users identifier
	 * @param inst Instance of the server
	 */
	public Kick(String[] rawL, String uuid, DongerIRCd inst) {
		IRCConnectionHandler irc = inst.getClientByUniqueID(uuid);
		Channel ch;
		try {
			//get the channel, and send error numeric if not found
			ch = inst.getChannel(rawL[1], inst);
		} catch (ChannelNotFoundException cnfex) {
			irc.getCommandHandler().sendRawLine(Numeric.ERR_NOSUCHCHANNEL + " " + irc.getUser().getNick() + " " + rawL[1] + " :No such channel");
			return;
		}
		if (isUserInChannel(rawL[2], ch) && ch.userIsOp(irc.getUser())) {
			String umask = ":" + irc.getUser().getNick() + "!" + irc.getUser().getUsername() + "@" + irc.getUser().getHostname();
			for (net.alphaatom.dongerircd.chusers.User u : ch.getUsers()) {
				//if the user is in the channel, tell everyone they got kicked and the reason why
				CommandHandler cmd = inst.getClientByUniqueID(u.getUniqueID()).getCommandHandler();
				String reason = rawL.length > 3 ? IRCServer.joinUpArrayFrom(rawL, 3, ' ') : ":Kicked from channel";
				cmd.sendRawWithoutPref(umask + " KICK " + ch.getChannelName() + " " + rawL[2] + " " + reason.trim());
			}
			//remove them from the Channel object
			ch.removeUser(getUser(rawL[2], inst), ch.userIsOp(getUser(rawL[2], inst)));
		} else {
			//if not in the channel, send error numeric
			irc.getCommandHandler().sendRawLine(Numeric.ERR_NOSUCHNICK + " " + irc.getUser().getNick() + " " + rawL[2] + " :No such nick.");
		}
		
	}
	
	/**
	 * Finds out if a user of the String name specified is in the channel
	 * 
	 * @param name Nick of user to check
	 * @param ch Channel to check
	 * @return True if a user is present
	 */
	private boolean isUserInChannel(String name, Channel ch) {
		for (net.alphaatom.dongerircd.chusers.User u : ch.getUsers()) {
			if (u.getNick().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the user from its nickname
	 * 
	 * @param name Nick to find
	 * @param inst Instance of the server
	 * @return User object associated with the nickname
	 */
	private net.alphaatom.dongerircd.chusers.User getUser(String name, DongerIRCd inst) {
		for (IRCConnectionHandler irc : inst.connectedClients) {
			if (irc.getUser().getNick().equals(name)) {
				return irc.getUser();
			}
		}
		return null;
	}

}
