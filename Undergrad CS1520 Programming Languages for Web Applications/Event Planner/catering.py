from flask import Flask, request, abort, url_for, redirect, session, render_template
from flask_sqlalchemy import SQLAlchemy
import datetime

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///catering.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)


### CREATE NEW USER IN SHELL
### 1)	$env:FLASK_APP = "catering.py"
### 2)	flask initdb
### 3)	flask SHELL
### 4)	from catering import db, User
###			User.query.all() ### TO LIST ALL USERS
### 5)	x = User(username="meba". email="meba@example.com")
###			x ### TO SHOW USER OBJECT x
### 6)	db.session.add(x)
### 7)	db.session.commit()

### GET STUFF
### 1) temp = myEVent.query.filter_by(eventName="example").first()
### 2) temp.eventDate
###	eventDate

##########################################
####----MODELS----MODELS----MODELS----####
##########################################

class myStaff(db.Model): # inherit from db.model
	id = db.Column(db.Integer, primary_key=True) # the user has an ID
	staffUserName = db.Column(db.String(80), unique=True) # username must be unique, >= 80 chars
	staffPass = db.Column(db.String(120)) # user has email, must be unique, >= 120 chars

	def __init__(self, staffUserName, staffPass):
		self.staffUserName = staffUserName
		self.staffPass = staffPass

	def __repr__(self): # representation of object myStaff
		return '<User %r>' % self.staffUserName # this will return when the object is displayed

class myUser(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	userName = db.Column(db.String(80), unique=True)
	userPass = db.Column(db.String(120))

	def __init__(self, userName, userPass):
		self.userName = userName
		self.userPass = userPass

	def __repr__(self):
		return '<User %r>' % self.userName

class myEvent(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	eventName = db.Column(db.String(100), unique=True)
	eventDate = db.Column(db.Date(), key='eventDate', unique=True)
	eventHost = db.Column(db.String(80))
	eventStaff1 = db.Column(db.String(80), nullable=True)
	eventStaff2 = db.Column(db.String(80), nullable=True)
	eventStaff3 = db.Column(db.String(80), nullable=True)

	def __init__(self, eventName, eventDate, eventHost, eventStaff1, eventStaff2, eventStaff3):
		self.eventName = eventName
		self.eventDate = eventDate
		self.eventHost = eventHost
		self.eventStaff1 = None
		self.eventStaff2 = None
		self.eventStaff3 = None

	def __repr__(self):
		return 'eventName %r eventDate %r eventHost %r eventStaff1 %r eventStaff2 %r eventStaff3 %r' % (self.eventName, self.eventDate, self.eventHost, self.eventStaff1, self.eventStaff2, self.eventStaff3)

#########################################################
####----CONTROLLERS----CONTROLLERS----CONTROLLERS----####
#########################################################

@app.cli.command('initdb')
def initdb_command():
	db.drop_all()
	db.create_all()

	print('Initialized the database.')

@app.cli.command('pushdb')
def pushdb_command():
	i = 1;
	while True:
		tempUser = myUser.query.filter_by(id=i).first()
		try:
			tempUserName = tempUser.userName
			tempUserPass = tempUser.userPass
			users[tempUserName] = tempUserPass
			i = i+1
		except:
			break

############################
####----CODE----CODE----####
############################

users = {"owner":"pass"}
staffUsers = {} # initialize empty becuase we start with no staff
eventDates = {}
eventHosts = {}
eventStaff1 = {}
eventStaff2 = {}
eventStaff3 = {}
# by default, direct to login
@app.route("/")
def default():
	pushUsers()
	pushStaff()
	pushEvents()
	return redirect(url_for("logger"))

@app.route("/login/", methods=["GET", "POST"])
def logger():
	pushUsers()
	pushStaff()
	pushEvents()
	#pushEvents()
	# first check if the user is already logged in
	if "username" in session:
		return redirect(url_for("profile", username=session["username"]))

	# if not, and the incoming request is via POST try to log them in
	elif request.method == "POST":
		if request.form["user"] in users and users[request.form["user"]] == request.form["pass"]:
			session["username"] = request.form["user"]
			return redirect(url_for("profile", username=session["username"]))
		elif request.form["user"] in staffUsers and staffUsers[request.form["user"]] == request.form["pass"]:
			session["username"] = request.form["user"]
			return redirect(url_for("profile", username=session["username"]))

	# if all else fails, offer to log them in
	return render_template("loginPage.html")

@app.route("/profile/")
def profiles():
	pushUsers()
	pushStaff()
	pushEvents()
	if "username" in session:
		return render_template("profiles.html", username=session["username"], users=users)
	else:
		return render_template("profile.html", username=session["username"], users=users)

@app.route("/profile/<username>", methods=["GET", "POST"])
def profile(username=None):
	pushUsers()
	pushStaff()
	pushEvents()
	if not username:
		return redirect(url_for("profiles"))

	elif username in users:
		# if specified, check to handle users looking up their own profile
		if "username" in session:
			if session["username"] == username:
				if request.method == "POST":
					try:
						eventDates[request.form["eventName"]] = request.form["eventDate"]
						eventHosts[request.form["eventName"]] = username
						tempEventName = request.form["eventName"]
						tempEventDate = request.form["eventDate"]
						tempEventDate = tempEventDate.replace("-", "")
						tempEventDate = datetime.datetime.strptime(tempEventDate, "%Y%m%d").date()

						tempEvent = myEvent(eventName=tempEventName, eventDate=tempEventDate, eventHost=username, eventStaff1="", eventStaff2="", eventStaff3="")

						db.session.add(tempEvent)
						db.session.commit()

						return render_template("eventAdded.html", username=session["username"], error2="no")
					except:
						return render_template("eventAdded.html", username=session["username"], error2="yes")

				else:
					return render_template("userProfile.html", username=session["username"], eventDates=eventDates, eventHosts=eventHosts, eventStaff1=eventStaff1, eventStaff2=eventStaff2, eventStaff3=eventStaff3)
			else:
				return render_template("otherProfile.html", name=username, username=session["username"])
		else:
			return render_template("otherProfile.html", name=username)

	elif username in staffUsers:
		if "username" in session:
			if session["username"] == username:
				return render_template("staffProfile.html", username=session["username"], eventDates=eventDates, eventName="", eventStaff1=eventStaff1, eventStaff2=eventStaff2, eventStaff3=eventStaff3)
			else:
				return render_template("otherProfile.html", name=username, username=session["username"])
		else:
			return render_template("otherProfile.html", name=username)

	else:
		# cant find profile
		abort(404)

@app.route("/cancelEvent/<eventName>", methods=["GET", "POST"])
def cancelEvent(eventName=None):
	pushUsers()
	pushStaff()
	pushEvents()
	try:
		temp = myEvent.query.filter_by(eventName=eventName).first()
		db.session.delete(temp)
		db.session.commit()

		del eventDates[eventName]
		del eventHosts[eventName]

		try:
			for k, v in eventStaff1():
				if v == eventName:
					del eventStaff1[k]
			for k, v in eventStaff2():
				if v == eventName:
					del eventStaff2[k]
			for k, v in eventStaff3():
				if v == eventName:
					del eventStaff3[k]
		except:
			i = i

		return render_template("cancelEvent.html", eventName=eventName, username=session["username"], error="no")
	except:
		return render_template("cancelEvent.html", eventName=eventName, username=session["username"], error="yees")

@app.route("/logout/")
def unlogger():
	pushUsers()
	pushStaff()
	pushEvents()
	if "username" in session: # if someone is logged in
		session.clear() # log them out
		return render_template("logoutPage.html") # inform user they have logged out
	else: # else no one is logged in
		return redirect(url_for("logger")) # offer to log them in

@app.route("/staffSignUp/<eventName>")
def staffSignUp(eventName=None):
	pushUsers()
	pushStaff()
	pushEvents()
	if "username" in session:
		flag = signUp(eventName, session["username"])
		return render_template("staffSignUp.html", username=session["username"], flag=flag)
	else:
		return redirect(url_for("logger")) # offer to log them in

@app.route("/newAccount/", methods=["GET", "POST"])
def newAccount():
	pushUsers()
	pushStaff()
	pushEvents()
	if "username" in session:
		return redirect(url_for("profile", username=session["username"]))
	elif request.method == "POST":
		users[request.form["newUser"]] = request.form["userPass"] # add new user to my python dictionary
		tempUserName = request.form["newUser"]
		tempUserPass = request.form["userPass"]
		tempUser = myUser(userName=tempUserName, userPass=tempUserPass)

		db.session.add(tempUser)
		db.session.commit()
		return render_template("userAdded.html")
	else:
		return render_template("newAccount.html")

@app.route("/addStaff/", methods=["GET", "POST"])
def adminPage():
	pushUsers()
	pushStaff()
	pushEvents()
	if "username" in session:
		if session["username"] == "owner":
			if request.method == "POST":
				#if request.form["user"] in users and users[request.form["user"]] == request.form["pass"]:
				try:
					staffUsers[request.form["newStaff"]] = request.form["staffPass"] # add to python dictionary
					tempStaffName = request.form["newStaff"]
					tempStaffPass = request.form["staffPass"]
					tempUser = myStaff(staffUserName=tempStaffName, staffPass=tempStaffPass)

					db.session.add(tempUser)
					db.session.commit()
					return render_template("staffAdded.html", username=session["username"])
				except:
					return render_template("staffAdded.html", username=session["username"], error=True)
			else:
				return render_template("addStaff.html", username=session["username"])
		else:
			return render_template("private.html")
	else:
		return redirect(url_for("logger"))
# needed to use sessions
# note that this is a terrible secret ke
app.secret_key = "this is a terrible secret key"

def signUp(eventName, staffName):
	tempEvent = myEvent.query.filter_by(eventName=eventName).first()
	tempEvent2 = myEvent.query.filter_by(eventName=eventName).first()
	tempStaff1 = tempEvent.eventStaff1
	tempStaff2 = tempEvent.eventStaff2
	tempStaff3 = tempEvent.eventStaff3
	if (staffName == tempStaff1) or (staffName == tempStaff2) or (staffName == tempStaff3):
		return 2 # staff member is already working the event
	elif not tempEvent.eventStaff1: # we have an empty staff1 slot
		tempEvent.eventStaff1 = staffName
		db.session.delete(tempEvent2)
		db.session.add(tempEvent)
		db.session.commit()
		eventStaff1[staffName] = eventName
		return 0
	elif not tempEvent.eventStaff2:
		tempEvent.eventStaff2 = staffName
		db.session.delete(tempEvent2)
		db.session.add(tempEvent)
		db.session.commit()
		eventStaff2[staffName] = eventName
		return 0
	elif not tempEvent.eventStaff3:
		tempEvent.eventStaff3 = staffName
		db.session.delete(tempEvent2)
		db.session.add(tempEvent)
		db.session.commit()
		eventStaff3[staffName] = eventName
		return 0
	else:
		return 1

def pushUsers():
	n = myUser.query.count()
	m = 0
	i = 1
	while True:
		tempUser = myUser.query.filter_by(id=i).first()
		try:
			tempUserName = tempUser.userName
			tempUserPass = tempUser.userPass
			users[tempUserName] = tempUserPass
			i = i+1
			m = m+1
		except:
			if m < n:
				i = i+1
			else:
				break
	return

def pushStaff():
	n = myStaff.query.count()
	m = 0
	i = 1
	while True:
		tempStaff = myStaff.query.filter_by(id=i).first()
		try:
			tempStaffName = tempStaff.staffUserName
			tempStaffPass = tempStaff.staffPass
			staffUsers[tempStaffName] = tempStaffPass
			i = i+1
			m = m+1
		except:
			if m < n:
				i = i+1
			else:
				break
	return

def pushEvents():
	n = myEvent.query.count()
	m = 0
	i = 1
	while True:
		tempEvent = myEvent.query.filter_by(id=i).first()
		try:
			tempEventName = tempEvent.eventName
			tempEventDate = tempEvent.eventDate
			tempEventHost = tempEvent.eventHost
			tempEventStaff1 = tempEvent.eventStaff1
			tempEventStaff2 = tempEvent.eventStaff2
			tempEventStaff3 = tempEvent.eventStaff3
			eventDates[tempEventName] = tempEventDate
			eventHosts[tempEventName] = tempEventHost
			eventStaff1[tempEventStaff1] = tempEventName
			eventStaff2[tempEventStaff2] = tempEventName
			eventStaff3[tempEventStaff3] = tempEventName
			i = i+1
			m = m+1
		except:
			if m < n:
				i = i+1
			else:
				break
	return

if __name__ == "__main__":
	app.run()
