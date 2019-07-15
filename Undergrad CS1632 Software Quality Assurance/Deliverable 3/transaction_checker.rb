class TransactionChecker

  attr_accessor :people

  def initialize
    @people = {}
  end

  def check_transaction(arr)
    i = 0

    for x in arr
      seperate_transactions = split_transactions(x)
      if perform_transaction(seperate_transactions, i) == 1
        return 1
      end
      i += 1
    end

    display_results()

    return 0
  end

  def split_transactions(string)
    return string.split(':')
  end

  def perform_transaction(arr, i)
    for x in arr
      temp_str = x.split('>')

      payer = temp_str[0]
      payee_and_payment = temp_str[1]

      temp_str = payee_and_payment.split('(')
      payee = temp_str[0]
      payment = temp_str[1].chomp(')')

      add_person(payer)
      add_person(payee)

      transfer_funds(payer, payee, payment)

      if check_balance(payer, i) == 1
        return 1
      end
    end
  end

  def add_person(person)
    if (@people.key?(person)) == false
      @people[person] = 0
    end
  end

  def transfer_funds(payer, payee, payment)
    payer_funds = @people[payer].to_i
    payee_funds = @people[payee].to_i

    if payer != "SYSTEM"
      payer_funds = (payer_funds - payment.to_i)
    end

    if payer == payee # handle case where payer and payee are the same
      return 0
    end

    payee_funds = (payee_funds + payment.to_i)
    @people[payer] = payer_funds
    @people[payee] = payee_funds
  end

  def display_results()
    first = true
    @people.each do |key, val|
      if first
        first = false
        next
      end

      puts "#{key}: #{val} billcoins"
    end
  end

  def check_balance(person, i)
    balance = @people[person].to_i

    if balance < 0
      puts "Line #{i}: Invalid block, address #{person} has #{@people[person]} billcoins!"
      return 1
    else
      return 0
    end
  end
end
