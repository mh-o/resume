# comment
class LineExecutor
  VAL_REGEX = /^[1]{1}$/
  VAR_REGEX = /^[2]{1}$/
  OP_REGEX = /^[[1-2]{2,}[3]{1,}]*[1-2]{1,}[3]{1,}$/
  KGEX = /^[4]{1}[1-3]{1,}$/

  # Accepts an array of inputs and string of types and decides what to do
  def decider(arr, str, variables, l_n)
    exit! if arr[0].to_s.casecmp('QUIT').zero?
    keyword_ops = KeywordOperations.new
    return arr[0].to_i if str.match(VAL_REGEX)
    return keyword_ops.get_val(arr[0], variables) if str.match(VAR_REGEX)
    decider2(arr, str, variables, l_n)
  end

  # stuff
  def decider2(arr, str, variables, l_n)
    keyword_ops = KeywordOperations.new
    error_handler = ErrorHandler.new

    return keyword_ops.pik_keyword(arr, str, variables, l_n) if str.match(KGEX)
    return op_handler(arr, str, variables, l_n) if str.match(OP_REGEX)
    error_handler.decider(arr, str, l_n)
  end

  # Accepts an array of inputs and string of types and makes sure vars exist
  def op_handler(arr, str, variables, l_n)
    keyword_ops = KeywordOperations.new

    cont, ans = keyword_ops.check_init(keyword_ops.vars_to_nums(arr, variables))
    return all_ops(keyword_ops.vars_to_nums(arr, variables), str, l_n) if cont

    ans
  end

  # Use stack algorithm to solve RPN
  def all_ops(arr, str, l_n)
    stack = []

    arr.each_with_index do |x, i|
      if str[i] == '3'
        switch = do_operator(x, stack)

        return "Operator #{x} applied to empty stack" if switch == 1
      end

      do_operand(x, stack) if str[i] == '1' || str[i] == '2'
    end

    check_stack_overflow(stack, l_n)
  end

  def check_stack_overflow(stack, l_n)
    res = stack.pop

    return "Line #{l_n}: elements in stack after evaluation" if stack.count > 0

    res
  end

  # Handle operator being applied to an empty stack
  def check_empty_stack(val1, val2)
    return true if val1.nil? || val2.nil?
    false
  end

  # Handle operator token case
  def do_operator(opp, stack)
    add_subs = %w[+ -]
    val2 = stack.pop
    val1 = stack.pop

    return 1 if check_empty_stack(val1, val2)

    res = add_sub(opp, val1, val2) if add_subs.include?(opp)
    res = mul_div(opp, val1, val2) unless add_subs.include?(opp)
    stack.push(res)
    0
  end

  # Handle operand token case
  def do_operand(token, stack)
    stack.push(token)
  end

  # Accepts an operation and two values and @return operation on these vals
  def add_sub(opp, val1, val2)
    return (val1.to_i + val2.to_i) if opp == '+'
    (val1.to_i - val2.to_i)
  end

  # Does a thing
  def mul_div(opp, val1, val2)
    return (val1.to_i * val2.to_i) if opp == '*'
    (val1.to_i / val2.to_i)
  end
end
