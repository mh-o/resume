# COMMENT
class FileMode
  def run_file(arr, vars, l_n)
    @lines = []

    loop do
      break unless file_loop(arr, vars, l_n)
    end
  end

  def decs
    repl_mode = ReplMode.new
    line_executor = LineExecutor.new
    token_parser = TokenParser.new
    [repl_mode, line_executor, token_parser]
  end

  def file_loop(arr, vars, l_n)
    deca = decs
    files_to_arr(arr)

    @lines.each do |line|
      l_n.inc
      inb = line.to_s.split(' ')[0]
      ina = deca[2].input_to_arr(line.to_s)
      types_str = deca[2].parse_arr(ina).to_s
      exit unless displ(deca[1].decider(ina, types_str, vars, l_n), l_n, inb)
    end

    false
  end

  def files_to_arr(arr)
    arr.each do |x|
      file_to_arr(x)
    end
  end

  def file_to_arr(path)
    unless File.exist?(path)
      puts "No such file '#{path}'"
      exit
    end

    text = File.open(path).read
    text.gsub!(/\r\n?/, "\n")
    text.each_line do |line|
      @lines.push(line)
    end
  end

  def check_flag(res)
    return true if res.include?('variable')
    return true if res.include?('empty')
    return true if res.include?('evaluation')
    return true if res.include?('unset')
    return true if res.include?('Unknown')
    false
  end

  # Accept a float and trim decimal if it is an integer
  def displ(res, l_n, inb)
    if res.is_a? String
      if check_flag(res)
        puts "Line #{l_n.get}: #{res}"
        return false
      end
    end

    puts_res(res) unless inb.casecmp('LET').zero?

    true
  end

  def puts_res(res)
    puts res if (res != '' || res.is_a?(Integer)) && !check_flag(res.to_s)
  end
end
