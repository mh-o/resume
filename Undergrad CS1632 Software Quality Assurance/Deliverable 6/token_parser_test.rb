require 'minitest/autorun'
require_relative 'token_parser'

# UNIT TESTS FOR METHOD token_parser.rb

class TokenParserTest < Minitest::Test
  #tests the input to arr method by checking if it correctly parses the array
  def test_input_to_arr
    token_parser = TokenParser.new
    str = 'h e l l o'
    arr = ['h','e','l','l','o']
    assert_equal arr, token_parser.input_to_arr(str)
  end
  #tests to see whether or not passing in a number will return its string counterpart
  def test_number?
    token_parser = TokenParser.new
    str = '8'
    assert_equal str.to_s.match(/\A[-+]?\d+\z/), token_parser.number?(str)
  end
  #tests to see if number behaves correctly with an edge case
  def test_number_error
    token_parser = TokenParser.new
    str = 'taco'
    assert_nil token_parser.number?(str)
  end
  #checks to see if variable behaves correctly
  def test_variable?
    token_parser = TokenParser.new
    str = 'u'
    assert_equal 0, token_parser.variable?(str)
  end
  #tests corner case for variable method
  def test_variable_error?
    token_parser = TokenParser.new
    str = '8'
    assert_nil token_parser.variable?(str)
  end
  # tests the plus operator
  def test_operator_plus
    token_parser = TokenParser.new
    str = '+'
    assert token_parser.operator?(str)
  end
  #tests the minus operator
  def test_operator_minus
    token_parser = TokenParser.new
    str = '-'
    assert token_parser.operator?(str)
  end
  #tests the multiply operator
  def test_operator_times
    token_parser = TokenParser.new
    str = '*'
    assert token_parser.operator?(str)
  end
  #tets the divide operator
  def test_operator_divide
    token_parser = TokenParser.new
    str = '/'
    assert token_parser.operator?(str)
  end
  #tests a corner case for the operator method
  def test_operator_error
    token_parser = TokenParser.new
    str = 'taco'
    refute token_parser.operator?(str)
  end
  #tets to see if all keywords work as expected
  def test_keyword_let
    token_parser = TokenParser.new
    str = 'LET'
    assert token_parser.keyword?(str)
  end

  def test_keyword_print
    token_parser = TokenParser.new
    str = 'PRINT'
    assert token_parser.keyword?(str)
  end

  def test_keyword_quit
    token_parser = TokenParser.new
    str = 'QUIT'
    assert token_parser.keyword?(str)
  end
  #tets a corner case for the keyword method
  def test_keyword_error
    token_parser = TokenParser.new
    str = 'TACO'
    refute token_parser.keyword?(str)
  end
  #tests the unkown keyword method works
  def test_unknown_keyword
    token_parser = TokenParser.new
    str = 'Cheese'
    assert token_parser.unknown_keyword?(str)
  end
  #tests an edge case of the method
  def test_unknown_keyword_known?
    token_parser = TokenParser.new
    str = 'LET'
    assert 0, token_parser.unknown_keyword?(str)
  end
#tests the parse arr method witb a number
  def test_parse_arr_num
    token_parser = TokenParser.new
    arr = ['8']
    def check_type; '1'; end
    assert '1', token_parser.parse_arr(arr)
  end
  #tests the parse array method with a variable
  def test_parse_arr_variable
    token_parser = TokenParser.new
    arr = ['a']
    def check_type; '2'; end
    assert '2', token_parser.parse_arr(arr)
  end
  #tests the parse array method with an operator
  def test_parse_arr_operator
    token_parser = TokenParser.new
    arr = ['+']
    def check_type; '3'; end
    assert '3', token_parser.parse_arr(arr)
  end
  #tests the parse array method with a kword
  def test_parse_arr_keyword
    token_parser = TokenParser.new
    arr = ['LET']
    def check_type; '4'; end
    assert '4', token_parser.parse_arr(arr)
  end
  #test the parse arr method with multiple things
  def test_parse_arr_multiple
    token_parser = TokenParser.new
    arr = ['LET','x','10']
    assert_equal '421', token_parser.parse_arr(arr)
  end
  #the following tests test the check type method will all available choices
  def test_check_type_num
    token_parser = TokenParser.new
    def number?(str); true; end
    def variable?(str); false; end
    def operator?(str); false; end
    def keyword?(str); false; end
    def unknown_keyword?(str); false; end
    str = '8'
    assert_equal '1', token_parser.check_type(str)
  end

  def test_check_type_var
    token_parser = TokenParser.new
    def number?(str); false; end
    def variable?(str); true; end
    def operator?(str); false; end
    def keyword?(str); false; end
    def unknown_keyword?(str); false; end
    str = 'x'
    assert_equal '2', token_parser.check_type(str)
  end

  def test_check_type_operator
    token_parser = TokenParser.new
    def number?(str); false; end
    def variable?(str); false; end
    def operator?(str); true; end
    def keyword?(str); false; end
    def unknown_keyword?(str); false; end
    str = '+'
    assert_equal '3', token_parser.check_type(str)
  end

  def test_check_type_keyword
    token_parser = TokenParser.new
    def number?(str); false; end
    def variable?(str); false; end
    def operator?(str); false; end
    def keyword?(str); true; end
    def unknown_keyword?(str); false; end
    str = 'QUIT'
    assert_equal '4', token_parser.check_type(str)
  end

  def test_check_type_keyword_unknown
    token_parser = TokenParser.new
    def number?(str); false; end
    def variable?(str); false; end
    def operator?(str); false; end
    def keyword?(str); false; end
    def unknown_keyword?(str); true; end
    str = 'cheese'
    assert_equal '5', token_parser.check_type(str)
  end
  #this checks a corner casae for the check type method
  def test_all_false
    token_parser = TokenParser.new
    def number?(str); false; end
    def variable?(str); false; end
    def operator?(str); false; end
    def keyword?(str); false; end
    def unknown_keyword?(str); false; end
    str = '&'
    assert_equal '0', token_parser.check_type(str)
  end

end
