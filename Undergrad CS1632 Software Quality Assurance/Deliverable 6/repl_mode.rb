# top comment
class ReplMode
  # Continue to run REPL untill user elects to quit
  def run_repl(variables, l_n)
    loop do
      break unless repl_loop(variables, l_n)
    end
  end

  # Handle a single line of code in REPL, quit on QUIT key
  def repl_loop(variables, l_n)
    token_parser = TokenParser.new
    line_executor = LineExecutor.new

    input = prompt_for_input(l_n)
    return true if input.strip == ''
    input_arr = token_parser.input_to_arr(input)
    types_str = token_parser.parse_arr(input_arr)

    exit! if input_arr[0].to_s.casecmp('QUIT').zero?
    display(line_executor.decider(input_arr, types_str, variables, l_n), l_n)
    true
  end

  # Prompt user to enter input @return input as string
  def prompt_for_input(l_n)
    print '> '
    l_n.inc
    gets.chomp
  end

  def check_flag(res)
    return true if res.include?('variable')
    return true if res.include?('empty')
    return true if res.include?('evaluation')
    return true if res.include?('unset')
    false
  end

  # Accept a float and trim decimal if it is an integer
  def display(res, l_n)
    if res.is_a? String
      if check_flag(res)
        puts "Line #{l_n.get}: #{res}"
        return
      end
    end

    puts res if (res != '' || res.is_a?(Integer)) && !check_flag(res.to_s)
  end
end
