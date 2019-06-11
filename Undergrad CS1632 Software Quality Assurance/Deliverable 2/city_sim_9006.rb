require_relative 'args_checker'
require_relative 'random_generator'
require_relative 'locations'
require_relative 'driver_generator'
require_relative 'driver_simulation'

args_checker = ArgsChecker::new
args_checker.check_args ARGV
my_seed = args_checker.assume_zero ARGV

random_generator = RandomGenerator::new
my_rand = random_generator.get_rand(my_seed[0].to_i)

my_hospital = Location::new
my_hospital.outbound_roads = ["Fourth Ave.", "Foo St."]
my_hospital.reachable_locations = [3, 2]

my_hillman = Location::new
my_hillman.outbound_roads = ["Fifth Ave.", "Foo St."]
my_hillman.reachable_locations = [5, 1]

my_cathedral = Location::new
my_cathedral.outbound_roads = ["Fourth Ave.", "Bar St."]
my_cathedral.reachable_locations = [6, 4]

my_museum = Location::new
my_museum.outbound_roads = ["Fifth Ave.", "Bar St."]
my_museum.reachable_locations = [2, 3]

driver_generator = Drivers::new
my_drivers = driver_generator.populate_drivers(5)

driver_simulation = DriverSimulation::new
driver_simulation.drive(my_drivers, my_hospital, my_hillman, my_cathedral, my_museum, my_rand)
