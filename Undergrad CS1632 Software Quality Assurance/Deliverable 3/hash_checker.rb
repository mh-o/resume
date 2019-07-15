class HashChecker
  # Check hashes via threading, then check previous hashes normally
  def check_all(arr_of_arrs, arr1, arr2, arr3, arr4, arr5, arr6, arr7, arr8)
    threads = []

    # Add or remove threads here. Each thread needs a corresponding array chunk
    threads << Thread.new { check_hash(arr1) }
    threads << Thread.new { check_hash(arr2) }
    threads << Thread.new { check_hash(arr3) }
    threads << Thread.new { check_hash(arr4) }
    threads << Thread.new { check_hash(arr5) }
    threads << Thread.new { check_hash(arr6) }
    threads << Thread.new { check_hash(arr7) }
    threads << Thread.new { check_hash(arr8) }

    total_err = 0
    threads.each do |t|
      t.join
      total_err += t[:found]
    end

    if total_err > 1
      return 1
    elsif do_prev(arr_of_arrs) == 1
      return 1
    else
      return 0
    end
  end

  # Check each previous hash, return 1 if (mismatch | !starts_with 0)
  def do_prev(arr_of_arrs)
    for i in 0...arr_of_arrs[0].count do
      if i != 0
        if check_prev_hash(i, arr_of_arrs[1][i], arr_of_arrs[4][i-1]) == 1
          return 1
        end
      elsif arr_of_arrs[1][i] != "0"
        prev_error(i, arr_of_arrs[1][i], "0")
        return 1
      end
    end

    return 0
  end

  # Calculate the hash of a line and check against given value
  def check_hash(arr_of_arrs)
    for i in 0...arr_of_arrs[0].count do
      post_mod_sum = 0
      string_to_convert = "#{arr_of_arrs[0][i]}|#{arr_of_arrs[1][i]}|#{arr_of_arrs[2][i]}|#{arr_of_arrs[3][i]}"
      utf_arr = get_utf(string_to_convert) # Translate string to array of UTF-8 vals
      pre_mod_sum = perform_hash(utf_arr) # Sum those vals
      post_mod_sum = perform_mod(pre_mod_sum) # Mod that sum by 65536
      hex_result = to_hex(post_mod_sum) # Convert to hex value
      err_ha = validate_hash(hex_result, arr_of_arrs[4][i]) # Make sure calculated hash matches given hash

      if err_ha > 0
        display_error(i, hex_result, arr_of_arrs[4][i], string_to_convert)
        Thread.current[:found] = 1
        return 1
      end
    end

    Thread.current[:found] = 0
    return 0
  end

  # Return an array of UTF-8 values given some string
  def get_utf(val)
    return val.unpack('U*')
  end

  # Given an array of UTF-8 vals, perform hash on each val and sum thos results
  def perform_hash(arr)
    sum = 0

    for x in arr
      sum = sum + calculate_hash(x)
    end

    return sum
  end

  # Perform hash on some integer
  def calculate_hash(val)
    return ((val ** 2000) * ((val + 2) ** 21) - ((val + 5) ** 3))
  end

  # Perform mod on some integer
  def perform_mod(val)
    return val % 65536
  end

  # Return string representation of some value in hexadecimal
  def to_hex(val)
    return val.to_s(16)
  end

  # Accepts calculated hash and actual hash and compares, return 1 if !=
  def validate_hash(calculated, actual)
    if calculated.to_i(16) == actual.to_i(16)
      return 0
    else
      return 1
    end
  end

  # Display hash error to the user
  def display_error(i, calculated, actual, string)
    puts "Line #{i + 1}: String '#{string}' hash set to #{actual}, should be #{calculated}"
  end

  # Display misatch in previous hash to the user
  def prev_error(i, supposed, actual)
    puts "Line #{i}: Previous hash was #{supposed}, should be #{actual}"
  end

  # Chech that first hex num in a line matches hash of previous line
  def check_prev_hash(i, supposed, actual)
    if supposed.to_i(16) != actual.to_i(16)
      prev_error(i, supposed, actual)
      return 1
    else
      return 0
    end
  end
end
