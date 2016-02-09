package net.alphaatom.dongerircd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import net.alphaatom.dongerircd.exceptions.MalformedConfigException;

public class Config {
	
	private File configFile;
	private HashMap<String, String> configItems = new HashMap<String, String>();
	
	/**
	 * Get, or create the config
	 * 
	 * @throws IOException
	 */
	public Config() throws IOException {
		File file = new File("config.txt");
		if (!file.exists()) {
			System.err.println("Failed to find \"config.txt\". Creating new config file.");
			file.createNewFile();
			BufferedWriter write = new BufferedWriter(new FileWriter(file, true));
			write.write("%pingtimeout=121\n");
			write.write("%ircaddress=888.888.888.888\n");
			write.close();
		}
		BufferedReader read = new BufferedReader(new FileReader(file));
		String s;
		while((s=read.readLine())!=null) {
			if (!s.startsWith("%")) {
				if (s.matches("[\\s]*+")) {
					//ignore whitespace
					continue;
				}
				read.close();
				throw new MalformedConfigException("Line did not start with %: " + s);
			}
			String[] inf = s.substring(1).trim().split("=");
			if (inf.length < 2) {
				read.close();
				throw new MalformedConfigException("Failed to read data: " + s);
			}
			configItems.put(inf[0], inf[1]); //put key and data in
		}
		System.out.println("Successfully loaded all config values from file.");
		configFile = file;
		read.close();
	}
	
	/**
	 * 
	 * @return The file object for the config
	 */
	public File getFile() {
		return configFile;
	}
	
	/**
	 * Get the string related to a key
	 * 
	 * @param key Key to check
	 * @return The string related to the key
	 */
	public String getString(String key) {
		if (configItems.containsKey(key)) {
			return configItems.get(key);
		} else {
			return null;
		}
	}
	
	/**
	 * Parse the value as an integer
	 * 
	 * @param key The key to check
	 * @return The integer value related to the key
	 */
	public int getInt(String key) {
		if (configItems.containsKey(key)) {
			try {
				return Integer.parseInt(configItems.get(key));
			} catch (NumberFormatException nfex) {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	/**
	 * Returns the true if the string is "true" (case insensitive), otherwise returns false
	 * 
	 * @param key The key to check
	 * @return The boolean value of the value
	 */
	public boolean getBoolean(String key) {
		if (configItems.containsKey(key)) {
			return Boolean.parseBoolean(configItems.get(key));
		}
		return false;
	}
	
}
