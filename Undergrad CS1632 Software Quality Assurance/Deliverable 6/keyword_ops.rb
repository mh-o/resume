require_relative 'line_executor'
require_relative 'let_ops'
require_relative 'print_ops'

# comment
class KeywordOperations
  # Perform LET, PRINT, or QUIT depending on keyword
  def pik_keyword(arr, str, variables, l_n)
    let_ops = LetOperations.new
    print_ops = PrintOperations.new
    do_let = arr[0].casecmp('LET')
    do_print = arr[0].casecmp('PRINT')

    return let_ops.pik_let_op(arr, str, variables, l_n) if do_let.zero?
    print_ops.pik_print_op(arr, str, variables, l_n) if do_print.zero?
  end

  # Accepts an array and @return a un-linked clone of the array
  def clone_array(arr)
    clone = []

    arr.each do |x|
      clone.push(x)
    end

    clone
  end

  # Accepts an array of inputs and converts variables to numbers if they exist
  def vars_to_nums(arr, variables)
    token_parser = TokenParser.new

    arr.each_with_index do |x, i|
      arr[i] = get_val(x, variables) if token_parser.check_type(x) == '2'
    end

    arr
  end

  # Accepts a variable and @return value of that variable
  def get_val(var, variables)
    return variables.get_val(var) if var_init?(var, variables)
    "variable #{var} is not initialized"
  end

  # Accepts a variable and @return true if it has been LET
  def var_init?(var, variables)
    return false if variables.get_val(var).nil?
    true
  end

  # Accepts an array and @return error if any variaable is not initialized
  def check_init(arr)
    arr.each do |x|
      return false, x if x.to_s.include?('variable')
    end

    [true, nil]
  end
end
