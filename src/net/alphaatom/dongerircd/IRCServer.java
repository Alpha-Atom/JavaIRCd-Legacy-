package net.alphaatom.dongerircd;

import java.io.IOException;

public class IRCServer {
	
	/**
	 * Start the server up
	 * 
	 * @param args command line arugments
	 */
	public static void main(String[] args) {
		try {
			new DongerIRCd();
		} catch (IOException e) {
			System.err.println("Failed to establish in-out relationship between sockets.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Utility method for joining up String arrays e.g
	 * {@code ["hello","world","!"]} might become {@code "hello world !"}
	 * if the inbetween char was {@code ' '}
	 * 
	 * @param array String array to join up
	 * @param beginIndex index to start from
	 * @param inbetween char to put inbetween the items
	 * @return String of joined up array
	 */
	public static String joinUpArrayFrom(String[] array, int beginIndex, char inbetween) {
		if (!(array.length >= beginIndex)) {
			throw new IndexOutOfBoundsException();
		} else {
			String str = "";
			for (int i = beginIndex; i < array.length; i++) {
				str = str + array[i] + inbetween;
			}
			return str;
		}
	}


}
