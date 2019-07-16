# driver simulation class
class DriverSimulation
  attr_accessor :my_books
  attr_accessor :my_dinos
  attr_accessor :my_classes

  def initialize
    @my_books = 0
    @my_dinos = 0
    @my_classes = 1
  end

  def drive(my_drivers, my_hospital, my_hillman, my_cathedral, my_museum,
            my_rand)
    my_drivers.each do |i|
      simulate_drive(i, my_hospital, my_hillman, my_cathedral, my_museum,
                     my_rand)
    end
    1
  rescue StandardError
    '---'
  end

  def simulate_drive(driver, my_hospital, my_hillman, my_cathedral, my_museum, my_rand)
    reset_items()
    my_start = get_starting_location(rand())
    my_results = visit_location(my_start)

    begin
      my_selector = get_selector(rand())
      next_location = 0
      next_location = display_output(driver, my_selector, my_start, next_location, my_hospital, my_hillman, my_cathedral, my_museum)
      visit_location(next_location)
      my_start = next_location
    end until my_start > 4

    display_items(driver)
  end

  def reset_items()
    @my_books = 0
    @my_dinos = 0
    @my_classes = 1
  end

  def get_starting_location(my_rand)
    if my_rand >= 0.75
      1
    elsif my_rand >= 0.5
      2
    elsif my_rand >= 0.25
      3
    else
      4
    end
  rescue
    '---'
  end

  def visit_location(loc)
    if loc == 1 # hospital
      # do nothing
    elsif loc == 2 # hillman
      @my_books += 1
    elsif loc == 3 # cathedral
      @my_classes = (@my_classes * 2)
    elsif loc == 4 # Museum
      @my_dinos += 1
    else
      '---'
    end

  rescue
    '---'
  end

  def get_selector(my_rand)
    #my_rand = rand()

    if my_rand >= 0.5
      1
    else
      0
    end
  rescue
    '---'
  end

  def display_output(driver, my_selector, my_start, next_location, my_hospital, my_hillman, my_cathedral, my_museum)
    if my_start == 1 # hospital
      next_location = my_hospital.reachable_locations[my_selector]
      puts 'Driver ' + driver.to_s + ' heading from Hospital to ' + get_location_by_index(next_location) + ' via ' + my_hospital.outbound_roads[my_selector]
    elsif my_start == 2 # hillman
      next_location = my_hillman.reachable_locations[my_selector]
      puts 'Driver ' + driver.to_s + ' heading from Hillman to ' + get_location_by_index(next_location) + ' via ' + my_hillman.outbound_roads[my_selector]
    elsif my_start == 3 # cathedral
      next_location = my_cathedral.reachable_locations[my_selector]
      puts 'Driver ' + driver.to_s + ' heading from Cathedral to ' + get_location_by_index(next_location) + ' via ' + my_cathedral.outbound_roads[my_selector]
    elsif my_start == 4 # museum
      next_location = my_museum.reachable_locations[my_selector]
      puts 'Driver ' + driver.to_s + ' heading from Museum to ' + get_location_by_index(next_location) + ' via ' + my_museum.outbound_roads[my_selector]
    end

    next_location
  rescue
    '---'
  end

  def get_location_by_index(index)
    if index == 1
      'Hospital'
    elsif index == 2
      'Hillman'
    elsif index == 3
      'Cathedral'
    elsif index == 4
      'Museum'
    elsif index == 5
      'Downtown'
    elsif index == 6
      'Monroeville'
    end
  end

  def display_items(driver)
    if @my_books == 1
      puts 'Driver ' + driver.to_s + ' obtained ' + @my_books.to_s + ' book!'
    else
      puts 'Driver ' + driver.to_s + ' obtained ' + @my_books.to_s + ' books!'
    end

    if @my_dinos == 1
      puts 'Driver ' + driver.to_s + ' obtained ' + @my_dinos.to_s + ' dinosaur toy!'
    else
      puts 'Driver ' + driver.to_s + ' obtained ' + @my_dinos.to_s + ' dinosaur toys!'
    end

    if @my_classes == 1
      puts 'Driver ' + driver.to_s + ' attended ' + @my_classes.to_s + ' class!'
    else
      puts 'Driver ' + driver.to_s + ' attended ' + @my_classes.to_s + ' classes!'
    end
    1

  rescue
    '---'
  end

end
