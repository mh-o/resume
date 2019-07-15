class ErrorChecker
  # Simply display if any error has been found
  def check_error(err)

    if err > 0
      puts "BLOCKCHAIN INVALID"
      return 1
    else
      return 0
    end
  end
end
