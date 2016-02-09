package net.alphaatom.dongerircd.cmd;

import java.io.IOException;

import net.alphaatom.dongerircd.DongerIRCd;
import net.alphaatom.dongerircd.IRCConnectionHandler;
import net.alphaatom.dongerircd.IRCServer;
import net.alphaatom.dongerircd.chusers.Channel;

public class Quit {
	
	/**
	 * Handles all quit requests
	 * 
	 * @param rawL Entire raw line
	 * @param uuid Users identifier
	 * @param inst Instance of the server
	 */
	public Quit(String[] rawL, String uuid, DongerIRCd inst) {
		IRCConnectionHandler irc = inst.getClientByUniqueID(uuid);
		//clean up the connection from the server
		inst.removeConnection(irc);
		for (Channel ch : inst.existingChannels) {
			if (ch.getUsers().contains(irc.getUser())) {
				//tell everyone they are leaving
				for (net.alphaatom.dongerircd.chusers.User u : ch.getUsers()) {
					if (!u.getNick().equalsIgnoreCase(irc.getUser().getNick())) {
						String reason = (rawL.length > 1 && !rawL[1].equals(":")) ? IRCServer.joinUpArrayFrom(rawL, 1, ' ') : ":Disconnected (Gone to Donger heaven)";
						String umask = irc.getUser().getNick() + "!" + irc.getUser().getUsername() + "@" + irc.getUser().getHostname();
						try {
							CommandHandler cmd = inst.getClientByUniqueID(u.getUniqueID()).getCommandHandler();
							cmd.sendRawWithoutPref(":" + umask + " QUIT " + reason);
						} catch (NullPointerException npe) {
							//do nothing, we can expect this error when the client quits
						}
						ch.removeUser(irc.getUser(), ch.userIsOp(irc.getUser()));
					}
				}
			}
		}
		try {
			//clear up the sockets and end gracefully
			irc.end();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException ex) {
			//irc variable is already cleaned up
		}
	}

}
