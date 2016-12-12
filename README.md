KBVT ist ein Tool f�r kleinere und mittlere B�chereien. Unter anderem
erm�glicht es die Verwaltung des Medienbestandes, der Benutzer
und erm�glicht eine elektronische Ausleihe. Au�erdem k�nnen
Veranstaltungen und Anmeldelisten verwaltet werden. 

Es wurde haupts�chlich im Jahr 2002 und 2003 f�r die K�B Oberbrechen
entwickelt und dort bis vor kurzem eingesetzt. Der Quellcode wurde
auch bis vor kurzem �ber die Seiten der K�B Oberbrechen verteilt und
auf um das Jahr 2002 popul�ren Internetseiten beworben. Inzwischen hat
sich viel ge�ndert. Vielleicht ist KBVT jedoch - obwohl in die
Jahre gekommen - immer noch f�r kleine deutsche B�chereien
interessant, die nicht viel Geld f�r Software ausgeben k�nnen oder
wollen. Der Quellcode ist komplett auf Deutsch dokumentiert und mit
Blick auf leichte Anpassbarkeit geschrieben.

Da die Originalseiten inzwischen abgeschaltet wurden, stelle ich KBVT
nun hier zur Verf�gung. Den Code habe ich auf einer aktuellen Ubuntu
16.04 Installation mit aktuellem Java und MySQL kurz
getestet. Abgesehen von minimalen �nderungen die ich an der SQL Syntax
f�r die initialisierung einer leeren Datenbank vornehmen mu�te, schien
auf den ersten Blick noch alles zu funktionieren.  Ich �bernehme
jedoch keinerlei Garantien. Ich selbst habe KBVT seit Jahren nicht
weiterentwickelt und in der B�cherei wurde es auf einem sehr alten
Rechner mit alten Softwareversionen benutzt.


# Kurzinstallationsanleitung

- neue MySQL Datenbank erstellen
- diese Datenbank mit `leere_datenbank.sql` initialisieren
- `einstellungen.conf` anpassen
- KBVT mittels `ant Run_Ausleihe` compilieren und starten