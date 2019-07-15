require 'minitest/autorun'
require_relative 'block_num_checker'

# UNIT TESTS FOR METHOD check_block(arr)
# Equivalence classes:
# arr starts with nonzero -> return 1
# arr time stamps not in consecutive order -> return 1
# arr time stamps in consecutive order starting at zero  -> return 0



class BlockNumCheckerTest < Minitest::Test

  #arr of blocks starting at nonzero should return 1
  def test_first_block_nonzero
    block_num_checker = BlockNumChecker::new
    arr = [1,2,3,4,5,6]
    assert_equal 1, block_num_checker.check_block(arr)
  end
  # arr of blocks not in consecutive order should return 1
  def test_blocks_not_consecutive
    block_num_checker = BlockNumChecker::new
    arr = [0,1,2,8,4,5,6]
    assert_equal 1, block_num_checker.check_block(arr)
  end

end
