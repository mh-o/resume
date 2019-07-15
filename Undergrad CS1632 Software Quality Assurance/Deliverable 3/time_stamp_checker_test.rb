require 'minitest/autorun'
require_relative 'time_stamp_checker'

# UNIT TESTS FOR METHOD check_time_stamp(x)
# Equivalence classes:
# arr time stamps in order -> return 0
# arr time stamps not in order in nano seconds  -> return 1
# arr time stamps not in order in seconds  -> return 1



class TimeStampCheckerTest < Minitest::Test
  #checks to see if a correct ordering of seconds and nano seconds returns 0 (no error)
  def test_checkTimeStamp_correct
    time_stamp_checker = TimeStampChecker::new
    arr = [0.1,0.2,1.6,4.8]
    assert_equal 0, time_stamp_checker.check_time_stamp(arr)
  end
  #checks to see if an incorrect ordering of nano seconds returns 1 (error)
  def test_checkTimeStamp_wrongOrderNano
    time_stamp_checker = TimeStampChecker::new
    arr = [0.2,0.1,0.3,0.8]
    assert_equal 1, time_stamp_checker.check_time_stamp(arr)
  end
  #checks to see if an incorrect ordering of seconds returns 1 (error)
  def test_checkTimeStamp_wrongOrderSeconds
    time_stamp_checker = TimeStampChecker::new
    arr = [1.1,3.2,2.3,4.8]
    assert_equal 1, time_stamp_checker.check_time_stamp(arr)
  end

end
