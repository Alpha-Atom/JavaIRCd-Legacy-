package net.alphaatom.dongerircd.cmd;

import net.alphaatom.dongerircd.DongerIRCd;

public class Ping {
	
	/**
	 * Handles all ping requests
	 * 
	 * @param rawL Entire raw line
	 * @param uuid Users identifier
	 * @param inst Instance of the server
	 */
	public Ping(String[] rawL, String uuid, DongerIRCd inst) {
		//Respond with an appropriate pong command
		String pingmsg = (rawL[1].startsWith(":")) ? rawL[1] : ":" + rawL[1];
		inst.getClientByUniqueID(uuid).getCommandHandler().sendRawLine("PONG " + inst.getConfig().getString("ircaddress") + " " + pingmsg);
	}

}
