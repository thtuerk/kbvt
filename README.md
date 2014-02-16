KBVT ist ein Tool für kleinere und mittlere Büchereien. Unter anderem
ermöglicht es die Verwaltung des Medienbestandes, der Benutzer
und ermöglicht eine elektronische Ausleihe. Außerdem können
Veranstaltungen und Anmeldelisten verwaltet werden. 

Es wurde hauptsächlich im Jahr 2002 und 2003 für die KöB Oberbrechen
entwickelt und dort bis vor kurzem eingesetzt. Der Quellcode wurde
auch bis vor kurzem über die Seiten der KöB Oberbrechen verteilt und
auf um das Jahr 2002 populären Internetseiten beworben. Inzwischen hat
sich viel geändert. Vielleicht ist KBVT jedoch - obwohl in die
Jahre gekommen - immer noch für kleine deutsche Büchereien
interessant, die nicht viel Geld für Software ausgeben können oder
wollen. Der Quellcode ist komplett auf Deutsch dokumentiert und mit
Blick auf leichte Anpassbarkeit geschrieben.

Da die Originalseiten inzwischen abgeschaltet wurden, stelle ich KBVT
nun hier zur Verfügung. Den Code habe ich auf einer aktuellen Ubuntu
16.04 Installation mit aktuellem Java und MySQL kurz
getestet. Abgesehen von minimalen Änderungen die ich an der SQL Syntax
für die initialisierung einer leeren Datenbank vornehmen mußte, schien
auf den ersten Blick noch alles zu funktionieren.  Ich übernehme
jedoch keinerlei Garantien. Ich selbst habe KBVT seit Jahren nicht
weiterentwickelt und in der Bücherei wurde es auf einem sehr alten
Rechner mit alten Softwareversionen benutzt.


# Kurzinstallationsanleitung

- neue MySQL Datenbank erstellen
- diese Datenbank mit `leere_datenbank.sql` initialisieren
- `einstellungen.conf` anpassen
- KBVT mittels `ant Run_Ausleihe` compilieren und starten