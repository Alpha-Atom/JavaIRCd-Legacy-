package net.alphaatom.dongerircd.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;

import net.alphaatom.dongerircd.DongerIRCd;
import net.alphaatom.dongerircd.chusers.Numeric;

public class CommandHandler {
	
	/**
	 * Private variables for use in methods
	 */
	private BufferedReader ircInput;
	private PrintWriter ircOutput;
	private DongerIRCd ircInst;
	
	/**
	 * Create a new command handler.
	 * 
	 * @param input Where to handle commands from
	 * @param output Where to send handled command outputs
	 * @param inst Instance of the server
	 */
	public CommandHandler(BufferedReader input, PrintWriter output, DongerIRCd inst) {
		ircInput = input;
		ircOutput = output;
		ircInst = inst;
	}
	
	/**
	 * Handles all raw lines that can be handled by the server, sends command not recognised for all else
	 * 
	 * @param rawLine
	 * @param uuid
	 */
	public void handleRawLine(String rawLine, String uuid) {
		String[] rawL = rawLine.split(" ");
		if (rawL[0].equalsIgnoreCase("PRIVMSG")) {
			new PrivMsg(rawL, uuid, ircInst);
		} else if (rawL[0].equalsIgnoreCase("JOIN")) {
			new Join(rawL, uuid, ircInst);
		} else if (rawL[0].equalsIgnoreCase("QUIT")) {
			new Quit(rawL, uuid, ircInst);
		} else if (rawL[0].equalsIgnoreCase("USER")) {
			new User(rawL, uuid, ircInst);
		} else if (rawL[0].equalsIgnoreCase("PART")) {
			new Part(rawL, uuid, ircInst);
		} else if (rawL[0].equalsIgnoreCase("NICK")) {
			new Nick(rawL, uuid, ircInst);
		} else if (rawL[0].equalsIgnoreCase("PING")) {
			new Ping(rawL, uuid, ircInst);
		} else if (rawL[0].equalsIgnoreCase("MODE")) {
			new Mode(rawL, uuid, ircInst);
		} else if (rawL[0].equalsIgnoreCase("PONG")) {
			new Pong(rawL, uuid, ircInst);
		} else if (rawL[0].equalsIgnoreCase("KICK")) {
			new Kick(rawL, uuid, ircInst);
		} else if (rawL[0].equalsIgnoreCase("TOPIC")) {
			new Topic(rawL, uuid, ircInst);
		} else {
			sendRawLine(Numeric.ERR_UNKNOWNCOMMAND + " 421 " + rawL[0] + " :Unknown command.");
		}
	}
	
	/**
	 * Sends a line to the output, with the server ip as a prefix, most irc messages sent from the server
	 * should be sent through this
	 * 
	 * @param s line to send
	 */
	public void sendRawLine(String s) {
		System.out.println("<- " + s);
		ircOutput.println(":" + ircInst.getConfig().getString("ircaddress") + " " + s);
	}
	
	/**
	 * Sends a line to the output with no prefix, used in exceptional cases where the message needs
	 * to originate from a user
	 * 
	 * @param s line to send
	 */
	public void sendRawWithoutPref(String s) {
		System.out.println("<- " + s);
		ircOutput.println(s);
	}
	
	/**
	 * 
	 * @return This command handlers input
	 */
	public BufferedReader getIrcInput() {
		return ircInput;
	}
	
	/**
	 * 
	 * @param ircInput New input to set
	 */
	public void setIrcInput(BufferedReader ircInput) {
		this.ircInput = ircInput;
	}
	
	/**
	 * 
	 * @return This command handlers output
	 */
	public PrintWriter getIrcOutput() {
		return ircOutput;
	}
	
	/**
	 * 
	 * @param ircOutput New output to set
	 */
	public void setIrcOutput(PrintWriter ircOutput) {
		this.ircOutput = ircOutput;
	}

}
