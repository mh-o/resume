# comments
class Variables
  def start
    @variables = {}
  end

  def get_val(var)
    @variables[var]
  end

  def set_var(var, val)
    @variables[var] = val
    @variables[var]
  end

  def clear_vars
    @variables.clear
  end
end
