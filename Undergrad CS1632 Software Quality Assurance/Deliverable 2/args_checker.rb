class ArgsChecker

  def check_args arr
    raise "Enter a seed and only a seed." unless arr.count == 1
    arr
  end

  def assume_zero arr
    result = Integer(arr[0]) rescue false
    if !result
      0
    else
      arr
    end
  end

end
