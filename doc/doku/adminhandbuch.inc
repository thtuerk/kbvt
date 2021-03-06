\chapter{Installation}

\section{Aktuelle Versionen}
In den meisten F�llen ist es sinnvoll die jeweils aktuellste Version von \KBVT\
und auch die aktuellste Dokumentation zu verwenden. Beides k�nnen Sie 
auf der \KBVT-Homepage\footnote{\urlkbvt} erhalten.

\KBVT-Versionen besitzen eine dreistellige Versionsnummer\index{Versionsnummer}
(z.B. 0.6.2). Die erste Stelle gibt die Nummer der gro�en Entwicklungschritte
an. 0 bedeutet hier also, dass noch kein gro�er Entwicklungsschritt erreicht
wurde. \KBVT\ befindet sich noch in der Entwicklung. Es werden keinerlei
Garantien gegeben, dass die wesentlichen Schnittstellen und
Benutzeroberfl�chen nicht mehr ver�ndert werden. 

Die zweite Stelle der Versionsnummer kennzeichnet die Version des verwendeten
Datenbankschemas. Versionen, die in den ersten beiden Stellen �bereinstimmen,
verwenden also die gleiche Datenbank. Wenn Sie also z.\,B. Version 0.6.1 nach
0.6.2 updaten, m�ssen Sie nur die Anwendungssoftware, nicht jedoch die
Datenbank aktualisieren. Damit k�nnen Versionen, die sich nur in der letzten
Stelle unterscheiden auch parallel auf der gleichen Datenbank arbeiten. 

Die letzte Stelle schlie�lich dient zur Unterscheidung zwischen Versionen, die
das gleiche Datenbankschema benutzen. Trotz des gleichen Datenbankschemas
k�nnen sich diese Versionen erheblich unterscheiden. Lesen Sie bitte jeweils
die Beschreibung der �nderungen auf der \KBVT-Homepage.

Meist ist es sinnvoll, die jeweils aktuellste Version von \KBVT\ zu
verwenden. F�r den Fall, dass Sie weiterhin mit einem alten Datenbankschema
arbeiten m�chten, werden weiterhin auch alte Versionen zum
Download angeboten.


\section{Systemvoraussetzungen}
\KBVT\ ist auf allen Systemen lauff�hig, f�r die eine
Java-Implementierung\footnote{\urljava} existiert und eine
MySQL-Datenbank\footnote{\urlmysql} zur Verf�gung steht. Sie ben�tigen
mindestens Java 1.3.1. Im Zusammenspiel mit Java 1.4.x wurde von Problemen im
Zusammenspiel mit einem Barcode-Scanner berichtet, die aber bisher noch nicht
�berpr�ft werden konnten. Weiter wird MySQL in mindestens der Version 3.23
ben�tigt. Benutzt werden aber -- soweit vorhanden -- auch die zus�tzlichen Features
von Version 4. Hier steht zus�tzlich Transitionsschutz und die automatische
�berpr�fung einiger Konsistenzeigenschaften zu Verf�gung.

Da \KBVT\ eine Java-Swing Oberfl�che benutzt und da bei der Entwicklung mehr
Wert auf ein leicht verst�ndliches, anpassbares und erweiterbares Design als
auf Performance gelegt wurde, ist \KBVT\ im Vergleich zu �hnlichen Programmen
leider recht langsam. In der K�B Oberbrechen wurde \KBVT\ lange auf einem
Pentium 100 mit 32 MB RAM unter SuSE Linux 7.1 verwendet. Dies ist f�r
sinnvolles Arbeiten gerade noch ausreichend, wenn man akzeptiert, dass ein
Start von \KBVT\ 2-3 Minuten und das T�tigen einer Ausleihe mittels des
Barcode-Scanners ca. 1 Sekunde dauert. Auf einem 500 MHz AMD-K6 mit 256 MB RAM
unter Windows XP startet KBVT dagegen in ca. 10 Sekunden.

\section{Datenbankeinrichtung}
Um \KBVT\ benutzen zu k�nnen, ben�tigen Sie einen MySQL-Datenbankserver.
Dieser Server muss nicht auf dem gleichen Rechner installiert sein, auf dem
sie \KBVT\ einsetzen m�chten. Sie k�nnen auch von evtl. mehreren \KBVT-Clients
einen gemeinsamen Server �ber ein Netzwerk benutzen. Die Installation von
MySQL soll hier nicht beschrieben werden. Informationen hierzu finden Sie in
der Dokumentation von MySQL.

Nach der Installation muss die Datenbank f�r \KBVT\ eingerichtet werden. Die
Datei \emph{leereDatenbank.sql} enth�lt SQL-Befehle, die eine neue Datenbank
anlegen, die ben�tigten Tabellen erzeugen und die Tabellen mit einigen
wenigen, grundlegenden Daten f�llen. Genaue Informationen, wie sie diese Datei
einspielen k�nnen, finden Sie in der Dokumentation zu MySQL. �blicherweise ist
das Einspielen mittels \texttt{mysql -u root < leereDatenbank.sql} m�glich.
Dies sollte eine neue Datenbank namens \texttt{koebDB} angelegen und mit den
n�tigen Tabellen und Daten f�llen. Sollten Sie einen anderen
Da\-ten\-bank\-namen w�nschen, muss die Datei \emph{leereDatenbank.sql} vor
dem Einspielen bearbeitet werden.

Bei der Installation erstellt MySQL automatisch einen Datenbankbenutzer namens
\emph{root}, der ohne sich durch ein Passwort authentifizieren zu m�ssen,
vom lokalen Rechner aus alle Zugriffsrechte auf alle Datenbanken besitzt. Aus
Sicherheitsgr�nden ist es empfehlenswert, ein Passwort f�r diesen Benutzer
einzurichten und einen Benutzer f�r den Zugriff auf die von \KBVT\ benutzte
Datenbank einzurichten. Dies ist zwingend erforderlich, wenn von einem anderen
Rechner auf die Datenbank zugegriffen werden soll. Der MySQL-Befehl, der einem
Benutzer \emph{benutzer}, der sich durch das Passwort \emph{passwort}
ausweist, an einem Rechner \emph{rechner} alle Rechte an der Datenbank
\emph{koebDB} gibt, lautet: \begin{flushleft}\texttt{grant all on koebDB.* to
  benutzer@rechner identified by 'passwort'}
\end{flushleft}
Wei\-ter\-f�h\-rende
Informationen finden Sie in der MySQL"=Dokumentation.

\section{Grundlegende Konfiguration}

\KBVT\ speichert Konfigurationseinstellungen auf zwei verschiedene Arten. Die
Datei \emph{einstellungen.conf} enth�lt alle Informationen, die ben�tigt
werden, um auf die Datenbank zuzugreifen. Alle weiteren Informationen sind
in der Datenbank gespeichert. An dieser Stelle soll auf die Datei
\emph{einstellungen.conf} eingegangen werden. Die Einstellungen, die
in der Datenbank gespeichert werden, werden in Abschnitt~\ref{einstellungen_datenbank} erkl�rt.

Die Datei \emph{einstellungen.conf} ist eine Textdatei, deren Zeilen
Name-Wert-Paare darstellen. Unterst�tzte Einstellungen sind: 

\begin{definitionlist}
\definition{Datenbank-Factory}{
  \KBVT\ ist daf�r ausgelegt, unterschiedliche Datenbankanwendungen zu
  unter\-st�t\-zen.  Mittels der Einstellung \emph{Datenbank-Factory} kann
  festgelegt werden, welcher Datenbanktyp benutzt werden soll. Zur Zeit wird
  jedoch nur MySQL unterst�tzt. Bis auf \emph{zeigeDebug} und
  \emph{ClientNr} beziehen sich alle weiteren Einstellungen auf die durch
  diese Einstellung bestimmte konkrete Datenbankanwendung. Die im folgenden
  beschriebenen Einstellungen beziehen sich auf MySQL. Informationen
  dazu, wie Sie Schnittstellen zu anderen Datenbankenanwendungen wie z.\,B. zu
  Postgres entwicklen k�nnen, finden Sie im Handbuch f�r Entwickler.
}
\definition{zeigeDebug}{
  Die erlaubten Werte sind \texttt{true} und \texttt{false}. Diese Einstellung
  bestimmt, ob Debug-Informationen ausgegeben werden sollen. Dabei handelt es
  sich um Informationen, die f�r Adminstratoren und Entwickler interessant
  sein k�nnen, den normalen Benutzer aber oft st�ren und verwirren. Daher
  sollte diese Einstellung im normalen Betrieb auf \texttt{false} gesetzt
  werden.
  
  Zur Zeit bestimmt diese Einstellung lediglich, ob �ber fehlende oder
  unsinnige Einstellungen informiert werden soll.
}
\definition{zeigeDebug}{
  Manche Einstellungen m�ssen einem speziellen Client zugeordent werden. Ein
  Beispiel daf�r ist eine Einstellung, die bestimmt, welches Programm zur
  Anzeige von PDF-Dateien benutzt werden soll. Die Einstellung
  \emph{ClientNr} dient dazu, Clients eindeutig zu indentifizieren. G�ltige
  Werte sind alle ganzen, positiven Zahlen. Es ist meist sinnvoll, f�r jeden
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
Nach diesen Vorbereitungen ist \KBVT\ bereit f�r den ersten Start. Im
Verzeichnis \emph{lib} befinden sich die von \KBVT\ ben�tigten Bibliotheken
sowie eine vorkompilierte Version von \KBVT. Tabelle~\ref{table_classes}
zeigt, welche Klasse f�r die einzelnen Oberfl�chen gestartet werden muss.

\begin{table*}[htbp]
\centering
\small
\begin{tabular}{|l|l|}
  \hline
  \textbf{Oberfl�che} & \textbf{Klasse} \\\hline\hline
  Admin & de.oberbrechen.koeb.gui.admin.Main \\\hline
  Ausgaben & de.oberbrechen.koeb.gui.ausgaben.Main \\\hline
  Ausleihe & de.oberbrechen.koeb.gui.ausleihe.Main \\\hline
  Bestand & de.oberbrechen.koeb.gui.bestand.Main \\\hline
  Veranstaltungen & de.oberbrechen.koeb.gui.veranstaltungen.Main \\\hline
\end{tabular}
\caption{Main-Klassen der einzelnen Oberfl�chen}
\label{table_classes}
\end{table*}

Beim Aufruf ist zu beachten, dass alle JAR-Dateien, die sich im Verzeichnis
\texttt{lib} befinden, in den Java-Class-Path eingebunden werden m�ssen. Die
zum Start n�tigen Java-Aufrufe sind abh�ngig vom verwendeten Betriebssystem.
Start-Skripte f�r Windows (\emph{startAdmin.bat, \ldots}) und Linux
(\emph{startAdmin, \ldots}) befinden sich in dem Verzeichnis, in das \KBVT\ 
entpackt wurde. Diese Skripte setzen voraus, dass Java ohne Angabe des Pfades
gestartet werden kann. Ist dies nicht der Fall, m�ssen die Skripte angepasst werden.
Meist bietet es sich an, abh�ngig von der verwendeten Umgebung eigene
Start-Vereinfachungen zu erstellen. Z.\,B. k�nnten unter Windows
Startmen�-Eintr�ge sinnvoll sein. Hierbei eventuell n�tzliche Icons finden Sie
im Verzeichnis \texttt{icons}.

F�r den Fall, dass Sie nicht die vorkompilierte Version nutzen,
sondern selbst die Quellen kompilieren m�chten, steht ein Ant-Build-File zur
Verf�gung. Dies ist besonders dann n�tzlich, wenn Sie \KBVT\ anpassen oder
erweitern m�chten. Um das Build-File nutzen zu k�nnen, ben�tigen Sie 
\emph{Apache Ant}\footnote{\urlant}. F�r Informationen zur Nutzung des Build-Files
sei auf die Dokumentation von Ant und das Build-File selbst verwiesen.

\chapter{Einrichtung}
Nach der Installation ist es erforderlich, \KBVT\ f�r ihre speziellen
Anforderungen zu konfigurieren. Es sind z.\,B. die Mitarbeiter anzulegen oder
die Ausleihfristen festzulegen. In diesem Abschnitt soll nur kurz aufgez�hlt
werden, welche Arbeitsschritte �blicherweise erforderlich sind. Alle diese
Arbeitsschritte k�nnen mit Hilfe der Admin-Oberfl�che ausgef�hrt werden. F�r
die detaillierte Beschreibung der Arbeitsschritte wird jeweils auf den
entsprechenden Abschnitt der Beschreibung der Admin-Oberfl�che verwiesen
werden. Die Arbeitsschritte sind weitestgehend unabh�ngig voneinader und
k�nnen in beliebiger Reihenfolge ausgef�hrt werden.

Beim ersten Start der Admin"=Oberfl�che sind normalerweise noch keine
Mitarbeiter in der Datenbank eingetragen. Daher wird automatisch ein neuer
Mitarbeiter erstellt. Dieser besitzt kein Passwort. Den Anmelde"=Dialog (siehe
Abb.~\ref{abb_anmeldedialog}) k�nnen Sie daher beim ersten Start ohne Eingabe
eines Passworts beenden. Es ist sinnvoll, zuerst die ben�tigten Mitarbeiter
anzulegen und Passw�rter f�r diese Mitarbeiter zu setzen. Lesen Sie hierzu
bitte Abschnitt~\ref{section_mitarbeiter} und Abschnitt~\ref{chap_sicherheit}.

\begin{figure*}[htbp]
  \centering
    \includegraphics[scale=0.4]{anmeldedialog}      
  \caption{Anmeldedialog}
  \label{abb_anmeldedialog}
\end{figure*}

Anschlie�end ist es sinnvoll, die ben�tigten Medientypen festzulegen. In der
bei der Installation angelegten Datenbank sind bereits die �blicherweise
ben�tigten Medientypen eingetragen. Entfernen Sie gegebenenfalls nicht
ben�tigte Medientypen und legen Sie neue Medientypen an. Beachten Sie bitte,
dass Medientypen, zu denen Medien in der Datenbank eingetragen sind, nicht
gel�scht werden k�nnen. Details finden Sie in Abschnitt~\ref{section_medientypen}.

Nach der Festlegung der zu verwendenden Medientypen k�n\-nen Sie die f�r die 
Ausleih


\chapter{Admin-Oberfl�che}

\section{Ausleihordnung / �ffnungszeiten}
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
