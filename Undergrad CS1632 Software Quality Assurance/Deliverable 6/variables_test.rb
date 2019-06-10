require 'minitest/autorun'
require_relative 'variables'

# UNIT TESTS FOR METHOD variables.rb

class VariablesTest < Minitest::Test
  #tests the variables method start and sees if its empty
  def test_start
    variables = Variables.new
    assert_empty variables.start
  end
  #tests whether a set operation works. should set to 5 and variables be equal to 5 
  def test_set_val
    variables = Variables.new
    assert_equal 5, variables.set_var(0,5)
  end
end
