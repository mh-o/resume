run_aging() {
  NUMFRAMES=$1
  ALGORITHM=$2
  INFILE=$3
  REFRESH=$4

  ./vmsim -n "$NUMFRAMES" -a "$ALGORITHM" -r "$REFRESH" "$INFILE" >> age.writes.txt
}

# MAIN
> age.writes.txt # Clear the results file

echo "Writing all permutations to age.faults.txt..."
start=`date +%s`

for a in aging
do
  for f in gcc.trace
  do
    for n in 4 8 16 32 64
    do
      for r in {2..100}
      do
        #echo "$r" >> age.writes.txt

        run_aging $n $a $f $r
      done

      echo $'\r' >> age.writes.txt
    done
  done
done

end=`date +%s`
runtime=$((end-start))
echo "...finished in $runtime seconds."
