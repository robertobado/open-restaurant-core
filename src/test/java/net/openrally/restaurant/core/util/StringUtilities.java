package net.openrally.restaurant.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;

public class StringUtilities {
	public static String httpResponseAsString(HttpResponse response)
			throws IOException {
		
		InputStream stream = response.getEntity().getContent();
		BufferedReader BR = new BufferedReader(new InputStreamReader(stream));
		StringBuilder SB = new StringBuilder();
		String line1 = null;

		while ((line1 = BR.readLine()) != null) {
			SB.append(line1 + "\n");
		}

		BR.close();
		return SB.toString();
	}
}
