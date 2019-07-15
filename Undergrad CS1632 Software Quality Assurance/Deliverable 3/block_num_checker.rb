class BlockNumChecker
  # Make sure blocks start at 0 and incriment by 1
  def check_block (arr)
    i = 0

    return 1 unless arr[i] == '0'

    for x in arr
      if i != 0
        if arr[i].to_i != (arr[i-1].to_i + 1)
          display_error(i, arr[i], arr[i-1])
          return 1
        end
      end

      i += 1
    end

    return 0
  end

  # Display out of order error
  def display_error(i, given, correct)
    puts "Line #{i+1}: Invalid block number #{given}, should be #{correct.to_i + 1}"
  end

end
