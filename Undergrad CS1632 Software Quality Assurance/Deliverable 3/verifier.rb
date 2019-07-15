require 'benchmark'
require_relative 'file_parser'
require_relative 'time_stamp_checker'
require_relative 'error_checker'
require_relative 'hash_checker'
require_relative 'block_num_checker'
require_relative 'transaction_checker'

def Verify(file)

  file_parser = FileParser::new
  arr_of_arrs = file_parser.parse_file(file)
  error_checker = ErrorChecker::new

  if arr_of_arrs != 1

    # Now each block from every line is in its own array
    block_nums = arr_of_arrs[0]
    prev_block_hashes = arr_of_arrs[1]
    transactions = arr_of_arrs[2]
    time_stamps = arr_of_arrs[3]
    new_hashes = arr_of_arrs[4]

    split = (block_nums.count / 8) # Divisor here is the number of threads!
    # For each additional/less thread, you should add/remove a bn/pb/tr/ts/nh var!
    bn1, bn2, bn3, bn4, bn5, bn6, bn7, bn8 = block_nums.each_slice(split).to_a
    pb1, pb2, pb3, pb4, pb5, pb6, pb7, pb8 = prev_block_hashes.each_slice(split).to_a
    tr1, tr2, tr3, tr4, tr5, tr6, tr7, tr8 = transactions.each_slice(split).to_a
    ts1, ts2, ts3, ts4, ts5, ts6, ts7, ts8 = time_stamps.each_slice(split).to_a
    nh1, nh2, nh3, nh4, nh5, nh6, nh7, nh8 = new_hashes.each_slice(split).to_a

    # Need to reconstruct an array for each thread!
    arr1 = [bn1, pb1, tr1, ts1, nh1]
    arr2 = [bn2, pb2, tr2, ts2, nh2]
    arr3 = [bn3, pb3, tr3, ts3, nh3]
    arr4 = [bn4, pb4, tr4, ts4, nh4]
    arr5 = [bn5, pb5, tr5, ts5, nh5]
    arr6 = [bn6, pb6, tr6, ts6, nh6]
    arr7 = [bn7, pb7, tr7, ts7, nh7]
    arr8 = [bn8, pb8, tr8, ts8, nh8]

    begin
      # Check blocks for numerical order
      block_num_checker = BlockNumChecker::new
      err = block_num_checker.check_block(block_nums)
      break if err > 0

      # Check timestamp monotonical incrimentation
      time_stamp_checker = TimeStampChecker::new
      err = time_stamp_checker.check_time_stamp(time_stamps)
      break if err > 0

      # Check current hash is valid, and previous hash matches new hash of previous line
      hash_checker = HashChecker::new
      err = hash_checker.check_all(arr_of_arrs, arr1, arr2, arr3, arr4, arr5, arr6, arr7, arr8)
      break if err > 0

      # Check transactions for negative balances
      transaction_checker = TransactionChecker::new
      errr = transaction_checker.check_transaction(transactions)
      break if errr > 0

    end while false

    # Check to see if we have any error
    error_checker.check_error(err) # pass in more values when classes are finished
  else
    error_checker.check_error(arr_of_arrs)
  end
end

##############
#### MAIN ####
##############
raise "Please input only a file name" unless ARGV.count == 1
raise "File does not exist" unless File.file?(ARGV[0])

time = Benchmark.measure {
  Verify(ARGV[0])
}

puts "Finished in #{time.real} seconds"
