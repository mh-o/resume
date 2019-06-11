require 'minitest/autorun'
require_relative 'args_checker'

class ArgsCheckerTest < Minitest::Test

  # UNIT TESTS FOR METHOD check_args(x)
  # Equivalence classes:
  # x= 1 seed -> returns x
  # x= !1 seed -> raises "Enter a seed and only a seed."

  # If no value is given for x, then the program will not run
  # EDGE CASE
  def test_no_args
    assert_raises "Enter a seed and only a seed." do
      args_checker = ArgsChecker::new
      args_checker.check_args []
    end
  end

  # If >1 value is given for x, then the program will not run
  # EDGE CASE
  def test_multiple_args
    assert_raises "Enter a seed and only a seed." do
      args_checker = ArgsChecker::new
      args_checker.check_args [0, 1]
    end
  end

  # UNIT TESTS FOR METHOD assume_zero(x)
  # Equivalence classes:
  # x= integer -> returns x
  # x= !integer -> returns 0


  # If an integer is given for x, then x is returned
  def test_good_args
    args_checker = ArgsChecker::new
    assert_equal 1, args_checker.assume_zero(1)
    assert_equal 123, args_checker.assume_zero(123)
    assert_equal -4567, args_checker.assume_zero(-4567)
  end

  # If a string is given for x, then 0 is returned
  # EDGE CASE
  def test_bad_args
    args_checker = ArgsChecker::new
    assert_equal 0, args_checker.assume_zero("asdf")
    assert_equal 0, args_checker.assume_zero("f46x")
  end

end
