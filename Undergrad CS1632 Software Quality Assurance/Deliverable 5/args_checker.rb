# args_checker class
class ArgsChecker
  def check_args(arr)
    raise 'Enter a seed and only a seed.' unless arr.count == 1
    arr
  end

  def assume_zero(arr)
    return 0 unless arr[0].is_a? Integer
  end
end
