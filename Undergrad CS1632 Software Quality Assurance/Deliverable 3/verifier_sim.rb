require 'flamegraph'

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
      err = hash_checker.check_hash(arr_of_arrs)
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

Flamegraph.generate('verifier_sim.html') do
raise "Please input only a file name" unless ARGV.count == 1
raise "File does not exist" unless File.file?(ARGV[0])

  Verify(ARGV[0])

end
