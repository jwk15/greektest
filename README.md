greektest
=========

Some classes for manipulating polytonic Greek strings. Respective Main methods demonstrate usage.

A couple of the classes are still pretty rough; I stopped actively working on this project more than a year ago (i.e., summer 2012), so factor in the haze of time when reading the descriptions below.

CharInfo - the most developed class; gives information on the characters in a given Greek string (e.g., unaccented/accented/circumflex, iota-subsripted, which vowel/diphthong).

WordInfo - gives information on words in string, e.g., syllable divisions, accentuation.

Pronunciation - gives a phonetic, accentuated spelling of a string.

DiacriticalTest & DiacriticalTest2 - I used these to report a bug with the way Eclipse handles UTF-8 (bug# 382257).

Works in Progress

PrincipleParts - construct the six principal parts of a given regular ω verb. Some results don't make sense.

ContractVowels - supposed to do vowel contraction.

Accent - handling accent combination and recessive acentuation. 

PreNormalizer - supposed to compensate for Eclipse's poor handling of UTF-8. I forget whether I gave this up as a lost cause.

GreekChar - I forget what my intention was here.

