package net.alphaatom.dongerircd;

import net.alphaatom.dongerircd.cmd.CommandHandler;
import net.alphaatom.dongerircd.cmd.Quit;

/**
 * Seperate thread for checking for ping timeouts and disconnecting
 * non-responsive clients
 * 
 * @author Matt
 *
 */
public class PingChecker implements Runnable {
	
	/**
	 * Private variables for the loop
	 */
	private CommandHandler cmdHandle;
	private IRCConnectionHandler ircHandle;
	private long timeSincePong = System.currentTimeMillis();
	private boolean receivedPong = true;
	private boolean firstRun = true;
	
	/**
	 * Constructor for a PingChecker each client must have one of these
	 * 
	 * @param cmd The clients command handler
	 * @param irc The actual connection handler
	 */
	public PingChecker(CommandHandler cmd, IRCConnectionHandler irc) {
		cmdHandle = cmd;
		ircHandle = irc;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			if (System.currentTimeMillis() - timeSincePong > ircHandle.ircInst.getConfig().getInt("pingtimeout")*1000) {
				//has it been 121 seconds since we sent a ping?
				if (!firstRun) {
					if (!receivedPong) {
						//if we haven't received a pong, disconnect the client
						try {
							new Quit("QUIT :Ping Timeout (121 seconds)".split(" "), ircHandle.getUniqueId(), ircHandle.ircInst);
						} catch (NullPointerException npe) {
							//client is dead anyway
							break;
						}
					} else {
						//ping was received, reset the variable
						receivedPong = false;
					}
					//reset the time since we sent the ping
					timeSincePong = System.currentTimeMillis();
					cmdHandle.sendRawWithoutPref("PING :" + ircHandle.ircInst.getConfig().getString("ircaddress")); //send new PING
				} else {
					firstRun = false;
				}
			}
		}
	}
	
	/**
	 * Public method to set the private receivedPong variable to true
	 */
	public void recvPong() {
		receivedPong = true;
	}


}
