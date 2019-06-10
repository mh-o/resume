require 'minitest/autorun'
require_relative 'args_checker'

# UNIT TESTS FOR METHOD args_checker.rb

class ArgsCheckerTest < Minitest::Test
  #tests if zero arguments returns true
  def test_zero_arguments
    args_checker = ArgsChecker.new
    arr = []
    assert_equal true, args_checker.check_mode(arr)
  end
  #test if nonzero arguments returns false
  def test_nonzero_arguments
    args_checker = ArgsChecker.new
    arr = [1]
    assert_equal false, args_checker.check_mode(arr)
  end
end
