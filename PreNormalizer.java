package greektext;

import java.text.Normalizer;

/**
 * Overcome Java's limited implementation of Unicode Normalization, at least for 
 * diacriticals in polytonic Greek.
 * 
 * @author John W. Keck
 */

public class PreNormalizer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	// String decompString = Normalizer.normalize(string, Normalizer.Form.NFD);
	
	/*  300/301 	= grave/acute accent; 
	 *  304/306		= macron/breve
	 *  313/314 	= soft/rough breathing mark; 
	 *  342 		= circumflex; 
	 *  345			= iota subscript
	 */

	public static String normalize(String string) {
		
		int len = string.length();
		String decomp = Normalizer.normalize(string, Normalizer.Form.NFD);
		char[] cd = decomp.toCharArray();
		for (int i = 0; i < cd.length - 1; i++) {
			if (cd[i] == 0x301 || cd[i] == 0x302 || cd[i+1] == 0x342) {
				if (cd[i + 1] == 0x313 || cd[i + 1] == 0x314) {
					// switch positions
					char tmp = cd[i];
					cd[i] = cd[i + 1];
					cd[i + 1] = tmp;
				}
			}
		}
		String res = cd.toString();
		return res;
	}

}
