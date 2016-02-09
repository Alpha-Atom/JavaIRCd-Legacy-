package net.alphaatom.dongerircd.cmd;

import net.alphaatom.dongerircd.DongerIRCd;

public class User {
	
	/**
	 * Handles all user requests
	 * 
	 * @param rawL Entire raw line
	 * @param uuid Users identifier
	 * @param inst Instance of the server
	 */
	public User(String[] rawL, String uuid, DongerIRCd ircInst) {
		//make a new user with info provided
		String username = rawL[1];
		byte modes = Byte.valueOf(rawL[2]);
		String realname = getReal(rawL).substring(1);
		ircInst.getClientByUniqueID(uuid).getUser().setUsername(username);
		ircInst.getClientByUniqueID(uuid).getUser().setMode(modes);
		ircInst.getClientByUniqueID(uuid).getUser().setRealname(realname);
		ircInst.getClientByUniqueID(uuid).sendWelcome();
	}
	
	/**
	 * Put a real name together
	 * 
	 * @param ar real name array
	 * @return String of real name
	 */
	private String getReal(String[] ar) {
		String str = "";
		for (int i = 4; i < ar.length; i++) {
			str = str + " " + ar[i];
		}
		return str.trim();
	}

}
