package net.alphaatom.dongerircd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

import net.alphaatom.dongerircd.cmd.CommandHandler;
import net.alphaatom.dongerircd.chusers.Numeric;
import net.alphaatom.dongerircd.chusers.User;

public class IRCConnectionHandler implements Runnable {
	
	/**
	 * Variables for internal use
	 */
	private Socket clientSocket;
	private User user;
	private CommandHandler cmdHandle;
	protected DongerIRCd ircInst;
	private String uniqueID = "";
	public PingChecker pingChecker;
	private Thread pingThread;
	
	/**
	 * Create a new client
	 * 
	 * @param sock The clients socket
	 * @param inst An instance of the server
	 * @throws IOException Throws when the I/O refuses to play nice
	 */
	public IRCConnectionHandler(Socket sock, DongerIRCd inst) throws IOException {
		clientSocket = sock;
		uniqueID = generateUniqueID();
		user = new User(sock.getInetAddress().getHostName(), uniqueID);
		ircInst = inst;
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		System.out.println("New connection, id: " + uniqueID);
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
			cmdHandle = new CommandHandler(input, output, ircInst);
			pingChecker = new PingChecker(cmdHandle, this);
			Thread pingThread = new Thread(pingChecker); //create a new keep-alive ping checker
			pingThread.start();
			while (true) {
				if (clientSocket.isClosed()) {
					break;
				}
				String s = null;
				try {
					s = input.readLine(); //read lines
				} catch (SocketException se) {
					break;
				}
				if (s != null) {
					System.out.println("-> " + s);
					cmdHandle.handleRawLine(s, uniqueID);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	/**
	 * Clean up the sockets and stop checking for pings, ignore deprecation warning
	 * stop is required for this
	 */
	public void end() throws IOException {
		clientSocket.close();
		pingThread.stop();
		System.out.println("Ended connection id: " + this.uniqueID);
	}
	
	/**
	 * 
	 * @return The command handler for this client
	 */
	public CommandHandler getCommandHandler() {
		return cmdHandle;
	}
	
	/**
	 * 
	 * @return The related user object
	 */
	public User getUser() {
		return this.user;
	}
	
	/**
	 * 
	 * @return Generate a new unique ID in string format
	 */
	private String generateUniqueID() {
		UUID uu = UUID.randomUUID();
		return uu.toString();
	}
	
	/**
	 * 
	 * @return This clients unique ID
	 */
	public String getUniqueId() {
		if (!uniqueID.equals("")) {
			return uniqueID;
		} else {
			return null;
		}
	}
	
	/**
	 * Send the welcome message to a client
	 */
	public void sendWelcome() {
		this.getCommandHandler().sendRawLine("001 " + this.getUser().getNick() + " :Welcome to the Internet Relay Network " + this.getUser().getNick() + "!" + this.getUser().getUsername() + "@" + this.getUser().getHostname());
		this.getCommandHandler().sendRawLine("002 " + this.getUser().getNick() + " :Your host is DongerIRCd, running version 0.1");
		this.getCommandHandler().sendRawLine("003 " + this.getUser().getNick() + " :This server was created 21:37 8th November 2014");
		this.getCommandHandler().sendRawLine("004 " + this.getUser().getNick() + " " + ircInst.getConfig().getString("ircaddress") + " DongerIRCd BIRSWcdhiorswx BCEFIJKLMNOPQRSTabdefghijklmnopqrstuvz FIJLabdefghjkloqv");
		this.getCommandHandler().sendRawLine("005 " + this.getUser().getNick() + " " + "NETWORK=DongerLand :are supported by this server");
		this.getCommandHandler().sendRawLine("042 " + this.getUser().getNick() + " " + this.getUniqueId() + " :your unique ID");
		this.getCommandHandler().sendRawLine(Numeric.ERR_NOMOTD + " " + this.getUser().getNick() + " :This server is too cool for MoTD");
	}

}
