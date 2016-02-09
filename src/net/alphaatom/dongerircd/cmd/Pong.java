package net.alphaatom.dongerircd.cmd;

import net.alphaatom.dongerircd.DongerIRCd;

public class Pong {
	
	/**
	 * Handles all pong requests
	 * 
	 * @param rawL Entire raw line
	 * @param uuid Users identifier
	 * @param inst Instance of the server
	 */
	public Pong(String[] rawL, String uuid, DongerIRCd inst) {
		String pongmsg = (rawL[1].startsWith(":")) ? rawL[1] : ":" + rawL[1];
		//if it isn't a reply to our own ping request, reply with a ping
		if (pongmsg.equals(":" + inst.getConfig().getString("ircaddress"))) {
			inst.getClientByUniqueID(uuid).pingChecker.recvPong();
			return;
		}
		inst.getClientByUniqueID(uuid).getCommandHandler().sendRawWithoutPref("PING " + pongmsg);
	}

}
