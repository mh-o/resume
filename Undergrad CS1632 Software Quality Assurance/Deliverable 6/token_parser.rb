# comment
class TokenParser
  # Accepts an input string @return array of items delimeted by ' '
  def input_to_arr(str)
    str.split(' ')
  end

  # Accepts an array of strings @return str of types
  def parse_arr(arr)
    str = ''

    arr.each do |x|
      str << check_type(x)
    end

    str
  end

  # Accepts a string @return its token type
  def check_type(str)
    return '1' if number?(str)
    return '2' if variable?(str)
    return '3' if operator?(str)
    return '4' if keyword?(str)
    return '5' if unknown_keyword?(str)
    '0'
  end

  # Accepts a string and @return true if it has a numeric value
  def number?(str)
    str.to_s.match(/\A[-+]?\d+\z/)
  end

  # Accepts a string and @return true if it is a valid variable
  def variable?(str)
    /^[A-Z]{1}$/ =~ str.upcase
  end

  # Accepts a string and @return true if it is a valid operator
  def operator?(str)
    operators = %w[+ - * /]
    operators.include?(str)
  end

  # Accepts a string and @return true if it is a valid keyword
  def keyword?(str)
    keywords = %w[LET PRINT QUIT]
    keywords.include?(str.upcase)
  end

  # Accepts a string and @return true if it is an unknown keyword
  def unknown_keyword?(str)
    /^[A-Z]{2,}$/ =~ str.upcase
  end
end
