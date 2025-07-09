# Labels
set title 'Embedded Database Comparison from benchmark.csv'
set ylabel 'SQL Operations per second'
set xlabel 'Database'
set xtics nomirror rotate by -45

# Ranges
set autoscale

# Input
set datafile separator ','

# Output
set terminal png enhanced font "Verdana,9"
set output 'benchmark.png'
set grid
set key off
set boxwidth 0.8 relative

# Box style
set style line 1 lc rgb '#5C91CD' lt 1
set style fill solid

# Remove top and right borders
set style line 2 lc rgb '#808080' lt 1
set border 3 back ls 2
set tics nomirror

# Indices are '1' based.
# every ::1 => 1. Zeile überspringen (Überschrift)
# using 0:5 => 0. Spalte X-Achse, 5. Spalte Y-Achse

plot 'benchmark.csv' every ::1 using 0:5:xticlabels(8) with boxes ls 1,\
     'benchmark.csv' every ::1 using 0:($5 + 1500):(sprintf("%d",$5)) with labels offset char 0,1

# Create Image
# gnuplot benchmark.plt
