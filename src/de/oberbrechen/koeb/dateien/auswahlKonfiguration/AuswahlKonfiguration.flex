package de.oberbrechen.koeb.dateien.auswahlKonfiguration;

%%

%byaccj
%unicode
%line
%column
%class AuswahlKonfigurationLexer

%{
  private StringBuffer string = new StringBuffer();
  private Object value = null;
  private int token = 0;

  public int getToken() {
    return token;
  }
  
  public int nextToken() {
    try {
      token = yylex();
    } catch (java.io.IOException e) {
      token = -1;
    }
    return token;
  }

  public Object getSemantic() {
    return value;
  }
  
  public int getLine() {
	return yyline;
  }

  public int getColumn() {
	return yycolumn;
  }
%}

%state STRING
%state KOMMENTAR

%%

/* Einzelne Zeichen */
<YYINITIAL> {
"&" | 
"|" | 
"!" | 
"(" | 
")" | 
"," | 
";" | 
":" { return (int) yycharat(0); }

"AUSGABE"                 {return AuswahlKonfigurationTokens.AUSGABE;}
"CHECK"                   {return AuswahlKonfigurationTokens.CHECK;}

"<->"                     {return AuswahlKonfigurationTokens.GDW;}
"->"                      {return AuswahlKonfigurationTokens.IMPLIES;}
"EINDEUTIG" | "eindeutig" {return AuswahlKonfigurationTokens.EINDEUTIG;}
"TRUE" | "true"           {return AuswahlKonfigurationTokens.TRUE;}
"FALSE" | "false"         {return AuswahlKonfigurationTokens.FALSE;}
"Medium.Medientyp"        {return AuswahlKonfigurationTokens.MEDIUM_MEDIENTYP;}
"Medium.Systematik"       {return AuswahlKonfigurationTokens.MEDIUM_SYSTEMATIK;}
"Medium.GehoertZuKeinerSystematik"       {return AuswahlKonfigurationTokens.MEDIUM_KEINE_SYSTEMATIK;}
"Medium.BesitztKeineBeschreibung"        {return AuswahlKonfigurationTokens.MEDIUM_KEINE_BESCHREIBUNG;}
"Medium.istInBestand"     {return AuswahlKonfigurationTokens.MEDIUM_IST_IN_BESTAND;}
"Ausleihe.BenutzerAlter"  {return AuswahlKonfigurationTokens.AUSLEIHE_BENUTZERALTER;}
"Ausleihe.NichtEingestelltesMedium"  {return AuswahlKonfigurationTokens.AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM;}
"Medium.EinstellungsdauerInTagen"    {return AuswahlKonfigurationTokens.MEDIUM_EINSTELLUNGSDAUER_TAGE;}
"Benutzer.BenutzerAlter"             {return AuswahlKonfigurationTokens.BENUTZER_BENUTZERALTER;}
"Benutzer.AnmeldedauerInTagen"       {return AuswahlKonfigurationTokens.BENUTZER_ANMELDEDAUER_TAGE;}
"Benutzer.AnmeldedauerInJahren"      {return AuswahlKonfigurationTokens.BENUTZER_ANMELDEDAUER_JAHRE;}

(0|([1-9][0-9]*))\.[0-9]+ {value = new Double(yytext());
                           return AuswahlKonfigurationTokens.NUM; }
0|([1-9][0-9]*)    	      {value = new Integer(yytext());
                           return AuswahlKonfigurationTokens.INTEGER; }
[a-zA-Z][a-zA-Z0-9_]*     {value = yytext(); 
                           return AuswahlKonfigurationTokens.IDENTIFIER;}

"\""                      {yybegin(STRING); string.setLength(0);}
"#"                       {yybegin(KOMMENTAR);}  

">"                       {return AuswahlKonfigurationTokens.GREATER;}
"<"                       {return AuswahlKonfigurationTokens.LESS;}
"=="                      {return AuswahlKonfigurationTokens.EQ;}
"!="|"<>"                 {return AuswahlKonfigurationTokens.NEQ;}
"<="                      {return AuswahlKonfigurationTokens.LEQ;}
">="                      {return AuswahlKonfigurationTokens.GEQ;}



/* whitespace */
[ \t\n\r]+ { }
}

<STRING> {
  "\""                           { yybegin(YYINITIAL); value = string.toString(); return AuswahlKonfigurationTokens.STRING;}  
  [^\n\r\"\\]+                   { string.append( yytext() );}
  
  /* escape sequences */
  "\\b"                          { string.append( '\b' ); }
  "\\t"                          { string.append( '\t' ); }
  "\\n"                          { string.append( '\n' ); }
  "\\f"                          { string.append( '\f' ); }
  "\\r"                          { string.append( '\r' ); }
  "\\\""                         { string.append( '\"' ); }
  "\\'"                          { string.append( '\'' ); }
  "\\\\"                         { string.append( '\\' ); }
}

<KOMMENTAR> {
  [\n\r]                         { yybegin(YYINITIAL); }  
  [^\n\r]                        { }  
}

/* error fallback */
[^]    { System.err.println("Error: unexpected character '"+yytext()+"'"); return -1; }

