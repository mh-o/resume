# CS1632_Deliverable_4

## Defects
#### Found the following defects via exploratory testing:
- Requirement 1 states that the homepage should display "Welcome, friend, to a land of pure calculation" but homepage displays "Welcome, friend, to a land of pure calculation." (period has been added)
- Requirement 4 states that the fibonacci page shall accept integers from 1 to 100 and show the user the Fibonacci of the value, but the page only returns the Fibonacci value for integers 1-30
- Requirement 5 states that the factorial/fibonacci pages should display 1 when an invalid value is entered. But when entering a string: "taco" or a decimal: "12.3", the user is given an Internal Server Error (both pages)
- Requirement 7 states that the Hello page shall display hello from all training values. But any character after a "#" training value will be ignored. Also, if a user's name was "///////", they would feel left out as these characters will also be ignored
- Requirement 7 states that the Hello page shall display hello from all training values. But any value preceeded by a "%" will result in an application error
