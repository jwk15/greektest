package greektext;

public class Accent {

	public static void main (String[] args) {
		
//		String str = "λύω ῥᾰψωδός ῃαυθ ἄνθρωπόςτ";
//		String str = "λύω ῃαυθ τῳλλυμιὶ ῡμῶν ἀποθνῄσκω ὁ ἄνθρωπός ῥᾰψωδός."; //ῡ̔  
		String str = "Ἐπειδὴ";// τὸ εἰδέναι καὶ τὸ ἐπίστασθαι συμβαίνει περὶ πάσας";
		
		WordInfo inf = new WordInfo(str);
		System.out.println(inf.toString());
		
		System.out.println("\nR E - A C C E N T E D");
		String acctd = recessive(inf.orig);		
		WordInfo ninf = new WordInfo(acctd);
		System.out.println(ninf.toString());
	}
	
	/* Bonus method: apply recessive accentuation */
	static String recessive(String str) {
		
		/* See Smyth 149, 163, 164
		 * 
		 *  
		 */
		StringBuilder result = new StringBuilder();
		WordInfo w = new WordInfo(str);
				
		int[][] accent = w.accentW;
//		System.out.println(Arrays.deepToString(w.accentW));
//		for (int i = 0; i < accent.length; i++) {
//			Arrays.fill(accent[i], 0);
//		}
//		System.out.println(Arrays.deepToString(accent));
//		System.out.println(Arrays.deepToString(w.accentW));
		
//		System.out.println("====== BEGIN recessiveAcc =======");
		
//		int[][] oldacc = accent;
		for (int i = 0; i < w.nWords; i++) {
//			System.out.println(w.words[i] + " " + w.nSyllables[i]);
			int u = w.nSyllables[i] - 1;
			int p = u - 1;
			int a = u - 2;
//			System.out.println("i: " + i + "- " + a + ", " + p + ", " + u);	
			boolean uIsLong = WordInfo.vowelIsLong(w.vowelnW[i][u]) || accent[i][u] == 3;
			
//			/* Clear all accents before a */
//			if (a > 0) for (int j = 0; j < a; j++) accent[i][j] = 0;
//			/* Clear all non-circumflex accents in a p u */
//			if (a >= 0) accent[i][a] = 0;
//			if (p >= 0) accent[i][p] = 0;
////			if (accent[i][u] != 3) accent[i][u] = 0; // but leave grave in place
//			accent[i][u] = 0;
			
			/* Put in place recessive accentuation, see Smyth 167-168 */
			if (uIsLong) {
				System.out.println("\nu is long");
				if (p >= 0) accent[i][p] = 2;
				else accent[i][u] = 2;
			}
			else { // u is short
				System.out.println("\nu is short");
				if (a >= 0) accent[i][a] = 2;
				else 
					if (p >= 0) {
						if (WordInfo.vowelIsLong(w.vowelnW[i][p]) || w.accentW[i][p] == 2) accent[i][p] = 3;
						else accent[i][p] = 2;
					}
					else accent[i][u] = 2;
			}

//			System.out.println();
			
			/* string the letters and accents together */
			for (int j = 0; j < w.nLetters[i]; j++) {
//				System.out.println("j: " + j);	
//				System.out.println("\n  unacc: "+ w.unacc[i].length);	
				result.append(w.unacc[i][j]);
				System.out.print(w.unacc[i][j]);
				int k = w.syllablesW[i][j] - 1;
//				System.out.println("\n  k: " + k + ", vpsize: "+ w.vowelpW[i].length);	
				if (w.vowelpW[i][k] == j) {
					switch (w.accentW[i][k]) {
						case 2: 
							result.append("\u0301"); // acute
							System.out.print("_\u0301_");
							break;
						case 3: 
							result.append("\u0342"); // circumflex
							System.out.print("_\u0342_");
							break;
						case 1: 
							result.append("\u0300"); // grave
							System.out.print("_\u0300_");					
					}
				}
			}
			if (i < w.nWords - 1) {
				result.append(' ');
			}
//			System.out.println(" (" + i + ") ");
			System.out.println();
		}

//		System.out.println("====== END recessiveAcc =======");

		return result.toString();
	}
	
	static String persistentAcc(String str) {
		//TODO the following started as paste from above
		// uses old accent numbering system
		
		WordInfo w = new WordInfo(str);
//		int[] oldacc = w.accent;
		int[][] accent = w.accentW;
		for (int i = 0; i < w.nWords; i++) {
			int u = w.nSyllables[i] - 1;
			int p = u - 1;
			int a = p - 1;
			int uVowel = w.vowelnW[i][u];
			boolean pIsLong = WordInfo.vowelIsLong(w.vowelnW[i][p]) || accent[i][u] == 2;
			boolean uIsLong = WordInfo.vowelIsLong(uVowel) || accent[i][u] == 2;
			
			/* Clear accents before a */
			if (a > 0) for (int j = 0; j < a; j++) accent[i][j] = 0;
			
			/* First, check for existing accentuation and check that it conforms to Smyth 163-168 */
			if (uIsLong) {
				if (accent[i][u] > 0) {
					// don't change u accent
					if (accent[i][u] != 3) { // if it's acute or circumflex, clear other accents
						accent[i][p] = 0;
						accent[i][a] = 0;
					}
				}
				if (accent[i][p] == 2) {
					accent[i][p] = 1;
					accent[i][a] = 0;
				} else if (accent[i][p] == 1) {
			}
			else { // short u
				if (accent[i][u] == 2) accent[i][p] = 1;
				if (accent[i][p] == 1 && pIsLong) accent[i][p] = 2;
				if (accent[i][a] > 0) accent[i][a] = 1; 
			}
				
				if (accent[i][u] == 2 ) {
					for (int j = 0; j < u; j++) accent[i][j] = 0; // clear any other accents from word
					continue;
				}
				if (p > 0 && accent[i][p] == 2) {
					for (int j = 0; j < p; j++) accent[i][j] = 0; // clear any other accents from word
					continue;
				}
				if (a > 0) {
					if (accent[i][a] == 1) {
						for (int j = 0; j < a; j++) accent[i][j] = 0; // clear any other accents from word
					} 
					else if (accent[i][a] == 2) {
						System.err.println("Error! Circumflex on a.");
					}
				}
			}
			System.out.println();
		}
		return "";
	}
	
}
