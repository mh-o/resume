## I worked on this project with a partner. I wrote the project code and my partner wrote the test code.
 
# 1632_D6

**Still not ready for testing. I'm going to split some classes into smaller classes once I figure out how everything is going to work.**

### Approach

- Read each line, and split it into an array of elements
- Create a string to REGEX based on each element's type, according to the key:
    * 1 => number
    * 2 => variable
    * 3 => operator
    * 4 => keyword
    $ 5 => not a keyword
    * 0 => not a token
    * FOR EXAMPLE the line `> LET a 3 2 + asdf` will be converted to a string: `'421135'`
- Using regex, decide what to do with the line and then do it

### DONE
Program will successfully do the following in REPL mode:
- perform a single operation, ex: `> 3 2 +`
- store a variable with a single operation, ex: `> LET a 3 2 +`
- perform a single operation w/ variables, ex: `> a b +`

Program will successfully do the following if file-reading mode:
- all REPL ops
- concat files
- quit on error

### TO DO
- allow variables set to variables (ex: let a b)
- try to break it!
