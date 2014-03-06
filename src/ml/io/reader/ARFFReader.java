package ml.io.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import ml.data.DataSet;
import ml.data.Instance;
import ml.data.features.*;

public class ARFFReader {

	private static final String DATA_DECL_LINE = "@data";
	private static final String RELATION_DECL = "relation";
	private static final String ATTRIBUTE_DECL = "attribute";
	private static final String STRING_TYPE = "string";
	private static final String NUMERIC_TYPE = "numeric";
	private static final String REAL_TYPE = "real";
	private static final String INTEGER_TYPE = "integer";
	private static final String DATE_TYPE = "date";

	private String _fileName;

	public ARFFReader(String fileName) {
		_fileName = fileName;
	}

	public DataSet readDataSet() throws Exception {
		FileReader fr = null;
		BufferedReader br = null;
		DataSet ds = null;

		try {
			fr = new FileReader(_fileName);
			br = new BufferedReader(fr);
			ds = parseDataSet(br);
		}
		finally {
			if (br != null) br.close();
			if (fr != null) fr.close();
		}

		return ds;
	}

	private DataSet parseDataSet(BufferedReader reader) throws Exception {
		DataSet ds = new DataSet();
		parseHeader(reader, ds);
		parseData(reader, ds);
		return ds;
	}

	private void parseHeader(BufferedReader reader, DataSet ds) throws Exception {
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) continue;
			line = line.trim();
			// throw away comment lines
			if (line.charAt(0) == '%') continue;
			// stop at the data section
			if (line.toLowerCase().compareTo(DATA_DECL_LINE) == 0) break;
			parseHeaderLine(line, ds);
		}
	}

	private void parseHeaderLine(String line, DataSet ds) throws Exception {
		ArrayList<Token> tokens = tokenizeLine(line);
		if (tokens.isEmpty()) return;
		int i = 0;

		Token t = tokens.get(i++);

		if (t.Type != TokenType.ATSIGN) throw new Exception("Header line must start with a @ sign. Line:" + line);
		t = tokens.get(i++);

		if (t.Type != TokenType.STRING) throw new Exception("A declaration must start with a letter. Line:" + line);

		String decl = ((String) t.Value).toLowerCase();

		// parse attribute declarations
		if (decl.compareTo(ATTRIBUTE_DECL) == 0) {
			t = tokens.get(i++);
			if (t.Type != TokenType.STRING) throw new Exception("Attribute name must be a string. Line:" + line);
			String name = (String) t.Value;
			t = tokens.get(i++);

			Feature attr = null;
			if (t.Type == TokenType.STRING) {
				String datatype = ((String) t.Value).toLowerCase();
				if (isNumericType(datatype)) {
					attr = new NumericFeature(name);
				}
				else if (datatype.compareTo(STRING_TYPE) == 0) {
					attr = new StringFeature(name);
				}
				else if (datatype.compareTo(DATE_TYPE) == 0) {
					String format = null;
					if (i < tokens.size()) {
						t = tokens.get(i++);
						if (t.Type != TokenType.STRING) throw new Exception("The DATE format must be string.");
						format = (String) t.Value;
					}
					attr = new DateFeature(name, format);
				}
			}
			else if (t.Type == TokenType.OPENBRACE) {
				boolean needValue = true;
				ArrayList<String> nominals = new ArrayList<String>();
				while (i < tokens.size()) {
					t = tokens.get(i++);
					if (t.Type == TokenType.CLOSEBRACE) break;
					// match comma delimited nominal values
					if (needValue) {
						// match a string value
						if (t.Type == TokenType.STRING) {
							nominals.add((String) t.Value);
						}
						else throw new Exception("Nominal value must be a string. Line:" + line);
						needValue = false;
					}
					else {
						// match a comma
						if (t.Type != TokenType.COMMA) throw new Exception("Missing ',' between nominal values. Line:" + line);
						needValue = true;
					}
				}
				if (nominals.size() == 0) throw new Exception("Nominal value set cannot be empty. Line:" + line);
				attr = new DiscreteFeature(name, nominals);
			}
			else {
				throw new Exception("Unknow data type. Line:" + line);
			}

			if (attr != null) ds.features().add(attr);

			return;
		}

		// parse relation declaration
		if (decl.compareTo(RELATION_DECL) == 0) {
			t = tokens.get(i++);
			if (t.Type != TokenType.STRING) throw new Exception("The relation must be a string. Line:" + line);
			ds.setRelation((String) t.Value);
			return;
		}
	}

	private boolean isNumericType(String datatype) {
		if (datatype.compareTo(REAL_TYPE) == 0) return true;
		if (datatype.compareTo(INTEGER_TYPE) == 0) return true;
		if (datatype.compareTo(NUMERIC_TYPE) == 0) return true;
		return false;
	}

	private void parseData(BufferedReader reader, DataSet ds) throws Exception {
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) continue;
			line = line.trim();
			// throw away comment lines
			if (line.charAt(0) == '%') continue;
			parseDataLine(line, ds);
		}
	}

	private void parseDataLine(String line, DataSet ds) throws Exception {
		ArrayList<Token> tokens = tokenizeLine(line);
		if (tokens.size() == 0) return;

		boolean needValue = true;
		int index = 0;

		Instance ins = new Instance(ds.features().count());
		ds.instances().add(ins);

		// parse comma delimited values
		for (Token t : tokens) {
			if (needValue) {
				parseDataValue(t, ds, ins, index, line);
				index++;
				needValue = false;
			}
			else {
				if (t.Type != TokenType.COMMA) throw new Exception("Missing ',' between values. Line:" + line);
				needValue = true;
			}
		}
	}

	private void parseDataValue(Token t, DataSet ds, Instance ins, int index, String line) throws Exception {
		Feature feature = ds.features().features(index);
		switch (t.Type) {
		case STRING:
			switch (feature.dataType()) {
			case DISCRETE:
				try {
					ins.setValue(new DiscreteValue(feature, (String) t.Value));
				}
				catch (Exception ex) {
					throw new Exception("Invalid nominal value. Line:" + line, ex);
				}
				break;

			case STRING:
				ins.setValue(new StringValue(feature, (String) t.Value));
				break;

			case DATE:
				try {
					ins.setValue(new DateValue(feature, (String) t.Value));
				}
				catch (Exception ex) {
					throw new Exception("Invalid date value. Line:" + line, ex);
				}
				break;

			default:
				break;
			}
			break;

		case NUMBER:
			if (feature.dataType() != Feature.DataType.NUMERIC) throw new Exception("Invalid attribute data format. Line:" + line);
			try {
				ins.setValue(new NumericValue(feature, (Double) t.Value));
			}
			catch (Exception ex) {
				throw new Exception("Invalid numeric value. Line:" + line, ex);
			}
			break;

		case QUESTIONMARK:
			ins.setValue(new NullValue(feature));
			break;

		default:
			throw new Exception("Unknow data format. Line:" + line);
		}
	}

	private ArrayList<Token> tokenizeLine(String line) throws Exception {
		ArrayList<Token> tokens = new ArrayList<Token>();
		int i = 0;

		while (i < line.length()) {
			char ch = line.charAt(i++);
			if (Character.isWhitespace(ch)) continue;

			switch (ch) {
			case '@':
				tokens.add(new Token(TokenType.ATSIGN, ch));
				break;
			case '\'':
			case '"':
				//need to back up one char because ' and " are part of the string token
				i = matchString(tokens, line, --i);
				break;
			case '{':
				tokens.add(new Token(TokenType.OPENBRACE, ch));
				break;
			case '}':
				tokens.add(new Token(TokenType.CLOSEBRACE, ch));
				break;
			case ',':
				tokens.add(new Token(TokenType.COMMA, ch));
				break;
			case '+':
			case '-':
			case '.':
				i = matchNumber(tokens, line, --i);
				break;
			case '?':
				tokens.add(new Token(TokenType.QUESTIONMARK, ch));
				break;
			case '%':
				return tokens;

			default:
				if (Character.isLetter(ch)) {
					i = matchString(tokens, line, --i);
				}
				else if (Character.isDigit(ch)) {
					i = matchNumber(tokens, line, --i);
				}
			}
		}

		return tokens;
	}

	private int matchString(ArrayList<Token> tokens, String line, int i) throws Exception {
		final char SINGLE_QUOTE = '\'';
		final char DOUBLE_QUOTE = '"';
		final char BACK_SLASH = '\\';

		char ch = line.charAt(i);
		char quoteSign = '\0';
		boolean inEscape = false;
		boolean quoted = false;

		StringBuilder sb = new StringBuilder();

		// string is quoted
		if (ch == SINGLE_QUOTE || ch == DOUBLE_QUOTE) {
			quoteSign = ch;
			quoted = true;
			i++; // skip the quote character
		}

		while (i < line.length()) {

			ch = line.charAt(i++);

			switch (ch) {

			case SINGLE_QUOTE:
				if (!quoted) throw new Exception("Invalid string format. Line:" + line);

				if (inEscape) {
					sb.append(ch);
					inEscape = false;
					break;
				}

				// double quote appears in single quoted string
				if (quoteSign == DOUBLE_QUOTE) {
					sb.append(ch);
					break;
				}

				// matching single quote => end of the string
				if (quoteSign == SINGLE_QUOTE) {
					if (sb.length() > 0) {
						tokens.add(new Token(TokenType.STRING, sb.toString()));
					}
					return i;
				}

			case DOUBLE_QUOTE:
				if (!quoted) throw new Exception("Invalid string format. Line:" + line);

				if (inEscape) {
					sb.append(ch);
					inEscape = false;
					break;
				}
				// single quote appears in double quoted string
				if (quoteSign == SINGLE_QUOTE) {
					sb.append(ch);
					break;
				}
				// matching double quote => end of the string
				if (quoteSign == DOUBLE_QUOTE) {
					if (sb.length() > 0) {
						tokens.add(new Token(TokenType.STRING, sb.toString()));
					}
					return i;
				}

			case BACK_SLASH:
				if (inEscape) {
					sb.append(ch);
					inEscape = false;
					break;
				}
				inEscape = true;

				break;

			default:
				if (!quoted && isTerminalCharNakedString(ch)) {
					if (sb.length() > 0) {
						tokens.add(new Token(TokenType.STRING, sb.toString()));
					}
					// back up one char because it could be part of the next
					// token
					i--;
					return i;
				}
				else {
					sb.append(ch);
				}
			}
		}
		// make sure we don't miss the last captured string
		if (sb.length() > 0) {
			tokens.add(new Token(TokenType.STRING, sb.toString()));
		}
		return i;
	}

	private boolean isTerminalCharNakedString(char ch) {
		if (Character.isWhitespace(ch)) return true;
		if (ch == ',') return true;
		if (ch == '{') return true;
		if (ch == '}') return true;
		if (ch == '%') return true;
		return false;
	}

	private int matchNumber(ArrayList<Token> tokens, String line, int i) throws Exception {
		StringBuffer sb = new StringBuffer();
		while (i < line.length()) {
			char ch = line.charAt(i);
			// just throw in all the sings and let Double.parseDouble to figure
			// if the string is a valid double
			if (!(Character.isDigit(ch) || ch == '-' || ch == '+' || ch == '.')) break;
			sb.append(ch);
			i++;
		}
		double val;
		try {
			val = Double.parseDouble(sb.toString());
		}
		catch (Exception ex) {
			throw new Exception("Invalid number format. Line:" + line, ex);
		}
		tokens.add(new Token(TokenType.NUMBER, val));
		return i;
	}

}