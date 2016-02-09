package net.alphaatom.dongerircd.cmd;

import java.util.ArrayList;

import net.alphaatom.dongerircd.DongerIRCd;
import net.alphaatom.dongerircd.IRCConnectionHandler;
import net.alphaatom.dongerircd.chusers.*;

public class Nick {
	
	/**
	 * Handles all nick requests
	 * 
	 * @param rawL Entire raw line
	 * @param uuid Users identifier
	 * @param inst Instance of the server
	 */
	public Nick(String[] rawL, String uuid, DongerIRCd inst) {
		String requestedNick = rawL[1].startsWith(":") ? rawL[1].substring(1) : rawL[1];
		ArrayList<IRCConnectionHandler> currentClients = inst.connectedClients;
		for (int i = 0; i < currentClients.size(); i++) {
			if (currentClients.get(i).getUser().getNick().equalsIgnoreCase(requestedNick)) {
				//dont let someone change their nick to an existing one
				inst.getClientByUniqueID(uuid).getCommandHandler().sendRawLine(Numeric.ERR_NICKNAMEINUSE + " " + requestedNick.split(" ")[0] + " :Nickname already in use");
				new Quit("QUIT :".split(" "), uuid, inst);
				return;
			}
		}
		IRCConnectionHandler irc = inst.getClientByUniqueID(uuid);
		String umask = irc.getUser().getNick() + "!" + irc.getUser().getUsername() + "@" + irc.getUser().getHostname();
		if (!inst.getClientByUniqueID(uuid).getUser().getNick().equals("")) {
			irc.getCommandHandler().sendRawWithoutPref(":" + umask + " NICK " + rawL[1]);
			for (Channel ch : inst.existingChannels) {
				//let everyone who is in the same channels as the user know their nick is changing
				if (ch.getUsers().contains(irc.getUser())) {
					for (net.alphaatom.dongerircd.chusers.User u : ch.getUsers()) {
						if (!u.getNick().equalsIgnoreCase(irc.getUser().getNick())) {
							CommandHandler cmd = inst.getClientByUniqueID(u.getUniqueID()).getCommandHandler();
							ch.removeUser(irc.getUser(), ch.userIsOp(irc.getUser()));
							cmd.sendRawWithoutPref(":" + umask + " NICK " + rawL[1]);
						}
					}
				}
			}
		}
		//change it in the user object
		inst.getClientByUniqueID(uuid).getUser().setNick(requestedNick);
	}

}
