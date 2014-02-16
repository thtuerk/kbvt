Copyright 2002, 2003 Thomas Türk  

KBVT is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
 
KBVT is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with KBVT; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 

------------------------------------------------------------------------

KBVT ist freie Software. Sie können es unter den Bedingungen der 
GNU General Public License (GNU GPL), wie von
der Free Software Foundation veröffentlicht, weitergeben 
und/oder modifizieren, entweder gemäß Version 2 der Lizenz 
oder (nach Ihrer Option) jeder späteren Version.
     
Die Veröffentlichung dieses Programms erfolgt in der Hoffnung,
dass es Ihnen von Nutzen sein wird, aber 
OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
Garantie der MARKTREIFE oder der 
VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. 
Details finden Sie in der GNU General Public License. 

------------------------------------------------------------------------



1. Dokumentation
----------------
Dokumentation zu KBVT, aktuelle Versionen, etc. ist unter
http://kbvt.koeb-oberbrechen.de zu finden. Ein allererster,
noch extrem lückenhafter Entwurf einer Dokumentation ist
unter "doc" zu finden.



2. Lizenz
---------
Eine Kopie der GNU GPL ist in der Datei gpl.txt zu finden. Die
Datei gpl-ger.pdf enthält eine deutsche, nicht offizielle 
Übersetzung der GPL. 



3. Kontakt
----------
Thomas Türk
t_tuerk@gmx.de
Albert-Otto-Str. 8
65611 Brechen

Ich bitte darum, Kontakt mit mir aufzunehmen. Ich bin
nämlich neugierig, ob und von wem KBVT eingesetzt wird. Außerdem 
bin ich daran interessiert zu erfahren, welche Probleme es beim Einsatz
von KBVT gibt. Ich bitte daher ausdrücklich darum, auch Kritik zu
äußern. Nur mit Hilfe solcher Rückmeldungen ist es möglich, KBVT zu 
verbessern.

Außerdem bin ich gerne bereit, bei der Installation von KBVT
oder der Anpassung an Ihre Bedürfnisse - wie bspw. der Erstellung eigener
Benutzerausweise - zu helfen. Dies umfaßt auch das Angebot, meine 
eMail-Adresse als Admin-eMail-Adresse einzutragen und Fehlermeldungen
dadurch direkt an mich weiterzuleiten.



4. Java-Version / Barcodescannerunterstützung
-----------------------------------------------
Version 0.7 ist für JDK 1.3.1 gedacht. Es wird die letzte Version von KBVT sein, 
die JDK 1.3.1 unterstützt. Aufgrund eines Bugs in JDK 1.3 gibt es Probleme beim
Einsatz von Barcodescannern unter aktuelleren Java-Versionen. Diese Probleme
lassen sich leicht beheben, indem die entsprechend dokumentierten Zeilen in der 
Datei de/oberbrechen/koeb/gui/AbstractMain.java in der Methode addBarcodeScanner 
einkommentiert werden.
