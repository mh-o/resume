class TimeStampChecker

  attr_accessor :time_in_seconds
  attr_accessor :time_in_nano

  def initialize
    @time_in_seconds = 0
    @time_in_nano = 0
  end

  # This function recieves an array of timestamps and checks to make sure they
  # are in ascending order. Will print the location of the error if one is found.
  # @param array of timestamps
  # @return 1 if error is found, 0 otherwise
  def check_time_stamp arr
    i = 0

    for x in arr
      split_str = x.to_s.split('.')

      if check_seconds(split_str[0]) == 0
        if check_nano(split_str[1]) == 1
          display_error(i, arr[i-1], arr[i])
          return 1 # error in nano
        end
      elsif check_seconds(split_str[0]) == 1 # error in seconds
        display_error(i, arr[i-1], arr[i])
        return 1
      end

      i += 1
    end

    return 0 # No error
  end

  def check_seconds(val)
    if val.to_i > @time_in_seconds
      @time_in_seconds = val.to_i
      @time_in_nano = 0
      return -1
    elsif val.to_i == @time_in_seconds
      return 0
    else
      return 1
    end

    return 0
  end

  def check_nano(val)
    if val.to_i > @time_in_nano
      @time_in_nano = val.to_i
    else
      puts val.to_i
      puts @time_in_nano
      return 1
    end

    return 0
  end

  def display_error(i, prev, cur)
    puts "Line #{i.to_s}: Previous timestamp #{prev} >= new timestamp #{cur}"
  end
end
