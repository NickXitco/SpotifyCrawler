package com.nxitco.maven.quickstart;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

public class Writer {
	BufferedWriter dbWriter;
	String line;
	
	public Writer(String fileName) {
		try {
			this.dbWriter = new BufferedWriter(new FileWriter(fileName, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeHeadArtist(String name, String id) throws IOException {
		this.dbWriter.write(stringPrep(name, id));
	}
	
	public void writeChildArtist(String name, String id) throws IOException {
		this.dbWriter.append("|" + stringPrep(name, id));
	}
	
	public void startNewLine() throws IOException {
		this.dbWriter.append("\n");
	}
	
	public void flushWriter() throws IOException {
		this.dbWriter.flush();
	}
	
	public void closeWriter() throws IOException {
		this.dbWriter.close();
	}
	
	public void errorMessage() throws IOException {
		this.dbWriter.append("//POSSIBLE ERROR//");
	}
	
	public String stringPrep(String name, String id) {
		if (name.contains("&")) {
			name =  unescapeHtml3(name);
		}
		if (name.contains("|")) {
			name = name.replace("|", "/");
		}
		return name + "|" + id;
	}
	
	//Thank you to Nick Frolov from StackOverflow for this code.
	public final String unescapeHtml3(final String input) {
        StringWriter writer = null;
        int len = input.length();
        int i = 1;
        int st = 0;
        while (true) {
            // look for '&'
            while (i < len && input.charAt(i-1) != '&')
                i++;
            if (i >= len)
                break;

            // found '&', look for ';'
            int j = i;
            while (j < len && j < i + MAX_ESCAPE + 1 && input.charAt(j) != ';')
                j++;
            if (j == len || j < i + MIN_ESCAPE || j == i + MAX_ESCAPE + 1) {
                i++;
                continue;
            }

            // found escape 
            if (input.charAt(i) == '#') {
                // numeric escape
                int k = i + 1;
                int radix = 10;

                final char firstChar = input.charAt(k);
                if (firstChar == 'x' || firstChar == 'X') {
                    k++;
                    radix = 16;
                }

                try {
                    int entityValue = Integer.parseInt(input.substring(k, j), radix);

                    if (writer == null) 
                        writer = new StringWriter(input.length());
                    writer.append(input.substring(st, i - 1));

                    if (entityValue > 0xFFFF) {
                        final char[] chrs = Character.toChars(entityValue);
                        writer.write(chrs[0]);
                        writer.write(chrs[1]);
                    } else {
                        writer.write(entityValue);
                    }

                } catch (NumberFormatException ex) { 
                    i++;
                    continue;
                }
            }
            else {
                // named escape
                CharSequence value = lookupMap.get(input.substring(i, j));
                if (value == null) {
                    i++;
                    continue;
                }

                if (writer == null) 
                    writer = new StringWriter(input.length());
                writer.append(input.substring(st, i - 1));

                writer.append(value);
            }

            // skip escape
            st = j + 1;
            i = st;
        }

        if (writer != null) {
            writer.append(input.substring(st, len));
            return writer.toString();
        }
        return input;
    }

    private final static String[][] ESCAPES = {
        {"\"",     "quot"}, // " - double-quote
        {"&",      "amp"}, // & - ampersand
        {"<",      "lt"}, // < - less-than
        {">",      "gt"}, // > - greater-than

        // Mapping to escape ISO-8859-1 characters to their named HTML 3.x equivalents.
        {"\u00A0", "nbsp"}, // non-breaking space
        {"\u00A1", "iexcl"}, // inverted exclamation mark
        {"\u00A2", "cent"}, // cent sign
        {"\u00A3", "pound"}, // pound sign
        {"\u00A4", "curren"}, // currency sign
        {"\u00A5", "yen"}, // yen sign = yuan sign
        {"\u00A6", "brvbar"}, // broken bar = broken vertical bar
        {"\u00A7", "sect"}, // section sign
        {"\u00A8", "uml"}, // diaeresis = spacing diaeresis
        {"\u00A9", "copy"}, // Â© - copyright sign
        {"\u00AA", "ordf"}, // feminine ordinal indicator
        {"\u00AB", "laquo"}, // left-pointing double angle quotation mark = left pointing guillemet
        {"\u00AC", "not"}, // not sign
        {"\u00AD", "shy"}, // soft hyphen = discretionary hyphen
        {"\u00AE", "reg"}, // Â® - registered trademark sign
        {"\u00AF", "macr"}, // macron = spacing macron = overline = APL overbar
        {"\u00B0", "deg"}, // degree sign
        {"\u00B1", "plusmn"}, // plus-minus sign = plus-or-minus sign
        {"\u00B2", "sup2"}, // superscript two = superscript digit two = squared
        {"\u00B3", "sup3"}, // superscript three = superscript digit three = cubed
        {"\u00B4", "acute"}, // acute accent = spacing acute
        {"\u00B5", "micro"}, // micro sign
        {"\u00B6", "para"}, // pilcrow sign = paragraph sign
        {"\u00B7", "middot"}, // middle dot = Georgian comma = Greek middle dot
        {"\u00B8", "cedil"}, // cedilla = spacing cedilla
        {"\u00B9", "sup1"}, // superscript one = superscript digit one
        {"\u00BA", "ordm"}, // masculine ordinal indicator
        {"\u00BB", "raquo"}, // right-pointing double angle quotation mark = right pointing guillemet
        {"\u00BC", "frac14"}, // vulgar fraction one quarter = fraction one quarter
        {"\u00BD", "frac12"}, // vulgar fraction one half = fraction one half
        {"\u00BE", "frac34"}, // vulgar fraction three quarters = fraction three quarters
        {"\u00BF", "iquest"}, // inverted question mark = turned question mark
        {"\u00C0", "Agrave"}, // Ð� - uppercase A, grave accent
        {"\u00C1", "Aacute"}, // Ð‘ - uppercase A, acute accent
        {"\u00C2", "Acirc"}, // Ð’ - uppercase A, circumflex accent
        {"\u00C3", "Atilde"}, // Ð“ - uppercase A, tilde
        {"\u00C4", "Auml"}, // Ð” - uppercase A, umlaut
        {"\u00C5", "Aring"}, // Ð• - uppercase A, ring
        {"\u00C6", "AElig"}, // Ð– - uppercase AE
        {"\u00C7", "Ccedil"}, // Ð— - uppercase C, cedilla
        {"\u00C8", "Egrave"}, // Ð˜ - uppercase E, grave accent
        {"\u00C9", "Eacute"}, // Ð™ - uppercase E, acute accent
        {"\u00CA", "Ecirc"}, // Ðš - uppercase E, circumflex accent
        {"\u00CB", "Euml"}, // Ð› - uppercase E, umlaut
        {"\u00CC", "Igrave"}, // Ðœ - uppercase I, grave accent
        {"\u00CD", "Iacute"}, // Ð� - uppercase I, acute accent
        {"\u00CE", "Icirc"}, // Ðž - uppercase I, circumflex accent
        {"\u00CF", "Iuml"}, // ÐŸ - uppercase I, umlaut
        {"\u00D0", "ETH"}, // Ð  - uppercase Eth, Icelandic
        {"\u00D1", "Ntilde"}, // Ð¡ - uppercase N, tilde
        {"\u00D2", "Ograve"}, // Ð¢ - uppercase O, grave accent
        {"\u00D3", "Oacute"}, // Ð£ - uppercase O, acute accent
        {"\u00D4", "Ocirc"}, // Ð¤ - uppercase O, circumflex accent
        {"\u00D5", "Otilde"}, // Ð¥ - uppercase O, tilde
        {"\u00D6", "Ouml"}, // Ð¦ - uppercase O, umlaut
        {"\u00D7", "times"}, // multiplication sign
        {"\u00D8", "Oslash"}, // Ð¨ - uppercase O, slash
        {"\u00D9", "Ugrave"}, // Ð© - uppercase U, grave accent
        {"\u00DA", "Uacute"}, // Ðª - uppercase U, acute accent
        {"\u00DB", "Ucirc"}, // Ð« - uppercase U, circumflex accent
        {"\u00DC", "Uuml"}, // Ð¬ - uppercase U, umlaut
        {"\u00DD", "Yacute"}, // Ð­ - uppercase Y, acute accent
        {"\u00DE", "THORN"}, // Ð® - uppercase THORN, Icelandic
        {"\u00DF", "szlig"}, // Ð¯ - lowercase sharps, German
        {"\u00E0", "agrave"}, // Ð° - lowercase a, grave accent
        {"\u00E1", "aacute"}, // Ð± - lowercase a, acute accent
        {"\u00E2", "acirc"}, // Ð² - lowercase a, circumflex accent
        {"\u00E3", "atilde"}, // Ð³ - lowercase a, tilde
        {"\u00E4", "auml"}, // Ð´ - lowercase a, umlaut
        {"\u00E5", "aring"}, // Ðµ - lowercase a, ring
        {"\u00E6", "aelig"}, // Ð¶ - lowercase ae
        {"\u00E7", "ccedil"}, // Ð· - lowercase c, cedilla
        {"\u00E8", "egrave"}, // Ð¸ - lowercase e, grave accent
        {"\u00E9", "eacute"}, // Ð¹ - lowercase e, acute accent
        {"\u00EA", "ecirc"}, // Ðº - lowercase e, circumflex accent
        {"\u00EB", "euml"}, // Ð» - lowercase e, umlaut
        {"\u00EC", "igrave"}, // Ð¼ - lowercase i, grave accent
        {"\u00ED", "iacute"}, // Ð½ - lowercase i, acute accent
        {"\u00EE", "icirc"}, // Ð¾ - lowercase i, circumflex accent
        {"\u00EF", "iuml"}, // Ð¿ - lowercase i, umlaut
        {"\u00F0", "eth"}, // Ñ€ - lowercase eth, Icelandic
        {"\u00F1", "ntilde"}, // Ñ� - lowercase n, tilde
        {"\u00F2", "ograve"}, // Ñ‚ - lowercase o, grave accent
        {"\u00F3", "oacute"}, // Ñƒ - lowercase o, acute accent
        {"\u00F4", "ocirc"}, // Ñ„ - lowercase o, circumflex accent
        {"\u00F5", "otilde"}, // Ñ… - lowercase o, tilde
        {"\u00F6", "ouml"}, // Ñ† - lowercase o, umlaut
        {"\u00F7", "divide"}, // division sign
        {"\u00F8", "oslash"}, // Ñˆ - lowercase o, slash
        {"\u00F9", "ugrave"}, // Ñ‰ - lowercase u, grave accent
        {"\u00FA", "uacute"}, // ÑŠ - lowercase u, acute accent
        {"\u00FB", "ucirc"}, // Ñ‹ - lowercase u, circumflex accent
        {"\u00FC", "uuml"}, // ÑŒ - lowercase u, umlaut
        {"\u00FD", "yacute"}, // Ñ� - lowercase y, acute accent
        {"\u00FE", "thorn"}, // ÑŽ - lowercase thorn, Icelandic
        {"\u00FF", "yuml"}, // Ñ� - lowercase y, umlaut
    };

    private static final int MIN_ESCAPE = 2;
    private static final int MAX_ESCAPE = 6;

    private static final HashMap<String, CharSequence> lookupMap;
    static {
        lookupMap = new HashMap<String, CharSequence>();
        for (final CharSequence[] seq : ESCAPES) 
            lookupMap.put(seq[1].toString(), seq[0]);
    }
	
	
}
