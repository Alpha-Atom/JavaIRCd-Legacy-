package net.alphaatom.dongerircd.exceptions;

/**
 * Thrown if the config is malformed
 * 
 * @author Matt
 *
 */
public class MalformedConfigException extends RuntimeException {
	
	private static final long serialVersionUID = 6421432745388914044L;

	public MalformedConfigException(String reason) {
		super(reason);
	}

}
