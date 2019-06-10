require_relative 'args_checker'
require_relative 'repl_mode'
require_relative 'file_mode'
require_relative 'token_parser'
require_relative 'keyword_ops'
require_relative 'error_handler'
require_relative 'variables'
require_relative 'l_n'

# Accepts a boolean and array of arguments.
# If true => run REPL mode
# if false => pass ARGV and execute files
def run_mode(arr)
  stuff = start
  args_checker = ArgsChecker.new

  if args_checker.check_mode(arr)
    repl_mode = ReplMode.new
    repl_mode.run_repl(stuff[0], stuff[1])
  else
    file_mode = FileMode.new
    file_mode.run_file(arr, stuff[0], stuff[1])
  end
end

def start
  variables = Variables.new
  l_n = LineNumber.new
  l_n.start
  variables.start

  [variables, l_n]
end

# MAIN CODE STARTS HERE
run_mode(ARGV)
