# Location class
class Location
  attr_accessor :outbound_roads
  attr_accessor :reachable_locations

  def initialize
    @outbound_roads = ['', '']
    @reachable_locations = [0, 0]
  end
end
