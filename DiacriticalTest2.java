package greektext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.Normalizer;

/*  Compare to Unicode standard at http://unicode.org/reports/tr15/  
 *  Normalizer docs: http://docs.oracle.com/javase/7/docs/api/java/text/Normalizer.html
 *  
 *  NFD: 1[ἅ: ά̔  ἅ] 2[ἄ: ά̓  ἄ] 3[ἃ: ὰ̔  ἃ] 4[ἂ: ὰ̓  ἂ] 5[ᾁ: ᾁ  ᾁ] 6[ἂ: ὰ̓  ἂ] 7[ἇ: ᾶ̔  ἇ] 8[ᾱ̔ . ἁ̄  ᾱ̔ ]
 */

public class DiacriticalTest2 {

	/**
	 * Want to test how Eclipse displays diacriticals
	 * and how Java normalizes combining diacriticals
	 * @throws IOException 
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
		
		String[] vowels = {"α", "ε", "η", "ι", "ο", "υ", "ω", "ᾳ", "ῃ", "ῳ"};
		char[][] diacrit = { {0x0300, 0x0301, 0x0342}, {0x0304, 0x0306}, {0x0313, 0x0314}, {0x345} };

		String filename = "C:/Users/John/Work/java_workspace/Greek/src/diacriticalTest_output.txt";
		
		StringBuilder txtOut = new StringBuilder();
		
		for (int i = 0; i < vowels.length; i++) {
			for (int j = 0; j < diacrit.length; j++) {
				if (j == 3 && !(i == 0 || i == 2 || i == 6) ) break; // iota-sub on only 3 vowels
				for (int k = 0; k < diacrit[j].length; k++) {
//					for (int j2 = 0; j2 < diacrit.length; j2++) {
					for (int j2 = 0; j2 < j; j2++) {
						if (j2 == j && j2 < diacrit.length - 1) j2++;
						if (j2 == 3 && !(i == 0 || i == 2 || i == 6) ) break; // iota-sub on only 3 vowels
						for (int k2 = 0; k2 < diacrit[j2].length - 1; k2++) {
							String out1 = vowels[i] + diacrit[j][k] + diacrit[j2][k2];
							String out2 = vowels[i] + diacrit[j2][k2] + diacrit[j][k];
							String out1a = Normalizer.normalize(out1, Normalizer.Form.NFC);
							String out2a = Normalizer.normalize(out2, Normalizer.Form.NFC);
							if (out1a.length() == 1) {
								System.out.print(out1a + ": ");
								txtOut.append(out1a + ": ");
							}
							else  if (out2a.length() == 1) {
								System.out.print(out2a + ": ");
								txtOut.append(out2a + ": ");
							}
							else {
								System.out.print("   ");
								txtOut.append("   ");
							}
							
							System.out.print(out1 + "κ " + out2 + "κ | " + out1a + "κ " + out2a + "κ | ");
							System.out.println(diacrit[j][k] + " " + diacrit[j2][k2]+" , " 
									+ diacrit[j2][k2] + " " + diacrit[j][k] + " ");
							
							txtOut.append(out1 + "κ " + out2 + "κ | " + out1a + "κ " + out2a + "κ | ");
							txtOut.append(diacrit[j][k] + " " + diacrit[j2][k2]+" , " 
									+ diacrit[j2][k2] + " " + diacrit[j][k] + " \n");
						}
					}
				}
			}
		}

		System.out.println("----------------------------------------------------------");
		String text = txtOut.toString();		
		System.out.println(text);

		
		/* write string to file */
		  Writer output = null;
		  File file = new File(filename);
		  try {
			output = new BufferedWriter(new FileWriter(file));
			  output.write(text);
			  output.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
