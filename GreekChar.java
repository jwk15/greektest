package greektext;


/*
 * Also need: 
 * 	- Recognize a vowel/diphthong -- DONE
 * 	- Contract vowels -- DONE
 * 	- Apply endings to given word (verb/noun)
 * 	- Conjugate a given verb (given principle parts)
 * 	- Decline a given noun (given noun and type)
 * 	- Drill to recognize a given verb's person, number, mood, voice, tense, etc.
 * 	- Drill to recognize a given noun's person, number, and case
 * 
 * 	- Divide given word into syllables (each ends in vowel, except for double consonants, word end) -- DONE
 * 	- Return pronunciation of given word
 * 	- Translate Perseus betacode into unicode (and back) -- FROM IMPORT
 *	- Give accented string from GreekCharactersInfo structure -- ESSENTIALLY DONE
 *	- Generate the principle parts of a regular verb given its present-tense active indicative -- CLOSE
 */

/** PERSEUS
 * This class tokenizes Greek text encoded using the Perseus subset of BetaCode:
 * letters: a-z, A-Z
 * breathings: )(
 * accents: /\=
 * capitals: *
 * iota subscript: |
 * apostrophe: '
 * diaeresis: +
 */

public class GreekChar {
	  public static final char DEFAULT_REPLACE_CHAR = '-';
	  public static final String DEFAULT_REPLACE = String.valueOf(DEFAULT_REPLACE_CHAR);
	  public static final char COMBINE_CHAR = '-';
	  
	  public static void main(String[] arg) {
			String str = "αἱρέ-ω";
			String[] tmp;
/*
 *  Now we're going to contract the vowels
 */
			tmp = str.split("\\"+COMBINE_CHAR);
			

	  }

	  public static String contractVowels(String[] args){
		  String output = "dummy";
		  
//		  StringUtils.join(new String[] {"Hello", "World", "!"}, ", ")
		  
		  return output;
	  }

	
	public static void testString() {
		
	}
}
