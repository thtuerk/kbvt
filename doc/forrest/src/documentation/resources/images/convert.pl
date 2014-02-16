$name = "$ARGV[0]";
$anzahl = "$ARGV[1]";
for ($i=1; $i <= $anzahl; $i++) {
    `convert -resize x100 $name$i.png $name$i-klein.jpg`;
}
