// Output created by jacc on Sat Dec 15 00:33:53 GMT 2007

package de.oberbrechen.koeb.dateien.auswahlKonfiguration;

import java.util.*;

class AuswahlKonfigurationParser implements AuswahlKonfigurationTokens {
    private int yyss = 100;
    private int yytok;
    private int yysp = 0;
    private int[] yyst;
    private Object[] yysv;
    private Object yyrv;

    public boolean parse() {
        int yyn = 0;
        yysp = 0;
        yyst = new int[yyss];
        yysv = new Object[yyss];
        yytok = (lexer.getToken()
                 );
    loop:
        for (;;) {
            switch (yyn) {
                case 0:
                    yyst[yysp] = 0;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 106:
                    switch (yytok) {
                        case AUSGABE:
                            yyn = 4;
                            continue;
                        case CHECK:
                            yyn = 5;
                            continue;
                        case IDENTIFIER:
                            yyn = 6;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 1:
                    yyst[yysp] = 1;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 107:
                    switch (yytok) {
                        case ENDINPUT:
                            yyn = 212;
                            continue;
                        case AUSGABE:
                            yyn = 4;
                            continue;
                        case CHECK:
                            yyn = 5;
                            continue;
                        case IDENTIFIER:
                            yyn = 6;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 2:
                    yyst[yysp] = 2;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 108:
                    switch (yytok) {
                        case IDENTIFIER:
                        case CHECK:
                        case ENDINPUT:
                        case AUSGABE:
                            yyn = yyr2();
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 3:
                    yyst[yysp] = 3;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 109:
                    switch (yytok) {
                        case ';':
                            yyn = 8;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 4:
                    yyst[yysp] = 4;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 110:
                    switch (yytok) {
                        case '(':
                            yyn = 9;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 5:
                    yyst[yysp] = 5;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 111:
                    switch (yytok) {
                        case '(':
                            yyn = 10;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 6:
                    yyst[yysp] = 6;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 112:
                    switch (yytok) {
                        case ':':
                            yyn = 11;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 7:
                    yyst[yysp] = 7;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 113:
                    switch (yytok) {
                        case IDENTIFIER:
                        case CHECK:
                        case ENDINPUT:
                        case AUSGABE:
                            yyn = yyr1();
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 8:
                    yyst[yysp] = 8;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 114:
                    switch (yytok) {
                        case IDENTIFIER:
                        case CHECK:
                        case ENDINPUT:
                        case AUSGABE:
                            yyn = yyr3();
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 9:
                    yyst[yysp] = 9;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 115:
                    switch (yytok) {
                        case STRING:
                            yyn = 12;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 10:
                    yyst[yysp] = 10;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 116:
                    switch (yytok) {
                        case STRING:
                            yyn = 13;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 11:
                    yyst[yysp] = 11;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 117:
                    yyn = yys11();
                    continue;

                case 12:
                    yyst[yysp] = 12;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 118:
                    switch (yytok) {
                        case ')':
                            yyn = 36;
                            continue;
                        case ',':
                            yyn = 37;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 13:
                    yyst[yysp] = 13;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 119:
                    switch (yytok) {
                        case ')':
                            yyn = 38;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 14:
                    yyst[yysp] = 14;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 120:
                    switch (yytok) {
                        case GDW:
                            yyn = 39;
                            continue;
                        case IMPLIES:
                            yyn = 40;
                            continue;
                        case '&':
                            yyn = 41;
                            continue;
                        case '|':
                            yyn = 42;
                            continue;
                        case ';':
                            yyn = yyr9();
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 15:
                    yyst[yysp] = 15;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 121:
                    yyn = yys15();
                    continue;

                case 16:
                    yyst[yysp] = 16;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 122:
                    yyn = yys16();
                    continue;

                case 17:
                    yyst[yysp] = 17;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 123:
                    switch (yytok) {
                        case '(':
                            yyn = 49;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 18:
                    yyst[yysp] = 18;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 124:
                    switch (yytok) {
                        case '(':
                            yyn = 50;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 19:
                    yyst[yysp] = 19;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 125:
                    switch (yytok) {
                        case '(':
                            yyn = 51;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 20:
                    yyst[yysp] = 20;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 126:
                    switch (yytok) {
                        case '(':
                            yyn = 52;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 21:
                    yyst[yysp] = 21;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 127:
                    switch (yytok) {
                        case '(':
                            yyn = 53;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 22:
                    yyst[yysp] = 22;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 128:
                    switch (yytok) {
                        case '(':
                            yyn = 54;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 23:
                    yyst[yysp] = 23;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 129:
                    yyn = yys23();
                    continue;

                case 24:
                    yyst[yysp] = 24;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 130:
                    yyn = yys24();
                    continue;

                case 25:
                    yyst[yysp] = 25;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 131:
                    yyn = yys25();
                    continue;

                case 26:
                    yyst[yysp] = 26;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 132:
                    switch (yytok) {
                        case '(':
                            yyn = 55;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 27:
                    yyst[yysp] = 27;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 133:
                    switch (yytok) {
                        case '(':
                            yyn = 56;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 28:
                    yyst[yysp] = 28;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 134:
                    switch (yytok) {
                        case '(':
                            yyn = 57;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 29:
                    yyst[yysp] = 29;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 135:
                    switch (yytok) {
                        case '(':
                            yyn = 58;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 30:
                    yyst[yysp] = 30;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 136:
                    switch (yytok) {
                        case '(':
                            yyn = 59;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 31:
                    yyst[yysp] = 31;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 137:
                    switch (yytok) {
                        case '(':
                            yyn = 60;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 32:
                    yyst[yysp] = 32;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 138:
                    yyn = yys32();
                    continue;

                case 33:
                    yyst[yysp] = 33;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 139:
                    yyn = yys33();
                    continue;

                case 34:
                    yyst[yysp] = 34;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 140:
                    yyn = yys34();
                    continue;

                case 35:
                    yyst[yysp] = 35;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 141:
                    yyn = yys35();
                    continue;

                case 36:
                    yyst[yysp] = 36;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 142:
                    switch (yytok) {
                        case IDENTIFIER:
                            yyn = 6;
                            continue;
                        case ':':
                            yyn = 65;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 37:
                    yyst[yysp] = 37;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 143:
                    switch (yytok) {
                        case INTEGER:
                            yyn = 66;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 38:
                    yyst[yysp] = 38;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 144:
                    switch (yytok) {
                        case IDENTIFIER:
                            yyn = 6;
                            continue;
                        case ':':
                            yyn = 65;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 39:
                    yyst[yysp] = 39;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 145:
                    yyn = yys39();
                    continue;

                case 40:
                    yyst[yysp] = 40;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 146:
                    yyn = yys40();
                    continue;

                case 41:
                    yyst[yysp] = 41;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 147:
                    yyn = yys41();
                    continue;

                case 42:
                    yyst[yysp] = 42;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 148:
                    yyn = yys42();
                    continue;

                case 43:
                    yyst[yysp] = 43;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 149:
                    yyn = yys43();
                    continue;

                case 44:
                    yyst[yysp] = 44;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 150:
                    yyn = yys44();
                    continue;

                case 45:
                    yyst[yysp] = 45;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 151:
                    yyn = yys45();
                    continue;

                case 46:
                    yyst[yysp] = 46;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 152:
                    yyn = yys46();
                    continue;

                case 47:
                    yyst[yysp] = 47;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 153:
                    yyn = yys47();
                    continue;

                case 48:
                    yyst[yysp] = 48;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 154:
                    yyn = yys48();
                    continue;

                case 49:
                    yyst[yysp] = 49;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 155:
                    switch (yytok) {
                        case ')':
                            yyn = 78;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 50:
                    yyst[yysp] = 50;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 156:
                    switch (yytok) {
                        case ')':
                            yyn = 79;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 51:
                    yyst[yysp] = 51;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 157:
                    switch (yytok) {
                        case ')':
                            yyn = 80;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 52:
                    yyst[yysp] = 52;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 158:
                    switch (yytok) {
                        case ')':
                            yyn = 81;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 53:
                    yyst[yysp] = 53;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 159:
                    switch (yytok) {
                        case ')':
                            yyn = 82;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 54:
                    yyst[yysp] = 54;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 160:
                    yyn = yys54();
                    continue;

                case 55:
                    yyst[yysp] = 55;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 161:
                    switch (yytok) {
                        case ')':
                            yyn = 85;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 56:
                    yyst[yysp] = 56;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 162:
                    switch (yytok) {
                        case ')':
                            yyn = 86;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 57:
                    yyst[yysp] = 57;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 163:
                    switch (yytok) {
                        case ')':
                            yyn = 87;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 58:
                    yyst[yysp] = 58;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 164:
                    switch (yytok) {
                        case ')':
                            yyn = 88;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 59:
                    yyst[yysp] = 59;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 165:
                    switch (yytok) {
                        case STRING:
                            yyn = 90;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 60:
                    yyst[yysp] = 60;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 166:
                    switch (yytok) {
                        case STRING:
                            yyn = 90;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 61:
                    yyst[yysp] = 61;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 167:
                    yyn = yys61();
                    continue;

                case 62:
                    yyst[yysp] = 62;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 168:
                    switch (yytok) {
                        case GDW:
                            yyn = 39;
                            continue;
                        case IMPLIES:
                            yyn = 40;
                            continue;
                        case '&':
                            yyn = 41;
                            continue;
                        case '|':
                            yyn = 42;
                            continue;
                        case ')':
                            yyn = 92;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 63:
                    yyst[yysp] = 63;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 169:
                    switch (yytok) {
                        case ';':
                            yyn = 93;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 64:
                    yyst[yysp] = 64;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 170:
                    switch (yytok) {
                        case ';':
                            yyn = yyr7();
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 65:
                    yyst[yysp] = 65;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 171:
                    yyn = yys65();
                    continue;

                case 66:
                    yyst[yysp] = 66;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 172:
                    switch (yytok) {
                        case ')':
                            yyn = 95;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 67:
                    yyst[yysp] = 67;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 173:
                    switch (yytok) {
                        case ';':
                            yyn = 96;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 68:
                    yyst[yysp] = 68;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 174:
                    yyn = yys68();
                    continue;

                case 69:
                    yyst[yysp] = 69;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 175:
                    yyn = yys69();
                    continue;

                case 70:
                    yyst[yysp] = 70;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 176:
                    yyn = yys70();
                    continue;

                case 71:
                    yyst[yysp] = 71;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 177:
                    yyn = yys71();
                    continue;

                case 72:
                    yyst[yysp] = 72;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 178:
                    yyn = yys72();
                    continue;

                case 73:
                    yyst[yysp] = 73;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 179:
                    yyn = yys73();
                    continue;

                case 74:
                    yyst[yysp] = 74;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 180:
                    yyn = yys74();
                    continue;

                case 75:
                    yyst[yysp] = 75;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 181:
                    yyn = yys75();
                    continue;

                case 76:
                    yyst[yysp] = 76;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 182:
                    yyn = yys76();
                    continue;

                case 77:
                    yyst[yysp] = 77;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 183:
                    yyn = yys77();
                    continue;

                case 78:
                    yyst[yysp] = 78;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 184:
                    yyn = yys78();
                    continue;

                case 79:
                    yyst[yysp] = 79;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 185:
                    yyn = yys79();
                    continue;

                case 80:
                    yyst[yysp] = 80;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 186:
                    yyn = yys80();
                    continue;

                case 81:
                    yyst[yysp] = 81;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 187:
                    yyn = yys81();
                    continue;

                case 82:
                    yyst[yysp] = 82;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 188:
                    yyn = yys82();
                    continue;

                case 83:
                    yyst[yysp] = 83;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 189:
                    yyn = yys83();
                    continue;

                case 84:
                    yyst[yysp] = 84;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 190:
                    switch (yytok) {
                        case ')':
                            yyn = 97;
                            continue;
                        case ',':
                            yyn = 98;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 85:
                    yyst[yysp] = 85;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 191:
                    yyn = yys85();
                    continue;

                case 86:
                    yyst[yysp] = 86;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 192:
                    yyn = yys86();
                    continue;

                case 87:
                    yyst[yysp] = 87;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 193:
                    yyn = yys87();
                    continue;

                case 88:
                    yyst[yysp] = 88;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 194:
                    yyn = yys88();
                    continue;

                case 89:
                    yyst[yysp] = 89;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 195:
                    switch (yytok) {
                        case ')':
                            yyn = 99;
                            continue;
                        case ',':
                            yyn = 100;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 90:
                    yyst[yysp] = 90;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 196:
                    switch (yytok) {
                        case ',':
                        case ')':
                            yyn = yyr21();
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 91:
                    yyst[yysp] = 91;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 197:
                    switch (yytok) {
                        case ',':
                            yyn = 100;
                            continue;
                        case ')':
                            yyn = 101;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 92:
                    yyst[yysp] = 92;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 198:
                    yyn = yys92();
                    continue;

                case 93:
                    yyst[yysp] = 93;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 199:
                    switch (yytok) {
                        case IDENTIFIER:
                        case CHECK:
                        case ENDINPUT:
                        case AUSGABE:
                            yyn = yyr6();
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 94:
                    yyst[yysp] = 94;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 200:
                    switch (yytok) {
                        case GDW:
                            yyn = 39;
                            continue;
                        case IMPLIES:
                            yyn = 40;
                            continue;
                        case '&':
                            yyn = 41;
                            continue;
                        case '|':
                            yyn = 42;
                            continue;
                        case ';':
                            yyn = yyr8();
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 95:
                    yyst[yysp] = 95;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 201:
                    switch (yytok) {
                        case IDENTIFIER:
                            yyn = 6;
                            continue;
                        case ':':
                            yyn = 65;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 96:
                    yyst[yysp] = 96;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 202:
                    switch (yytok) {
                        case IDENTIFIER:
                        case CHECK:
                        case ENDINPUT:
                        case AUSGABE:
                            yyn = yyr4();
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 97:
                    yyst[yysp] = 97;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 203:
                    yyn = yys97();
                    continue;

                case 98:
                    yyst[yysp] = 98;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 204:
                    yyn = yys98();
                    continue;

                case 99:
                    yyst[yysp] = 99;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 205:
                    yyn = yys99();
                    continue;

                case 100:
                    yyst[yysp] = 100;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 206:
                    switch (yytok) {
                        case STRING:
                            yyn = 104;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 101:
                    yyst[yysp] = 101;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 207:
                    yyn = yys101();
                    continue;

                case 102:
                    yyst[yysp] = 102;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 208:
                    switch (yytok) {
                        case ';':
                            yyn = 105;
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 103:
                    yyst[yysp] = 103;
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 209:
                    yyn = yys103();
                    continue;

                case 104:
                    yyst[yysp] = 104;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 210:
                    switch (yytok) {
                        case ',':
                        case ')':
                            yyn = yyr20();
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 105:
                    yyst[yysp] = 105;
                    yysv[yysp] = (lexer.getSemantic()
                                 );
                    yytok = (lexer.nextToken()
                            );
                    if (++yysp>=yyst.length) {
                        yyexpand();
                    }
                case 211:
                    switch (yytok) {
                        case IDENTIFIER:
                        case CHECK:
                        case ENDINPUT:
                        case AUSGABE:
                            yyn = yyr5();
                            continue;
                    }
                    yyn = 215;
                    continue;

                case 212:
                    return true;
                case 213:
                    yyerror("stack overflow");
                case 214:
                    return false;
                case 215:
                    yyerror("syntax error");
                    return false;
            }
        }
    }

    protected void yyexpand() {
        int[] newyyst = new int[2*yyst.length];
        Object[] newyysv = new Object[2*yyst.length];
        for (int i=0; i<yyst.length; i++) {
            newyyst[i] = yyst[i];
            newyysv[i] = yysv[i];
        }
        yyst = newyyst;
        yysv = newyysv;
    }

    private int yys11() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM:
                return 18;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case EINDEUTIG:
                return 22;
            case FALSE:
                return 23;
            case IDENTIFIER:
                return 24;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case MEDIUM_IST_IN_BESTAND:
                return 27;
            case MEDIUM_KEINE_BESCHREIBUNG:
                return 28;
            case MEDIUM_KEINE_SYSTEMATIK:
                return 29;
            case MEDIUM_MEDIENTYP:
                return 30;
            case MEDIUM_SYSTEMATIK:
                return 31;
            case NUM:
                return 32;
            case TRUE:
                return 33;
            case '!':
                return 34;
            case '(':
                return 35;
        }
        return 215;
    }

    private int yys15() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr17();
        }
        return 215;
    }

    private int yys16() {
        switch (yytok) {
            case EQ:
                return 43;
            case GEQ:
                return 44;
            case GREATER:
                return 45;
            case LEQ:
                return 46;
            case LESS:
                return 47;
            case NEQ:
                return 48;
        }
        return 215;
    }

    private int yys23() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr23();
        }
        return 215;
    }

    private int yys24() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr24();
        }
        return 215;
    }

    private int yys25() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GREATER:
            case NEQ:
            case GEQ:
            case GDW:
            case ';':
            case LESS:
            case EQ:
            case ',':
            case '|':
            case LEQ:
            case ')':
                return yyr38();
        }
        return 215;
    }

    private int yys32() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GREATER:
            case NEQ:
            case GEQ:
            case GDW:
            case ';':
            case LESS:
            case EQ:
            case ',':
            case '|':
            case LEQ:
            case ')':
                return yyr37();
        }
        return 215;
    }

    private int yys33() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr22();
        }
        return 215;
    }

    private int yys34() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM:
                return 18;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case EINDEUTIG:
                return 22;
            case FALSE:
                return 23;
            case IDENTIFIER:
                return 24;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case MEDIUM_IST_IN_BESTAND:
                return 27;
            case MEDIUM_KEINE_BESCHREIBUNG:
                return 28;
            case MEDIUM_KEINE_SYSTEMATIK:
                return 29;
            case MEDIUM_MEDIENTYP:
                return 30;
            case MEDIUM_SYSTEMATIK:
                return 31;
            case NUM:
                return 32;
            case TRUE:
                return 33;
            case '!':
                return 34;
            case '(':
                return 35;
        }
        return 215;
    }

    private int yys35() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM:
                return 18;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case EINDEUTIG:
                return 22;
            case FALSE:
                return 23;
            case IDENTIFIER:
                return 24;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case MEDIUM_IST_IN_BESTAND:
                return 27;
            case MEDIUM_KEINE_BESCHREIBUNG:
                return 28;
            case MEDIUM_KEINE_SYSTEMATIK:
                return 29;
            case MEDIUM_MEDIENTYP:
                return 30;
            case MEDIUM_SYSTEMATIK:
                return 31;
            case NUM:
                return 32;
            case TRUE:
                return 33;
            case '!':
                return 34;
            case '(':
                return 35;
        }
        return 215;
    }

    private int yys39() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM:
                return 18;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case EINDEUTIG:
                return 22;
            case FALSE:
                return 23;
            case IDENTIFIER:
                return 24;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case MEDIUM_IST_IN_BESTAND:
                return 27;
            case MEDIUM_KEINE_BESCHREIBUNG:
                return 28;
            case MEDIUM_KEINE_SYSTEMATIK:
                return 29;
            case MEDIUM_MEDIENTYP:
                return 30;
            case MEDIUM_SYSTEMATIK:
                return 31;
            case NUM:
                return 32;
            case TRUE:
                return 33;
            case '!':
                return 34;
            case '(':
                return 35;
        }
        return 215;
    }

    private int yys40() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM:
                return 18;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case EINDEUTIG:
                return 22;
            case FALSE:
                return 23;
            case IDENTIFIER:
                return 24;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case MEDIUM_IST_IN_BESTAND:
                return 27;
            case MEDIUM_KEINE_BESCHREIBUNG:
                return 28;
            case MEDIUM_KEINE_SYSTEMATIK:
                return 29;
            case MEDIUM_MEDIENTYP:
                return 30;
            case MEDIUM_SYSTEMATIK:
                return 31;
            case NUM:
                return 32;
            case TRUE:
                return 33;
            case '!':
                return 34;
            case '(':
                return 35;
        }
        return 215;
    }

    private int yys41() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM:
                return 18;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case EINDEUTIG:
                return 22;
            case FALSE:
                return 23;
            case IDENTIFIER:
                return 24;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case MEDIUM_IST_IN_BESTAND:
                return 27;
            case MEDIUM_KEINE_BESCHREIBUNG:
                return 28;
            case MEDIUM_KEINE_SYSTEMATIK:
                return 29;
            case MEDIUM_MEDIENTYP:
                return 30;
            case MEDIUM_SYSTEMATIK:
                return 31;
            case NUM:
                return 32;
            case TRUE:
                return 33;
            case '!':
                return 34;
            case '(':
                return 35;
        }
        return 215;
    }

    private int yys42() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM:
                return 18;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case EINDEUTIG:
                return 22;
            case FALSE:
                return 23;
            case IDENTIFIER:
                return 24;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case MEDIUM_IST_IN_BESTAND:
                return 27;
            case MEDIUM_KEINE_BESCHREIBUNG:
                return 28;
            case MEDIUM_KEINE_SYSTEMATIK:
                return 29;
            case MEDIUM_MEDIENTYP:
                return 30;
            case MEDIUM_SYSTEMATIK:
                return 31;
            case NUM:
                return 32;
            case TRUE:
                return 33;
            case '!':
                return 34;
            case '(':
                return 35;
        }
        return 215;
    }

    private int yys43() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case NUM:
                return 32;
        }
        return 215;
    }

    private int yys44() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case NUM:
                return 32;
        }
        return 215;
    }

    private int yys45() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case NUM:
                return 32;
        }
        return 215;
    }

    private int yys46() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case NUM:
                return 32;
        }
        return 215;
    }

    private int yys47() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case NUM:
                return 32;
        }
        return 215;
    }

    private int yys48() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case NUM:
                return 32;
        }
        return 215;
    }

    private int yys54() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM:
                return 18;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case EINDEUTIG:
                return 22;
            case FALSE:
                return 23;
            case IDENTIFIER:
                return 24;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case MEDIUM_IST_IN_BESTAND:
                return 27;
            case MEDIUM_KEINE_BESCHREIBUNG:
                return 28;
            case MEDIUM_KEINE_SYSTEMATIK:
                return 29;
            case MEDIUM_MEDIENTYP:
                return 30;
            case MEDIUM_SYSTEMATIK:
                return 31;
            case NUM:
                return 32;
            case TRUE:
                return 33;
            case '!':
                return 34;
            case '(':
                return 35;
        }
        return 215;
    }

    private int yys61() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr12();
        }
        return 215;
    }

    private int yys65() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM:
                return 18;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case EINDEUTIG:
                return 22;
            case FALSE:
                return 23;
            case IDENTIFIER:
                return 24;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case MEDIUM_IST_IN_BESTAND:
                return 27;
            case MEDIUM_KEINE_BESCHREIBUNG:
                return 28;
            case MEDIUM_KEINE_SYSTEMATIK:
                return 29;
            case MEDIUM_MEDIENTYP:
                return 30;
            case MEDIUM_SYSTEMATIK:
                return 31;
            case NUM:
                return 32;
            case TRUE:
                return 33;
            case '!':
                return 34;
            case '(':
                return 35;
        }
        return 215;
    }

    private int yys68() {
        switch (yytok) {
            case GDW:
                return 39;
            case IMPLIES:
                return 40;
            case '&':
                return 41;
            case '|':
                return 42;
            case ';':
            case ',':
            case ')':
                return yyr13();
        }
        return 215;
    }

    private int yys69() {
        switch (yytok) {
            case IMPLIES:
                return 40;
            case '&':
                return 41;
            case '|':
                return 42;
            case GDW:
            case ';':
            case ',':
            case ')':
                return yyr14();
        }
        return 215;
    }

    private int yys70() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr10();
        }
        return 215;
    }

    private int yys71() {
        switch (yytok) {
            case '&':
                return 41;
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr11();
        }
        return 215;
    }

    private int yys72() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr35();
        }
        return 215;
    }

    private int yys73() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr31();
        }
        return 215;
    }

    private int yys74() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr33();
        }
        return 215;
    }

    private int yys75() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr32();
        }
        return 215;
    }

    private int yys76() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr34();
        }
        return 215;
    }

    private int yys77() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr36();
        }
        return 215;
    }

    private int yys78() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GREATER:
            case NEQ:
            case GEQ:
            case GDW:
            case ';':
            case LESS:
            case EQ:
            case ',':
            case '|':
            case LEQ:
            case ')':
                return yyr39();
        }
        return 215;
    }

    private int yys79() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr30();
        }
        return 215;
    }

    private int yys80() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GREATER:
            case NEQ:
            case GEQ:
            case GDW:
            case ';':
            case LESS:
            case EQ:
            case ',':
            case '|':
            case LEQ:
            case ')':
                return yyr43();
        }
        return 215;
    }

    private int yys81() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GREATER:
            case NEQ:
            case GEQ:
            case GDW:
            case ';':
            case LESS:
            case EQ:
            case ',':
            case '|':
            case LEQ:
            case ')':
                return yyr42();
        }
        return 215;
    }

    private int yys82() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GREATER:
            case NEQ:
            case GEQ:
            case GDW:
            case ';':
            case LESS:
            case EQ:
            case ',':
            case '|':
            case LEQ:
            case ')':
                return yyr41();
        }
        return 215;
    }

    private int yys83() {
        switch (yytok) {
            case GDW:
                return 39;
            case IMPLIES:
                return 40;
            case '&':
                return 41;
            case '|':
                return 42;
            case ',':
            case ')':
                return yyr19();
        }
        return 215;
    }

    private int yys85() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GREATER:
            case NEQ:
            case GEQ:
            case GDW:
            case ';':
            case LESS:
            case EQ:
            case ',':
            case '|':
            case LEQ:
            case ')':
                return yyr40();
        }
        return 215;
    }

    private int yys86() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr26();
        }
        return 215;
    }

    private int yys87() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr29();
        }
        return 215;
    }

    private int yys88() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr28();
        }
        return 215;
    }

    private int yys92() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr16();
        }
        return 215;
    }

    private int yys97() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr15();
        }
        return 215;
    }

    private int yys98() {
        switch (yytok) {
            case AUSLEIHE_BENUTZERALTER:
                return 17;
            case AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM:
                return 18;
            case BENUTZER_ANMELDEDAUER_JAHRE:
                return 19;
            case BENUTZER_ANMELDEDAUER_TAGE:
                return 20;
            case BENUTZER_BENUTZERALTER:
                return 21;
            case EINDEUTIG:
                return 22;
            case FALSE:
                return 23;
            case IDENTIFIER:
                return 24;
            case INTEGER:
                return 25;
            case MEDIUM_EINSTELLUNGSDAUER_TAGE:
                return 26;
            case MEDIUM_IST_IN_BESTAND:
                return 27;
            case MEDIUM_KEINE_BESCHREIBUNG:
                return 28;
            case MEDIUM_KEINE_SYSTEMATIK:
                return 29;
            case MEDIUM_MEDIENTYP:
                return 30;
            case MEDIUM_SYSTEMATIK:
                return 31;
            case NUM:
                return 32;
            case TRUE:
                return 33;
            case '!':
                return 34;
            case '(':
                return 35;
        }
        return 215;
    }

    private int yys99() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr25();
        }
        return 215;
    }

    private int yys101() {
        switch (yytok) {
            case '&':
            case IMPLIES:
            case GDW:
            case '|':
            case ';':
            case ',':
            case ')':
                return yyr27();
        }
        return 215;
    }

    private int yys103() {
        switch (yytok) {
            case GDW:
                return 39;
            case IMPLIES:
                return 40;
            case '&':
                return 41;
            case '|':
                return 42;
            case ',':
            case ')':
                return yyr18();
        }
        return 215;
    }

    private int yyr1() { // prog : prog befehl
        yysp -= 2;
        return 1;
    }

    private int yyr2() { // prog : befehl
        yysp -= 1;
        return 1;
    }

    private int yyr10() { // ausdruck : ausdruck '&' ausdruck
        { yyrv = Auswahl.createANDAuswahl((Auswahl) yysv[yysp-3], (Auswahl) yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yypausdruck();
    }

    private int yyr11() { // ausdruck : ausdruck '|' ausdruck
        { yyrv = Auswahl.createORAuswahl((Auswahl) yysv[yysp-3], (Auswahl) yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yypausdruck();
    }

    private int yyr12() { // ausdruck : '!' ausdruck
        { yyrv = Auswahl.createNOTAuswahl((Auswahl) yysv[yysp-1]); }
        yysv[yysp-=2] = yyrv;
        return yypausdruck();
    }

    private int yyr13() { // ausdruck : ausdruck GDW ausdruck
        { yyrv = Auswahl.createGDWAuswahl((Auswahl) yysv[yysp-3], (Auswahl) yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yypausdruck();
    }

    private int yyr14() { // ausdruck : ausdruck IMPLIES ausdruck
        { yyrv = Auswahl.createIMPLIESAuswahl((Auswahl) yysv[yysp-3], (Auswahl) yysv[yysp-1]); }
        yysv[yysp-=3] = yyrv;
        return yypausdruck();
    }

    private int yyr15() { // ausdruck : EINDEUTIG '(' ausdruckliste ')'
        { yyrv = Auswahl.createEINDEUTIGAuswahl((Auswahl[]) ((Vector) yysv[yysp-2]).toArray(new Auswahl[0])); }
        yysv[yysp-=4] = yyrv;
        return yypausdruck();
    }

    private int yyr16() { // ausdruck : '(' ausdruck ')'
        { yyrv = yysv[yysp-2]; }
        yysv[yysp-=3] = yyrv;
        return yypausdruck();
    }

    private int yyr17() { // ausdruck : atom
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return yypausdruck();
    }

    private int yypausdruck() {
        switch (yyst[yysp-1]) {
            case 65: return 94;
            case 54: return 83;
            case 42: return 71;
            case 41: return 70;
            case 40: return 69;
            case 39: return 68;
            case 35: return 62;
            case 34: return 61;
            case 11: return 14;
            default: return 103;
        }
    }

    private int yyr18() { // ausdruckliste : ausdruckliste ',' ausdruck
        { ((Vector) yysv[yysp-3]).add(yysv[yysp-1]);
                                                         yyrv = yysv[yysp-3]; }
        yysv[yysp-=3] = yyrv;
        return 84;
    }

    private int yyr19() { // ausdruckliste : ausdruck
        { Vector erg = new Vector();
                                                                         erg.add(yysv[yysp-1]);
                                                                         yyrv = erg;}
        yysv[yysp-=1] = yyrv;
        return 84;
    }

    private int yyr3() { // befehl : labeled_definition ';'
        yysp -= 2;
        return yypbefehl();
    }

    private int yyr4() { // befehl : CHECK '(' STRING ')' definition ';'
        { CheckAuswahl erg = new CheckAuswahl((String) yysv[yysp-4], (Auswahl) yysv[yysp-2]); 
                                                    checks.add(erg); }
        yysv[yysp-=6] = yyrv;
        return yypbefehl();
    }

    private int yyr5() { // befehl : AUSGABE '(' STRING ',' INTEGER ')' definition ';'
        { AusgabeAuswahl erg = new AusgabeAuswahl((String) yysv[yysp-6], ((Integer) yysv[yysp-4]).intValue(), (Auswahl) yysv[yysp-2]); 
                                                    ausgaben.add(erg); }
        yysv[yysp-=8] = yyrv;
        return yypbefehl();
    }

    private int yyr6() { // befehl : AUSGABE '(' STRING ')' definition ';'
        { AusgabeAuswahl erg = new AusgabeAuswahl((String) yysv[yysp-4], 0, (Auswahl) yysv[yysp-2]); 
                                                        ausgaben.add(erg); }
        yysv[yysp-=6] = yyrv;
        return yypbefehl();
    }

    private int yypbefehl() {
        switch (yyst[yysp-1]) {
            case 0: return 2;
            default: return 7;
        }
    }

    private int yyr7() { // definition : labeled_definition
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=1] = yyrv;
        return yypdefinition();
    }

    private int yyr8() { // definition : ':' ausdruck
        { yyrv = yysv[yysp-1]; }
        yysv[yysp-=2] = yyrv;
        return yypdefinition();
    }

    private int yypdefinition() {
        switch (yyst[yysp-1]) {
            case 38: return 67;
            case 36: return 63;
            default: return 102;
        }
    }

    private int yyr9() { // labeled_definition : IDENTIFIER ':' ausdruck
        { putAuswahl((String) yysv[yysp-3], (Auswahl) yysv[yysp-1]); 
                                                 yyrv = yysv[yysp-1];
                                               }
        yysv[yysp-=3] = yyrv;
        switch (yyst[yysp-1]) {
            case 1: return 3;
            case 0: return 3;
            default: return 64;
        }
    }

    private int yyr22() { // atom : TRUE
        { yyrv = Auswahl.createTRUEAuswahl();}
        yysv[yysp-=1] = yyrv;
        return 15;
    }

    private int yyr23() { // atom : FALSE
        { yyrv = Auswahl.createFALSEAuswahl();}
        yysv[yysp-=1] = yyrv;
        return 15;
    }

    private int yyr24() { // atom : IDENTIFIER
        { yyrv = Auswahl.createREFERENZAuswahl(getAuswahl((String) yysv[yysp-1]));}
        yysv[yysp-=1] = yyrv;
        return 15;
    }

    private int yyr25() { // atom : MEDIUM_MEDIENTYP '(' stringliste ')'
        { yyrv = Auswahl.createMedientypAuswahl((String[]) ((Vector) yysv[yysp-2]).toArray(new String[0]));}
        yysv[yysp-=4] = yyrv;
        return 15;
    }

    private int yyr26() { // atom : MEDIUM_IST_IN_BESTAND '(' ')'
        { yyrv = Auswahl.createMedienInBestandAuswahl();}
        yysv[yysp-=3] = yyrv;
        return 15;
    }

    private int yyr27() { // atom : MEDIUM_SYSTEMATIK '(' stringliste ')'
        { yyrv = Auswahl.createSystematikAuswahl((String[]) ((Vector) yysv[yysp-2]).toArray(new String[0]));}
        yysv[yysp-=4] = yyrv;
        return 15;
    }

    private int yyr28() { // atom : MEDIUM_KEINE_SYSTEMATIK '(' ')'
        { yyrv = Auswahl.createKeineSystematikAuswahl();}
        yysv[yysp-=3] = yyrv;
        return 15;
    }

    private int yyr29() { // atom : MEDIUM_KEINE_BESCHREIBUNG '(' ')'
        { yyrv = Auswahl.createKeineBeschreibungAuswahl();}
        yysv[yysp-=3] = yyrv;
        return 15;
    }

    private int yyr30() { // atom : AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM '(' ')'
        { yyrv = Auswahl.createNICHT_EINGESTELLTES_MEDIUMAuswahl();}
        yysv[yysp-=3] = yyrv;
        return 15;
    }

    private int yyr31() { // atom : zahl GEQ zahl
        { yyrv = Auswahl.createGEQ_ZAHLAuswahl((AuswahlWert) yysv[yysp-3], (AuswahlWert) yysv[yysp-1]);}
        yysv[yysp-=3] = yyrv;
        return 15;
    }

    private int yyr32() { // atom : zahl LEQ zahl
        { yyrv = Auswahl.createGEQ_ZAHLAuswahl((AuswahlWert) yysv[yysp-1], (AuswahlWert) yysv[yysp-3]);}
        yysv[yysp-=3] = yyrv;
        return 15;
    }

    private int yyr33() { // atom : zahl GREATER zahl
        { yyrv = Auswahl.createGREATER_ZAHLAuswahl((AuswahlWert) yysv[yysp-3], (AuswahlWert) yysv[yysp-1]);}
        yysv[yysp-=3] = yyrv;
        return 15;
    }

    private int yyr34() { // atom : zahl LESS zahl
        { yyrv = Auswahl.createGREATER_ZAHLAuswahl((AuswahlWert) yysv[yysp-1], (AuswahlWert) yysv[yysp-3]);}
        yysv[yysp-=3] = yyrv;
        return 15;
    }

    private int yyr35() { // atom : zahl EQ zahl
        { yyrv = Auswahl.createEQ_ZAHLAuswahl((AuswahlWert) yysv[yysp-3], (AuswahlWert) yysv[yysp-1]);}
        yysv[yysp-=3] = yyrv;
        return 15;
    }

    private int yyr36() { // atom : zahl NEQ zahl
        { yyrv = Auswahl.createNEQ_ZAHLAuswahl((AuswahlWert) yysv[yysp-3], (AuswahlWert) yysv[yysp-1]);}
        yysv[yysp-=3] = yyrv;
        return 15;
    }

    private int yyr20() { // stringliste : stringliste ',' STRING
        { ((Vector) yysv[yysp-3]).add(yysv[yysp-1]);
                                                         yyrv = yysv[yysp-3]; }
        yysv[yysp-=3] = yyrv;
        return yypstringliste();
    }

    private int yyr21() { // stringliste : STRING
        { Vector erg = new Vector();
                                                                         erg.add(yysv[yysp-1]);
                                                                         yyrv = erg;}
        yysv[yysp-=1] = yyrv;
        return yypstringliste();
    }

    private int yypstringliste() {
        switch (yyst[yysp-1]) {
            case 59: return 89;
            default: return 91;
        }
    }

    private int yyr37() { // zahl : NUM
        { yyrv = new AuswahlWert(AuswahlWert.ZAHL, yysv[yysp-1]);}
        yysv[yysp-=1] = yyrv;
        return yypzahl();
    }

    private int yyr38() { // zahl : INTEGER
        { yyrv = new AuswahlWert(AuswahlWert.ZAHL, new Double(((Integer) yysv[yysp-1]).doubleValue()));}
        yysv[yysp-=1] = yyrv;
        return yypzahl();
    }

    private int yyr39() { // zahl : AUSLEIHE_BENUTZERALTER '(' ')'
        { yyrv = new AuswahlWert(AuswahlWert.AUSLEIHE_BENUTZERALTER);}
        yysv[yysp-=3] = yyrv;
        return yypzahl();
    }

    private int yyr40() { // zahl : MEDIUM_EINSTELLUNGSDAUER_TAGE '(' ')'
        { yyrv = new AuswahlWert(AuswahlWert.MEDIUM_EINSTELLUNGSDAUER_TAGE);}
        yysv[yysp-=3] = yyrv;
        return yypzahl();
    }

    private int yyr41() { // zahl : BENUTZER_BENUTZERALTER '(' ')'
        { yyrv = new AuswahlWert(AuswahlWert.BENUTZER_BENUTZERALTER);}
        yysv[yysp-=3] = yyrv;
        return yypzahl();
    }

    private int yyr42() { // zahl : BENUTZER_ANMELDEDAUER_TAGE '(' ')'
        { yyrv = new AuswahlWert(AuswahlWert.BENUTZER_ANMELDEDAUER_TAGE);}
        yysv[yysp-=3] = yyrv;
        return yypzahl();
    }

    private int yyr43() { // zahl : BENUTZER_ANMELDEDAUER_JAHRE '(' ')'
        { yyrv = new AuswahlWert(AuswahlWert.BENUTZER_ANMELDEDAUER_JAHRE);}
        yysv[yysp-=3] = yyrv;
        return yypzahl();
    }

    private int yypzahl() {
        switch (yyst[yysp-1]) {
            case 48: return 77;
            case 47: return 76;
            case 46: return 75;
            case 45: return 74;
            case 44: return 73;
            case 43: return 72;
            default: return 16;
        }
    }


  private AuswahlKonfigurationLexer lexer;

  Vector ausgaben = new Vector();
  Vector checks = new Vector();
  Hashtable definitionen = new Hashtable();  

  private Auswahl getAuswahl(String string) {
    Auswahl erg = (Auswahl) definitionen.get(string);
    if (erg == null)
      yyerror("Unbekannter Identifier '"+string+"'!");
    return erg;
  }
  
  private void putAuswahl(String string, Auswahl auswahl) {
    boolean already_defined = definitionen.containsKey(string);
    if (already_defined)
      yyerror("Doppelt definierter Identifierer '"+string+"'!");
        definitionen.put(string, auswahl);
  }

  public AuswahlKonfigurationParser(java.io.Reader reader) { 
    this.lexer = new AuswahlKonfigurationLexer(reader); 
    lexer.nextToken();
  } 

  public AuswahlKonfigurationParser(java.io.InputStream input) { 
    this.lexer = new AuswahlKonfigurationLexer(input); 
    lexer.nextToken();
  }

  void yyerror(String error) {  
        throw new ParseException("Fehler bei Zeile "+(lexer.getLine()+1)+", Spalte "+(lexer.getColumn()+1)+"!\n"+error);
  }
  
  void yyerror(Exception e) {   
        throw new ParseException("Fehler bei Zeile "+(lexer.getLine()+1)+", Spalte "+(lexer.getColumn()+1)+"!\n"+e.getMessage());
  }  
  
  public boolean parseWithExceptions() {
        try{
      return parse();
        } catch(Exception e) {
          yyerror(e);
        }
        return false;
  }
  

}
