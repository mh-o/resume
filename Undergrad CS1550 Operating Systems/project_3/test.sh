run_aging() {
  NUMFRAMES=$1
  ALGORITHM=$2
  INFILE=$3

  ./vmsim -n "$NUMFRAMES" -a "$ALGORITHM" -r 25 "$INFILE" >> results.txt
  echo $'\r' >> results.txt
}

run_sims() {
  NUMFRAMES=$1
  ALGORITHM=$2
  INFILE=$3

  ./vmsim -n "$NUMFRAMES" -a "$ALGORITHM" "$INFILE" >> results.txt
  echo $'\r' >> results.txt
}

# MAIN
#> results.txt # Clear the results file

echo "Writing all permutations to results.txt..."
start=`date +%s`

for a in aging
do
  for f in gcc.trace swim.trace gzip.trace
  do
    for n in 4 8 16 32 64
    do
      echo "--- ALGORITHM: $a, TRACEFILE: $f, FRAMES: $n ----" >> results.txt
      echo $'\r' >> results.txt
      if [ $a = "aging" ]
      then
        run_aging $n $a $f
      else
        run_sims $n $a $f
      fi
    done
  done
done

end=`date +%s`
runtime=$((end-start))
echo "...finished in $runtime seconds."
