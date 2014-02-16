\chapter{Installation}

\section{Aktuelle Versionen}
In den meisten Fällen ist es sinnvoll die jeweils aktuellste Version von \KBVT\
und auch die aktuellste Dokumentation zu verwenden. Beides können Sie 
auf der \KBVT-Homepage\footnote{\urlkbvt} erhalten.

\KBVT-Versionen besitzen eine dreistellige Versionsnummer\index{Versionsnummer}
(z.B. 0.6.2). Die erste Stelle gibt die Nummer der großen Entwicklungschritte
an. 0 bedeutet hier also, dass noch kein großer Entwicklungsschritt erreicht
wurde. \KBVT\ befindet sich noch in der Entwicklung. Es werden keinerlei
Garantien gegeben, dass die wesentlichen Schnittstellen und
Benutzeroberflächen nicht mehr verändert werden. 

Die zweite Stelle der Versionsnummer kennzeichnet die Version des verwendeten
Datenbankschemas. Versionen, die in den ersten beiden Stellen übereinstimmen,
verwenden also die gleiche Datenbank. Wenn Sie also z.\,B. Version 0.6.1 nach
0.6.2 updaten, müssen Sie nur die Anwendungssoftware, nicht jedoch die
Datenbank aktualisieren. Damit können Versionen, die sich nur in der letzten
Stelle unterscheiden auch parallel auf der gleichen Datenbank arbeiten. 

Die letzte Stelle schließlich dient zur Unterscheidung zwischen Versionen, die
das gleiche Datenbankschema benutzen. Trotz des gleichen Datenbankschemas
können sich diese Versionen erheblich unterscheiden. Lesen Sie bitte jeweils
die Beschreibung der Änderungen auf der \KBVT-Homepage.

Meist ist es sinnvoll, die jeweils aktuellste Version von \KBVT\ zu
verwenden. Für den Fall, dass Sie weiterhin mit einem alten Datenbankschema
arbeiten möchten, werden weiterhin auch alte Versionen zum
Download angeboten.


\section{Systemvoraussetzungen}
\KBVT\ ist auf allen Systemen lauffähig, für die eine
Java-Implementierung\footnote{\urljava} existiert und eine
MySQL-Datenbank\footnote{\urlmysql} zur Verfügung steht. Sie benötigen
mindestens Java 1.3.1. Im Zusammenspiel mit Java 1.4.x wurde von Problemen im
Zusammenspiel mit einem Barcode-Scanner berichtet, die aber bisher noch nicht
überprüft werden konnten. Weiter wird MySQL in mindestens der Version 3.23
benötigt. Benutzt werden aber -- soweit vorhanden -- auch die zusätzlichen Features
von Version 4. Hier steht zusätzlich Transitionsschutz und die automatische
Überprüfung einiger Konsistenzeigenschaften zu Verfügung.

Da \KBVT\ eine Java-Swing Oberfläche benutzt und da bei der Entwicklung mehr
Wert auf ein leicht verständliches, anpassbares und erweiterbares Design als
auf Performance gelegt wurde, ist \KBVT\ im Vergleich zu ähnlichen Programmen
leider recht langsam. In der KöB Oberbrechen wurde \KBVT\ lange auf einem
Pentium 100 mit 32 MB RAM unter SuSE Linux 7.1 verwendet. Dies ist für
sinnvolles Arbeiten gerade noch ausreichend, wenn man akzeptiert, dass ein
Start von \KBVT\ 2-3 Minuten und das Tätigen einer Ausleihe mittels des
Barcode-Scanners ca. 1 Sekunde dauert. Auf einem 500 MHz AMD-K6 mit 256 MB RAM
unter Windows XP startet KBVT dagegen in ca. 10 Sekunden.

\section{Datenbankeinrichtung}
Um \KBVT\ benutzen zu können, benötigen Sie einen MySQL-Datenbankserver.
Dieser Server muss nicht auf dem gleichen Rechner installiert sein, auf dem
sie \KBVT\ einsetzen möchten. Sie können auch von evtl. mehreren \KBVT-Clients
einen gemeinsamen Server über ein Netzwerk benutzen. Die Installation von
MySQL soll hier nicht beschrieben werden. Informationen hierzu finden Sie in
der Dokumentation von MySQL.

Nach der Installation muss die Datenbank für \KBVT\ eingerichtet werden. Die
Datei \emph{leereDatenbank.sql} enthält SQL-Befehle, die eine neue Datenbank
anlegen, die benötigten Tabellen erzeugen und die Tabellen mit einigen
wenigen, grundlegenden Daten füllen. Genaue Informationen, wie sie diese Datei
einspielen können, finden Sie in der Dokumentation zu MySQL. Üblicherweise ist
das Einspielen mittels \texttt{mysql -u root < leereDatenbank.sql} möglich.
Dies sollte eine neue Datenbank namens \texttt{koebDB} angelegen und mit den
nötigen Tabellen und Daten füllen. Sollten Sie einen anderen
Da\-ten\-bank\-namen wünschen, muss die Datei \emph{leereDatenbank.sql} vor
dem Einspielen bearbeitet werden.

Bei der Installation erstellt MySQL automatisch einen Datenbankbenutzer namens
\emph{root}, der ohne sich durch ein Passwort authentifizieren zu müssen,
vom lokalen Rechner aus alle Zugriffsrechte auf alle Datenbanken besitzt. Aus
Sicherheitsgründen ist es empfehlenswert, ein Passwort für diesen Benutzer
einzurichten und einen Benutzer für den Zugriff auf die von \KBVT\ benutzte
Datenbank einzurichten. Dies ist zwingend erforderlich, wenn von einem anderen
Rechner auf die Datenbank zugegriffen werden soll. Der MySQL-Befehl, der einem
Benutzer \emph{benutzer}, der sich durch das Passwort \emph{passwort}
ausweist, an einem Rechner \emph{rechner} alle Rechte an der Datenbank
\emph{koebDB} gibt, lautet: \begin{flushleft}\texttt{grant all on koebDB.* to
  benutzer@rechner identified by 'passwort'}
\end{flushleft}
Wei\-ter\-füh\-rende
Informationen finden Sie in der MySQL"=Dokumentation.

\section{Grundlegende Konfiguration}

\KBVT\ speichert Konfigurationseinstellungen auf zwei verschiedene Arten. Die
Datei \emph{einstellungen.conf} enthält alle Informationen, die benötigt
werden, um auf die Datenbank zuzugreifen. Alle weiteren Informationen sind
in der Datenbank gespeichert. An dieser Stelle soll auf die Datei
\emph{einstellungen.conf} eingegangen werden. Die Einstellungen, die
in der Datenbank gespeichert werden, werden in Abschnitt~\ref{einstellungen_datenbank} erklärt.

Die Datei \emph{einstellungen.conf} ist eine Textdatei, deren Zeilen
Name-Wert-Paare darstellen. Unterstützte Einstellungen sind: 

\begin{definitionlist}
\definition{Datenbank-Factory}{
  \KBVT\ ist dafür ausgelegt, unterschiedliche Datenbankanwendungen zu
  unter\-stüt\-zen.  Mittels der Einstellung \emph{Datenbank-Factory} kann
  festgelegt werden, welcher Datenbanktyp benutzt werden soll. Zur Zeit wird
  jedoch nur MySQL unterstützt. Bis auf \emph{zeigeDebug} und
  \emph{ClientNr} beziehen sich alle weiteren Einstellungen auf die durch
  diese Einstellung bestimmte konkrete Datenbankanwendung. Die im folgenden
  beschriebenen Einstellungen beziehen sich auf MySQL. Informationen
  dazu, wie Sie Schnittstellen zu anderen Datenbankenanwendungen wie z.\,B. zu
  Postgres entwicklen können, finden Sie im Handbuch für Entwickler.
}
\definition{zeigeDebug}{
  Die erlaubten Werte sind \texttt{true} und \texttt{false}. Diese Einstellung
  bestimmt, ob Debug-Informationen ausgegeben werden sollen. Dabei handelt es
  sich um Informationen, die für Adminstratoren und Entwickler interessant
  sein können, den normalen Benutzer aber oft stören und verwirren. Daher
  sollte diese Einstellung im normalen Betrieb auf \texttt{false} gesetzt
  werden.
  
  Zur Zeit bestimmt diese Einstellung lediglich, ob über fehlende oder
  unsinnige Einstellungen informiert werden soll.
}
\definition{zeigeDebug}{
  Manche Einstellungen müssen einem speziellen Client zugeordent werden. Ein
  Beispiel dafür ist eine Einstellung, die bestimmt, welches Programm zur
  Anzeige von PDF-Dateien benutzt werden soll. Die Einstellung
  \emph{ClientNr} dient dazu, Clients eindeutig zu indentifizieren. Gültige
  Werte sind alle ganzen, positiven Zahlen. Es ist meist sinnvoll, für jeden
  Client eine eigene Nummer zu verwenden. Verwenden zwei Clients die gleiche
  Nummer, so teilen sie sich alle in der Datenbank gespeicherten Einstellungen.
}
\definition{MySQL"=Host, MySQL"=Datenbankname, MySQL"=Port, 
  MySQL-User, MySQL"=Passwort}{
  Diese Einstellungen bestimmen, welcher MySQL-Server kontaktiert 
  und welche Datenbank mit welchen Benutzerrechten verwendet werden soll.
}
\end{definitionlist}

\section{Start von \KBVT}
Nach diesen Vorbereitungen ist \KBVT\ bereit für den ersten Start. Im
Verzeichnis \emph{lib} befinden sich die von \KBVT\ benötigten Bibliotheken
sowie eine vorkompilierte Version von \KBVT. Tabelle~\ref{table_classes}
zeigt, welche Klasse für die einzelnen Oberflächen gestartet werden muss.

\begin{table*}[htbp]
\centering
\small
\begin{tabular}{|l|l|}
  \hline
  \textbf{Oberfläche} & \textbf{Klasse} \\\hline\hline
  Admin & de.oberbrechen.koeb.gui.admin.Main \\\hline
  Ausgaben & de.oberbrechen.koeb.gui.ausgaben.Main \\\hline
  Ausleihe & de.oberbrechen.koeb.gui.ausleihe.Main \\\hline
  Bestand & de.oberbrechen.koeb.gui.bestand.Main \\\hline
  Veranstaltungen & de.oberbrechen.koeb.gui.veranstaltungen.Main \\\hline
\end{tabular}
\caption{Main-Klassen der einzelnen Oberflächen}
\label{table_classes}
\end{table*}

Beim Aufruf ist zu beachten, dass alle JAR-Dateien, die sich im Verzeichnis
\texttt{lib} befinden, in den Java-Class-Path eingebunden werden müssen. Die
zum Start nötigen Java-Aufrufe sind abhängig vom verwendeten Betriebssystem.
Start-Skripte für Windows (\emph{startAdmin.bat, \ldots}) und Linux
(\emph{startAdmin, \ldots}) befinden sich in dem Verzeichnis, in das \KBVT\ 
entpackt wurde. Diese Skripte setzen voraus, dass Java ohne Angabe des Pfades
gestartet werden kann. Ist dies nicht der Fall, müssen die Skripte angepasst werden.
Meist bietet es sich an, abhängig von der verwendeten Umgebung eigene
Start-Vereinfachungen zu erstellen. Z.\,B. könnten unter Windows
Startmenü-Einträge sinnvoll sein. Hierbei eventuell nützliche Icons finden Sie
im Verzeichnis \texttt{icons}.

Für den Fall, dass Sie nicht die vorkompilierte Version nutzen,
sondern selbst die Quellen kompilieren möchten, steht ein Ant-Build-File zur
Verfügung. Dies ist besonders dann nützlich, wenn Sie \KBVT\ anpassen oder
erweitern möchten. Um das Build-File nutzen zu können, benötigen Sie 
\emph{Apache Ant}\footnote{\urlant}. Für Informationen zur Nutzung des Build-Files
sei auf die Dokumentation von Ant und das Build-File selbst verwiesen.

\chapter{Einrichtung}
Nach der Installation ist es erforderlich, \KBVT\ für ihre speziellen
Anforderungen zu konfigurieren. Es sind z.\,B. die Mitarbeiter anzulegen oder
die Ausleihfristen festzulegen. In diesem Abschnitt soll nur kurz aufgezählt
werden, welche Arbeitsschritte üblicherweise erforderlich sind. Alle diese
Arbeitsschritte können mit Hilfe der Admin-Oberfläche ausgeführt werden. Für
die detaillierte Beschreibung der Arbeitsschritte wird jeweils auf den
entsprechenden Abschnitt der Beschreibung der Admin-Oberfläche verwiesen
werden. Die Arbeitsschritte sind weitestgehend unabhängig voneinader und
können in beliebiger Reihenfolge ausgeführt werden.

Beim ersten Start der Admin"=Oberfläche sind normalerweise noch keine
Mitarbeiter in der Datenbank eingetragen. Daher wird automatisch ein neuer
Mitarbeiter erstellt. Dieser besitzt kein Passwort. Den Anmelde"=Dialog (siehe
Abb.~\ref{abb_anmeldedialog}) können Sie daher beim ersten Start ohne Eingabe
eines Passworts beenden. Es ist sinnvoll, zuerst die benötigten Mitarbeiter
anzulegen und Passwörter für diese Mitarbeiter zu setzen. Lesen Sie hierzu
bitte Abschnitt~\ref{section_mitarbeiter} und Abschnitt~\ref{chap_sicherheit}.

\begin{figure*}[htbp]
  \centering
    \includegraphics[scale=0.4]{anmeldedialog}      
  \caption{Anmeldedialog}
  \label{abb_anmeldedialog}
\end{figure*}

Anschließend ist es sinnvoll, die benötigten Medientypen festzulegen. In der
bei der Installation angelegten Datenbank sind bereits die üblicherweise
benötigten Medientypen eingetragen. Entfernen Sie gegebenenfalls nicht
benötigte Medientypen und legen Sie neue Medientypen an. Beachten Sie bitte,
dass Medientypen, zu denen Medien in der Datenbank eingetragen sind, nicht
gelöscht werden können. Details finden Sie in Abschnitt~\ref{section_medientypen}.

Nach der Festlegung der zu verwendenden Medientypen kön\-nen Sie die für die 
Ausleih


\chapter{Admin-Oberfläche}

\section{Ausleihordnung / Öffnungszeiten}
\label{section_ausleihordnung}

\section{Mitarbeiter}
\label{section_mitarbeiter}

\section{Medientypen}
\label{section_medientypen}

\chapter{Datensicherung}

\chapter{Internetzugangskontrolle}

\chapter{Sicherheit}
\label{chap_sicherheit}

%%% Local Variables: 
%%% mode: latex
%%% TeX-master: "handbuch-a5"
%%% End: 
