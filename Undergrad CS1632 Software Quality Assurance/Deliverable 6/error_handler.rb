# comment
class ErrorHandler
  VALS_REGEX = /^[1-2]{2,}$/
  UNKNOWN_KEY_REGEX = /[5]{1,}/

  def decider(arr, str, l_n)
    return 'Elements in stack after evaluation' if str.match(VALS_REGEX)
    return unknown_key(arr, str) if str.match(UNKNOWN_KEY_REGEX)
    "Line #{l_n.get}: Could not evaluate expression"
  end

  def unknown_key(arr, str)
    arr.each_with_index do |x, i|
      return "Unknownn keyword #{x.upcase}" if str[i] == '5'
    end
  end
end
