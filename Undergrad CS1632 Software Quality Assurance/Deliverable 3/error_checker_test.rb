require 'minitest/autorun'
require_relative 'error_checker'

# UNIT TESTS FOR METHOD error_checker(err)
# Equivalence classes:
# err > 0 -> return 1
# 0 or anything else -> return 0



class ErrorCheckerTest < Minitest::Test
  def test_error_one_case
    error_checker = ErrorChecker::new
    assert_equal 1, error_checker.check_error(1)
  end

  def test_negative_value_case
    error_checker = ErrorChecker::new
    assert_equal 0, error_checker.check_error(-1)
  end

  def test_zero_value_case
    error_checker = ErrorChecker::new
    assert_equal 0, error_checker.check_error(0)
  end

end
