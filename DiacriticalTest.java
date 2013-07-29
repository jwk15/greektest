package greektext;

import java.text.Normalizer;

/*  Compare to Unicode standard at http://unicode.org/reports/tr15/  
 *  Normalizer docs: http://docs.oracle.com/javase/7/docs/api/java/text/Normalizer.html
 *  
 *  NFD: 1[ἅ: ά̔  ἅ] 2[ἄ: ά̓  ἄ] 3[ἃ: ὰ̔  ἃ] 4[ἂ: ὰ̓  ἂ] 5[ᾁ: ᾁ  ᾁ] 6[ἂ: ὰ̓  ἂ] 7[ἇ: ᾶ̔  ἇ] 8[ᾱ̔ . ἁ̄  ᾱ̔ ]
 */

public class DiacriticalTest {

	/**
	 * Want to test how Eclipse displays combining diacriticals
	 * and how Java normalizes combining diacriticals
	 * 
	 *  
	 */
	public static void main(String[] args) {
		/*  300/301 	= grave/acute accent; 
		 *  304/306		= macron/breve
		 *  313/314 	= soft/rough breathing mark; 
		 *  342 		= circumflex; 
		 *  345			= iota subscript
		 */
		String str = 
				"1[ἅ: α\u0301\u0314  α\u0314\u0301] " +
				"2[ἄ: α\u0301\u0313  α\u0313\u0301] " +
				"3[ἃ: α\u0300\u0314  α\u0314\u0300] " +
				"4[ἂ: α\u0300\u0313  α\u0313\u0300] " +
				"5[ᾁ: α\u0345\u0314  α\u0314\u0345] " +
				"6[ἂ: α\u0300\u0313  α\u0313\u0300] " +
				"7[ἇ: α\u0342\u0314  α\u0314\u0342] " +
				"8[ᾱ̔ . α\u0314\u0304  α\u0304\u0314 ]" +
				"7[ἇ: α\u0303\u0314  α\u0314\u0303 ] ";		
		printcombos(str);
		
//		String str1 = "λύω ῃαυθ τῳλλυμιὶ ῡμῶν ἀποθνῄσκω ὁ ἄνθρωπός ῥᾰψωδός.";
//		printcombos(str1);

		String str1 = "ᾤκησα";
		printcombos(str1);
		
		
	}

	private static void printcombos(String str) {
		System.out.print("Raw string: "); 
			printStringAndCode(str);
		System.out.println();
		
		String str1 = Normalizer.normalize(str, Normalizer.Form.NFC);
		System.out.print("NFC: "); 
			printStringAndCode(str1);
		System.out.println();

		String str2 = Normalizer.normalize(str, Normalizer.Form.NFD);
		System.out.print("NFD: "); 
			printStringAndCode(str2);
		System.out.println();
		
		String str3 = Normalizer.normalize(str, Normalizer.Form.NFC);
		System.out.println("NFC again: ");
			printStringAndCode(str3);
		System.out.println();
	}

	/* prints the character and its (unicode) code */
	public static String printStringAndCode(String str) {
		char[] c = str.toCharArray();
		System.out.println(str);
		
		StringBuilder out1 = new StringBuilder();
		StringBuilder out2 = new StringBuilder();
		
		for (char d : c) {
			String cp = String.format("%x ", (int) d); // hex formatted codepoint of char d
			out2.append(cp);
			
			int len = cp.length() - 1;
			String ch = String.format("%"+len + "s ", d);
			out1.append(ch);
			// insert extra space to make combining diacritics display alone (NON_SPACING_MARK)
			// insert AFTER(?) 
			if (Character.getType(d) == 6) out1.append(" "); 
		}

		out1.append("\n"); out2.append("\n");

		String out = out1.append(out2).toString();
		
		System.out.println(out);
		
		return out;
	}
	
	/* prints the hex codes of the characters in a given string */
	public static void stringToCodePts(String string) {
		char[] cArray = string.toCharArray();
		for (char c : cArray) {
			int d = (int) c;
			// String hexCode = Integer.toHexString(c);
			System.out.printf("%1$#06x ", d);
		}
	}
}
