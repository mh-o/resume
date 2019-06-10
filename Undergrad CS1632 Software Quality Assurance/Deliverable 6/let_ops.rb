# comment
class LetOperations
  LET_SIMPLE_SET_REGEX = /^[4]{1}[2]{1}[1]{1}$/
  LET_VAR_TO_VAR_REGEX = /^[4]{1}[2]{2}$/
  LET_OP_REGEX = /^[4]{1}[[1-2]{2,}[3]{1,}]*[1-2]{1,}[3]{1,}$/

  # Decide which opperation to perform
  def pik_let_op(arr, str, variables, l_n)
    return let_simple_set(arr, variables) if str.match(LET_SIMPLE_SET_REGEX)
    return let_var_to_var(arr, variables) if str.match(LET_VAR_TO_VAR_REGEX)
    let_op(arr, variables, l_n) if str.match(LET_OP_REGEX)
  end

  # Handle LET followed by value
  def let_simple_set(arr, variables)
    variables.set_var(arr[1], arr[2])
    arr[2].to_i
  end

  def let_var_to_var(arr, variables)
    keyword_ops = KeywordOperations.new
    variables.set_var(arr[1], keyword_ops.get_val(arr[2], variables))
  end

  # Handle LET followed by expression
  def let_op(arr, variables, l_n)
    keyword_ops = KeywordOperations.new
    line_executor = LineExecutor.new
    token_parser = TokenParser.new

    clone = keyword_ops.clone_array(arr)
    arr.shift(2)
    arr = keyword_ops.vars_to_nums(arr, variables)
    str = token_parser.parse_arr(arr)
    variables.set_var(clone[1], line_executor.decider(arr, str, variables, l_n))
  end
end
