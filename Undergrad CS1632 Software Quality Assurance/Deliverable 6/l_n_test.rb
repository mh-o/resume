require 'minitest/autorun'
require_relative 'l_n'

# UNIT TESTS FOR METHOD l_n.rb

class LNTest < Minitest::Test
  #tests if the start method returns 0
  def test_start
    l_n = LineNumber.new
    assert_equal 0, l_n.start
  end
  #tests if the inc method increments correctly
  def test_inc
    l_n = LineNumber.new
    l_n.instance_variable_set(:@l_n, 0)
    assert_equal 1, l_n.inc
  end
  #tests the get method for the line number 
  def test_get
    l_n = LineNumber.new
    l_n.instance_variable_set(:@l_n, 5)
    assert_equal 5, l_n.get
  end
end
