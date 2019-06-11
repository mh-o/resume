require 'minitest/autorun'
require_relative 'random_generator'

class RandomGeneratorTest < Minitest::Test

  # UNIT TESTS FOR METHOD get_rand(x)
  # Equivalence classes:
  # x= 1 -> returns x
  # x= 2 -> returns y

  # If x is given, it will always produce the same stream of integers
  # STUBBING
  def test_equivalent_rand_for_equivalent_seeds
    random_generator = RandomGenerator::new
    random_generator.get_rand(1)
    first_val = rand()
    random_generator.get_rand(1)
    second_val = rand()
    assert_equal first_val, second_val
  end

  # If x is given, it will produce a stream of integers
  # If y is given, it will produce a different stream of integers
  # STUBBING
  def test_different_rand_for_different_seeds
    random_generator = RandomGenerator::new
    random_generator.get_rand(1)
    first_val = rand()
    random_generator.get_rand(2)
    second_val = rand()
    assert_equal false, (first_val == second_val)
  end

end
