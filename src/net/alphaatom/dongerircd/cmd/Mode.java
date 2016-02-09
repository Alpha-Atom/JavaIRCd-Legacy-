package net.alphaatom.dongerircd.cmd;

import net.alphaatom.dongerircd.DongerIRCd;
import net.alphaatom.dongerircd.IRCConnectionHandler;
import net.alphaatom.dongerircd.chusers.Channel;
import net.alphaatom.dongerircd.chusers.Numeric;
import net.alphaatom.dongerircd.exceptions.ChannelNotFoundException;

public class Mode {
	
	/**
	 * Handles all mode requests
	 * 
	 * @param rawL Entire raw line
	 * @param uuid Users identifier
	 * @param inst Instance of the server
	 */
	public Mode(String[] rawL, String uuid, DongerIRCd inst) {
		if (rawL.length == 2) {
			if (rawL[1].startsWith("#")) {
				//simply requesting channel modes
				IRCConnectionHandler handle = inst.getClientByUniqueID(uuid);
				Channel ch;
				try {
					ch = inst.getChannel(rawL[1], inst);
				} catch (ChannelNotFoundException cnfex) {
					handle.getCommandHandler().sendRawLine(Numeric.ERR_NOSUCHCHANNEL + " " + handle.getUser().getNick() + " " + rawL[1] + " :No such channel");
					return;
				}
				//send all channel modes
				handle.getCommandHandler().sendRawLine(Numeric.RPL_CHANNELMODEIS + " " + handle.getUser().getNick() + " " + rawL[1] + " +" + ch.getChannelModes());
				//handle.getCommandHandler().sendRawLine("329" + " " + handle.getUser().getNick() + " " + rawL[1] + " " + ch.getCreationTime());
				// ^^ potentially useful code for sending channel creation time to client ^^ unimplemented in this version
			}
		} else {
			if (rawL[1].startsWith("#")) {
				//other channel mode options, note only implemented modes are support
				IRCConnectionHandler handle = inst.getClientByUniqueID(uuid);
				Channel ch;
				try {
					ch = inst.getChannel(rawL[1], inst);
				} catch (ChannelNotFoundException cnfex) {
					//send error numeric and return
					return;
				}
				String umask = ":" + handle.getUser().getNick() + "!" + handle.getUser().getUsername() + "@" + handle.getUser().getHostname();
				//deal with ban modes only
				if (rawL[2].equals("+b") || rawL[2].equals("-b")) {
					if (rawL[2].startsWith("+")) {
						if (rawL.length > 3) {
							String bString = "";
							if (rawL[3].matches("\\S+!\\S@\\S+")) { //if ban is in form *!*@*
								bString = rawL[3];
							} else {
								bString = rawL[3] + "!*@*"; //make it in form *!*@*
							}
							if (!ch.banUser(bString)) { return; } //if we can't ban them, return, if not add them and continue
							for (net.alphaatom.dongerircd.chusers.User user : ch.getUsers()) {
								CommandHandler cmd = inst.getClientByUniqueID(user.getUniqueID()).getCommandHandler();
								cmd.sendRawWithoutPref(umask + " MODE " + ch.getChannelName() + " +b " + bString);
								//let every client know they were banned
							}
						} else {
							//just return the banlist
							CommandHandler cmd = handle.getCommandHandler();
							for (String s : ch.getBanList()) {
								cmd.sendRawLine(Numeric.RPL_BANLIST + " " + handle.getUser().getNick() + " " + ch.getChannelName() + " " + s);
							}
							cmd.sendRawLine(Numeric.RPL_ENDOFBANLIST + " " + handle.getUser().getNick() + " " + ch.getChannelName() + " :End of channel ban list.");
						}
					} else {
						//same code for banning, but simply removes them
						if (rawL.length > 3) {
							String bString = "";
							if (rawL[3].matches("\\S+!\\S@\\S+")) {
								bString = rawL[3];
							} else {
								bString = rawL[3] + "!*@*";
							}
							if (!ch.unbanUser(bString)) { return; }
							for (net.alphaatom.dongerircd.chusers.User user : ch.getUsers()) {
								CommandHandler cmd = inst.getClientByUniqueID(user.getUniqueID()).getCommandHandler();
								cmd.sendRawWithoutPref(umask + " MODE " + ch.getChannelName() + " -b " + bString);
							}
						} else {
							net.alphaatom.dongerircd.chusers.User u = handle.getUser();
							handle.getCommandHandler().sendRawLine(Numeric.ERR_NEEDMOREPARAMS + " " + u.getNick() + " MODE :Need more params");
						}
					}
				}
			}
		}
	}

}
