require 'minitest/autorun'
require_relative 'driver_simulation'
require_relative 'locations'

class DriverSimulationTest < Minitest::Test

  # UNIT TESTS FOR METHOD drive(x, l1, l2, l3, l4, r)
  # SUCCESS CASES: (x = array of drivers) && (l vars = Location objects) && (r = srand stream)
  # FAILURE CASES: If x.count is 0, the program will run 0 times
  #                If any l var !Location object, will return "---"
  #                If r != srand stream, will return "---"

  # If x, l1, l2, l3, l4, and r are proper format, will return 1
  # STUBBING
  def test_good_drive_args
    driver_simulation = DriverSimulation::new
    x = []
    l1 = Location::new
    l2 = Location::new
    l3 = Location::new
    l4 = Location::new
    r = srand(1)

    assert_equal 1, driver_simulation.drive(x, l1, l2, l3, l4, r)
  end

  # If any l var != Location object, will return "---"
  # STUBBING
  def test_bad_location_objects
    driver_simulation = DriverSimulation::new
    x = [1]
    l1 = 23
    l2 = "taco"
    l3 = 45
    l4 = ""
    r = srand(1)

    assert_equal "---", driver_simulation.drive(x, l1, l2, l3, l4, r)
  end

  # If r != srand stream, will return "---"
  def test_bad_srand_stream
    driver_simulation = DriverSimulation::new
    x = [1]
    l1 = Location::new
    l2 = Location::new
    l3 = Location::new
    l4 = Location::new
    r = "taco"

    assert_equal "---", driver_simulation.drive(x, l1, l2, l3, l4, r)
  end

  # UNIT TESTS FOR METHOD get_starting_location(x)
  # Equivalence classes:
  # x>= 0.75 -> returns 1
  # x>= 0.5 && < 0.75 -> returns 2
  # x>= 0.25 && < 0.5 -> returns 3
  # x< 0.25 -> returns 4

  # If x >= 0.75, will return 1
  def test_1_starting_location
    driver_simulation = DriverSimulation::new
    assert_equal 1, driver_simulation.get_starting_location(0.75)
  end

  # If 0.75 > x >= 0.5, will return 2
  def test_2_starting_location
    driver_simulation = DriverSimulation::new
    assert_equal 2, driver_simulation.get_starting_location(0.5)
  end

  # If 0.5 > x >= 0.25, will return 3
  def test_3_starting_location
    driver_simulation = DriverSimulation::new
    assert_equal 3, driver_simulation.get_starting_location(0.25)
  end

  # If x < 0.25, will return 4
  def test_4_starting_location
    driver_simulation = DriverSimulation::new
    assert_equal 4, driver_simulation.get_starting_location(0.05)
  end

  # If x is a string, will return "---"
  def test_bad_starting_location
    driver_simulation = DriverSimulation::new
    assert_equal "---", driver_simulation.get_starting_location("asdf")
  end

  # UNIT TESTS FOR METHOD visit_location(x)
  # Equivalence classes:
  # x= 1 -> number of books, dinos, and classes remains unchanged
  # x= 2 -> number of books += 1
  # x= 3 -> number of classes doubles
  # x= 4 -> number of dinos += 1

  # After visiting the hospital (1), no item values should change
  def test_visit_hospital
    driver_simulation = DriverSimulation::new

    initial_books = driver_simulation.my_books
    initial_dinos = driver_simulation.my_dinos
    initial_classes = driver_simulation.my_classes

    driver_simulation.visit_location(1)

    final_books = driver_simulation.my_books
    final_dinos = driver_simulation.my_dinos
    final_classes = driver_simulation.my_classes

    assert_equal initial_books, final_books
    assert_equal initial_dinos, final_dinos
    assert_equal initial_classes, final_classes
  end

  # After visiting hillman (2), only books should incriment
  def test_visit_hillman
    driver_simulation = DriverSimulation::new

    initial_books = driver_simulation.my_books
    initial_dinos = driver_simulation.my_dinos
    initial_classes = driver_simulation.my_classes

    driver_simulation.visit_location(2)

    final_books = driver_simulation.my_books
    final_dinos = driver_simulation.my_dinos
    final_classes = driver_simulation.my_classes

    assert_equal 1, (final_books - initial_books)
    assert_equal initial_dinos, final_dinos
    assert_equal initial_classes, final_classes
  end

  # After visiting the cathedral (3), only classes should double
  def test_visit_cathedral
    driver_simulation = DriverSimulation::new

    initial_books = driver_simulation.my_books
    initial_dinos = driver_simulation.my_dinos
    initial_classes = driver_simulation.my_classes

    driver_simulation.visit_location(3)

    final_books = driver_simulation.my_books
    final_dinos = driver_simulation.my_dinos
    final_classes = driver_simulation.my_classes

    assert_equal initial_books, final_books
    assert_equal initial_dinos, final_dinos
    assert_equal 2, (final_classes / initial_classes)
  end

  # After visiting the museum (4), only dinos should incriment
  def test_visit_museum
    driver_simulation = DriverSimulation::new

    initial_books = driver_simulation.my_books
    initial_dinos = driver_simulation.my_dinos
    initial_classes = driver_simulation.my_classes

    driver_simulation.visit_location(4)

    final_books = driver_simulation.my_books
    final_dinos = driver_simulation.my_dinos
    final_classes = driver_simulation.my_classes

    assert_equal initial_books, final_books
    assert_equal 1, (final_dinos - initial_dinos)
    assert_equal initial_classes, final_classes
  end

  # If an invalid location is visited, will return "---"
  # EDGE CASE
  def test_bad_location_visit
    driver_simulation = DriverSimulation::new
    assert_equal "---", driver_simulation.visit_location(5)
  end

  # UNIT TESTS FOR METHOD get_selector(x)
  # Equivalence classes:
  # x>= 0.5 -> returns 1
  # x< 0.5 -> returns 0

  # If x >= 0.5, will return 1
  def test_selector_1
    driver_simulation = DriverSimulation::new
    assert_equal 1, driver_simulation.get_selector(0.6)
  end

  # If x < 0.5, will return 0
  def test_selector_0
    driver_simulation = DriverSimulation::new
    assert_equal 0, driver_simulation.get_selector(0.4)
  end

  # UNIT TESTS FOR METHOD display_output(x, y, z, w, l1, l2, l3, l4)
  # SUCCESS CASES: (x = array of drivers) && (y = selector switch) &&
  #                (z = starting location [int 1-4]) && (w = next location) &&
  #                (l vars = Location objects) will return next location (int 1-6)
  # FAILURE CASES: (if x.count is zero, program will run 0 times)
  #                If any l var !Location object, will return "---"
  #                If (y || z || w) != valid integers, will return "---"

  # When y, z, w, are out of valid range, will return ("---")
  def test_bad_display_output_range
    driver_simulation = DriverSimulation::new
    x = []
    y = 1
    z = 2
    w = 3
    l1 = Location::new
    l2 = Location::new
    l3 = Location::new
    l4 = Location::new
    assert_equal ("---"), driver_simulation.display_output(x, y, z, w, l1, l2, l3, l4)
  end

  # When l objects are not of type Location, will return ("---")
  def test_bad_display_output_objects
    driver_simulation = DriverSimulation::new
    x = []
    y = 1
    z = 1
    w = 1
    l1 = "asdf"
    l2 = 32
    l3 = 34
    l4 = "jjj"
    assert_equal ("---"), driver_simulation.display_output(x, y, z, w, l1, l2, l3, l4)
  end

  # UNIT TESTS FOR METHOD display_items(x)
  # SUCCESS CASES: (x = array of drivers) will display number of items collected by each driver
  # FAILURE CASES: If x is not a valid integer, will return "---"

  # Passing any x will return 1 and print string representation of x and items collected
  def test_good_display_items
    driver_simulation = DriverSimulation::new
    assert_equal 1, driver_simulation.display_items(3)
  end

end
