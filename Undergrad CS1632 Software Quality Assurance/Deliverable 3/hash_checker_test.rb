require 'minitest/autorun'
require_relative 'hash_checker'




class HashCheckerTest < Minitest::Test
  def test_perform_mod
    hash_checker = HashChecker::new
    assert_equal 45686, hash_checker.perform_mod(897654)
  end
  def test_calculate_hash
    hash_checker = HashChecker::new
    assert_equal 10460352987, hash_checker.calculate_hash(1)
  end
  def test_perform_hash
    hash_checker = HashChecker::new
    arr = [1,1]
    assert_equal 10460352987*2, hash_checker.perform_hash(arr)
  end
  def test_prev_error
    hash_checker = HashChecker::new
    assert_output(/Line 1: Previous hash was 1, should be 2/) { hash_checker.prev_error(1,1,2) }
  end
end
