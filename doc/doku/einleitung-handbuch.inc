\KBVT\  ist ein Tool für kleinere und mittlere Büchereien. Unter anderem
er\-mög\-li\-cht es die Verwaltung des Medienbestandes, der Benutzer und ermöglicht
eine elektronische Ausleihe. Außerdem können Veranstaltungen und
Anmeldelisten verwaltet werden. Einige besonders hervorzuhebende Eigenschaften
sind:
\begin{itemize}
\item Betriebssystem- und Rechnerarchitektur-Unabhängigkeit
\item leichte Anpassbarkeit, Erweiterbarkeit
\item Netzwerkfähigkeit
\item Modularität
\item leichte Interaktion mit anderen Systemen
\end{itemize}

\section*{Lizenz}
\KBVT\  ist freie Software. Es wird Ihnen kostenlos über die
\KBVT-Webseite\footnote{\urlkbvt}
angeboten. Sie können es unter den Bedingungen der \emph{GNU General Public
License (GNU GPL)}\footnote{\urlgpl},
wie von der Free Software Foundation veröffentlicht, weitergeben und/oder
modifizieren, entweder gemäß Version 2 der Lizenz oder (nach Ihrer Option)
jeder späteren Version. Die Veröffentlichung erfolgt in der Hoffnung, dass es
Ihnen von Nutzen sein wird, aber ohne irgendeine Garantie, sogar ohne die
implizite Garantie der Marktreife oder der Verwendbarkeit für einen bestimmten
Zweck. Details finden Sie in der GNU General Public License.

\section*{Architektur}
Den Kern von \KBVT\  bildet eine MySQL-Datenbank, auf die von mehreren
JAVA-SWING-Oberflächen zugegriffen wird. Für den Benutzer interessant daran
ist, dass man aus sehr vielen Programmen und aus so gut wie allen gängigen
Programmiersprachen auf die Daten zugreifen kann.  D.\,h. egal, ob sie mit
Microsoft-Word einen Serienbrief an alle Leser schicken, aus ihrem Bestand
automatisch per Perl-Script HTML-Seiten generieren oder per PHP Besuchern
Ihrer Internet-Seite das Durchsuchen Ihres Bestandes erlauben wollen, all dies
und vieles mehr ist leicht möglich. Außerdem ist hierdurch auch ein Import und
Export von Daten sehr einfach.  So können vorhandene Daten leicht
weiterverwendet werden.  Desweiteren erleichtert diese Architektur durch die
mit ihr verbundene Modularität den nur teilweisen Einsatz des Systems.  So
kann beispielsweise \KBVT\  auch nur für die Verwaltung des Bestandes oder der
Leser eingesetzt werden, ohne dass durch unnötig komplexe Benutzeroberflächen
der Benutzer behindert wird.

\section*{Zielgruppe}
\KBVT\  kann und will nicht mit kommerziellen Systemen konkurrieren. Dies liegt
zum einen daran, dass keine vergleichbare Dokumentation und sehr wenig Support
zur Ver\-fügung steht. Es wird vorausgesetzt, dass in jeder Bücherei, die \KBVT\ 
einsetzt, ein Administrator zur Verfügung steht, der sich um Installation,
Anpassung an die Bedürfnisse der einzelnen Bücherei, Import und Export von
Daten, Datensicherung, Update auf neue Versionen von \KBVT\  und ähnliches
kümmert.  Dieser Administrator muss kein Experte sein, es ist aber notwendig,
dass er sich mit \KBVT\  und benutzten Programmen wie z.B. MySQL näher
beschäftigt. Bei der Entwicklung von \KBVT\  wurde bewusst darauf verzichtet,
den Anwendern vorzugaukeln, dass Aufgaben wie beispielsweise der Schutz vor
Zugriff von Unbefugten auf die gespeicherten Daten ohne Beschäftigung mit \KBVT\ 
und MySQL möglich ist. Ein solcher Adminstrator wird jedoch nach der
Installation und Einrichtung von \KBVT\  nur noch selten benötigt. Die
eigentliche Benutzung wurde versucht, so einfach wie möglich zu
gestalten, so dass es nach sehr kurzer Einarbeitungszeit auch von Laien
benutzt werden kann.

Eine weitere möglicherweise als Nachteil empfundene Eigenschaft von \KBVT\ 
ist, dass es pragmatisch entwickelt wird. Zum Beispiel werden zu einem
Medium nur Titel, Autor, Mediennummer, Einstellungsdatum, Medientyp,
Systematiken, ISBN-Nummer und eine Beschreibung gespeichert. Auf weitere, sonst
übliche Informationen wie Seitenanzahl, Auflage, Verlag,
Orginalsprache usw. wurde bewusst verzichtet, da für die Zwecke 
der KöB-Oberbrechen diese Informationen nicht nötig sind, aber bei der
Eingabe viel Arbeit verursachen. Da inzwischen \KBVT\  nicht nur in de
KöB-Oberbrechen eingesetzt wird, sind Erweiterungen für die fernere Zukunft
geplant.

Klarer Vorteil von \KBVT\  ist die einfache graphische Benutzeroberfläche,
die Betriebssystemunabhängigkeit und
die leichte Konfigurierbarkeit und Erweiterbarkeit. 
Ein Entwicklungsziel war es, dass \KBVT\  auch von Nicht-Experten leicht erweitert und
genau auf die Bedürfnisse einer speziellen Bücherei angepasst werden kann.
Außerdem wurde versucht, das Rad nicht immer wieder neu zu erfinden. Statt
für die Steuerung des Internetzugangs z.B. einen eigenen Browser und
Webserver anzubieten, wird stattdessen ein beliebiger Browser und der bekannte
Proxy-Server Squid verwendet.
\medskip

\KBVT\  ist also für kleinere und mittlere Büchereien geeignet, in denen jemand
zur Verfügung steht, der bereit ist, Zeit in die Installation und 
Administration von \KBVT\  zu investieren. Vorteil gegenüber den meisten
kommerziellen Lösungen sind die leichte Anpassbarkeit und die offenen
Schnittstellen. Besonders Administratoren mit Programmiererfahrung werden dies
zu schätzen wissen. Außerdem kann gerade für kleinere, ehrenamtlich
geführte Büchereien die Kostenfrage ein entscheidender Vorteil von \KBVT\  sein.

\section*{Dank}
Ich möchte mich hiermit bei allen bedanken, die direkt oder indirekt bei der
Entwicklung von \KBVT\  geholfen haben. Besonders möchte ich den Entwicklern der
verwendeten Programme und Bibliotheken, aber auch den Benutzer
danken, die Fehler in \KBVT\  gemeldet und durch Verbesserungsvorschläge die
Entwicklung von \KBVT\  unterstützt haben. \KBVT\  verwendet die folgenden
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

Auch für die Zukunft bitte ich darum, Fehler in \KBVT\ zu melden und
Verbesserungsvorschläge, sowie Kritik zu äußern. Sie erreichen
mich\footnote{\Autor} per eMail unter \emailautor.  Ebenso bitte ich darum,
Kontakt mit mir aufzunehmen, wenn Sie Fehler, Ungenauigkeiten oder Lücken in
dieser Dokumentation finden.



\section*{Aufbau des Handbuchs}
Dieses Handbuch gliedert sich in 3 Teile
\begin{itemize}
\item Benutzerhandbuch
\item Handbuch für Administratoren
\item Handbuch für Entwickler.
\end{itemize}
Im \emph{Handbuch für Benutzer} werden die einzelnen Oberflächen von \KBVT\ 
vorgestellt. Es wendet sich -- wie der Name sagt -- an die Benutzer von \KBVT\ 
und soll die Bedienung erklären. Das \emph{Handbuch für Administratoren}
umfasst Installation und Konfiguration von \KBVT. Außerdem werden Themen wie
Datensicherung und die Zusammenarbeit von \KBVT\  mit anderen Programmen
diskutiert. Das \emph{Handbuch für Entwickler} schließlich wendet sich an
Personen, die \KBVT\  oder Tools, die mit \KBVT\  zusammenarbeiten, entwickeln bzw.
weiterentwickeln möchten. Es werden das Datenbankschema, die Architektur von
\KBVT, die internen und externen Schnittstellen und ähnliches behandelt.

Da das Handbuch für Benutzer bereits ein installiertes KBVT
voraussetzt, ist es bei einer Neuinstallation sinnvoll, zunächst das Handbuch
für Adminstratoren zu lesen. Sind sie dagegen nur daran interessiert zu
lernen, wie KBVT zu bedienen ist, genügt die Lektüre des Handbuchs für
Benutzer. 




%%% Local Variables: 
%%% mode: latex
%%% TeX-master: "handbuch"
%%% End: 
