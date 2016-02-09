package net.alphaatom.dongerircd;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import net.alphaatom.dongerircd.chusers.Channel;
import net.alphaatom.dongerircd.exceptions.ChannelNotFoundException;

public class DongerIRCd {
	
	/**
	 * Variables for internal use
	 */
	private Config config;
	private int port = 6667;
	protected ServerSocket sSocket;
	private Socket clientSock;
	public ArrayList<IRCConnectionHandler> connectedClients;
	public ArrayList<Channel> existingChannels = new ArrayList<Channel>();
	
	/**
	 * Create a new server
	 * 
	 * @throws IOException Throws if the in/out isn't playing nicely
	 */
	public DongerIRCd() throws IOException {
		config = new Config();
		if (config.getString("ircaddress").equals("888.888.888.888")) {
			System.err.println("Please input your own IRC address in config.txt");
			System.exit(0);
		}
		System.out.println("Started DongerIRCd on the ip: " + config.getString("ircaddress") + " port: 6667");
		System.out.println("Ping timeout is set to " + config.getString("pingtimeout") + " seconds");
		connectedClients = new ArrayList<IRCConnectionHandler>();
		sSocket = new ServerSocket(port);
		this.start(sSocket);
	}
	
	/**
	 * Start the server running
	 * 
	 * @param socket
	 * @throws IOException
	 */
	private void start(ServerSocket socket) throws IOException {
		while (!socket.isClosed()) {
			clientSock = socket.accept();
			IRCConnectionHandler irc = new IRCConnectionHandler(clientSock, this);
			connectedClients.add(irc);
			new Thread(irc).start();
		}
	}
	
	/**
	 * Get a client by its unique ID
	 * 
	 * @param uuid Unique id to check
	 * @return IRCConnectionHandler object
	 */
	public IRCConnectionHandler getClientByUniqueID(String uuid) {
		for (IRCConnectionHandler client : this.connectedClients) {
			if (client.getUniqueId().equals(uuid)) {
				return client;
			}
		}
		return null;
	}
	
	/**
	 * Remove a connection
	 * 
	 * @param irc connection to remove
	 */
	public void removeConnection(IRCConnectionHandler irc) {
		connectedClients.remove(irc);
	}
	
	/**
	 * Get the config file
	 * 
	 * @return
	 */
	public Config getConfig() {
		return config;
	}
	
	/**
	 * Find out if a channel exists and return its Channel object
	 * 
	 * @param name Name of channel
	 * @param inst Instance of server upon which to find Channel
	 * @return Channel if found
	 * @throws ChannelNotFoundException If channel isn't found this is thrown
	 */
	public Channel getChannel(String name, DongerIRCd inst) throws ChannelNotFoundException {
		for (Channel channel : inst.existingChannels) {
			if (channel.getChannelName().equalsIgnoreCase(name)) {
				return channel;
			}
		}
		throw new ChannelNotFoundException();
	}

}
