/* The following code was generated by JFlex 1.3.5 on 12/15/07 12:33 AM */

package de.oberbrechen.koeb.dateien.auswahlKonfiguration;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.3.5
 * on 12/15/07 12:33 AM from the specification file
 * <tt>file:/media/MMC-SD/partition1/user/workspace/KoeB/src/de/oberbrechen/koeb/dateien/auswahlKonfiguration/AuswahlKonfiguration.flex</tt>
 */
class AuswahlKonfigurationLexer {

  /** This character denotes the end of file */
  final public static int YYEOF = -1;

  /** initial size of the lookahead buffer */
  final private static int YY_BUFFERSIZE = 16384;

  /** lexical states */
  final public static int STRING = 1;
  final public static int YYINITIAL = 0;
  final public static int KOMMENTAR = 2;

  /** 
   * Translates characters to character classes
   */
  final private static String yycmap_packed = 
    "\11\0\1\66\1\70\2\0\1\70\22\0\1\66\1\2\1\63\1\64"+
    "\2\0\1\1\1\71\1\1\1\1\2\0\1\1\1\15\1\44\1\0"+
    "\1\57\11\60\1\1\1\1\1\14\1\65\1\16\2\0\1\3\1\7"+
    "\1\11\1\21\1\10\1\34\1\6\1\12\1\17\1\56\1\13\1\35"+
    "\1\42\1\20\3\61\1\32\1\5\1\22\1\4\4\61\1\52\1\0"+
    "\1\67\2\0\1\62\1\0\1\37\1\55\1\54\1\26\1\23\1\36"+
    "\1\31\1\50\1\24\1\61\1\47\1\40\1\43\1\25\1\51\1\46"+
    "\1\61\1\33\1\41\1\30\1\27\3\61\1\45\1\53\1\0\1\1"+
    "\uff83\0";

  /** 
   * Translates characters to character classes
   */
  final private static char [] yycmap = yy_unpack_cmap(yycmap_packed);

  /** 
   * Translates a state to a row index in the transition table
   */
  final private static int yy_rowMap [] = { 
        0,    58,   116,   174,   174,   232,   290,   348,   406,   464, 
      522,   580,   638,   696,   754,   812,   870,   928,   986,  1044, 
     1102,  1160,   174,   174,  1218,  1276,  1334,   174,  1392,   174, 
      174,   174,  1450,  1508,  1566,  1624,  1682,  1740,   174,   174, 
      174,  1798,  1856,  1914,  1972,  2030,  2088,  2146,   174,   174, 
      174,   174,   174,   174,   174,   174,   174,  2204,  2262,  2320, 
     2378,  2436,   174,  2494,  2552,  2610,  2668,  2726,  2784,  2146, 
     2842,  2900,  2958,  3016,  3074,   348,  3132,  3190,  3248,  3306, 
     3364,  3422,  3480,  3538,   348,  3596,   348,  3654,  3712,  3770, 
     3828,  3886,  3944,  4002,   348,  4060,  4118,  4176,  4234,  4292, 
     4350,  4408,  4466,  4524,  4582,  4640,  4698,  4756,  4814,  4872, 
     4930,  4988,   348,  5046,  5104,  5162,  5220,  5278,  5336,  5394, 
     5452,  5510,  5568,  5626,  5684,  5742,  5800,  5858,  5916,  5974, 
     6032,  6090,  6148,  6206,  6264,  6322,  6380,  6438,  6496,  6554, 
     6612,  6670,  6728,  6786,  6844,  6902,  6960,  7018,  7076,  7134, 
     7192,  7250,  7308,  7366,  7424,  7482,  7540,  7598,  7656,  7714, 
     7772,  7830,  7888,  7946,  8004,  8062,  8120,  8178,  8236,  8294, 
     8352,  8410,  8468,  8526,  8584,  8642,  8700,  8758,  8816,  8874, 
     8932,  8990,  9048,  9106,  9164,  9222,  9280,  9338,   174,  9396, 
     9454,  9512,  9570,   174,  9628,  9686,  9744,  9802,  9860,  9918, 
     9976, 10034, 10092, 10150, 10208, 10266, 10324, 10382, 10440, 10498, 
    10556, 10614, 10672,   174, 10730, 10788, 10846, 10904, 10962, 11020, 
    11078, 11136, 11194, 11252, 11310, 11368, 11426, 11484,   174, 11542, 
    11600,   174, 11658, 11716, 11774, 11832, 11890, 11948, 12006, 12064, 
    12122, 12180, 12238, 12296, 12354, 12412, 12470, 12528, 12586, 12644, 
    12702, 12760, 12818, 12876, 12934, 12992, 13050, 13108, 13166, 13224, 
    13282, 13340, 13398, 13456, 13514,   174, 13572, 13630, 13688, 13746, 
    13804,   174, 13862, 13920, 13978, 14036, 14094, 14152, 14210, 14268, 
    14326,   174,   174, 14384,   174,   174
  };

  /** 
   * The packed transition table of the DFA (part 0)
   */
  final private static String yy_packed0 = 
    "\1\4\1\5\1\6\1\7\3\10\1\11\1\12\1\13"+
    "\2\10\1\14\1\15\1\16\3\10\1\17\1\20\4\10"+
    "\1\21\3\10\1\22\1\10\1\23\3\10\1\24\1\10"+
    "\1\4\12\10\1\25\1\26\1\10\1\4\1\27\1\30"+
    "\1\31\1\32\1\4\1\32\1\4\63\33\1\34\3\33"+
    "\1\35\1\4\1\33\70\36\1\37\1\36\157\0\1\40"+
    "\7\0\1\10\1\41\7\10\3\0\10\10\1\42\14\10"+
    "\1\0\16\10\12\0\11\10\3\0\25\10\1\0\16\10"+
    "\12\0\11\10\3\0\4\10\1\43\20\10\1\0\16\10"+
    "\12\0\11\10\3\0\1\44\24\10\1\0\16\10\12\0"+
    "\7\10\1\45\1\10\3\0\25\10\1\0\16\10\24\0"+
    "\1\46\1\40\46\0\1\47\22\0\1\50\140\0\1\51"+
    "\7\0\11\10\3\0\13\10\1\52\11\10\1\0\16\10"+
    "\12\0\11\10\3\0\5\10\1\53\17\10\1\0\16\10"+
    "\12\0\11\10\3\0\14\10\1\54\10\10\1\0\16\10"+
    "\12\0\1\55\10\10\3\0\25\10\1\0\16\10\12\0"+
    "\11\10\3\0\20\10\1\56\4\10\1\0\16\10\12\0"+
    "\11\10\3\0\4\10\1\57\20\10\1\0\16\10\53\0"+
    "\1\60\71\0\1\60\12\0\2\26\76\0\1\61\72\0"+
    "\1\32\1\0\1\32\1\0\63\33\1\0\3\33\2\0"+
    "\1\33\25\0\1\62\2\0\1\63\2\0\1\64\2\0"+
    "\1\65\16\0\1\66\5\0\1\67\3\0\1\70\1\0"+
    "\1\71\3\0\2\10\1\72\6\10\3\0\25\10\1\0"+
    "\16\10\12\0\11\10\3\0\22\10\1\73\2\10\1\0"+
    "\16\10\12\0\11\10\3\0\6\10\1\74\16\10\1\0"+
    "\16\10\12\0\11\10\3\0\1\10\1\75\23\10\1\0"+
    "\16\10\12\0\5\10\1\76\3\10\3\0\25\10\1\0"+
    "\16\10\25\0\1\77\56\0\1\10\1\100\7\10\3\0"+
    "\25\10\1\0\16\10\12\0\11\10\3\0\6\10\1\101"+
    "\16\10\1\0\16\10\12\0\11\10\3\0\10\10\1\102"+
    "\14\10\1\0\16\10\12\0\11\10\3\0\16\10\1\103"+
    "\6\10\1\0\16\10\12\0\11\10\3\0\21\10\1\104"+
    "\3\10\1\0\16\10\12\0\11\10\3\0\7\10\1\105"+
    "\15\10\1\0\16\10\66\0\2\106\14\0\3\10\1\107"+
    "\5\10\3\0\25\10\1\0\16\10\12\0\11\10\3\0"+
    "\21\10\1\110\3\10\1\0\16\10\12\0\11\10\3\0"+
    "\10\10\1\111\14\10\1\0\16\10\12\0\11\10\3\0"+
    "\2\10\1\112\22\10\1\0\16\10\12\0\6\10\1\113"+
    "\2\10\3\0\25\10\1\0\16\10\12\0\5\10\1\114"+
    "\3\10\3\0\25\10\1\0\16\10\12\0\11\10\3\0"+
    "\7\10\1\115\15\10\1\0\16\10\12\0\11\10\3\0"+
    "\4\10\1\114\20\10\1\0\16\10\12\0\2\10\1\116"+
    "\6\10\3\0\25\10\1\0\16\10\12\0\11\10\3\0"+
    "\22\10\1\117\2\10\1\0\16\10\12\0\11\10\3\0"+
    "\5\10\1\120\17\10\1\0\16\10\12\0\1\121\10\10"+
    "\3\0\25\10\1\0\16\10\12\0\11\10\3\0\4\10"+
    "\1\122\20\10\1\0\16\10\12\0\11\10\3\0\11\10"+
    "\1\123\13\10\1\0\16\10\12\0\5\10\1\124\3\10"+
    "\3\0\25\10\1\0\16\10\12\0\10\10\1\125\3\0"+
    "\25\10\1\0\16\10\12\0\11\10\3\0\4\10\1\126"+
    "\20\10\1\0\16\10\12\0\5\10\1\127\3\10\3\0"+
    "\25\10\1\0\16\10\12\0\11\10\3\0\4\10\1\127"+
    "\20\10\1\0\16\10\12\0\11\10\3\0\10\10\1\130"+
    "\14\10\1\0\16\10\12\0\4\10\1\131\4\10\3\0"+
    "\25\10\1\0\16\10\12\0\11\10\3\0\5\10\1\132"+
    "\17\10\1\0\16\10\12\0\11\10\3\0\25\10\1\0"+
    "\6\10\1\133\7\10\12\0\1\10\1\134\7\10\3\0"+
    "\25\10\1\0\16\10\12\0\11\10\3\0\10\10\1\135"+
    "\14\10\1\0\16\10\12\0\11\10\3\0\24\10\1\136"+
    "\1\0\16\10\12\0\5\10\1\137\3\10\3\0\25\10"+
    "\1\0\16\10\12\0\11\10\3\0\25\10\1\0\3\10"+
    "\1\140\12\10\12\0\11\10\3\0\4\10\1\141\20\10"+
    "\1\0\16\10\12\0\11\10\3\0\3\10\1\142\21\10"+
    "\1\0\16\10\12\0\11\10\3\0\11\10\1\143\13\10"+
    "\1\0\16\10\12\0\11\10\3\0\25\10\1\144\16\10"+
    "\12\0\11\10\3\0\4\10\1\145\20\10\1\0\16\10"+
    "\12\0\11\10\3\0\14\10\1\146\10\10\1\0\16\10"+
    "\12\0\11\10\3\0\1\147\24\10\1\0\16\10\12\0"+
    "\11\10\3\0\5\10\1\150\17\10\1\0\16\10\14\0"+
    "\1\151\1\152\1\153\1\154\13\0\1\155\15\0\1\156"+
    "\32\0\11\10\3\0\25\10\1\157\16\10\12\0\11\10"+
    "\3\0\25\10\1\160\16\10\12\0\3\10\1\161\5\10"+
    "\3\0\25\10\1\0\16\10\12\0\11\10\3\0\12\10"+
    "\1\161\12\10\1\0\16\10\54\0\1\162\47\0\1\163"+
    "\71\0\1\164\72\0\1\165\106\0\1\166\53\0\1\167"+
    "\55\0\1\170\10\0\1\171\54\0\1\172\3\0\1\173"+
    "\123\0\1\174\100\0\1\175\62\0\1\176\55\0\1\177"+
    "\74\0\1\200\67\0\1\201\66\0\1\202\72\0\1\203"+
    "\72\0\1\204\67\0\1\205\76\0\1\206\112\0\1\207"+
    "\44\0\1\210\106\0\1\211\47\0\1\212\76\0\1\213"+
    "\72\0\1\214\120\0\1\215\60\0\1\216\53\0\1\217"+
    "\67\0\1\220\71\0\1\221\76\0\1\222\71\0\1\223"+
    "\66\0\1\224\67\0\1\225\75\0\1\226\112\0\1\227"+
    "\44\0\1\230\75\0\1\231\105\0\1\232\61\0\1\233"+
    "\111\0\1\234\41\0\1\235\55\0\1\236\107\0\1\237"+
    "\74\0\1\240\71\0\1\241\101\0\1\242\61\0\1\243"+
    "\100\0\1\244\62\0\1\245\71\0\1\246\101\0\1\247"+
    "\54\0\1\250\76\0\1\251\114\0\1\252\26\0\1\253"+
    "\107\0\1\254\116\0\1\255\46\0\1\256\113\0\1\257"+
    "\32\0\1\260\116\0\1\261\72\0\1\262\75\0\1\263"+
    "\47\0\1\264\72\0\1\265\70\0\1\266\71\0\1\267"+
    "\72\0\1\270\74\0\1\271\65\0\1\272\75\0\1\273"+
    "\72\0\1\274\107\0\1\275\56\0\1\276\63\0\1\277"+
    "\72\0\1\300\76\0\1\301\105\0\1\302\35\0\1\303"+
    "\102\0\1\304\72\0\1\305\103\0\1\306\35\0\1\307"+
    "\117\0\1\310\77\0\1\311\35\0\1\312\111\0\1\313"+
    "\73\0\1\314\75\0\1\315\65\0\1\316\104\0\1\317"+
    "\54\0\1\320\75\0\1\321\102\0\1\322\55\0\1\323"+
    "\70\0\1\324\107\0\1\325\56\0\1\326\73\0\1\327"+
    "\102\0\1\330\53\0\1\331\76\0\1\332\66\0\1\333"+
    "\53\0\1\334\110\0\1\335\66\0\1\336\76\0\1\337"+
    "\74\0\1\340\61\0\1\341\71\0\1\342\71\0\1\343"+
    "\105\0\1\344\65\0\1\345\61\0\1\346\65\0\1\347"+
    "\105\0\1\350\71\0\1\351\77\0\1\352\57\0\1\353"+
    "\102\0\1\354\56\0\1\355\51\0\1\356\140\0\1\357"+
    "\40\0\1\360\106\0\1\361\53\0\1\362\33\0\1\363"+
    "\60\0\1\364\74\0\1\365\54\0\1\366\66\0\1\367"+
    "\100\0\1\370\71\0\1\371\73\0\1\372\63\0\1\373"+
    "\55\0\1\374\75\0\1\375\77\0\1\376\110\0\1\377"+
    "\51\0\1\u0100\64\0\1\u0101\73\0\1\u0102\105\0\1\u0103"+
    "\53\0\1\u0104\101\0\1\u0105\61\0\1\u0106\72\0\1\u0107"+
    "\67\0\1\u0108\111\0\1\u0109\54\0\1\u010a\67\0\1\u010b"+
    "\111\0\1\u010c\103\0\1\u010d\53\0\1\u010e\55\0\1\u010f"+
    "\73\0\1\u0110\103\0\1\u0111\61\0\1\u0112\73\0\1\u0113"+
    "\66\0\1\u0114\73\0\1\u0115\66\0\1\u0116\67\0\1\u0117"+
    "\72\0\1\u0118\71\0\1\u0119\76\0\1\u011a\65\0\1\u011b"+
    "\73\0\1\u011c\111\0\1\u011d\65\0\1\u011e\26\0";

  /** 
   * The transition table of the DFA
   */
  final private static int yytrans [] = yy_unpack();


  /* error codes */
  final private static int YY_UNKNOWN_ERROR = 0;
  final private static int YY_ILLEGAL_STATE = 1;
  final private static int YY_NO_MATCH = 2;
  final private static int YY_PUSHBACK_2BIG = 3;

  /* error messages for the codes above */
  final private static String YY_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Internal error: unknown state",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * YY_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private final static byte YY_ATTRIBUTE[] = {
     0,  0,  0,  9,  9,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  9,  9,  1,  1,  1,  9,  1,  9,  9,  9, 
     1,  1,  1,  1,  1,  0,  9,  9,  9,  1,  1,  1,  1,  1,  1,  0, 
     9,  9,  9,  9,  9,  9,  9,  9,  9,  1,  1,  1,  1,  1,  9,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  0,  1,  1,  1,  1,  0,  0,  0,  0,  0,  0,  0,  0, 
     1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 
     0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 
     0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 
     0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 
     0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  9,  0,  0,  0, 
     0,  9,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 
     0,  0,  0,  0,  0,  9,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 
     0,  0,  0,  0,  9,  0,  0,  9,  0,  0,  0,  0,  0,  0,  0,  0, 
     0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 
     0,  0,  0,  0,  0,  0,  0,  0,  0,  9,  0,  0,  0,  0,  0,  9, 
     0,  0,  0,  0,  0,  0,  0,  0,  0,  9,  9,  0,  9,  9
  };

  /** the input device */
  private java.io.Reader yy_reader;

  /** the current state of the DFA */
  private int yy_state;

  /** the current lexical state */
  private int yy_lexical_state = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char yy_buffer[] = new char[YY_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int yy_markedPos;

  /** the textposition at the last state to be included in yytext */
  private int yy_pushbackPos;

  /** the current text position in the buffer */
  private int yy_currentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int yy_startRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int yy_endRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn; 

  /** 
   * yy_atBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean yy_atBOL = true;

  /** yy_atEOF == true <=> the scanner is at the EOF */
  private boolean yy_atEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean yy_eof_done;

  /* user code: */
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


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  AuswahlKonfigurationLexer(java.io.Reader in) {
    this.yy_reader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  AuswahlKonfigurationLexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the split, compressed DFA transition table.
   *
   * @return the unpacked transition table
   */
  private static int [] yy_unpack() {
    int [] trans = new int[14442];
    int offset = 0;
    offset = yy_unpack(yy_packed0, offset, trans);
    return trans;
  }

  /** 
   * Unpacks the compressed DFA transition table.
   *
   * @param packed   the packed transition table
   * @return         the index of the last entry
   */
  private static int yy_unpack(String packed, int offset, int [] trans) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do trans[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] yy_unpack_cmap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 162) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   IOException  if any I/O-Error occurs
   */
  private boolean yy_refill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (yy_startRead > 0) {
      System.arraycopy(yy_buffer, yy_startRead, 
                       yy_buffer, 0, 
                       yy_endRead-yy_startRead);

      /* translate stored positions */
      yy_endRead-= yy_startRead;
      yy_currentPos-= yy_startRead;
      yy_markedPos-= yy_startRead;
      yy_pushbackPos-= yy_startRead;
      yy_startRead = 0;
    }

    /* is the buffer big enough? */
    if (yy_currentPos >= yy_buffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[yy_currentPos*2];
      System.arraycopy(yy_buffer, 0, newBuffer, 0, yy_buffer.length);
      yy_buffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = yy_reader.read(yy_buffer, yy_endRead, 
                                            yy_buffer.length-yy_endRead);

    if (numRead < 0) {
      return true;
    }
    else {
      yy_endRead+= numRead;  
      return false;
    }
  }


  /**
   * Closes the input stream.
   */
  final public void yyclose() throws java.io.IOException {
    yy_atEOF = true;            /* indicate end of file */
    yy_endRead = yy_startRead;  /* invalidate buffer    */

    if (yy_reader != null)
      yy_reader.close();
  }


  /**
   * Closes the current stream, and resets the
   * scanner to read from a new input stream.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>YY_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  final public void yyreset(java.io.Reader reader) throws java.io.IOException {
    yyclose();
    yy_reader = reader;
    yy_atBOL  = true;
    yy_atEOF  = false;
    yy_endRead = yy_startRead = 0;
    yy_currentPos = yy_markedPos = yy_pushbackPos = 0;
    yyline = yychar = yycolumn = 0;
    yy_lexical_state = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  final public int yystate() {
    return yy_lexical_state;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  final public void yybegin(int newState) {
    yy_lexical_state = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  final public String yytext() {
    return new String( yy_buffer, yy_startRead, yy_markedPos-yy_startRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  final public char yycharat(int pos) {
    return yy_buffer[yy_startRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  final public int yylength() {
    return yy_markedPos-yy_startRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void yy_ScanError(int errorCode) {
    String message;
    try {
      message = YY_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = YY_ERROR_MSG[YY_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  private void yypushback(int number)  {
    if ( number > yylength() )
      yy_ScanError(YY_PUSHBACK_2BIG);

    yy_markedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void yy_do_eof() throws java.io.IOException {
    if (!yy_eof_done) {
      yy_eof_done = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   IOException  if any I/O-Error occurs
   */
  public int yylex() throws java.io.IOException {
    int yy_input;
    int yy_action;

    // cached fields:
    int yy_currentPos_l;
    int yy_startRead_l;
    int yy_markedPos_l;
    int yy_endRead_l = yy_endRead;
    char [] yy_buffer_l = yy_buffer;
    char [] yycmap_l = yycmap;

    int [] yytrans_l = yytrans;
    int [] yy_rowMap_l = yy_rowMap;
    byte [] yy_attr_l = YY_ATTRIBUTE;

    while (true) {
      yy_markedPos_l = yy_markedPos;

      boolean yy_r = false;
      for (yy_currentPos_l = yy_startRead; yy_currentPos_l < yy_markedPos_l;
                                                             yy_currentPos_l++) {
        switch (yy_buffer_l[yy_currentPos_l]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          yy_r = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          yy_r = true;
          break;
        case '\n':
          if (yy_r)
            yy_r = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          yy_r = false;
          yycolumn++;
        }
      }

      if (yy_r) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean yy_peek;
        if (yy_markedPos_l < yy_endRead_l)
          yy_peek = yy_buffer_l[yy_markedPos_l] == '\n';
        else if (yy_atEOF)
          yy_peek = false;
        else {
          boolean eof = yy_refill();
          yy_markedPos_l = yy_markedPos;
          yy_buffer_l = yy_buffer;
          if (eof) 
            yy_peek = false;
          else 
            yy_peek = yy_buffer_l[yy_markedPos_l] == '\n';
        }
        if (yy_peek) yyline--;
      }
      yy_action = -1;

      yy_startRead_l = yy_currentPos_l = yy_currentPos = 
                       yy_startRead = yy_markedPos_l;

      yy_state = yy_lexical_state;


      yy_forAction: {
        while (true) {

          if (yy_currentPos_l < yy_endRead_l)
            yy_input = yy_buffer_l[yy_currentPos_l++];
          else if (yy_atEOF) {
            yy_input = YYEOF;
            break yy_forAction;
          }
          else {
            // store back cached positions
            yy_currentPos  = yy_currentPos_l;
            yy_markedPos   = yy_markedPos_l;
            boolean eof = yy_refill();
            // get translated positions and possibly new buffer
            yy_currentPos_l  = yy_currentPos;
            yy_markedPos_l   = yy_markedPos;
            yy_buffer_l      = yy_buffer;
            yy_endRead_l     = yy_endRead;
            if (eof) {
              yy_input = YYEOF;
              break yy_forAction;
            }
            else {
              yy_input = yy_buffer_l[yy_currentPos_l++];
            }
          }
          int yy_next = yytrans_l[ yy_rowMap_l[yy_state] + yycmap_l[yy_input] ];
          if (yy_next == -1) break yy_forAction;
          yy_state = yy_next;

          int yy_attributes = yy_attr_l[yy_state];
          if ( (yy_attributes & 1) == 1 ) {
            yy_action = yy_state; 
            yy_markedPos_l = yy_currentPos_l; 
            if ( (yy_attributes & 8) == 8 ) break yy_forAction;
          }

        }
      }

      // store back cached position
      yy_markedPos = yy_markedPos_l;

      switch (yy_action) {

        case 27: 
          {  yybegin(YYINITIAL); value = string.toString(); return AuswahlKonfigurationTokens.STRING; }
        case 287: break;
        case 29: 
          {   }
        case 288: break;
        case 25: 
          {   }
        case 289: break;
        case 4: 
        case 5: 
          {  return (int) yycharat(0);  }
        case 290: break;
        case 6: 
        case 7: 
        case 8: 
        case 9: 
        case 10: 
        case 14: 
        case 15: 
        case 16: 
        case 17: 
        case 18: 
        case 19: 
        case 32: 
        case 33: 
        case 34: 
        case 35: 
        case 36: 
        case 41: 
        case 42: 
        case 43: 
        case 44: 
        case 45: 
        case 46: 
        case 57: 
        case 58: 
        case 59: 
        case 60: 
        case 61: 
        case 63: 
        case 64: 
        case 65: 
        case 66: 
        case 67: 
        case 68: 
        case 70: 
        case 71: 
        case 72: 
        case 73: 
        case 74: 
        case 76: 
        case 77: 
        case 78: 
        case 79: 
        case 80: 
        case 81: 
        case 82: 
        case 83: 
        case 85: 
        case 87: 
        case 88: 
        case 89: 
        case 90: 
        case 91: 
        case 92: 
        case 93: 
        case 95: 
        case 96: 
        case 97: 
        case 98: 
        case 100: 
        case 101: 
        case 102: 
        case 103: 
          { value = yytext(); 
                           return AuswahlKonfigurationTokens.IDENTIFIER; }
        case 291: break;
        case 20: 
        case 21: 
          { value = new Integer(yytext());
                           return AuswahlKonfigurationTokens.INTEGER;  }
        case 292: break;
        case 69: 
          { value = new Double(yytext());
                           return AuswahlKonfigurationTokens.NUM;  }
        case 293: break;
        case 49: 
          {  string.append( '\n' );  }
        case 294: break;
        case 50: 
          {  string.append( '\t' );  }
        case 295: break;
        case 51: 
          {  string.append( '\r' );  }
        case 296: break;
        case 52: 
          {  string.append( '\f' );  }
        case 297: break;
        case 53: 
          {  string.append( '\b' );  }
        case 298: break;
        case 54: 
          {  string.append( '\"' );  }
        case 299: break;
        case 55: 
          {  string.append( '\\' );  }
        case 300: break;
        case 56: 
          {  string.append( '\'' );  }
        case 301: break;
        case 22: 
          { yybegin(STRING); string.setLength(0); }
        case 302: break;
        case 84: 
          { return AuswahlKonfigurationTokens.CHECK; }
        case 303: break;
        case 86: 
          { return AuswahlKonfigurationTokens.FALSE; }
        case 304: break;
        case 282: 
          { return AuswahlKonfigurationTokens.MEDIUM_EINSTELLUNGSDAUER_TAGE; }
        case 305: break;
        case 39: 
          { return AuswahlKonfigurationTokens.IMPLIES; }
        case 306: break;
        case 13: 
          { return AuswahlKonfigurationTokens.GREATER; }
        case 307: break;
        case 94: 
          { return AuswahlKonfigurationTokens.AUSGABE; }
        case 308: break;
        case 26: 
          {  string.append( yytext() ); }
        case 309: break;
        case 3: 
        case 12: 
        case 24: 
        case 28: 
          {  System.err.println("Error: unexpected character '"+yytext()+"'"); return -1;  }
        case 310: break;
        case 231: 
          { return AuswahlKonfigurationTokens.BENUTZER_BENUTZERALTER; }
        case 311: break;
        case 228: 
          { return AuswahlKonfigurationTokens.AUSLEIHE_BENUTZERALTER; }
        case 312: break;
        case 213: 
          { return AuswahlKonfigurationTokens.MEDIUM_IST_IN_BESTAND; }
        case 313: break;
        case 188: 
          { return AuswahlKonfigurationTokens.MEDIUM_MEDIENTYP; }
        case 314: break;
        case 271: 
          { return AuswahlKonfigurationTokens.BENUTZER_ANMELDEDAUER_JAHRE; }
        case 315: break;
        case 193: 
          { return AuswahlKonfigurationTokens.MEDIUM_SYSTEMATIK; }
        case 316: break;
        case 284: 
          { return AuswahlKonfigurationTokens.MEDIUM_KEINE_SYSTEMATIK; }
        case 317: break;
        case 48: 
          { return AuswahlKonfigurationTokens.EQ; }
        case 318: break;
        case 40: 
          { return AuswahlKonfigurationTokens.GEQ; }
        case 319: break;
        case 38: 
          { return AuswahlKonfigurationTokens.LEQ; }
        case 320: break;
        case 31: 
          { return AuswahlKonfigurationTokens.NEQ; }
        case 321: break;
        case 11: 
          { return AuswahlKonfigurationTokens.LESS; }
        case 322: break;
        case 62: 
          { return AuswahlKonfigurationTokens.GDW; }
        case 323: break;
        case 75: 
          { return AuswahlKonfigurationTokens.TRUE; }
        case 324: break;
        case 30: 
          {  yybegin(YYINITIAL);  }
        case 325: break;
        case 281: 
          { return AuswahlKonfigurationTokens.MEDIUM_KEINE_BESCHREIBUNG; }
        case 326: break;
        case 265: 
          { return AuswahlKonfigurationTokens.BENUTZER_ANMELDEDAUER_TAGE; }
        case 327: break;
        case 112: 
          { return AuswahlKonfigurationTokens.EINDEUTIG; }
        case 328: break;
        case 23: 
          { yybegin(KOMMENTAR); }
        case 329: break;
        case 285: 
          { return AuswahlKonfigurationTokens.AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM; }
        case 330: break;
        default: 
          if (yy_input == YYEOF && yy_startRead == yy_currentPos) {
            yy_atEOF = true;
            yy_do_eof();
              { return 0; }
          } 
          else {
            yy_ScanError(YY_NO_MATCH);
          }
      }
    }
  }


}
