package greektext;
//import java.text.Normalizer;
import java.util.Arrays;

/**
 * Handle combining letters that result from adding endings
 * 
 * TODO Methods: recessive accentuation
 * 
 * Augment summary, p. 94 GE
 * 
 * */

public class ContractVowels {
		public static final String[] VOWEL_LIST = {"α", "ε", "η", "ι", "ο", "υ", "ω", "ᾳ", "ῃ", "ῳ"};
		public static final char[] CVOWEL_LIST = {'α', 'ε', 'η', 'ι', 'ο', 'υ', 'ω', 'ᾳ', 'ῃ', 'ῳ'};
//		public static final String[] SVOWEL_LIST = {"α", "ε", "η", "ι", "ο", "υ", "ω"};
		public static final String[] DIPHTHONG_LIST = {"αι", "ει", "οι", "υι", "αυ", "ευ", "ηυ", "ου"};
	
		public static final String[] PALATALS = {"κ", "γ", "χ"}; //, "ττ"};
		public static final String[] LABIALS = {"π", "β", "φ"}; //, "πτ"};
		public static final String[] DENTALS = {"τ", "δ", "θ"};
		private static final String[][] SECOND_SYLL_CONSONANTS = {PALATALS, LABIALS, DENTALS};
		
		private static final String[] thetaC = {"χθ", "φθ", "σθ"}; // order: PALATALS, LABIALS, DENTALS
		private static final String[] kappaC = {"χ", "", "κ"};
		private static final String[] muC = {"γμ", "μμ", "σμ"};
		private static final String[] sigmaC = {"ξ", "ψ", "σ"};
		private static final String[] tauC = {"κτ", "πτ", "στ"};
		public static final String[][] resultC = {thetaC, kappaC, muC, sigmaC, tauC};

		
		
	/**
	 * 
	 * add string2 to end of string1, and combine vowels using proper contraction rules
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		String[] strings1 = {"λα"};//, "ε", "ο"};
		String[] strings1 = {"βαίν", "ὁρα", "ποιέ", "δηλό"};
		String[] strings2 = {"ω", "εις", "ει", "ομεν", "ετε", "ουσι(ν)"};
		
//		String[] strings2 = {"α", "ε", "ει", "ι", "η", "ῃ", "ο", "ου", "οι", "ω", "ῳ"};
//		String s1 = "βαίν"; 
//		String s1 = "ὁρα"; 
//		String s1 = "ποιέ"; 
//		String s1 = "δηλό"; 

//		String s2 = "α";
//		String s2 = "ο";

//		printContractVowelCodes();
		
		for (String s1 : strings1) {
			for (String s2 : strings2) {
				String sresult = addEnding(s1, s2);
				System.out.println(s1 + " + " + s2 + " = " +  sresult);
			}
			System.out.println();
		}
				
		System.out.println("resultc: " + resultC.length +" " + resultC[0].length);
		System.out.println("endopt: " + SECOND_SYLL_CONSONANTS.length +" " + SECOND_SYLL_CONSONANTS[0].length);

		System.out.println("resultc = " + Arrays.deepToString(resultC));
		Object[] tmp3 = twoD2oneD(resultC);
		
		System.out.println("resultc = " + Arrays.deepToString( tmp3 ) );		
		
		System.out.println();
//		String[] secondCons = {"θxyz", "κxyz", "μxyz", "σxyz", "τxyz"};
		String[] secondCons = {"θ", "κ", "μ", "σ", "τ"};

//		int[] tmp = which2d("β", endOptions);
//		System.out.println("test of which 2d " + Arrays.toString(tmp));
		
		for (Object s1 : twoD2oneD(SECOND_SYLL_CONSONANTS)) {
			for (Object s2 : secondCons) {
//				String a = "pqr" + s1; 	String b = (String) s2 + "xyz";
				String a = (String) s1; 	String b = (String) s2;
//				System.out.println(a + " + " + b + " = ");		
				String tmp1 = combineConsonants( a, b );
				System.out.println(a + " + " + b + " = " + tmp1);	
				System.out.println();
			}

		}
		
		String[] endings = {"tr", "im", "me", "t", "ime", "tre", "time", "type", "pan", "ben"};
		int tt = whichBeg("treepan", endings);
		System.out.println(Arrays.toString(endings));
		System.out.println(tt + ": " + endings[tt]);

	}

	/*
	 * Add string1 to string2 using contraction, (TODO consonant transformation) rules
	 * return the string that results
	 * 
	 * TODO accentuation!
	 */
	protected static String addEnding(String s1, String s2) {
		String result;
		
		int[] vw1 = CharInfo.detectVowels(s1);
		int[] vw2 = CharInfo.detectVowels(s2);
		int v1 = vw1[vw1.length-1];	// look at end of first string
		int v2 = vw2[0];			// look at beginning of second string

		if (v1 == 0 && v2 == 0) { // both are consonants
			String sresult = combineConsonants(s1, s2);
			if (sresult.length() > 0) {
				int sub1 = s1.length();
				String tmp = s1.substring(sub1 - 2);
				if (tmp.equals("πτ") || tmp.equals("ττ") || tmp.equals("σκ")) sub1 -= 2;
				String a = s1.substring(0, sub1);
				String b = s2.substring(1); 
				result = a + sresult + b;
			}
			else result = s1 + s2;
//			result = "";
		}
		else if (v1 == 0 || v2 == 0) { // either is a consonant, other may be a vowel
			result = CharInfo.killAccents(s1 + s2);
			//TODO put accent on right syllable
		}
		else {
			int sub1 = 1; int sub2 = 1;
			if (v1 >= 0x10) sub1 = 2;
			if (v2 >= 0x10) sub2 = 2;
	
	//		System.out.println("Strings: " + s1 + " + " + s2);
	//		System.out.println("lengths: "+ s1.length()+ ", "+ s2.length());
			String a = s1.substring(0, s1.length() - sub1);
			String b = s2.substring(sub2); 
			String sresult = findContractVowel(s1, s2);
	
	//		System.out.println("  sresult " + sresult);
	//		System.out.println("  sub " + sub1 + " + "+ sub2);
	//		System.out.println("  " + s1 + " + "+ s2);
	//		System.out.println("  " + a + " + "+ b);
	//		System.out.println("  sresult " + sresult);	
	//		System.out.println(a + sresult + b);
			
			result = a + sresult + b;
		}
		return result;
	}

/**
 * Given two vowels, return the contract vowel
 * 
 * @param s1
 * @param s2
 * @return 
 */
	protected static String findContractVowel(String s1, String s2) {
//		String[] secondVowel =  {"α", "ε", "ει", "ι", "η", "ῃ", "ο", "ου", "οι", "ω", "ῳ"};
//		String[] alphaContr = {"α", "α", "ᾳ", "αι", "α", "ᾳ", "ω", "ω", "ῳ", "ω", "ῳ"}; // α
//		String[] epsilonContr ={"η", "ει", "ει", "ει", "η", "ῃ", "ου", "ου", "οι", "ω", "ῳ"}; // ε
//		String[] omicronContr ={"ω", "ου", "οι", "οι", "ω", "οι", "ου", "ου", "οι", "ω", "ῳ"}; // ο
//
//		int[] v2nd = {1, 2, 17, 4, 3, 9, 5, 23, 18, 7, 10}; 
//		int[] valf = {1, 1, 8, 17, 1, 8, 7, 7, 10, 7, 10};
//		int[] veps = {3, 18, 18, 18, 3, 9, 24, 24, 19, 7, 10};
//		int[] vomi = {7, 24, 19, 19, 7, 19, 24, 24, 19, 7, 10}; 

//		System.out.println("int[] v2nd = " + Arrays.toString(v2nd));
		
		String result;
		
		int[] vw1 = CharInfo.detectVowels(s1);
		int[] vw2 = CharInfo.detectVowels(s2);
//		System.out.println("  S1 " + s1);
//		for (int c : vw1) System.out.printf("%1$#04x ", c);
//		System.out.println();
//		System.out.println("  S2 " + s2);
//		for (int c : vw2) System.out.printf("%1$#04x ", c);
//		System.out.println();
		
		int v1 = vw1[vw1.length-1];	// look at end of first string
		int v2 = vw2[0];			// look at beginning of second string
//		System.out.println("  V1 " + v1);
//		System.out.println("  V2 " + v2);

		
		/* NB we're adding in the circumflex here */
		int whichVowel = contractVowels(v1, v2);
		if (whichVowel <= 0) result = " "; 
			else if (whichVowel <= 0x0a) result = VOWEL_LIST[whichVowel - 1] + "\u0342";
				else if (whichVowel >= 0x10) result = DIPHTHONG_LIST[whichVowel - 0x11] + "\u0342"; 
				else result = " ";

//		System.out.println("  Result = " + result+ " " + whichVowel);		
		return result;

	}

	
	/**
	 * @param 
	 * @return the result of the combination (string)
	 * */
	static String combineConsonants(String s1, String s2) {
		String[] secondCons = {"θ", "κ", "μ", "σ", "τ"};
		String result;
		
//		System.out.println("2nd-syll cons = " + Arrays.deepToString(SECOND_SYLL_CONSONANTS));
//		System.out.println("resultC = " + Arrays.deepToString(resultC));
		
		int len1 = s1.length();
		String b = s2.substring(0, 1);
		int wb = CharInfo.which(b, secondCons);
		
		if (len1 > 1) {
			String a = s1.substring(len1 - 2);
			switch (a) {
				case "ττ": result = resultC[wb][0]; break; //palatal
				case "πτ": result = resultC[wb][1]; break; // labial
				case "σκ": {
					if (b.equalsIgnoreCase("σ")) {
						result = "ξ"; 
					}
					else result = "";
					break;
				}
				default: result = "";
			}
		}
		else {
			String a = s1.substring(len1 - 1);
			if (b.equalsIgnoreCase("σ") && a.equalsIgnoreCase("ζ")) {
				return "σ";
			}
			else {
				int[] tmp = which2d(a, SECOND_SYLL_CONSONANTS);
//				System.out.println(tmp[0] + " " + tmp2);
				result = resultC[wb][tmp[0]];
			}

		}
//		System.out.println(result);
		return result; //TODO
	}
	/*+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%
	/* Subroutines
	/*+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+% */

	/**
	 * given codes for two vowels
	 * return code for their contract
	 * 
	 **/
	
	private static int contractVowels(int v1, int v2) {
		int[] v2nd = {1, 2, 18, 4, 3, 9, 5, 24, 19, 7, 10}; 
		int[] valf = {1, 1, 8, 17, 1, 8, 7, 7, 10, 7, 10};
		int[] veps = {3, 18, 18, 18, 3, 9, 24, 24, 19, 7, 10};
		int[] vomi = {7, 24, 19, 19, 7, 19, 24, 24, 19, 7, 10}; 

		if (v1 != 0x01 & v1 != 0x02 & v1 != 0x05) return 0; // if it's not α, ε, or ο return ZERO
		if (v2 >= 32) {v2 -= 16;}
		int w2 = which(v2, v2nd);
//		System.out.println("W2 " + w2);
		if (w2 < 0) return 0; // second vowel has to be in v2nd

		int result = 0;
		switch (v1) {
			case 0x01: {
				result = valf[w2];
				break;
			}
			case 0x02: {
				result = veps[w2];
				break;
			}
			case 0x05: {
				result = vomi[w2];
				break;
			}
		}

		return result;
	}
	
	/* Like it says*/
	protected static void printContractVowelCodes() {
		String[] secondVowel =  {"α", "ε", "ει", "ι", "η", "ῃ", "ο", "ου", "οι", "ω", "ῳ"};
		String[] alphaContr = {"α", "α", "ᾳ", "αι", "α", "ᾳ", "ω", "ω", "ῳ", "ω", "ῳ"}; // α
		String[] epsilonContr ={"η", "ει", "ει", "ει", "η", "ῃ", "ου", "ου", "οι", "ω", "ῳ"}; // ε
		String[] omicronContr ={"ω", "ου", "οι", "οι", "ω", "οι", "ου", "ου", "οι", "ω", "ῳ"}; // ο

		int[] v2nd = CharInfo.identifyVowels(secondVowel);
		int[] valf = CharInfo.identifyVowels(alphaContr);
		int[] veps = CharInfo.identifyVowels(epsilonContr);
		int[] vomi = CharInfo.identifyVowels(omicronContr);
	
		System.out.println("int[] v2nd = " + Arrays.toString(v2nd));
		System.out.println("int[] valf = " + Arrays.toString(valf));
		System.out.println("int[] veps = " + Arrays.toString(veps));
		System.out.println("int[] vomi = " + Arrays.toString(vomi));
		
		System.out.println(CharInfo.identifyVowel("ᾳ"));
		
/*		Output (2012-05-24):
 * 
 * 		int[] v2nd = {1, 2, 18, 4, 3, 9, 5, 24, 19, 7, 10}; 
 * 		int[] valf = {1, 1, 8, 17, 1, 8, 7, 7, 10, 7, 10};
 * 		int[] veps = {3, 18, 18, 18, 3, 9, 24, 24, 19, 7, 10};
 * 		int[] vomi = {7, 24, 19, 19, 7, 19, 24, 24, 19, 7, 10}; 
 * */
		
	}

	/*+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%
	/* General-purpose routines
	/*+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+% */	 

	/** Match beginning
	 * 
	 * array's elements can be of varying length
	 * @param str
	 * @param array (String)
	 * @return matching array element number
	 * 
	 * */
	
	static int whichBeg(String str, String[] array) {
		int size = array.length;
		int len = str.length();
		
		/* find lengths of elements of array, as well as max and min lengths */
		int[] lenA = new int[size];	// to contain lengths of array's elements
		int min =  array[0].length();
		int max = 0;
		for (int i = 0; i < size; i++) {
			lenA[i] = array[i].length();
			if (lenA[i] > max) max = lenA[i];
			if (lenA[i] < min) min = lenA[i];
//			System.out.println(i + ": " + lenA[i]+ "; max: "+max+"; min: "+ min);
		}
		if (max > len) {
			max = len;
		}
		
		/* now step through endings (descending order from max to min) and check for match */
		int result = -1;
		lengthLoop:
		for (int lookLen = max; lookLen > min; lookLen--) {
			String a = str.substring(0, lookLen);
			for (int j = 0; j < size; j++) {
				if(lenA[j] == lookLen && a.equalsIgnoreCase(array[j])) {
						result = j;
						break lengthLoop;
				}
			}
		}
		return result;
	}
	
	/** Match ending
	 * 
	 * array's elements can be of varying length
	 * @param str
	 * @param array (String)
	 * @return matching array element number
	 * 
	 * */
	
	static int whichEnd(String str, String[] array) {
		int size = array.length;
		int len = str.length();
		
		/* find lengths of elements of array, as well as max and min lengths */
		int[] lenA = new int[size];	// to contain lengths of array's elements
		int min =  array[0].length();
		int max = 0;
		for (int i = 0; i < size; i++) {
			lenA[i] = array[i].length();
			if (lenA[i] > max) max = lenA[i];
			if (lenA[i] < min) min = lenA[i];
//			System.out.println(i + ": " + lenA[i]+ "; max: "+max+"; min: "+ min);
		}
		if (max > len) {
			max = len;
		}
		
		/* now step through endings (descending order from max to min) and check for match */
		int result = -1;
		lengthLoop:
		for (int lookLen = max; lookLen > min; lookLen--) {
			String a = str.substring(len - lookLen);
			for (int j = 0; j < size; j++) {
				if(lenA[j] == lookLen && a.equalsIgnoreCase(array[j])) {
						result = j;
						break lengthLoop;
				}
			}
		}
		return result;
	}
	
	/*
	 * Convert 2d array to 1d
	 * 
	 * */
	
	static Object[] twoD2oneD(Object[][] array) {
		int height = array.length;
		int width = array[0].length;
//		System.out.println(height +", "+ width);
		
		Object[] newArray = new Object[height * width];
		
	    for (int y = 0; y < height; y++ ) {
	    	for (int x = 0; x < width; x++) {
//	    		System.out.println(x+", "+ y + " = "+ (y*width + x));
//	    		System.out.println(array[y][x]);
				newArray[y*width + x] = array[y][x];
			}
	    }
	    return newArray;
	}
	
	/*
	 * http://stackoverflow.com/questions/4376915/search-a-2-dimensional-array-in-java
	 * */
	
	static int[] which2d(Object search, Object[][] array) {

	    if (search == null || array == null) return null;

	    for (int rowIndex = 0; rowIndex < array.length; rowIndex++ ) {
	       Object[] row = array[rowIndex];
	       if (row != null) {
	          for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
	             if (search.equals(row[columnIndex])) {
	            	 int[] result = {rowIndex, columnIndex};
	                 return result;
	             }
	          }
	       }
	    }
	    return null; // value not found in array
	 }
	
	// which of the b's does a match?
	// returns 0 if no match, otherwise matched element number +1
	static short which(String a, String[] b) {
		int size = b.length;
		int j = 0;
		for (String c : b) {
			j++; if (a.equalsIgnoreCase(c)) break;
		}
		if (j == size) j = 0;
		return (short) j;
	}
	static int which(int a, int[] b) {
		int size = b.length;
		int match = -1;
		int j = 0;
		for (; j < size; j++) if (a == b[j]) {match = j; break;}	
		return match;
	}

//	static short which(int a, int[] b) {
//		int size = b.length;
//		int j = 0;
//		for (; j < size; j++) if (a == b[j]) break;
//		return (short) ((j < size)?j+1:0);
//	}

	static short which(short a, short[] b) {
		int size = b.length;
		short match = -1;
		short j = 0;
		for (; j < size; j++) if (a == b[j]) {match = j; break;}	
		return match;
	}
}
