package greektext;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/* TODO see http://en.wikipedia.org/wiki/Wikipedia:IPA_for_Greek */

public class Pronunciation {
    private static final Map<Integer, String> vowels;
    static {
        Map<Integer, String> tmpMap = new HashMap<Integer, String>();
        tmpMap.put(0x01, "ah");
        tmpMap.put(0x02, "eh");
        tmpMap.put(0x03, "ay");
        tmpMap.put(0x04, "ee");
        tmpMap.put(0x05, "au");
        tmpMap.put(0x06, "ü");
        tmpMap.put(0x07, "oh");
        tmpMap.put(0x08, "ah");
        tmpMap.put(0x09, "ay");
        tmpMap.put(0x0a, "oh");
        
		/* simple vowels:
		 *  	{'α', 'ε', 'η', 'ι', 'ο', 'υ', 'ω', "ᾳ", "ῃ", "ῳ"}
		 *		{ x01, x02, x03, x04, x05, x06, x07, x08, x09, x0a}
		 *	diphthongs:
		 *		{"αι", "ει", "οι", "υι", "αυ", "ευ", "ηυ", "ου"}
		 *		{0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28}
		 */
        tmpMap.put(0x21, "aye"); // WordInfo gives 2nd letter of diphthong
        tmpMap.put(0x22, "aye");
        tmpMap.put(0x23, "oi");
        tmpMap.put(0x24, "ü'i");
        tmpMap.put(0x25, "au");
        tmpMap.put(0x26, "eh'ü");
        tmpMap.put(0x27, "ay'ü");
        tmpMap.put(0x28, "oo");

        vowels = Collections.unmodifiableMap(tmpMap);
    }
    // {"βδ", "βλ", "βρ", "γλ", "γν", "γρ", "θλ", "θν", "θρ", "κλ", "κρ", "κτ", "μν", 
	// "πλ", "πν", "πρ", "πτ", "ρμ", "σθ", "σκ", "σμ", "σπ", "στ", "σφ", "σχ", "τρ", "φθ", "φλ", "φρ", "χθ", "χλ", "χξ", "χρ"};
    private static final Map<String, String> consonants;
    static {
        Map<String, String> tmpMap = new HashMap<String, String>();
        tmpMap.put("β", "b");
        tmpMap.put("γ", "g");
        tmpMap.put("δ", "d");
        tmpMap.put("ζ", "zd");
        tmpMap.put("θ", "th");
        tmpMap.put("κ", "k");
        tmpMap.put("λ", "l");
        tmpMap.put("μ", "m");
        tmpMap.put("ν", "n");
        tmpMap.put("ξ", "cs");
        tmpMap.put("π", "p");
        tmpMap.put("ρ", "r");
        tmpMap.put("σ", "s");
        tmpMap.put("ς", "s");
        tmpMap.put("τ", "t");
        tmpMap.put("φ", "f");
        tmpMap.put("χ", "kh");
        tmpMap.put("ψ", "ps");

        consonants = Collections.unmodifiableMap(tmpMap);
    }
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

		
//		String str = "ὁ ἄνθρωπός λύω ῃαυθ τῳλλυμὶ ῡμῶν ἀποθνῄσκω ὁ ἄνθρωπός ῥᾰψωδός ἀναγκή.";
		String str = "πέφυκε δὲ ἐκ τῶν γνωριμωτέρων ἡμῖν ἡ ὁδὸς ῥᾰψωδός";
		
		System.out.println(str);
		String res2 = pronounce1(str);
		System.out.println(res2);
		
	}

	/* TODO special treatment of gamma 
	 * TODO rough breathing with rho
	 * TODO docs
	 * */
	public static String[][] pronounce(String str) {

		WordInfo inf = new WordInfo(str);
		
		String[][] syls = inf.lettersSunacc;
		String[][] res = new String[inf.nWords][];

		for (int i = 0; i < inf.nWords; i++) {
			res[i] = new String[inf.nSyllables[i]];
			for (int j = 0; j < inf.nSyllables[i]; j++) {
				StringBuilder lett = new StringBuilder();
				if (j == 0) {
					String lett1 = (String) Normalizer.normalize(inf.words[i].subSequence(0, 1), Normalizer.Form.NFD);
					// detect rough breathing mark 0x314
//					System.out.println(" first letter: " + tmp);
					if ( lett1.contains("\u0314") && !lett1.contains("ρ") ) { lett.append("h"); }
				}
				for (int k = 0; k < syls[i][j].length(); k++) {
					char current = syls[i][j].charAt(k);
					if ( !CharInfo.isVowel(current) ) {
						if (current == 'ρ') { //***
							
						}
						lett.append( consonants.get( Character.toString(current) ) );
					} 
					else {
						
						int currv = inf.vowelnW[i][j];
						lett.append( vowels.get(currv) );
						if (currv > 0x10) k++; // if diphthong, skip next letter
					}
//					System.out.print("\n" + i + " " + j + " " + " " + k + ": " + current + " ");// + lett);
				}
//				int tmp = inf.accentW[i][j];
				if (inf.accentW[i][j] > 1) { res[i][j] = lett.toString().toUpperCase(); }
				else { res[i][j] = lett.toString(); }
//				System.out.print(res[i][j]);
//				if (j < inf.nSyllables[i]-1) { System.out.print("-"); }
			}
//			System.out.print(" ");
		}
//		System.out.println();
		
		return res;
	}
	
	/* single-string output version */
	public static String pronounce1(String str) {
		StringBuilder res = new StringBuilder();
		String[][] res1 = pronounce(str);

		for (int i = 0; i < res1.length; i++) {
			for (int j = 0; j < res1[i].length; j++) {
				res.append(res1[i][j]);
				if (j < res1[i].length - 1) { res.append("-"); }
			}
			res.append(" ");
		}
		return res.toString();

	}
	
}


/* 		Hashmap notes
 * 
		Iterator iter = consonants.entrySet().iterator();
		 
		while (iter.hasNext()) {
			Map.Entry mEntry = (Map.Entry) iter.next();
			System.out.println(mEntry.getKey() + " : " + mEntry.getValue());
		}
		
		Iterator iterator = consonants.keySet().iterator();

		String[][] tt = new String[consonants.size()][2];
		int m = 0;
		while (iterator.hasNext()) {
		   String key = iterator.next().toString();
		   String value = consonants.get(key).toString();
		   tt[m][0] = key;
		   tt[m][1] = value; 
		   m++;
		   System.out.println(key + " " + value);
		}
		System.out.println(Arrays.deepToString(tt));
		
		------------------------------------------------
				List res_tmp = Arrays.asList(res1);
 * 
 * */
