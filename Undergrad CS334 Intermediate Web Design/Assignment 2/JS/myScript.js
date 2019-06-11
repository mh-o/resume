function myFunction(){
  var myAge = prompt("Please enter your age as an integer:")
  var myInt = parseInt(myAge)
  
  if (myInt >= 0 && myInt <= 17) {
	alert("You're a bit young for my type of humor.")
	document.getElementById('myImage').src='Images/img1.jpg'
  }
  else if (myInt >= 18 && myInt <= 25) {
	alert("You're just as immature as me... maybe.")
	document.getElementById('myImage').src='Images/img2.jpg'
  }
  else if (myInt >= 26 && myInt <115) {
	alert("You may not appreciate my humor... try this.")
	document.getElementById('myImage').src='Images/img3.jpg'
  }
  else if (myInt > 115) {
	alert("Please take this seriously, or contact the Guiness World Records.")
    document.getElementById('myImage').src='Images/img4.jpg'
  }
  else{
    alert("Are you sure you entered an integer? Try again.")
  }
}