package org.jboss.jbossset.bugclerk;

import java.net.MalformedURLException;
import java.net.URL;

public final class URLUtils {

	private URLUtils() {}
	
	public static URL createURLFromString(String URL) {
		try {
			return new URL(URL);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid URL:" + URL,e);
		}
	}
}
