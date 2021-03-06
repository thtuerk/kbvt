\KBVT\  ist ein Tool f�r kleinere und mittlere B�chereien. Unter anderem
er\-m�g\-li\-cht es die Verwaltung des Medienbestandes, der Benutzer und erm�glicht
eine elektronische Ausleihe. Au�erdem k�nnen Veranstaltungen und
Anmeldelisten verwaltet werden. Einige besonders hervorzuhebende Eigenschaften
sind:
\begin{itemize}
\item Betriebssystem- und Rechnerarchitektur-Unabh�ngigkeit
\item leichte Anpassbarkeit, Erweiterbarkeit
\item Netzwerkf�higkeit
\item Modularit�t
\item leichte Interaktion mit anderen Systemen
\end{itemize}

\section*{Lizenz}
\KBVT\  ist freie Software. Es wird Ihnen kostenlos �ber die
\KBVT-Webseite\footnote{\urlkbvt}
angeboten. Sie k�nnen es unter den Bedingungen der \emph{GNU General Public
License (GNU GPL)}\footnote{\urlgpl},
wie von der Free Software Foundation ver�ffentlicht, weitergeben und/oder
modifizieren, entweder gem�� Version 2 der Lizenz oder (nach Ihrer Option)
jeder sp�teren Version. Die Ver�ffentlichung erfolgt in der Hoffnung, dass es
Ihnen von Nutzen sein wird, aber ohne irgendeine Garantie, sogar ohne die
implizite Garantie der Marktreife oder der Verwendbarkeit f�r einen bestimmten
Zweck. Details finden Sie in der GNU General Public License.

\section*{Architektur}
Den Kern von \KBVT\  bildet eine MySQL-Datenbank, auf die von mehreren
JAVA-SWING-Oberfl�chen zugegriffen wird. F�r den Benutzer interessant daran
ist, dass man aus sehr vielen Programmen und aus so gut wie allen g�ngigen
Programmiersprachen auf die Daten zugreifen kann.  D.\,h. egal, ob sie mit
Microsoft-Word einen Serienbrief an alle Leser schicken, aus ihrem Bestand
automatisch per Perl-Script HTML-Seiten generieren oder per PHP Besuchern
Ihrer Internet-Seite das Durchsuchen Ihres Bestandes erlauben wollen, all dies
und vieles mehr ist leicht m�glich. Au�erdem ist hierdurch auch ein Import und
Export von Daten sehr einfach.  So k�nnen vorhandene Daten leicht
weiterverwendet werden.  Desweiteren erleichtert diese Architektur durch die
mit ihr verbundene Modularit�t den nur teilweisen Einsatz des Systems.  So
kann beispielsweise \KBVT\  auch nur f�r die Verwaltung des Bestandes oder der
Leser eingesetzt werden, ohne dass durch unn�tig komplexe Benutzeroberfl�chen
der Benutzer behindert wird.

\section*{Zielgruppe}
\KBVT\  kann und will nicht mit kommerziellen Systemen konkurrieren. Dies liegt
zum einen daran, dass keine vergleichbare Dokumentation und sehr wenig Support
zur Ver\-f�gung steht. Es wird vorausgesetzt, dass in jeder B�cherei, die \KBVT\ 
einsetzt, ein Administrator zur Verf�gung steht, der sich um Installation,
Anpassung an die Bed�rfnisse der einzelnen B�cherei, Import und Export von
Daten, Datensicherung, Update auf neue Versionen von \KBVT\  und �hnliches
k�mmert.  Dieser Administrator muss kein Experte sein, es ist aber notwendig,
dass er sich mit \KBVT\  und benutzten Programmen wie z.B. MySQL n�her
besch�ftigt. Bei der Entwicklung von \KBVT\  wurde bewusst darauf verzichtet,
den Anwendern vorzugaukeln, dass Aufgaben wie beispielsweise der Schutz vor
Zugriff von Unbefugten auf die gespeicherten Daten ohne Besch�ftigung mit \KBVT\ 
und MySQL m�glich ist. Ein solcher Adminstrator wird jedoch nach der
Installation und Einrichtung von \KBVT\  nur noch selten ben�tigt. Die
eigentliche Benutzung wurde versucht, so einfach wie m�glich zu
gestalten, so dass es nach sehr kurzer Einarbeitungszeit auch von Laien
benutzt werden kann.

Eine weitere m�glicherweise als Nachteil empfundene Eigenschaft von \KBVT\ 
ist, dass es pragmatisch entwickelt wird. Zum Beispiel werden zu einem
Medium nur Titel, Autor, Mediennummer, Einstellungsdatum, Medientyp,
Systematiken, ISBN-Nummer und eine Beschreibung gespeichert. Auf weitere, sonst
�bliche Informationen wie Seitenanzahl, Auflage, Verlag,
Orginalsprache usw. wurde bewusst verzichtet, da f�r die Zwecke 
der K�B-Oberbrechen diese Informationen nicht n�tig sind, aber bei der
Eingabe viel Arbeit verursachen. Da inzwischen \KBVT\  nicht nur in de
K�B-Oberbrechen eingesetzt wird, sind Erweiterungen f�r die fernere Zukunft
geplant.

Klarer Vorteil von \KBVT\  ist die einfache graphische Benutzeroberfl�che,
die Betriebssystemunabh�ngigkeit und
die leichte Konfigurierbarkeit und Erweiterbarkeit. 
Ein Entwicklungsziel war es, dass \KBVT\  auch von Nicht-Experten leicht erweitert und
genau auf die Bed�rfnisse einer speziellen B�cherei angepasst werden kann.
Au�erdem wurde versucht, das Rad nicht immer wieder neu zu erfinden. Statt
f�r die Steuerung des Internetzugangs z.B. einen eigenen Browser und
Webserver anzubieten, wird stattdessen ein beliebiger Browser und der bekannte
Proxy-Server Squid verwendet.
\medskip

\KBVT\  ist also f�r kleinere und mittlere B�chereien geeignet, in denen jemand
zur Verf�gung steht, der bereit ist, Zeit in die Installation und 
Administration von \KBVT\  zu investieren. Vorteil gegen�ber den meisten
kommerziellen L�sungen sind die leichte Anpassbarkeit und die offenen
Schnittstellen. Besonders Administratoren mit Programmiererfahrung werden dies
zu sch�tzen wissen. Au�erdem kann gerade f�r kleinere, ehrenamtlich
gef�hrte B�chereien die Kostenfrage ein entscheidender Vorteil von \KBVT\  sein.

\section*{Dank}
Ich m�chte mich hiermit bei allen bedanken, die direkt oder indirekt bei der
Entwicklung von \KBVT\  geholfen haben. Besonders m�chte ich den Entwicklern der
verwendeten Programme und Bibliotheken, aber auch den Benutzer
danken, die Fehler in \KBVT\  gemeldet und durch Verbesserungsvorschl�ge die
Entwicklung von \KBVT\  unterst�tzt haben. \KBVT\  verwendet die folgenden
Programme:
\begin{flushleft}
\begin{itemize}
\item MySQL und den entsprechenden JDBC-Treiber
\item iText
\item Kunststoff Look And Feel
\item JCalendar
\item NanoXML
\item jacc - just another compiler compiler for Java
\item JFlex - The Fast Scanner Generator for Java
\end{itemize}
\end{flushleft}

Auch f�r die Zukunft bitte ich darum, Fehler in \KBVT\ zu melden und
Verbesserungsvorschl�ge, sowie Kritik zu �u�ern. Sie erreichen
mich\footnote{\Autor} per eMail unter \emailautor.  Ebenso bitte ich darum,
Kontakt mit mir aufzunehmen, wenn Sie Fehler, Ungenauigkeiten oder L�cken in
dieser Dokumentation finden.



\section*{Aufbau des Handbuchs}
Dieses Handbuch gliedert sich in 3 Teile
\begin{itemize}
\item Benutzerhandbuch
\item Handbuch f�r Administratoren
\item Handbuch f�r Entwickler.
\end{itemize}
Im \emph{Handbuch f�r Benutzer} werden die einzelnen Oberfl�chen von \KBVT\ 
vorgestellt. Es wendet sich -- wie der Name sagt -- an die Benutzer von \KBVT\ 
und soll die Bedienung erkl�ren. Das \emph{Handbuch f�r Administratoren}
umfasst Installation und Konfiguration von \KBVT. Au�erdem werden Themen wie
Datensicherung und die Zusammenarbeit von \KBVT\  mit anderen Programmen
diskutiert. Das \emph{Handbuch f�r Entwickler} schlie�lich wendet sich an
Personen, die \KBVT\  oder Tools, die mit \KBVT\  zusammenarbeiten, entwickeln bzw.
weiterentwickeln m�chten. Es werden das Datenbankschema, die Architektur von
\KBVT, die internen und externen Schnittstellen und �hnliches behandelt.

Da das Handbuch f�r Benutzer bereits ein installiertes KBVT
voraussetzt, ist es bei einer Neuinstallation sinnvoll, zun�chst das Handbuch
f�r Adminstratoren zu lesen. Sind sie dagegen nur daran interessiert zu
lernen, wie KBVT zu bedienen ist, gen�gt die Lekt�re des Handbuchs f�r
Benutzer. 




%%% Local Variables: 
%%% mode: latex
%%% TeX-master: "handbuch"
%%% End: 
