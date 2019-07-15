class FileParser
  attr_accessor :pattern

  # Contains REGEX to match an entire line
  def initialize
    @pattern = /^\d+[|]{1}([a-z0-9]{1,4})[|]{1}([a-zA-Z]{1,6}[>]{1}[a-zA-Z]{1,6}[(]{1}\d+[)]{1}){1}([:]{1}[a-zA-Z]{1,6}[>]{1}[a-zA-Z]{1,6}[(]{1}\d+[)]{1})*[|]{1}\d+[.]{1}\d+[|]{1}([a-z0-9]{1,4})$/
  end

  # This fucntion recieves a .txt file and sorts each line
  # into an array of elements of the same type.
  # @param .txt file
  # @return array[arr1[], arr2[], arr3[], arr4[], arr5[]]
  def parse_file file

    block_nums = []
    prev_block_hashes = []
    transactions = []
    time_stamps = []
    new_hashes = []
    i = 0

    File.open(file, "r") do |f| # Open file for reading
      f.each_line do |line| # For each line of the file, do:
        i += 1
        if check_regex(line) == 1
          display_error(i)
          return 1
        end
        split_line = line.split('|') # Split line into array of 5 elements

        # Push each element to corresponding array...
        block_nums.push(split_line[0])
        prev_block_hashes.push(split_line[1])
        transactions.push(split_line[2])
        time_stamps.push(split_line[3])
        new_hashes.push(split_line[4])
      end
    end

    return block_nums, prev_block_hashes, transactions, time_stamps, new_hashes
  end

  # Check each line against REGEX
  # NOTE: will NOT properly check when run with "ruby verifier.rb test_file.txt"
  # For Ruby, pattern.match(line) must be changed to pattern.match?(line)
  # For JRuby, use pattern.match(line)
  def check_regex(line)
    if pattern.match(line)
      return 0
    else
      return 1
    end
  end

  # Desplay systax error to the user (REGEX mismatch)
  def display_error(i)
    puts "Line #{i}: Invalid syntax"
  end
end
