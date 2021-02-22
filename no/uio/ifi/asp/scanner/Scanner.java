package no.uio.ifi.asp.scanner;

import java.io.*;
import java.util.*;

import no.uio.ifi.asp.main.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class Scanner {
    private LineNumberReader sourceFile = null;
    private String curFileName;
    private ArrayList<Token> curLineTokens = new ArrayList<>();
    private Stack<Integer> indents = new Stack<>();
    private final int TABDIST = 4;


  public Scanner(String fileName) {
	curFileName = fileName;
	indents.push(0);

	try {
	    sourceFile = new LineNumberReader(
			    new InputStreamReader(
				new FileInputStream(fileName),
				"UTF-8"));
	} catch (IOException e) {
	    scannerError("Cannot read " + fileName + "!");
	}
    }


    private void scannerError(String message) {
	String m = "Asp scanner error";
	if (curLineNum() > 0)
	    m += " on line " + curLineNum();
	m += ": " + message;

	Main.error(m);
    }


    public Token curToken() {
	while (curLineTokens.isEmpty()) {
	    readNextLine();
	}
	return curLineTokens.get(0);
    }


    public void readNextToken() {
	if (! curLineTokens.isEmpty())
	    curLineTokens.remove(0);
    }


    private void readNextLine() {
	curLineTokens.clear();

	// Read the next line:
	String line = null;
	try {
	    line = sourceFile.readLine();
	    if (line == null) {
		sourceFile.close(); //
		sourceFile = null;

    for (int n : indents) { //legger til eventuelle siste dedents
      if (n > 0) {
        Token token = new Token(dedentToken,curLineNum());
        curLineTokens.add(token);
        Main.log.noteToken(token);
      }
    }
    Token token = new Token(eofToken,curLineNum()); //EOF-token
    curLineTokens.add(token);
    Main.log.noteToken(token);

	    } else {
		Main.log.noteSourceLine(curLineNum(), line);
	    }
	} catch (IOException e) {
	    sourceFile = null;
	    scannerError("Unspecified I/O error!");
	}
  catch (NullPointerException e) {
    System.out.println("Nullpointerexception paa linje " + curLineNum());
    return;
  }

	//-- Must be changed in part 1:
  // først omforme line til riktig antall blanke, og evt. legge på indents i stacken:
  if (line == null || isComment(line)) { return; }
  line = expandLeadingTabs(line);
  int n = findIndent(line);
  if (n == line.length()) { //hvis linja kun inneholder blanke, så returnerer vi.
    return;
  }

  if (n > indents.peek()) {
    indents.push(n);
    curLineTokens.add(new Token(indentToken,curLineNum()));
  }
  else {
    while (n < indents.peek()) {
      indents.pop();
      curLineTokens.add(new Token(dedentToken,curLineNum()));
    }
    if (n != indents.peek()) {
      scannerError("Indenteringsfeil!");
    }
  }


  int i = 0;
  boolean kommentar = false; //endres til true om hele linja er en kommentar
  while (i < line.length()) { //gå gjennom hvert symbol på linja
    if (line.charAt(i) == ' ') { //hopper over blanke
      i++;
    }

    else if (line.charAt(i) == '#') { //om vi finner en kommentar, brytes loopen.
      if (curLineTokens.isEmpty()) { //om det ikke er noen tokens fra denne linja, vet vi det er en ren kommentarlinje og det skal ikke legges til NEWLINE
        kommentar = true;
      }
      break;
    }

    //navn eller keywords
    else if (isLetterAZ(line.charAt(i)) || line.charAt(i) == '_') { //om vi finner en bokstav eller "_"
      String navn = "";
      navn += line.charAt(i);
      i++;
      while (i < line.length() && (isLetterAZ(line.charAt(i)) || isDigit(line.charAt(i)) || line.charAt(i) == '_')) { //legger til hver bokstav/tall vi finner, helt til et annet tegn dukker opp
        navn += line.charAt(i);
        i++;
      }
      Token token = new Token(nameToken,curLineNum()); //når hele ordet er ferdig, sjekker vi om det er et reservert keyword.
      token.name = navn;
      token.checkResWords(); //om tokenkind var et reservert keyword, skifter denne metoden kind for oss.
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    //integers og floats
    else if (isDigit(line.charAt(i))) { //om vi finner et tall
      String tall = "";
      boolean desimal = false;
      tall += line.charAt(i);
      i++;
      if (tall.equals("0") && i < line.length() && line.charAt(i) != '.') { //om tallet som ble lest inn er 0, er vi ferdige
        Token token = new Token(integerToken,curLineNum());
        token.integerLit = 0;
        curLineTokens.add(token);
      }
      else if (tall.equals("0") && i < line.length() && line.charAt(i) == '.') { //desimal som starter på 0
        tall += '.';
        i++;
        while (i < line.length() && isDigit(line.charAt(i))) {
          tall += line.charAt(i);
          i++;
        }
        Token token = new Token(floatToken,curLineNum());
        token.floatLit = Float.valueOf(tall);
        curLineTokens.add(token);
      }
      else {
        while (i < line.length() && isDigit(line.charAt(i))) { //legger opp til at parseren skal passe på "ulovlige" tall, som feks 0123.
          tall += line.charAt(i);
          i++;
        }
        if (i < line.length() && line.charAt(i) == '.') { //legger opp til at parseren passer på at det faktisk kommer gyldige tall etter desimaltegnet '.', og at tallet ikke begynner eller slutter med '.'
        desimal = true;
        tall += '.';
        i++;
        while (i < line.length() && isDigit(line.charAt(i))) {
          tall += line.charAt(i);
          i++;
        }
      }
      Token token = null;
      if (desimal == false) {
        token = new Token(integerToken,curLineNum());
        token.integerLit = Integer.valueOf(tall);
      }
      else {
        token = new Token(floatToken,curLineNum());
        token.floatLit = Float.valueOf(tall);
      }
      curLineTokens.add(token);
      // Main.log.noteToken(token);
      }
    }

    //stringToken med ""
    else if (line.charAt(i) == '"') { //om vi finner starten på en string literal.
      String streng = "";
      i++;
      while (i < line.length() && line.charAt(i) != '"') { //så lenge vi ikke kommer til slutten av strengen
        streng += line.charAt(i);
        i++;
      }
      if (i >= line.length()) {
        scannerError("String blir ikke terminert paa samme linje!");
      }
      i++;
      Token token = new Token(stringToken,curLineNum());
      token.stringLit = streng;
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    //stringToken med ''
    else if (line.charAt(i) == 39) {  // ' har ASCII-verdi 39
      String streng = "";
      i++;
      while (i < line.length() && line.charAt(i) != 39) {
        streng += line.charAt(i);
        i++;
      }
      if (i >= line.length()) {
        scannerError("String blir ikke terminert paa samme linje!");
      }
      i++;
      Token token = new Token(stringToken,curLineNum());
      token.stringLit = streng;
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    //operatorer
    else if (line.charAt(i) == '*') {
      Token token = new Token(astToken,curLineNum());
      curLineTokens.add(token);
      // Main.log.noteToken(token);
      i++;
    }

    else if (line.charAt(i) == '=') { //behandler singel og dobbelt likhetstegn
      i++;
      Token token = null;
      if (line.charAt(i) == '=') { //hvis det er dobbelt likhetstegn
        token = new Token(doubleEqualToken,curLineNum());
        curLineTokens.add(token);
        // Main.log.noteToken(token);
        i++; //da går vi videre til neste tegn
      }
      else { //singel likhetstegn delimiter
        token = new Token(equalToken,curLineNum());
        curLineTokens.add(token);
        //om det var singeltegn, går vi ikke videre i linja - så tegnet kan behandles et annet sted i den overordnede while-loopen
        // Main.log.noteToken(token);
      }
    }

    else if (line.charAt(i) == '/') { //behandler singel og dobbel slash
      i++;
      Token token = null;
      if (line.charAt(i) == '/') {
        token = new Token(doubleSlashToken,curLineNum());
        curLineTokens.add(token);
        // Main.log.noteToken(token);
        i++;
      }
      else {
        token = new Token(slashToken,curLineNum());
        curLineTokens.add(token);
        // Main.log.noteToken(token);
      }
    }

    else if (line.charAt(i) == '>') { // > og >=
      i++;
      Token token = null;
      if (line.charAt(i) == '=') {
        token = new Token(greaterEqualToken,curLineNum());
        curLineTokens.add(token);
        // Main.log.noteToken(token);
        i++;
      }
      else {
        token = new Token(greaterToken,curLineNum());
        curLineTokens.add(token);
        // Main.log.noteToken(token);
      }
    }

    else if (line.charAt(i) == '<') { // < og <=
      i++;
      Token token = null;
      if (line.charAt(i) == '=') {
        token = new Token(lessEqualToken,curLineNum());
        curLineTokens.add(token);
        // Main.log.noteToken(token);
        i++;
      }
      else {
        token = new Token(lessToken,curLineNum());
        curLineTokens.add(token);
        // Main.log.noteToken(token);
      }
    }

    else if (line.charAt(i) == '-') { // minus
      i++;
      Token token = new Token(minusToken,curLineNum());
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    else if (line.charAt(i) == '!') { // !=
      i++;
      if (line.charAt(i) == '=') {
        Token token = new Token(notEqualToken,curLineNum());
        curLineTokens.add(token);
        // Main.log.noteToken(token);
        i++;
      }
      else {
        scannerError("! er ikke et gyldig tegn. Mente du '!=' ?");
        //itererer ikke fremover her (i++)
      }
    }

    else if (line.charAt(i) == '%') {
      i++;
      Token token = new Token(percentToken,curLineNum());
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    else if (line.charAt(i) == '+') {
      i++;
      Token token = new Token(plusToken,curLineNum());
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    //delimiters
    else if (line.charAt(i) == ':') { // :
      i++;
      Token token = new Token(colonToken,curLineNum());
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    else if (line.charAt(i) == ',') { // ,
      i++;
      Token token = new Token(commaToken,curLineNum());
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    else if (line.charAt(i) == '{') { // {
      i++;
      Token token = new Token(leftBraceToken,curLineNum());
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    else if (line.charAt(i) == '[') { // [
      i++;
      Token token = new Token(leftBracketToken,curLineNum());
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    else if (line.charAt(i) == '(') { // (
      i++;
      Token token = new Token(leftParToken,curLineNum());
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    else if (line.charAt(i) == '}') { // }
      i++;
      Token token = new Token(rightBraceToken,curLineNum());
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    else if (line.charAt(i) == ']') { // ]
      i++;
      Token token = new Token(rightBracketToken,curLineNum());
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    else if (line.charAt(i) == ')') { // )
      i++;
      Token token = new Token(rightParToken,curLineNum());
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    else if (line.charAt(i) == ';') { // ;
      i++;
      Token token = new Token(semicolonToken,curLineNum());
      curLineTokens.add(token);
      // Main.log.noteToken(token);
    }

    //ukjente tegn
    else {
      scannerError("Ukjent tegn: " + line.charAt(i));
    }
  }



	// Terminate line:
  if (!kommentar) { //så lenge linja ikke er en ren kommentar
    curLineTokens.add(new Token(newLineToken,curLineNum()));
  }

	for (Token t: curLineTokens)
	    Main.log.noteToken(t);
  }

  ///// END OF READNEXTLINE/DEL 1

    public boolean isComment(String l) { //hjelpemetode for å sjekke om en linje starter med '#'
      for (int i = 0; i < l.length(); i++) {
        if (l.charAt(i) != ' ') { //når vi finner noe som ikke er en blank, har vi kommet til linjas første tegn
          if (l.charAt(i) == '#') { //om tegnet er en hashtag, er det en ren kommentar
            return true;
          }
          else { return false; }
        }
      }
      return false; //om ikke, inneholder linja i det minste noe kode også
    }

    public int curLineNum() {
	return sourceFile!=null ? sourceFile.getLineNumber() : 0;
    }

    private int findIndent(String s) {
	int indent = 0;

	while (indent<s.length() && s.charAt(indent)==' ') indent++;
	return indent;
    }

    private String expandLeadingTabs(String s) {
	String newS = "";
	for (int i = 0;  i < s.length();  i++) {
	    char c = s.charAt(i);
	    if (c == '\t') {
		do {
		    newS += " ";
		} while (newS.length()%TABDIST > 0);
	    } else if (c == ' ') {
		newS += " ";
	    } else {
		newS += s.substring(i);
		break;
	    }
	}
	return newS;
    }


    private boolean isLetterAZ(char c) {
	return ('A'<=c && c<='Z') || ('a'<=c && c<='z') || (c=='_');
    }


    private boolean isDigit(char c) {
	return '0'<=c && c<='9';
    }


  public boolean isCompOpr() {
  	TokenKind k = curToken().kind;
  	if (k == lessToken) { return true; }
    else if (k == greaterToken) { return true; }
    else if (k == doubleEqualToken) { return true; }
    else if (k == greaterEqualToken) { return true; }
    else if (k == lessEqualToken) { return true; }
    else if (k == notEqualToken) { return true; }
  	return false;
    }


  public boolean isFactorPrefix() {
  	TokenKind k = curToken().kind;
    if (k == plusToken) { return true; }
    else if (k == minusToken) { return true; }
    return false;
  }


  public boolean isFactorOpr() {
  	TokenKind k = curToken().kind;
    if (k == astToken) { return true; }
    else if (k == slashToken) { return true; }
    else if (k == doubleSlashToken) { return true; }
    else if (k == percentToken) { return true; }
    return false;
  }


  public boolean isTermOpr() {
  	TokenKind k = curToken().kind;
    if (k == plusToken) { return true; }
    else if (k == minusToken) { return true; }
    return false;
  }


    public boolean anyEqualToken() {
	for (Token t: curLineTokens) {
	    if (t.kind == equalToken) return true;
	    if (t.kind == semicolonToken) return false;
	}
	return false;
    }

}
