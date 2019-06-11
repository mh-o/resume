#### > $env:FLASK_APP = "chat.py"
#### > flask initdb
#### > flask run

import time, os, json
from datetime import datetime
from flask import Flask, request, abort, url_for, redirect, session, render_template
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///chat.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.secret_key = 'A1Rr645j/3eX B~XHH!fgN]LWX/,?RT'
db = SQLAlchemy(app)
chatRooms = {}
##########################################
####////MODELS----MODELS----MODELS\\\\####
##########################################

##########################################
####----MYUSER----MYUSER----MYUSER----####
class myUser(db.Model): # Model for each unique user, to be stored in chat.db
    id = db.Column(db.Integer, unique = True, primary_key = True) # The user has an ID, must be unique
    userName = db.Column(db.String(80), unique = True) # The user has up to 80 chars for userName, must be unique
    userPass = db.Column(db.String(30)) # The user has up to 30 chars for userPass

    def __init__(self, userName, userPass):
        self.userName = userName
        self.userPass = userPass

    def __repr__(self): # Representation of myUser object
        return '<User %r with password %r>' % self.userName % self.userPass
##########################################
##########################################

################################################
####----CHATROOM----CHATROOM----CHATROOM----####
class chatRoom(db.Model): # Model for each chat room, to be stored in chat.db
    chatroom_id = db.Column(db.Integer, unique = True, primary_key = True) # The room has an ID, must be unique
    roomName = db.Column(db.String(40), unique = True) # The room has up to 40 chars for roomName, must be unique
    roomUser = db.Column(db.String(80)) # The chat room was created by some user

    def __init__(self, roomName, roomUser):
        self.roomName = roomName
        self.roomUser = roomUser

    #def __repr__(self): # Representation of chatRoom object
        #return '<Chat room %r created by user %r' % self.roomName % self.roomUser
################################################
################################################

#############################################
####----MESSAGE----MESSAGE----MESSAGE----####
class myMessage(db.Model):
    msg_id = db.Column(db.Integer, primary_key = True)
    author = db.Column(db.String(80), nullable = False)
    contents = db.Column(db.String(1024), nullable = False)
    chatroom = db.Column(db.Integer, nullable = False)

    def __init__(self, author, contents, chatroom):
        self.author = author
        self.contents = contents
        self.chatroom = chatroom

    def as_dict(self):
        return {'author': self.author, 'contents': self.contents}


#############################################
#############################################

##########################################
####\\\\MODELS----MODELS----MODELS////####
##########################################

#########################################################
####////CONTROLLERS----CONTROLLERS----CONTROLLERS\\\\####
#########################################################

##########################################
####----INITDB----INITDB----INITDB----####
@app.cli.command('initdb')
def initdb_command():
    db.drop_all()
    db.create_all()

    print('Database chat.db has been cleared/initialized.')
##########################################
##########################################

#########################################################
####\\\\CONTROLLERS----CONTROLLERS----CONTROLLERS////####
#########################################################

###################################################
####////APPROUTES----APPROUTES----APPROUTES\\\\####
###################################################

#############################################
####----DEFAULT----DEFAULT----DEFAULT----####
@app.route("/")
def default():
    return redirect(url_for("login"))
#############################################
#############################################

###############################################################
####----CREATEACCOUNT----CREATEACCOUNT----CREATEACCOUNT----####
@app.route("/createAccount/", methods = ["GET", "POST"])
def createAccount():
    if "sessionUserName" in session: # If someone manually tries this link while logged in
        return redirect(url_for("profile", curUserName = session["sessionUserName"])) # Take them to their profile
    elif request.method == "POST": # If no one is logged in and they are submitting their credentials
        userExists = db.session.query(myUser.id).filter_by(userName = request.form["formUserName"]).scalar() # Check to see if the userName is taken

        if userExists is not None:
            return render_template("createAccount.html", curUserExists = 1)
        else:
            newUser = myUser(userName = request.form["formUserName"], userPass = request.form["formPass"])
            db.session.add(newUser)
            db.session.commit()
            return render_template("createAccount.html", curUserAdded = 1)
    else:
        return render_template("createAccount.html")
###############################################################
###############################################################

######################################################
####----CREATEROOM----CREATEROOM----CREATEROOM----####
@app.route("/createRoom/", methods = ["GET", "POST"])
def createRoom():
    if "sessionUserName" in session: # Make sure someone is logged in
        if request.method == "POST":
            newRoom = chatRoom(roomName = request.form["formRoomName"], roomUser = session["sessionUserName"])
            db.session.add(newRoom)
            db.session.commit()
            return render_template("createRoom.html", curUserName = session["sessionUserName"], curRoomCreated = 1)
        else:
            return render_template("createRoom.html", curUserName = session["sessionUserName"])
    else: # If no one is logged in, this page can't be accessed
        return redirect(url_for("login"))
######################################################
######################################################

##################################################
####----CHATROOM----CHATEROOM----CHATEROOM----####
@app.route("/room/<id>", methods=["GET", "POST"])
def room(id):
    if "sessionUserName" in session: # Make sure someone is logged in
        if request.method == "POST":
            if not request.form['msgs']:
                error = "Please enter something."
                messages = Message.query.filter_by(chatroom=id).all()
                return render_template("chatRoom.html", curUserName = session["sessionUserName"], id=id, msgs=messages)

        messages = myMessage.query.filter_by(chatroom=id).all()
        return render_template("chatRoom.html", curUserName = session["sessionUserName"], id=id, msgs=messages)
    else:
        return redirect(url_for("login"))
##################################################
##################################################

######################################################
####----DELETEROOM----DELETEROOM----DELETEROOM----####
@app.route("/deleteRoom/<roomToDelete>/<roomNum>", methods = ["GET", "POST"])
def deleteRoom(roomToDelete=None, roomNum=None):
    if "sessionUserName" in session:
        if request.method == "POST":
            ## delete that jawn
            try:
                tempRoom = chatRoom.query.filter_by(roomName = roomToDelete).first()
                tempMessages = myMessage.query.filter_by(chatroom = roomNum).delete()
                db.session.delete(tempRoom)
                db.session.commit()
                del chatRooms[roomToDelete]
                return redirect(url_for("profile", curUserName = session["sessionUserName"], curRoomDeleted = 1))
            except:
                return redirect(url_for("profile", curUserName = session["sessionUserName"], curRoomDeleted = 2))
        else:
            return render_template("deleteRoom.html", curUserName = session["sessionUserName"], roomToDelete = roomToDelete, roomNum=roomNum)
    else:
        return redirect(url_for("login"))
######################################################
######################################################

@app.route("/new_msg", methods = ["POST"])
def new_msg():
    newMessage = myMessage(author = request.form["username"], contents = request.form["msg"], chatroom = request.form["room"])
    db.session.add(newMessage)
    db.session.commit()
    return ""

@app.route('/msgs/<id>', methods = ["GET"])
def msgs(id):
    m = myMessage.query.filter_by(chatroom = id).all()
    a = []
    for msg in m:
        a.append({'author':msg.author, 'content':msg.contents})
    return json.dumps(a)

#############################################
####----PROFILE----PROFILE----PROFILE----####
@app.route("/profile/")
def profile():
    if "sessionUserName" in session:
        pushRooms()
        cr = chatRoom.query.all()
        return render_template("profile.html", curUserName = session["sessionUserName"], curChatRooms = cr)
    else:
        return redirect(url_for("login"))
#############################################
#############################################

#######################################
####----LOGIN----LOGIN----LOGIN----####
@app.route("/login/", methods = ["GET", "POST"])
def login():
    if "sessionUserName" in session: # Check if user is already logged in
        return redirect(url_for("profile", curUserName = session["sessionUserName"]))
    elif request.method == "POST": # Otherwise check if they are currently trying to log in
        userExists = db.session.query(myUser.id).filter_by(userName = request.form["formUserName"]).scalar() # userExists set to None if the user is not in chat.db

        if userExists is not None: # If the POST sessionUserName exists in chat.db
            tempUser = myUser.query.filter_by(userName = request.form["formUserName"]).first() # Create temp object to access user data

            if tempUser.userPass == request.form["formPass"]: # Password was correct
                session["sessionUserName"] = request.form["formUserName"]
                return redirect(url_for("profile", curUserName = session["sessionUserName"]))
            else: # Password was incorrect
                return render_template("login.html", curPassError = 1)
        else: # POST curUserName does not exist in chat.db
            return render_template("login.html", curUserNameError = 1)

    return render_template("login.html") # Otherwise offer to log them in
#######################################
#######################################

##########################################
####----LOGOUT----LOGOUT----LOGOUT----####
@app.route("/logout/")
def logout():
    if "sessionUserName" in session:
        session.clear()
        return redirect(url_for("login"))
    else:
        return redirect(url_for("login"))
##########################################
##########################################

###################################################
####\\\\APPROUTES----APPROUTES----APPROUTES////####
###################################################

###################################################
####////FUNCTIONS----FUNCTIONS----FUNCTIONS\\\\####
###################################################

###################################################
####----PUSHROOMS----PUSHROOMS----PUSHROOMS----####
def pushRooms():
    n = chatRoom.query.count() # Count of all current chat rooms
    m = 0 # Count of chat rooms we've added to the array
    i = 1 # Current ID index

    while True: # Loop
        tempRoom = chatRoom.query.filter_by(chatroom_id=i).first()

        try: # If the ID was valid
            tempRoomName = tempRoom.roomName
            tempRoomUser = tempRoom.roomUser
            chatRooms[tempRoomName] = tempRoomUser
            i = i + 1 # Incriment ID
            m = m + 1 # Incriment # of chat rooms we've added
        except: # If the ID wasn't valid
            if m < n: # If we havent added all the chat rooms
                i = i + 1 # Incriment ID
            else: # Otherwise we're done
                break
    return
###################################################
###################################################

###################################################
####\\\\FUNCTIONS----FUNCTIONS----FUNCTIONS////####
###################################################

if __name__ == "__main__":
    app.run()
