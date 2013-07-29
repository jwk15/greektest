package greektext;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* 
 * Information about words in given string, e.g., syllable divisions 
 * 
 * TODO Transliterator for pronunciation
 * TODO check accentuation for correctness
 * TODO add-recessive-accentuation method
 * 
 * */

public class WordInfo {
	public static final String[] DIPHTHONG_LIST = {"αι", "ει", "οι", "υι", "αυ", "ευ", "ηυ", "ου"};
/* These are consonants combinations that can begin a word or syllable */
	public static final String[] CONSONANT2_LIST = {"βδ", "βλ", "βρ", "γλ", "γν", "γρ", "θλ", "θν", "θρ", "κλ", "κρ", "κτ", "μν", 
		"πλ", "πν", "πρ", "πτ", "ρμ", "σθ", "σκ", "σμ", "σπ", "στ", "σφ", "σχ", "τρ", "φθ", "φλ", "φρ", "χθ", "χλ", "χξ", "χρ"};
	public static final String[] CONSONANT3_LIST = {"στρ"};

	String orig;
	String dashed;		// original with dashes dividing syllables
	int[] syllable; 	// syllable no. for each letter in original string (size: orig.length)

	int totSyll;		// no. syllables in whole string
	int[] vowelp;		// positions of vowels (for diphthongs, second vowel)
	int[] voweln;		// vowel no. for each syllable
	int[] sylBeg;		// syllable divisions in each word (positions of hyphens)
	int[] accent;		// accentuation for each syll.

	int nWords;			// number of words in given string
	String[] words;		// words in string;
	int[] wordBegs;		// array of the positions of beginnings of words in the string
	int[] wordEnds;		// array of the positions of ends of words
	int[] nSyllables;	// array of no. of syllables for each word
	int[] nLetters;		// no. letters in each word
	int[] accSyll;		// on which syllable does accent fall? -1 = multiple; 0 = none

	/* 2d arrays[nWords][x], where x is no. syllables in each word */
	int[][] accentW;	// accent
	int[][] vowelnW;	// vowel number
	int[][] vowelpW;	// vowel position (within single word)
	int[][] sylBegW;	// syllable position (within single word)
	String[][] lettersS;	// characters divided by syllable
	String[][] lettersSunacc;	// characters divided by syllable, stripped of diacriticals
	
	/* 2d arrays[nWords][x], where x no. letters within each word */
	char[][] letters;		// letters of original string
	char[][] unacc;			// unaccented version of original string
	int[][] syllablesW; 	// syllable no. for each letter in original string

	
	/*   C O N S T R U C T O R   */
	WordInfo(String str) {
		orig = Normalizer.normalize(str, Normalizer.Form.NFC);
		int len = orig.length();
//		System.out.println("====== START WordInfo constructor ======");
//		System.out.println(orig);
		
		short[] letter = CharInfo.findLetters(orig);
//		System.out.println("letter: " + letter.length + " "+ Arrays.toString(letter));
		
		int[] vowel = CharInfo.detectVowels(orig);
		
		char[] cArray = CharInfo.withIotas(orig).toCharArray();
		int[] syll = new int[len];
		int tSyll = 0; //total syllables in whole string
		
		/* divide string into 2d array of chars TODO*/
		words = orig.split("\\s+"); /* split at whitespace, any of it */
		String[] unacc_tmp = CharInfo.killAccents(orig).split("\\s+");
		nWords = words.length;
		letters = new char[nWords][];
		unacc = new char[nWords][];
		nLetters = new int[nWords];
		for (int i = 0; i < nWords; i++) {
			letters[i] = words[i].toCharArray();
			unacc[i] = unacc_tmp[i].toCharArray();
			nLetters[i] = letters[i].length;
		}
		
		/* step through the characters, mark each with syllable number */
		ArrayList<Integer> vpos_tmp = new ArrayList<Integer>(); // vowel positions
//		ArrayList<Integer> vno_tmp = new ArrayList<Integer>(); // vowel numbers
		int nsyl = 0;	//ordinal for syllable being examined
		int lastv = -1;	// position of previous vowel
		
		for (int i = 0; i < len; i++) {
			System.out.println("i: "+ i +": " + cArray[i]);
			
			syll[i] = nsyl;
			if (letter[i] == 0) { // if non-letter, reset (input string can include multiple words)
				syll[i] = 0;
				tSyll += nsyl;
				nsyl = 0; lastv = i;
			} else
			if (vowel[i] > 0) {
				nsyl++;
				syll[i] = nsyl;
				// look backward
				if (i - lastv > 1) {syll[i - 1] = nsyl; } // previous consonant is always with the present vowel
				if (i - lastv > 2) { //look at previous characters for consonants
					String dd = Character.toString(cArray[i - 2]) + Character.toString(cArray[i - 1]);
					if (CharInfo.which(dd, CONSONANT2_LIST) > -1) {syll[i - 2] = nsyl;}
					else {syll[i-2] = nsyl - 1;}
				}
				if (i - lastv > 3) { //look at previous characters for consonants
					String dd = Character.toString(cArray[i - 3]) + Character.toString(cArray[i - 2]) + Character.toString(cArray[i - 1]);
					if (CharInfo.which(dd, CONSONANT3_LIST) > -1) {syll[i - 3] = nsyl;}
					else {syll[i - 3] = nsyl - 1;}
				}
				// look forward to see if diphthong
				if (vowel[i] > 0x10 && vowel[i + 1] > 0x20) {
					syll[++i] = nsyl; 
					lastv = i; 
				}
				vpos_tmp.add(i);
//				vno_tmp.add(vowel[i]);
				lastv = i;
			}
		}
		totSyll = tSyll + nsyl;
		syllable = syll;
		vowelp = convertIntegers(vpos_tmp);
//		voweln = convertIntegers(vno_tmp);
//		printArray(syllable, 3);
//		printArray(vowelp, 3);
		
		/*scan through syllables:  read off vowel nos. and accents */
		voweln = new int[totSyll];
		accent = new int[totSyll];
		for (int i = 0; i < totSyll; i++) {
			int j = vowelp[i];
			voweln[i] = vowel[j];
			String tmp = Normalizer.normalize(orig.substring(j, j + 1), Normalizer.Form.NFD);
			if ( tmp.contains("\u0301") ) accent[i] = 2; // acute
			if ( tmp.contains("\u0342") ) accent[i] = 3; // circumflex
			if ( tmp.contains("\u0300") ) accent[i] = 1; // grave
		}
		
//		nWords = 0;
		int[] d = diff(syll, syll);
//		printArray(d, 3);
//		for (int i = 0; i < d.length; i++) if (d[i] < 0) nWords++;	// count word endings
		
//		int[] wordEnds_tmp = CharInfo.whereGT(0, d); //whereGT(0, d); //whereLT(d[i], 0);
//		int[] wordEnds_tmp = CharInfo.whereGT(0, d); //whereGT(0, d); //whereLT(d[i], 0);
		int[] dd;
		if (letter[0] > 0) dd = concat(new int[]{1}, d); // need to include beginning of 1st word even if it starts string;
		else dd = concat(new int[]{0}, d);;
// int[] spos_tmp = CharInfo.whereEQ(1, dd);
		sylBeg = CharInfo.whereEQ(1, dd);

		//		int[] tmp = new int[spos_tmp.length];
//		Arrays.fill(tmp, 1);
//		if (letter[0] > 0) sylBeg = concat(new int[]{0}, spos_tmp);
		
		
		ArrayList<Integer> beg_tmp = new ArrayList<Integer>();
		ArrayList<Integer> div_tmp = new ArrayList<Integer>();
		ArrayList<Integer> end_tmp = new ArrayList<Integer>();
//		int[] wordEnds_tmp;
		if (syll[0] > 0) beg_tmp.add(0);
		for (int i = 0; i < d.length; i++) {
			if (d[i] < 0) {
//				nWords++;	// counting word endings
				end_tmp.add(i);
			}
			else if (d[i] == 1) {
				if (syll[i] == 0) {
					beg_tmp.add(i + 1);
				}	
				else {
					div_tmp.add(i + 1);
				}
			}
		}
		if (syll[len - 1] > 0) end_tmp.add(len - 1);
		
//		System.out.print("\nends: "); printArray(wordEnds_tmp, 5);
		
//		if (syll[syll.length - 1] > 0) {
//			wordEnds = concat(wordEnds_tmp, new int[]{syll.length - 1});
//		}
//		System.out.println("ends: " + (wordEnds.length));
//		System.out.println("ends_tmp: " + wordEnds_tmp.length + ": ");
//		printArray(wordEnds, 5);
		wordEnds = convertIntegers(end_tmp); //ends:     5    8   16   20   23   34   44   49   55
		wordBegs = convertIntegers(beg_tmp);
		/* insert hyphens at syllables' boundaries internal to word */;
		Collections.reverse(div_tmp);
		int[] sylPos = convertIntegers(div_tmp);
		StringBuilder dashed1 = new StringBuilder(str);		
		for (int m : sylPos) dashed1.insert(m, "-");
		
//		System.out.print("Syllable positions: "); printArray(sylPos, 5);
//		System.out.println("dashed1: " + dashed1);
		dashed = dashed1.toString();
//		dashed = dashed(str, syll);
		
//		System.out.print("Syllable positions: "); printArray(sylPos, 5);
//		System.out.println(dashed1);
		int syllNo = 0;
		int maxNSyll = 0;
		int maxNLett = 0;
		nSyllables = new int[nWords];
//		System.out.println( nWords );
//		System.out.println( nSyllables.length );
//		System.out.println( "wordBegs: " + Arrays.toString(wordBegs) );
//		System.out.println( "wordEnds: " + Arrays.toString(wordEnds) );
		for (int i = 0; i < nWords; i++) {
			nSyllables[i] = syll[wordEnds[i]];
			if (nSyllables[i] > maxNSyll) maxNSyll = nSyllables[i];
			if ( maxNLett < (wordEnds[i] - wordBegs[i]) ) maxNLett = wordEnds[i] - wordBegs[i];
		}
//		System.out.println( "nSyllables: " + Arrays.toString(nSyllables) );
		
		/* Now put the data into 2d arrays[y][x], where y is word number, x is syllable no. */
		accentW = new int[nWords][];
		vowelnW = new int[nWords][];
		vowelpW = new int[nWords][];
		sylBegW = new int[nWords][];
		syllablesW = new int[nWords][];
//		System.out.println("dims: " + nWords + ", " + maxNSyll);
//		System.out.println(wordBegs.length);
//		System.out.println(Arrays.toString(nSyllables));
//		System.out.println(Arrays.deepToString(vowelpW));

		for (int i = syllNo = 0; i < nWords; i++) {
//			System.out.println("=" + i);
			accentW[i] = Arrays.copyOfRange(accent, syllNo, syllNo + nSyllables[i]);
			vowelnW[i] = Arrays.copyOfRange(voweln, syllNo, syllNo + nSyllables[i]);
			vowelpW[i] = Arrays.copyOfRange(vowelp, syllNo, syllNo + nSyllables[i]);
			sylBegW[i] = Arrays.copyOfRange(sylBeg, syllNo, syllNo + nSyllables[i]);
			syllablesW[i] = Arrays.copyOfRange(syllable, wordBegs[i], wordEnds[i] + 1);
			for (int j = 0; j < nSyllables[i]; j++) { // turn pos. in line to pos. in string
				vowelpW[i][j] -= wordBegs[i];
				sylBegW[i][j] -= wordBegs[i];
			}
			syllNo +=  nSyllables[i];
		}
		
		// divide syllables into an array TODO: cleanup the code
//		String[] ls = new String[totSyll];
		lettersS = new String[nWords][];
		lettersSunacc = new String[nWords][];
		String una = CharInfo.killDiacritics(orig);
		int k = 0;
//		System.out.println("orig: " + orig.length() + " " + orig);
		for (int i = 0; i < nWords; i++) {
			String[] ls = new String[nSyllables[i]];
			String[] ls2 = new String[nSyllables[i]];
			for (int j = 0; j < nSyllables[i]; j++) {
				int endIndex;
				if (j < nSyllables[i] - 1) endIndex = sylBeg[k + 1];
				else endIndex = wordEnds[i] + 1;
				ls[j] = orig.substring(sylBeg[k], endIndex);
				ls2[j] = una.substring(sylBeg[k], endIndex);
				k++;
			}
			lettersS[i] = ls; //Arrays.copyOfRange(ls, m, m + nSyllables[i]);
			lettersSunacc[i] = ls2;
//			m += nSyllables[i];
		}
//		System.out.println(Arrays.deepToString(lettersS));
		


//		System.out.print(ls.length);
//		for (int i = 0; i < nWords; i++) {
//			System.out.println(i);
//				lettersS[i] = Arrays.copyOfRange(ls, m, m + nSyllables[i]);
//				m += nSyllables[i];
//		}
		
//		System.out.println("====== END WordInfo constructor ======");
	}

	/*+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%
	   MAIN - test of the constructor 
	  +%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+*/
	public static void main(String[] args) {
//		consonantCombos();
		
//		String str = "λύω ῥᾰψωδός ῃαυθ ἄνθρωπόςτ";
//		String str = "λύω ῃαυθ τῳλλυμιὶ ῡμῶν ἀποθνῄσκω ὁ ἄνθρωπός ῥᾰψωδός."; //ῡ̔  
		String str = "Ἐπειδὴ";// τὸ εἰδέναι καὶ τὸ ἐπίστασθαι συμβαίνει περὶ πάσας";
//		int[] syll = syllables(str);
		int[] counting = new int[str.length()];
		for (int i = 0; i < str.length(); i++) counting[i] = i;

		WordInfo inf = new WordInfo(str);
		System.out.println(inf.toString());
		
//		System.out.println("\nR E - A C C E N T E D");
//		String acctd = recessiveAcc(inf.orig);		
//		WordInfo ninf = new WordInfo(acctd);
//		System.out.println(ninf.toString());
		
	}
	
	@Override
	public String toString() {
		
		final int width = 3;
		String ft = "%"+width+"d";
		String sft = "%"+width+"s";
		
	    StringBuilder result = new StringBuilder();

	    result.append(orig);
		result.append("\nordinal: "); 
		for (int c = 0; c < orig.length(); c++) result.append(String.format(ft, c));

		result.append("\norig:    ");
		for (char c : orig.toCharArray()) result.append(String.format(sft, c));
	    
		result.append("\nsyll:    "); 
		for (int c : syllable) result.append(String.format(ft, c));
		
		result.append("\n\ntot syll: " + totSyll);
		result.append("\nvowelp:  "); 
		for (int c : vowelp) result.append(String.format(ft, c));

		result.append("\nvoweln:  ");
		for (int c : voweln) result.append(String.format(" %0" + (width-1) + "x", c));

		result.append("\nsyllBeg: ");
		for (int c : sylBeg) result.append(String.format(ft, c));
		
		result.append("\naccent:  ");
		for (int c : accent) result.append(String.format(ft, c));
		
		result.append("\nordinal: "); 
		for (int c = 0; c < totSyll; c++) result.append(String.format(ft, c));
		
		result.append("\n\nnWords:  " + nWords);
		result.append("\nwords: ");
		for (String c : words) result.append(String.format("(%s)", c));
		
		result.append("\nbegs:  "); 
		for (int c : wordBegs) result.append(String.format(ft, c));
		
		result.append("\nends:  "); 
		for (int c : wordEnds) result.append(String.format(ft, c));
		
		result.append("\nnSyll: "); 
		for (int c : nSyllables) result.append(String.format(ft, c));
		
		result.append("\nordnl: "); 
		for (int c = 0; c < nWords; c++) result.append(String.format(ft, c));

		result.append("\n\ndashed:  " + dashed);	
		result.append("\n");
		
		result.append("\naccent:     ");
		result.append(array2dToString(accentW));
		result.append("\nvowel no.:  ");
		result.append(array2dToString(vowelnW));
		result.append("\nvowel pos.: ");
		result.append(array2dToString(vowelpW));
		result.append("\nsyll. beg.: ");
		result.append(array2dToString(sylBegW));
		result.append("\nlettersS:   ");
		result.append(array2dToString(lettersS));
		result.append("\n\nletters:    ");
		result.append(array2dToString(letters));		
		result.append("\nsyll. nos.: ");
		result.append(array2dToString(syllablesW));	
		
		return result.toString();
	}
	
	public String array2dToString(int[][] arry) {
		final int w = 3;
		int height = arry.length;

	    StringBuilder result = new StringBuilder();
		
	    result.append("[");
		for (int i = 0; i < height; i++) {
			if (i > 0) result.append("|");
			for (int j = 0; j < arry[i].length; j++) {
				if (j > 0) result.append(" ");
				result.append(String.format("%2d", arry[i][j]));
			}
		}
		result.append("]");
		
		return result.toString();
	}
	public String array2dToString(char[][] arry) {
		final int w = 3;
		int height = arry.length;

	    StringBuilder result = new StringBuilder();
		
	    result.append("[");
		for (int i = 0; i < height; i++) {
			if (i > 0) result.append("|");
			for (int j = 0; j < arry[i].length; j++) {
				if (j > 0) result.append(" ");
				result.append(String.format("%2s", arry[i][j]));
			}
		}
		result.append("]");
		return result.toString();
	}
	public String array2dToString(String[][] arry) {
		int height = arry.length;

	    StringBuilder result = new StringBuilder();
		
	    result.append("[");
		for (int i = 0; i < height; i++) {
			if (i > 0) result.append("|");
			int j;
			for (j = 0; j < arry[i].length; j++) {
				if (j > 0) result.append(" ");
				result.append(String.format("%s", arry[i][j]));
			}
		}	
		
		result.append("]");
		return result.toString();
	}
	
	/**
	 * The IndGen function returns an integer array with the specified dimensions.
	 *  
	 *  Each element of the returned integer array is set to the value of its one-dimensional subscript.
	 *  
	 *  Borrowed from IDL: 
	 *  	http://idlastro.gsfc.nasa.gov/idl_html_help/INDGEN.html 
	 *  
	 *  @params size
	 *  @return int[size], each element set to value of its subscript
	 *  
	 *  */
	public int[] IndGen(int size) {
		int[] result = new int[size];
		for (int i = 0; i < size; i++) result[i] = i;
		return result;
	}
		
	/**
	 * divide word into syllables
	 * 
	 * @params string
	 * @return array, with length of string, consisting of syllable numbers for each letter
	 * */

	public static int[] syllables(String str) {
		short[] letter = CharInfo.findLetters(str);
		int[] vowel = CharInfo.detectVowels(str);
		char[] cArray = CharInfo.withIotas(str).toCharArray();
		int len = str.length();
		int[] syll = new int[len];

		// step through the characters, mark each with syllable number
		int nsyl = 0;	// number of syllable being examined
		int lastv = -1;	// position of last vowel
		for (int i = 0; i < len; i++) {
//			System.out.print(i + ": "); printArray(syll, 5);
			syll[i] = nsyl;
			if (letter[i] == 0) { // if non-letter, reset
				syll[i] = 0;
				nsyl = 0; lastv = i;
			} else
			if (vowel[i] > 0) {
				nsyl++;
				syll[i] = nsyl;
				// look backward
				if (i - lastv > 1) {syll[i - 1] = nsyl; 
//				System.out.println(">1! " + lastv+" "+(i - lastv));
				} // previous consonant is always with the present vowel
				if (i - lastv > 2) { //look at previous characters for consonants
//					syll[i-1] = nsyl; // previous consonant is always with the present vowel
//					System.out.println(">2!" + lastv + " " + (i - lastv));
					String dd = Character.toString(cArray[i - 2]) + Character.toString(cArray[i - 1]);
					if (CharInfo.which(dd, CONSONANT2_LIST) > -1) {syll[i - 2] = nsyl;}
					else {syll[i-2] = nsyl - 1;}
				}
				if (i - lastv > 3) { //look at previous characters for consonants
					String dd = Character.toString(cArray[i - 3]) + Character.toString(cArray[i - 2]) + Character.toString(cArray[i - 1]);
					if (CharInfo.which(dd, CONSONANT3_LIST) > -1) {syll[i - 3] = nsyl;}
					else {syll[i - 3] = nsyl - 1;}
				}
				// look forward
				if (vowel[i] > 0x10 && vowel[i + 1] > 0x20) {syll[++i] = nsyl; lastv = i;}
				lastv = i;
			}
//			System.out.print(i + ": "); printArray(syll, 5);
		}
		
		
//		dashed(str, syll);
		
		return syll;
	}

	/**
	 * @param str
	 * @param syll
	 * @return str divided into syllables by dashes
	 */
	static String dashed(String str, int[] syll) {
		/* divide syllables with dashes*/
		ArrayList<Integer> tmp = new ArrayList<Integer>(); //positions of syllables
		StringBuilder dashed1 = new StringBuilder(str);
		
		int[] d = diff(syll, syll);
//		printArray(d, 5);
//		System.out.println("***");
		for (int i = 0; i < d.length; i++) if (d[i] == 1 && syll[i] !=0) tmp.add(i + 1);
		
		/* for Java purists: notice this could've been one or two simple casts (heavens to Betsy!) 
		 * instead of these wretched loops, but so as not to make you swoon, we'll be explicit but inelegant */
//		Object[] tmp2 = tmp.toArray();
//		int[] sylPos = new int[tmp2.length];
//		for (int i = 0; i < tmp2.length; i++) sylPos[i] = (Integer) tmp2[tmp2.length - i - 1];
		Collections.reverse(tmp);

		int[] sylPos = convertIntegers(tmp);
		
		for (int m : sylPos) dashed1.insert(m, "-");

		// Syllable positions:    53   48   42   39   31   28   26   14   12    4    1
//		System.out.print("Syllable positions: "); printArray(sylPos, 5);
//		System.out.println(dashed1);
		return dashed1.toString();
	}
	

	/* Given vowel no., determine if vowel is long 
	 * (but not for optative or locative, see Smyth 169) */
	static boolean vowelIsLong(int x) {
		if (x == 0x21 || x == 0x23) return false; // these (αι, οι) aren't short in optative or locative
		if (x == 3 || x > 6) return true;
		return false;
	}
	
/* +%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+% */	
/* General-purpose routines */
/* +%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+%+% */	  

	/**
	 * Diff - given two arrays subtract one from the other
	 * 
	 * function taken from IDL
	 */
	
	static int[] diff(int[] arr1, int[] arr2) {
		int len1 = arr1.length;
		if (len1 != arr2.length) System.err.println("Error! Arrays of different length");
		
		int[] result = new int[len1 - 1];
		for (int i = 0; i < len1 - 1; i++) {
			result[i] = arr1[i + 1] - arr2[i];
		}
		return result;
	}
	
	public static void consonantCombos() {
		String[] tmp = CharInfo.SINGLE_CONSONANT_LIST;
		for (String s : tmp) {
			for (String t : tmp) {
//				System.out.println(i+". "+s+ " + "+ t + " = "+ s+t); i++;
				if (!s.equalsIgnoreCase(t)) System.out.print(s + t + ", ");
			}
			System.out.println();
		}
	}
	
	/**
	 * printArray - print the values of a given array using given column width
	 * 
	 * @param array
	 * @param width
	 * @return void
	 * 
	 * E.g., 
	 * 		printArray(letter, 5);
	 * 		printArray(vowel, 5);
	 * 		printArrayHex(vowel, 5);
	 * 		printArray(cArray, 5);
	 * 
	 */
	static <T extends Number> void printArray(T[] array, int width) {
		for (T c : array) System.out.printf("%" + width + "d", c); // The formatting probably doesn't work here
		System.out.println();
	}
	static <T extends CharSequence> void printArray(T[] array, int width) {
		for (T c : array) System.out.printf("%" + width + "s", c);
		System.out.println();
	}
	static void printArray(int[] array, int width) {
		for (int c : array) System.out.printf("%" + width + "d", c);
		System.out.println();
	}
	static void printArray(Integer[] array, int width) {
		for (int c : array) System.out.printf("%" + width + "d", c);
		System.out.println();
	}
	static void printArray(short[] array, int width) {
		for (int c : array) System.out.printf("%" + width + "d", c);
		System.out.println();
	}
	static void printArray(char[] array, int width) {
		for (int c : array) System.out.printf("%" + width + "c", c);
		System.out.println();
	}
	static void printArrayHex(int[] array, int width) {
		System.out.print(" ");
		for (int c : array) System.out.printf("%" + (width-1) + "x ", c);
		System.out.println();
	}
	static void printString(String string, int width) {
		for (char c : string.toCharArray()) System.out.printf("%" + width + "s", c);
		System.out.println();
	}
	
	
	/* http://stackoverflow.com/questions/718554/how-to-convert-an-arraylist-containing-integers-to-primitive-int-array */
	public static int[] convertIntegers(List<Integer> integers) {
	    int[] ret = new int[integers.size()];
	    Iterator<Integer> iterator = integers.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().intValue();
	    }
	    return ret;
	}
	
	/* http://stackoverflow.com/questions/80476/how-to-concatenate-two-arrays-in-java */
	public static <T> T[] concat(T[] first, T[] second) {
		  T[] result = Arrays.copyOf(first, first.length + second.length);
		  System.arraycopy(second, 0, result, first.length, second.length);
		  return result;
	}
	/* http://stackoverflow.com/questions/4697255/combine-two-integer-arrays */
	public static int[] concat(int[] array1, int[] array2) {
		int[] array1and2 = new int[array1.length + array2.length];
		System.arraycopy(array1, 0, array1and2, 0, array1.length);
		System.arraycopy(array2, 0, array1and2, array1.length, array2.length);
		return array1and2;
	}
	

}
