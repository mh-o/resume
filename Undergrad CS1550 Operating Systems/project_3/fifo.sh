run_sims() {
  NUMFRAMES=$1
  ALGORITHM=$2
  INFILE=$3

  ./vmsim -n "$NUMFRAMES" -a "$ALGORITHM" "$INFILE" >> fifo.txt
}

# MAIN
> fifo.txt # Clear the results file

echo "Writing all permutations to fifo.txt..."
start=`date +%s`

for a in fifo
do
  for f in gcc.trace swim.trace gzip.trace
  do
    for n in {2..100}
    do
      #echo "$r" >> age.writes.txt

      run_sims $n $a $f
    done
    echo $'\r' >> fifo.txt
  done
done

end=`date +%s`
runtime=$((end-start))
echo "...finished in $runtime seconds."
