package greektext;

import java.text.Normalizer;
import java.util.Arrays;

public class PrincipalParts {
/**
 * The point here is to construct the six principal parts of a given regular ω verb.
 * 
 * The algorithm won't produce every case of every verb that might be called regular, but
 * for the purposes of this program, we'll have define those as "irregular" and "regular" 
 * to be what it does produce.
 * 
 * */
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

//		String str = "παύω";
//		String str = "λύω";
		
//		String str = "θύω";
//		String str = "φαίνω";
//		String str = "χαίρω";
//		String str = "οἰκέω";
//		String str = "ὀρθόω";
//		String str = "φονεύω";
//		String str = "χορεύω";
//		String str = "γράφω";
//		String str = "κλίνω";
//		String str = "βλάπτω";
//		String str = "πρίω";
//		String str = "ῥιπτω"; // see p. 441	
		
		/* From Smyth 387 Principal Parts of Contracted Verbs
		 * 
		 * τιμάω, τιμήσω, ἐτίμησα, τετίμηκα, τετίμημαι, ἐτιμήθην
		 * θηρᾱ́ω, θηρᾱ́σω, ἐθήρᾱσα, τεθήρᾱκα, τεθήρᾱμαι, ἐθηρᾱ́θην [combining chars problem]
		 * ποιέω, ποιήσω, ἐποίησα, πεποίηκα, πεποίημαι, ἐποιήθην
		 * δηλόω, δηλώσω, ἐδήλωσα, δεδήλωκα, δεδήλωμαι, ἐδηλώθην
		 * 
		 * */
		
		String str = "τι\u0304μάω"; // Smyth 387, 388
//		String str = "θηρα\u0304\u0301ω";
//		String str = "ποιέω";
//		String str = "δηλόω";		
		
//		String tmp = string2StringAndCode(Normalizer.normalize(str, Normalizer.Form.NFC)); // 
		
//		System.out.println(tmp);

		String pres1sg = contractPres(str);
		System.out.println(str + " --> " + pres1sg);
		String[] pp = principalParts(str);
		System.out.println("PP:   " + Arrays.toString(pp));
		
		StringBuilder str2 = new StringBuilder();
		for (int i = 0; i < pp.length; i++) {
			str2.append(pp[i]+" ");
		}
		
		System.out.println(Pronunciation.pronounce1(str2.toString()));
		
	}

	/**
	 * Return the six principal parts in an array
	 * 
	 * 	#	Stem		Formation
     * 	1	Present		Given					
	 *	2	Future		Present + σ			
	 *	3	Aorist (σ)	Present + σα		
	 *	4	Perfect		redup. + Present	
	 *	5	Perf. Pass.	Perfect				
	 *	6	Aor. Pass.	Aorist + θ			
	 *
	 */
	static String[] principalParts(String pp1_) {
		String[] result = new String[6];
		String[] stem = new String[6];
		String pp1 = pp1_.toLowerCase();
		int len = pp1.length();
//		int len1 = CharInfo.killAccents(pp1).length();		
		int len1 = CharInfo.killBreath(CharInfo.killAccents(pp1)).length();		

		
		// 1. Present (stem)
		// scrape the omega off pp#1
		result[0] = pp1;
		if (pp1.charAt(len - 1) == 'ω') {
			stem[0] = pp1.substring(0, len - 1);
		} 
		else {
			stem[0] = pp1;
		}
		
		CharInfo inf =  new CharInfo(stem[0]);
		inf.print();
		String stemToo;
		System.out.println(Arrays.toString(inf.macron));
		System.out.println(len);
		for (int i = 0; i < len1 - 1; i++) {
			System.out.println(i + " " + inf.macron[i] + " ");
		}
		System.out.println();

		
		if (inf.macron[len1 - 2] < (short) 2 ) {
			stemToo = lengthenEnd(stem[0]);
		} else stemToo = stem[0]; // accounts for long penultimate syllable
		
		// 2. Future
		stem[1] = ContractVowels.addEnding(stemToo, "σ"); // TODO lengthen contract vowel/transmute consonants
		result[1] = Accent.recessive(stem[1] + "ω"); 
		
		// 3. Aorist
		stem[2] = ContractVowels.addEnding(stemToo, "σα"); // TODO lengthen contract vowel/transmute consonants
//		stem[2] = lengthenEnd(stem[0]) + "σα"; // TODO lengthen contract vowel/transmute consonants
//		result[2] = "ἐ" + stem[2];
		result[2] = Accent.recessive(augment(stem[2]));
		
		// 4. Perfect
		stem[3] = reduplicate(stemToo);
//		result[3] = stem[3] + "κα"; // TODO lengthen contract vowel before ending, p. 283
		result[3] = Accent.recessive(ContractVowels.addEnding(stem[3], "κα"));
		
		// 5. Perfect Middle/Passive
		stem[4] = stem[3];
		result[4] = Accent.recessive(ContractVowels.addEnding(stem[4], "μαι"));

		// 6. Aorist Passive
		stem[5] = ContractVowels.addEnding(stemToo, "θ");
		result[5] = Accent.recessive(augment(stem[5]) + "ην");	
		
		System.out.println("stem: " + Arrays.toString(stem));
		
		return result;
	}

	/**
	 * Given first principal part, contract it to 1st-p. sg. present active indicative
	 * 
	 * @params pp_ 1st PP
	 * @return actual verb in 1st-p. sg. present active indicative
	 */
	static String contractPres(String pp1_) {
		String pp1 = CharInfo.killAccents(pp1_);
		int len = pp1.length();
		String ending = pp1.substring(len-1, len);
		// scrape off the omega from pp#1
		if (!ending.equals("ω")) {
			System.err.println("ERROR! Not 1st PP of omega (ω) verb.");
		}
		String stem = pp1.substring(0, len - 1);
		
		return ContractVowels.addEnding(stem, ending);
	}
	
	
	/**
	 * Given a 6-member array of principal parts of a verb, 
	 * fill in the missing parts assuming they are regular
	 * 
	 * TODO check for reasonableness of submitted P.P.
	 * 
	 * */
	static String[] fillPP(String[] str) {
		if (str.length != 6) {
			System.err.println("Error! Must have 6 P.P. in input array.");
			return null;
		}
		String[] result = str;
		String[] idealPP = principalParts(str[0]);
		for (int i = 0; i < 6; i++) {
			if (!containsLetters(str[i])) {
				result[i] = idealPP[i]; // if the element contains no letters, the PP must be missing
			}
		}
		return result;
	}
	
	/**
	 * Augment verb
	 * 
	 * TODO account for prefixes, 
	 * TODO account for ρει rule (after ρ or ε, α becomes ᾱ, see Hdt 18, fn)
	 * 
	 * 
	 * */
	static String augment(String str) {
		String result;
		int[] vw = CharInfo.detectVowels(str);
		int v = vw[0];			// look at beginning of string
		String aug;
		String oldAug;
		String base;
		String breath;
		int len = str.length();
//		System.out.println("INPUT: " + str+ " " + len);
		
		// Is it a vowel or not?
		if (v > 0) { // vowel
			if (v > 0x10) { // diphthong
				oldAug = str.substring(0, 2);
				base = str.substring(2, len);
//				System.out.println("oldAug: " + oldAug + " " + oldAug.length());
//				System.out.println("BASE: " + base + " " + base.length());
			} 
			else { // single vowel
				oldAug = str.substring(0, 1);
				base = str.substring(1, len);
//				System.out.println("oldAug: " + oldAug);
//				System.out.println("BASE: " + base);
				}
			
			// look for breathing mark with last char of augment
			String tmp = Normalizer.normalize(oldAug, Normalizer.Form.NFD); 
//			CharInfo.printStringAndCode(tmp);
			breath = tmp.substring(tmp.length() - 1, tmp.length());
//			System.out.println("breath: " + breath+" ");
			if ( !( breath.equalsIgnoreCase("\u0313") || breath.equalsIgnoreCase("\u0314") ) ) {
				breath = "";
			}
			
			aug = lengthenVowel(v) + breath;
		}
		else { // not a vowel
			if (CharInfo.killDiacritics(str.substring(0, 1)).equalsIgnoreCase("ρ")) {
				base = CharInfo.killDiacritics(str);
				aug = "ἐρ";
			}
			else {
				aug = "ε\u0313";
				base = str;
			}
		}
//		System.out.println("aug: " + aug);
		result = Normalizer.normalize(aug + base, Normalizer.Form.NFC);
		
		//		result = aug + breath + base;
		return result;
	}
	
	/**
	 * Add reduplication (for perfect forms)
	 * 
	 * */
	static String reduplicate(String str) {
		int len = str.length();
		String oldAug;
		String base;
		String breath;

		String begLett = str.substring(0, 1);
		String redup;

		int[] vw = CharInfo.detectVowels(str);
		int v = vw[0];			// look at beginning of string
		// Is it a vowel or not?
		if (v > 0) { // vowel
			if (v > 0x10) { // diphthong
				oldAug = str.substring(0, 2);
				base = str.substring(2, len);
//				System.out.println("oldAug: " + oldAug + " " + oldAug.length());
//				System.out.println("BASE: " + base + " " + base.length());
			} 
			else { // single vowel
				oldAug = str.substring(0, 1);
				base = str.substring(1, len);
//				System.out.println("oldAug: " + oldAug);
//				System.out.println("BASE: " + base);
				}
			
			// look for breathing mark with last char of augment
			String tmp = Normalizer.normalize(oldAug, Normalizer.Form.NFD); 
//			CharInfo.printStringAndCode(tmp);
			breath = tmp.substring(tmp.length()-1, tmp.length());
//			System.out.println("breath: " + breath+" ");
			if ( !( breath.equalsIgnoreCase("\u0313") || breath.equalsIgnoreCase("\u0314") ) ) {
				breath = "";
			}
			
			redup = lengthenVowel(v) + breath;
		}
		else { // not a vowel		
		
			// if it's a single consonant
			base = CharInfo.killBreath(str);
			System.out.println(begLett);
			switch (CharInfo.killDiacritics(begLett)) {
				case "φ": redup = "πε"; break;
				case "θ": redup = "τε"; break;
				case "χ": redup = "κε"; break;
				case "ρ": redup = "ἐρ"; break;
				default: redup = begLett + "ε";
			}
		}
		return redup + base;
	}
	
	
	/**
	 *  Lengthen contract vowel (for future and sigmatic aorist tenses)
	 * 
	 */
	static String lengthenEnd(String str) {
		int len = str.length();
		String base;
		String oldVowel;
		String newVowel;
//		System.out.println("INPUT: " + str + " " + str.length());

		base = str.substring(0, len - 1);
		oldVowel = str.substring(len - 1, len);

		
		switch ( CharInfo.killDiacritics(oldVowel) ) {
			case "α": newVowel = "η"; break;
			case "ε": newVowel = "η"; break;
			case "ο": newVowel = "ω"; break;
			default: newVowel = oldVowel;
		}
		
//		System.out.println("base = " + base+ " " + base.length());
//		System.out.println("lengthened = " + newVowel);
		return base + newVowel;
	}
	
	
	/*+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%	
	 * Subroutine
	 *+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+% */	
	
	/**
	 * 
	 * Given the code for a vowel, return the character(s) of the lengthened form.
	 *  cf. Smyth 435
	 * 
	 * Private because it's just a length of common code.
	 * 
	 * @param v
	 * @return
	 */
	private static String lengthenVowel(int v) {
		String aug;
		switch (v) { 
			case 0x01: aug = "η"; break; // "α"
			case 0x02: aug = "η"; break; // "ε"
			case 0x03: aug = "η"; break; // "η"
			case 0x04: aug = "ῑ"; break; // "ι" result should be long ῑ
			case 0x05: aug = "ω"; break; // "ο"	ὀρθόω
			case 0x06: aug = "ῡ"; break; // "υ"	result should be long ῡ; e.g., ὑβρίζω (aor. ὕβρισα), ὑγροπορέω, ὑδνέω, ὑδραίνω (aor. ὕδρηνα), ὑθλέω, ὑλακτέω (aor. ὑλάκτησα)
			case 0x07: aug = "ω"; break; // "ω"
			case 0x08: aug = "ᾳ"; break; // "ᾳ"
			case 0x09: aug = "ῃ"; break; // "ῃ"
			case 0x0a: aug = "ῳ"; break; // "ῳ"
			
			case 0x11: aug = "ῃ"; break; // "αι"
			case 0x12: aug = "ῃ"; break; // "ει"
			case 0x13: aug = "ῳ"; break; // "οι"
			
			case 0x15: aug = "ηυ"; break; // "αυ"
			case 0x16: aug = "ηυ"; break; // "ευ"

			// the remainder (not included in Smyth)
			case 0x14: aug = "υι"; break; // "υι" not in Middle Little
			case 0x17: aug = "ηυ"; break; // "ηυ" not in Middle Little
			case 0x18: aug = "ου"; break; // "ου" e.g., οὔλω, οὐρέω (impf. ἐούρουν, aor. ἐούρησα), οὐρίζω, οὐριόω, οὐτάω (aor. οὔτασα, οὔτησα)

			default: aug = "??"; System.err.println("ERROR! Unexpected augment");
		}
		return aug;
	}

	
	/*+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%
	 * General-purpose routines
	 *+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+% */	  
	
	/**
	 * Does the given string contain letters?
	 * 
	 * @param string
	 * @return boolean
	 * 
	 * */
	static boolean containsLetters(String str) {
		char[] c = str.toCharArray();
		for (char d : c) {
			if (Character.isLetter(d)) return true;
		}
		return false;
	}
	
	/* prints the character and its (unicode) code */
	public static String string2StringAndCode(String str) {
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
		
//		System.out.println(out);
		
		return out;
	}
}
