package net.openrally.restaurant.core.util;

import java.util.Random;

public class RandomGenerator {
	private static Random random = new Random();

	private static String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private static int maxStringLength = 100;

	public static String generateString() {

		return generateString(randomPositiveInt(maxStringLength));
	}

	public static String generateString(int length) {
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(random.nextInt(characters.length()));
		}
		return new String(text);
	}

	public static int randomInt() {
		return random.nextInt();
	}
	
	public static int randomInt(int length) {
		return random.nextInt(length);
	}

	public static int randomPositiveInt() {
		int randomLength = randomInt();

		if (randomLength < 0) {
			randomLength *= -1;
		}
		
		return randomLength;
	}
	
	public static int randomPositiveInt(int length) {
		int randomLength = randomInt(length);

		if (randomLength < 0) {
			randomLength *= -1;
		}
		
		return randomLength;
	}
}
