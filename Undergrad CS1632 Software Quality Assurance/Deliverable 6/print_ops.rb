# comment
class PrintOperations
  # These are just the REGEXs from 'line_executor' with preceding [4]{1}s
  PRINT_VAL_REGEX = /^[4]{1}[1]{1}$/
  PRINT_VAR_REGEX = /^[4]{1}[2]{1}$/
  PRINT_OP_REGEX = /^[4]{1}[[1-2]{2,}[3]{1,}]*[1-2]{1,}[3]{1,}$/
  EXCESS_PRINT_REGEX = /^[4]{1}[1-2]{2,}$/

  # Accepts array of inputs and string of types and decides what to do
  def pik_print_op(arr, str, variables, l_n)
    ans = print_val(arr) if str.match(PRINT_VAL_REGEX)
    ans = print_var(arr, variables) if str.match(PRINT_VAR_REGEX)
    ans = print_op(arr, variables, l_n) if str.match(PRINT_OP_REGEX)
    ans = excess_print if str.match(EXCESS_PRINT_REGEX)
    ans
  end

  # Handle printing a single number
  def print_val(arr)
    arr[1]
  end

  # Handle printing a single variable
  def print_var(arr, variables)
    keyword_ops = KeywordOperations.new
    keyword_ops.get_val(arr[1], variables)
  end

  # Handle printing any operation
  def print_op(arr, variables, l_n)
    keyword_ops = KeywordOperations.new
    line_executor = LineExecutor.new
    token_parser = TokenParser.new

    arr.shift(1)
    arr = keyword_ops.vars_to_nums(arr, variables)

    return arr[0] if arr[0].to_s.include? 'variable'
    line_executor.decider(arr, token_parser.parse_arr(arr), variables, l_n)
  end

  def excess_print
    'Elements in stack after evaluation'
  end
end
