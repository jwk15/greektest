package greektext;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CharInfo {
	public static final String[] VOWEL_LIST = {"α", "ε", "η", "ι", "ο", "υ", "ω", "ᾳ", "ῃ", "ῳ"};
	public static final String[] DIPHTHONG_LIST = {"αι", "ει", "οι", "υι", "αυ", "ευ", "ηυ", "ου"};
	public static final String[] ALPHABET = {"α", "β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", 
		"κ", "λ", "μ", "ν", "ξ", "ο", "π", "ρ", "σ", "τ", "υ", "φ", "χ", "ψ", "ω"};
	public static final String[] CONSONANT_LIST = {"β", "γ", "δ", "ζ", "θ", "κ", "λ", "μ", "ν", "ξ", "π", 
		"ρ", "σ", "τ", "φ", "χ", "ψ"};
	public static final String[] SINGLE_CONSONANT_LIST = {"β", "γ", "δ", "θ", "κ", "λ", "μ", "ν", "π", 
		"ρ", "σ", "τ", "φ", "χ"};
	
	/* 
	 * These are the output variables. CharInfo is basically 
	 * a struct consisting of the variables right below. Each is an array,
	 * and each element of each array describes the corresponding character
	 * of the input string.
	 * 
	 * These variables have default access set, so are only accessible to 
	 * classes within the package.
	 */
	String orig;
	String naked;	// stripped of diacriticals
	short[] breath; // soft 0x313, rough 0x314
	short[] accent; // acute 0x301, circumflex 0x342, grave 0x300
	short[] iota;	// 0x345
	short[] macron;	// macron 0x304, breve 0x306
	short[] letter;
	short[] vowel;
	short[] upcase;

	
	/**
	 * <P>The constructor. It also fills out the arrays with the info. on the string.
	 * 
	 *
	 * <P>The idea here is to convert a polytonic Greek string (UTF-8 encoding presumed) 
	 * into a struct that consists of a string without diacritic marks, along with the 
	 * following informational arrays (each array element is an attribute of a single letter).
	 * 
	 * <P>Returns these arrays (all have size of original string's length): 
	 * <OL TYPE="a">
	 * <LI> stripped characters of unaccented string (char), 
	 * <LI> breathing marks (short: 0-2),
	 * 		<BR>0 = none
	 * 		<BR>1 = soft
	 * 		<BR>2 = rough
	 * <LI> accent (short: 0-3)
	 * 		<BR>0 = none
	 * 		<BR>1 = grave
	 * 		<BR>2 = acute
	 * 		<BR>3 = circumflex
	 * <BR>[NB: originally had 1=acute; 2=circumflex; 3=grave--should be updated everywhere]
	 * <LI> iota subscript (short, for consistency)
	 * 		0 = none; 1 = iota-sub
	 * <LI> macron (short: 0-2)
	 * 		<BR>0 = none
	 * 		<BR>1 = breve
	 * 		<BR>2 = macron
	 * <LI> letter (short): ordinal of position in Greek alphabet (1 - 24)
	 * <LI> vowel (short): which vowel/diphthong (see detectVowels method for more info.)
	 * <LI> upcase (short): 0 = lowercase, 1 = uppercase
	 * </OL>
	 * 
	 * @param String string (UTF-8)
	 * @returns 8 arrays of strings and shorts, as described above
	 * 
	 */
	CharInfo(String string){
		int len = string.replaceAll(
				"\\p{InCombiningDiacriticalMarks}+", "").length();
		
		orig = Normalizer.normalize(string, Normalizer.Form.NFC);
		breath = new short[len];
		accent = new short[len];
		iota = new short[len];
		macron = new short[len];
		letter = new short[len];
		vowel = new short[len];
		upcase = new short[len];
		
		// decompose the string (separate diacritical marks)
		String decompString = Normalizer.normalize(string, Normalizer.Form.NFD);
		naked = decompString.replaceAll(
				"\\p{InCombiningDiacriticalMarks}+", "");
		
//		String withIotas = withIotas(string);
		
//		System.out.println("== decomp");
//		printStringAndCode(decompString);
//		System.out.println("== decomp lower");
//		printStringAndCode(decompString.toLowerCase());

		
		char[] cArray = string.toCharArray();
		char[] cArrayDecomp = decompString.toCharArray();
		char[] plainletters = (naked.toLowerCase()).toCharArray();
		char[] naked = this.naked.toCharArray();
		
	/*
	 * Now we're going to step through the decomposed string to record the diacritical 
	 * marks in the appropriately named arrays.
	 */
		int i = 0; // index of character being examined in original string
		int j = 0; // index of character being examined in decomposed string 
		letter[0] = (short) (Character.isLetter(cArrayDecomp[0])?1:0);
		if (letter[0]==1) {j = 1;}
		for (; j < cArrayDecomp.length; j++) {
					//	System.out.print("i = "+ i + " "+ cArray[i]); System.out.println(" j = " + j);
			char c = cArrayDecomp[j];
					//	System.out.print("c = "+ c + "  ");
//			letter[i] = (short) (Character.isLetter(cArray[i])?1:0);
			if (Character.isLetter(cArray[i])) {
				letter[i] = (short) (plainletters[i] - 0x03B0);
				if (letter[i] > 18) {letter[i] -= 1;} // this corrects for the "GREEK SMALL LETTER FINAL SIGMA" stuck in before "real" sigma
				upcase[i] = (short) (Character.isUpperCase(naked[i])?1:0);
			}
			
			if (c >= 0x0300 && c <= 0x036f) {
						//	System.out.print("c = "+ c + "  ");
				switch (c) {
					case 0x313: // soft breathing
						breath[i] = 1; //System.out.println("soft!"); 
						break;
					case 0x314: // rough breathing
						breath[i] = 2; //System.out.println("rough!"); 
						break;
					case 0x301: // acute accent
						accent[i] = 2; //System.out.println("acute!"); 
						break;
					case 0x342: // circumflex accent
						accent[i] = 3; //System.out.println("circumflex!"); 
						break;
					case 0x300: // grave accent
						accent[i] = 1; //System.out.println("grave!"); 
						break;
					case 0x345:  // iota subscript
						iota[i] = 1; //System.out.println("iota-sub!"); 
						break;
					case 0x304: // macron
						macron[i] = 2; //System.out.println("macron!"); 
						break;
					case 0x306: //breve
						macron[i] = 1; //System.out.println("breve!");
				}
			}
			else { 
				i++; // if it's not a combining diacritic, move on to next character!
				//System.out.println();
			} 
		}
		// No explicit return value, but the output is the ("struct") arrays that describe the input string.
	}

/*********************************
   MAIN - test of the constructor 
  **********************************/
	  public static void main(String[] arg) {
//			String string = "ῡ̔μῶν ἀποθνῄσκω· ἄνθρωπός ῥᾰψωδός."; //ῠ TODO: the first character doesn't work
//			String string = "ῃαυθ αἰθήρ τῳλλυμιὶ ῡμῶν ἀποθνῄσκω ἄνθρωπός ῥᾰψωδός."; //ῡ̔  
			String string = "Ἐπειδὴ τὸ εἰδέναι καὶ τὸ ἐπίστασθαι συμβαίνει περὶ πάσας";
			//			String string = "αἰθήρ οἶνος Εἰλείθυια αὐτόνομος Ὀδυσσεύς ηὕρηκα Οὐρανός";
			
			
			System.out.println("----");
			printStringAndCode(string);
			System.out.println("-----");
			printStringAndCode(Normalizer.normalize(string, Normalizer.Form.NFD));
			
			CharInfo infoGk = new CharInfo(string);
//			infoGk.print(5);
			System.out.println(infoGk);
			
			String tmp = infoGk.withIotas();
			printStringAndCode(tmp);
			
			System.out.println(tmp);

//			char[] cArray = string.toCharArray();
//			char[] cArray2 = infoGk.unaccented.toCharArray();
//			System.out.println(string);
//			System.out.println(Arrays.toString(cArray)+" orig. chars");
//			System.out.println(Arrays.toString(cArray2)+" unaccented chars");
//			System.out.println(Arrays.toString(infoGk.breath)+" breath");
//			System.out.println(Arrays.toString(infoGk.accent)+ " accent");
//			System.out.println(Arrays.toString(infoGk.iota)+ " iota");
//			System.out.println(Arrays.toString(infoGk.macron)+ " macron");
//			System.out.println(Arrays.toString(infoGk.letter)+ " letter");
			System.out.println("======");
			
			int[] vowels = detectVowels(string);
			
			System.out.print("orig");
			for (char c : string.toCharArray()) System.out.printf("%5s", c);
			System.out.println();
			System.out.print("vowl ");
			for (int c : vowels) System.out.printf("%1$#04x ", c);
			System.out.println();
			
			String tmp2 = infoGk.orig;
			String wiotas = withIotas(tmp2);
			System.out.println(wiotas);
			
			char[] cwiotas = wiotas.toCharArray();
			WordInfo.printArray(cwiotas, 4);
			
			System.out.println("=================================");
			
			infoGk.print();
	  }

/* +%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+% */	
/* Below are the separated information methods */
/* +%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+% */	  

		/**
		 * findLetters - converts the characters in a Greek string to 
		 * that letter's position in the alphabet
		 * 
		 * <P>Note: SIGMA and GREEK SMALL LETTER FINAL SIGMA map to same number
		 * 
		 * @param str
		 * @return letter[]
		 * 
		 * <P>TODO converting to uppercase should automatically take care of sigma mapping issue
		 * 
		 */
		static short[] findLetters(String str) {
//			int len = str.length(); // unfortunately this counts combining diacritics that fail to combine
//			short[] letter = new short[len]; 
			
			String unaccented = killDiacritics(str);
			char[] naked = unaccented.toLowerCase().toCharArray();
			int len = unaccented.length();
			short[] letter = new short[len];
			System.err.println("len: "+len);

			for (int i = 0; i < len; i++) {
				int c = naked[i];
				if (Character.isLetter(c)) {
					short tmp = (short) (c - 0x03B0);
					if (tmp > 18) {tmp -= 1;} // this corrects for the "GREEK SMALL LETTER FINAL SIGMA" stuck in before "real" sigma
					letter[i] = tmp;
				}
			}
			return letter;
		}
	  
	  /**
	   * Is the given char a vowel?
	   * 
	   * @param b char
	   * @return boolean (true = b is a vowel; false = it's not)
	   * 
	   */
		static boolean isVowel(char b) {
			String d = killDiacritics(Character.toString(b)); //Normalizer.normalize(Character.toString(b), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
			char c = Character.toLowerCase(d.charAt(0));
			
			if(c=='α' || c=='ε' || c=='η' || c=='ι' || c=='ο' || c=='υ' || c=='ω') return true;
			else return false;
		}
		static boolean isVowel(String b) {
			String d = killDiacritics(b); //Normalizer.normalize(b, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
			char c = Character.toLowerCase(d.charAt(0));
			
			if(c=='α' || c=='ε' || c=='η' || c=='ι' || c=='ο' || c=='υ' || c=='ω') return true;
			else return false;
		}

		/**
		 * @param CharInfo object 
		 * <P> Uses info from naked string & iota array
		 * 
		 * @return string of the object with no accents or breathing makrs, but only subscript iotas
		 * 
		 * */
			  public String withIotas() {
//					int len = unaccented.length();
//					int number = 0;
					String iotaS = Character.toString((char) 0x345);
					StringBuilder result = new StringBuilder(naked);
					for (int i = iota.length-1; i >= 0 ; i--) {
					 if (iota[i] == 1) result.insert(i+1, iotaS);
					}
		
					String res = Normalizer.normalize(result.toString(), Normalizer.Form.NFC);
					 
					return res;
			  }
			/**
			 * 
			 * TODO check this works right
			 * 
			 * @param Given accented Greek string
			 * @return the string stripped of all diacriticals except the iotas 
			 * 
			 * */
			  public static String withIotas(String str) {
//					printStringAndCode(str);
					String decompString = Normalizer.normalize(str, Normalizer.Form.NFD)
							.replaceAll("\u0345", "\u0002"); //expand diacritics, then replace all iota-sub with CNTRL-b
//					printStringAndCode(decompString);
					String unaccented = decompString.replaceAll(
							"\\p{InCombiningDiacriticalMarks}+", "").replaceAll("\u0002", "\u0345"); //remove remaining diacritics, then replace all CNTRL-b with iota-sub
//					printStringAndCode(unaccented);

					String res = Normalizer.normalize(unaccented, Normalizer.Form.NFC) ; //re-combine diacritics
//					printStringAndCode(res);
				  
					return res;
			  }		  
	  
	/**
	 *  Given Greek string, find the separate vowels/diphthongs and return their codes in an array
	 *  
	 *  @return array (length that of input string) with numbered vowels/diphthongs:
	 *  	<P><B>simple vowels</B>
	 *  			<BR>{'α', 'ε', 'η', 'ι', 'ο', 'υ', 'ω', "ᾳ", "ῃ", "ῳ"}
	 *		  		<BR>{ x01, x02, x03, x04, x05, x06, x07, x08, x09, x0a}
	 *		<P><B>diphthongs</B>
	 *				<BR>{"αι", "ει", "οι", "υι", "αυ", "ευ", "ηυ", "ου"}
	 *				<BR>{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}
	 *				<BR>to the vowel-value of the first letter of diphthong is added 0x10,
	 *				<BR>to that of the second letter is added 0x20
	 *
	 *<P> E.g. εἰδέναι --> 0x12 0x22 0x00 0x02 0x00 0x11 0x21
	 *  
	 * @param string
	 * @return whichVowel
	 */
		protected static int[] detectVowels(String string) {
			
			char[] cArray = withIotas(string.toLowerCase()).toCharArray();
			int len = cArray.length;
			int[] whichVowel = new int[len];
			
//			System.out.print("vowl ");
//			for (int c : whichVowel) System.out.printf("%1$#04x ", c);
//			System.out.println();

//			System.out.println(Arrays.toString(cArray)+" orig. chars");

			// first pass: which are vowels, and which vowels?
			int j = 0;
			for (char c : cArray) {
				whichVowel[j] = which(Character.toString(c), VOWEL_LIST) + 1;
				j++;
			}
//			int w = 5;
//			System.out.println(Arrays.toString(whichVowel)+ " whichVowel");	
//			System.out.print("ordn ");		
//			for (int i = 0; i < len; i++) System.out.printf("%4d ", i);
//			System.out.println();
//			System.out.print("orig");
//			for (char c : string.toCharArray()) System.out.printf("%"+w+"s", c);
//			System.out.println();
//			System.out.print("unac");
//			for (char c : cArray) System.out.printf("%"+w+"s", c);
//			System.out.println();
//			System.out.print("vowl ");
//			for (int c : whichVowel) System.out.printf("%1$#04x ", c);
//			System.out.println();
			
			//second pass: which are diphthongs, and which diphthongs?
			// output: second hex digit is paired to diphthong, first digit is ordinal for 1st or 2nd vowel of dip.
			j = 0;
			int[] dip = new int[len];
			int[] iFirsts = {0x01, 0x02, 0x05, 0x06}; // {"α", "ε", "ο", "υ"}
			int[] uFirsts = {0x01, 0x02, 0x03, 0x05}; // {"α", "ε", "η", "ο"}
			for (int i=0; i < len - 1; i++) {
//				if (whichVowel[i] > 0) {
					if (whichVowel[i + 1] == 0x04) { // which pairs of letters end in i?
//						println("i-dip detected " + whichVowel[i]+" at "+i);
						int tmp = which(whichVowel[i], iFirsts) + 1; // {"α", "ε", "ο", "υ"}
//						System.out.println(tmp);
						if (tmp > 0) {dip[i] = 0x10 + tmp; dip[i + 1] = 0x20 + tmp;
							whichVowel[i] = 0x10 + tmp; whichVowel[++i] = 0x20 + tmp;
						}
					} 
					else if (whichVowel[i + 1] == 0x06) { // which pairs of letters end in u?
//						println("u-dip detected " + whichVowel[i]+" at "+i);
						int tmp = which(whichVowel[i], uFirsts) + 1; // {"α", "ε", "η", "ο"}
//						System.out.println(tmp);
						if (tmp > 0) {dip[i] = 0x10 + 0x04 + tmp; dip[i + 1] = 0x20 + 0x04 + tmp;
							whichVowel[i] = 0x10 + 0x04 + tmp; whichVowel[++i] = 0x20 + 0x04 + tmp;
						} //0x04 is offset for u diphthongs
					}
				}
				return whichVowel;
		}
		/**
		 * meant to ID a single vowel or single diphthong
		 * 
		 * @param 1-2 character string (vowel/diphthong)
		 * @return the code for that vowel(/diphthong)
		 */

		protected static int identifyVowel(String string) {
			char[] cArray = CharInfo.withIotas(string).toCharArray();
			int len = string.length();
			
			if (len > 2) {
				System.err.println("ERROR! not a single vowel or diphthong"); 
				Thread.currentThread().getStackTrace();
				return 0;
			}
			
			// first pass: which are vowels, and which vowels?
			int[] whichVowel = new int[len];
			int j = 0;
			for (char c : cArray) {
				whichVowel[j] = which(Character.toString(c), VOWEL_LIST) + 1;
				j++;
			}
			if (len == 1) {return whichVowel[0];} // if it's a single vowel, job done--get outta here!
			// if it's a two-letter string, but not a diphthong, return the code of the second letter
			if (whichVowel[1] != 0x04 && whichVowel[1] != 0x06) {
				System.err.println("GLITCH! 2-letter, but not a diphthong");
				Thread.currentThread().getStackTrace();
				return whichVowel[1];
				}
			
			//second pass: which are diphthongs, and which diphthongs?
			// output: second hex digit is paired to diphthong, first digit is ordinal for 1st or 2nd vowel of dip.
			j = 0;
			int dip = 0; // by this point we know len = 2
			int[] iFirsts = {0x01, 0x02, 0x05, 0x06}; // {"α", "ε", "ο", "υ"}
			int[] uFirsts = {0x01, 0x02, 0x03, 0x05}; // {"α", "ε", "η", "ο"}
			switch (whichVowel[1]) {
				case 0x04: { // ends in i
					int tmp = which(whichVowel[0], iFirsts) + 1; // {"α", "ε", "ο", "υ"}
					if (tmp > 0) {dip = 0x10 + tmp;}
				}
					break;
				case 0x06: { // ends in u
					int tmp = which(whichVowel[0], uFirsts) + 1; // {"α", "ε", "η", "ο"}
					if (tmp > 0) {dip = 0x10 + 0x04 + tmp;} //0x04 is offset for u diphthongs
					break;
				}
			}
			return dip;
		}
		/**
		 * Vector wrapper for {@link CharInfo#identifyVowel(String string) identifyVowel(String string)}
		 * 
		 * @param string array of individual vowels (include dipthongs)
		 * @return int array of the code numbers of the vowels
		 */
		protected static int[] identifyVowels(String[] string) {
			int len = string.length;
			int[] result = new int[len];
			for (int i = 0; i < len; i++) {
				result[i] = identifyVowel(string[i]);
			} 
			return result;
		}


/* +%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+% */	
/* General-purpose routines */
/* +%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+% */	  

	/**
	 * removes ALL diacritical marks from given string 
	 *  
	 * @param string
	 * @return original string, but stripped of diacriticals
	 */
		public static String killDiacritics(String string) {
		    return Normalizer.normalize(string, Normalizer.Form.NFD)
		        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		}
		
		/**
		 * Removes accents, but not breathing marks, macrons/breves, or iotas
		 * 
		 * @param str
		 * @return
		 */
		public static String killAccents(String str) {
			String decompString = Normalizer.normalize(str, Normalizer.Form.NFD); //expand diacritics 
			//replace all accents with "": acute 0x301, circumflex 0x342, grave 0x300
			decompString = decompString.replaceAll("\u0301", "").replaceAll("\u0342", "").replaceAll("\u0300", ""); 
			String result = Normalizer.normalize(decompString, Normalizer.Form.NFC) ; //re-combine diacritics
			return result;
		}

		/**
		 * Removes breathing marks, but not accents, macrons/breves, or iotas
		 * 
		 * @param string
		 * @return original string stripped of breathing marks
		 */
		public static String killBreath(String string) {
			String decompString = Normalizer.normalize(string, Normalizer.Form.NFD); //expand diacritics 
			//replace all breathing marks with "": 313/314 	= soft/rough breathing mark
			decompString = decompString.replaceAll("\u0313", "").replaceAll("\u0314", ""); 
			String result = Normalizer.normalize(decompString, Normalizer.Form.NFC) ; //re-combine diacritics
			return result;
		}

		
	/** Prints the character and its (unicode) code 
	 * 
	 * @return characters of original string, each with respective codepoint beneath  
	 * 
	 */
		public static void printStringAndCode(String str) {
//			int len = str.length();
			char[] c = str.toCharArray();
			System.out.println(str);
			for (char d : c) {
				System.out.print("    " + d + " ");
				if (Character.getType(d) == 6) System.out.print(" "); //extra space to make combining diacritics display rightly (NON_SPACING_MARK)
			}
			System.out.println();
//			for (char d : c) {
//				int type = Character.getType(d);
//				System.out.printf("%1$#05x ", type);
//			}
//			System.out.println();
			stringToCodePts(str);
			System.out.println();
		}
		
		/**
		 *  prints the codepoints in hex of the characters in a given string 
		 */
		public static void stringToCodePts(String string) {
			char[] cArray = string.toCharArray();
			for (char c : cArray) {
				int d = (int) c;
				// String hexCode = Integer.toHexString(c);
				System.out.printf("%1$#05x ", d);
			}
		}

		/**
		 * returns in a sring a prettified presentation of the CharInfo arrays (struct)
		 * 
		 */
		@Override
		public String toString() {
			final int width = 3;
			String ft = "%"+width+"d";
			String sft = "%"+width+"s";
			
//			if (arg[0] == null) w = 5;
//			else w = Integer.parseInt(arg[0]);
			
		    StringBuilder result = new StringBuilder();

//			char[] corig = orig.toCharArray();
//		    result.append("orig");
//		    for (char d : corig) {
//				result.append("    " + d + " ");
//				if (Character.getType(d) == 6) result.append(" "); //extra space to make combining diacritics display rightly (NON_SPACING_MARK)
//				System.out.println("here!");
//		    }
			result.append("\norig"); // TODO: doesn't work with comb. diac. left in original string
			for (char c : orig.toCharArray()) result.append(String.format(sft, c));
		    
			// unaccented ("stripped") string
			result.append("\nnake");
			for (char c : naked.toCharArray()) result.append(String.format(sft, c));


			// letter
			result.append("\nlett");
			for (short c : letter) result.append(String.format(ft, c));
			
			// iota
			result.append("\niota");
			for (short c : iota) result.append(String.format(ft, c));

			// vowel number
			result.append("\nvowl ");
			for (int c : vowel) result.append(String.format("%0" + (width-1) + "x ", c));

			// breath
			result.append("\nbrea");
			for (short c : breath) result.append(String.format(ft, c));
			
			// accent
			result.append("\nacce");
			for (short c : accent) result.append(String.format(ft, c));

			// macron
			result.append("\nmacr");
			for (short c : macron) result.append(String.format(ft, c));

			result.append("\nuppr");
			for (short c : upcase) result.append(String.format(ft, c));
//			result.append("\n");
		    
		    return result.toString();
		}
		
		// default width = 2
		public void print() {print(3);}		

		 /* print the fields of the object/structure */
		public void print(int width) {

			//w is width/no. chars for each element
			// TODO: if < 2, error!
			if (width < 3) {
				System.err.println("Error! Width < 3 not allowed.");
				Thread.currentThread().getStackTrace();
			}
			
			// original string - TODO: doesn't work with comb. diac. in original string
			System.out.print("orig");
			for (char c : orig.toCharArray()) {
				System.out.printf("%"+width+"s", c);
				if (Character.getType(c) == 6) System.out.print(" "); //extra space to make combining diacritics display rightly (NON_SPACING_MARK)
			}
			System.out.println();

			// unaccented ("stripped") string
			System.out.print("nake");
			for (char c : naked.toCharArray()) System.out.printf("%"+width+"s", c);
			System.out.println();

			String ft = "%"+width+"d";
			// breath
			System.out.print("brea");
			for (short c : breath) System.out.printf(ft, c);
			System.out.println();
			
			// accent
			System.out.print("acce");
			for (short c : accent) System.out.printf(ft, c);
			System.out.println();
			// iota
			System.out.print("iota");
			for (short c : iota) System.out.printf(ft, c);
			System.out.println();
			// macron
			System.out.print("macr");
			for (short c : macron) System.out.printf(ft, c);
			System.out.println();
			// letter
			System.out.print("lett");
			for (short c : letter) System.out.printf(ft, c);
			System.out.println();

			System.out.print("uppr");
			for (short c : upcase) System.out.printf(ft, c);
			System.out.println();
			
//			System.out.println();
			
			
		}

		  
		  
/**
 * Where is a command in IDL, and I've modelled the implementations here on it. Which is where but only returns the first match
 * @see http://idlastro.gsfc.nasa.gov/idl_html_help/WHERE.html
 * 
 * TODO A more efficient way to get this function in Java?
 * 
 * 
 *  
 *  
 *  @param a, single string or integer
 *  @param b, array: string or integer
 *  @return element no. of first element in array that matches a; or -1 if no match
 * 
 **/
		  
			// which of the b's does a match?
			// returns 0 if no match, otherwise matched element number
//			static int which(String a, String[] b) {
//				int size = b.length;
////				System.out.println("==> "+a);
//				int match = -1;
//				int j = 0;
//				for (; j < size; j++) {
//					if (a.equalsIgnoreCase(b[j])) {match = j; break;}
//				}
////				System.out.println("match: "+match);
//				return match;
//			}
			static <T extends Number> int which(T a, Collection<T> b) {
//				int size = b.size();
				int match = -1;
				int j = 0;
				for (Iterator itr = b.iterator(); itr.hasNext();) {
					T t = (T) itr.next();
					 if (a == t) {
						 match = j; 
						 break;	
					 }
					 j++;
				}
				return match;
			}
			static <T extends CharSequence> int which(T a, T[] b) {
				int size = b.length;
//				System.out.println("==> "+a);
				int match = -1;
				int j = 0;
				for (; j < size; j++) {
					if (a.equals(b[j])) {match = j; break;}
				}
//				System.out.println("match: "+match);
				return match;
			}
			static int which(int a, int[] b) {
				int size = b.length;
				int match = -1;
				int j = 0;
				for (; j < size; j++) if (a == b[j]) {match = j; break;}	
				return match;
			}
			static int[] whereGT(int a, int[] b) {
//				int match = -1;
				ArrayList<Integer> matches = new ArrayList<Integer>();
				int j = 0;
				for (; j < b.length; j++) 
					if (a > b[j]) 
						matches.add(j);
				if (matches.size() == 0) matches.add(-1);
				return convertIntegers(matches);
			}
			static int[] whereEQ(int a, int[] b) {
//				int match = -1;
				ArrayList<Integer> matches = new ArrayList<Integer>();
				int j = 0;
				for (; j < b.length; j++) 
					if (a == b[j]) 
						matches.add(j);
				if (matches.size() == 0) matches.add(-1);
				return convertIntegers(matches);
			}
			
			/** 
			 * Converts an array of Integers to an array of ints
			 * 
			 * @param integers, Integer list
			 * @return int array
			 * @see http://stackoverflow.com/questions/718554/how-to-convert-an-arraylist-containing-integers-to-primitive-int-array 
			 */
			public static int[] convertIntegers(List<Integer> integers) {
			    int[] ret = new int[integers.size()];
			    Iterator<Integer> iterator = integers.iterator();
			    for (int i = 0; i < ret.length; i++)
			    {
			        ret[i] = iterator.next().intValue();
			    }
			    return ret;
			}

}

