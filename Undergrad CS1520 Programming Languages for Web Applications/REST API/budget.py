from flask import Flask, session, redirect, url_for, escape, request, render_template, flash
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import and_
from sqlalchemy import func
from sqlalchemy import exc
import json


app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///budget.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

app.secret_key = "this is a terrible secret key"


##########################################
####////MODELS----MODELS----MODELS\\\\####
##########################################

class Category(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(30), nullable=False, unique=True)
    limit = db.Column(db.Integer, nullable=False)

    def __init__(self, name, limit):
        self.name = name
        self.limit = limit

    def __repr__(self):
        return '<Category %r %r %r>' % (self.id, self.name, self.limit)

    def as_dict(self):
        return {
            "id": self.id,
            "name": self.name,
            "limit": self.limit
        }


class Purchase(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    cat_id = db.Column(db.Integer, db.ForeignKey('category.id'), nullable=False)
    name = db.Column(db.String(30), nullable=False)
    cost = db.Column(db.Integer, nullable=False)
    date = db.Column(db.String(10), nullable=False)

    def __init__(self, cat_id, name, cost, date):
        self.cat_id = cat_id
        self.name = name
        self.cost = cost
        self.date = date

    def __repr__(self):
        return '<Purchase = %r %r %r %r %r>' % (self.id, self.cat_id, self.name, self.cost, self.date)

    def as_dict(self):
        return {
            "id": self.id,
            "cat_id": self.cat_id,
            "name": self.name,
            "cost": self.cost,
            "date": self.date
        }

#########################################################
####////CONTROLLERS----CONTROLLERS----CONTROLLERS\\\\####
#########################################################

@app.cli.command('initdb')
def initdb_command():
    db.drop_all()
    db.create_all()

    default_cat = Category('rentUtils', 0)
    db.session.add(default_cat)
    db.session.commit()

    print('Initialized database.')


###################################################
####////FUNCTIONS----FUNCTIONS----FUNCTIONS\\\\####
###################################################

def add_category(new_cat_name, limit):
    cat_exists = Category.query.filter_by(name=new_cat_name).first()
    if not cat_exists:
        try:
            new_cat = Category(new_cat_name, limit)
            db.session.add(new_cat)
            db.session.commit()
            return True
        except exc.SQLAlchemyError:
            pass

    return False


def remove_category(selected_cat):
    cat_exists = Category.query.filter_by(name=selected_cat).first()
    if cat_exists:
        cats_p = Purchase.query.filter_by(cat_id=cat_exists.id).all()
        if cats_p:
            try:
                for p in cats_p:
                    db.session.delete(p)
            except exc.SQLAlchemyError:
                pass
        try:
            db.session.delete(cat_exists)
            db.session.commit()
            return True
        except exc.SQLAlchemyError:
            pass

    return False


def get_category(selected_cat_name):
    cat_exists = Category.query.filter_by(name=selected_cat_name).first().as_dict()
    if cat_exists:
        s = db.session.query(func.sum(Purchase.cost)).filter(Purchase.cat_id == cat_exists['id']).scalar()
        if s is None:
            s = 0
        pts = db.session.query(func.sum(Category.limit)).scalar()

        if selected_cat_name != 'rentUtils':
            return {
                'name': cat_exists['name'],
                'cost': s,
                'limit': cat_exists['limit'],
                'purchases': get_purchases_by_cat(selected_cat_name),
                'percent_total_spending': int(round((cat_exists['limit']/pts)*100, 0))
            }
        else:
            return {
                'name': cat_exists['name'],
                'cost': s,
                'limit': cat_exists['limit'],
                'purchases': get_purchases_by_cat(selected_cat_name)
            }

    return {}


def get_all_categories():
    try:
        raw_info = [x.as_dict() for x in Category.query.all()]
        for item in raw_info:
            s = db.session.query(func.sum(Purchase.cost)).filter(Purchase.cat_id == item['id']).scalar()
            if s is None:
                s = 0
            item['cost'] = s
        return raw_info
    except exc.SQLAlchemyError:
        return {}

def add_purchase(name, category, cost, date):
    cat_id = Category.query.filter_by(name=category).first().id
    if cat_id:
        try:
            p = Purchase(cat_id, name, cost, date)
            db.session.add(p)
            db.session.commit()
            return True
        except exc.SQLAlchemyError:
            pass

    return False


def remove_purchase(p_id):
    p = Purchase.query.filter_by(id=p_id).first()
    if p:
        try:
            db.session.delete(p)
            db.session.commit()
            return True
        except exc.SQLAlchemyError:
            pass

    return False


def remove_purchases(cat, p_list):
    cat_id = Category.query.filter_by(name=cat).first().id
    for x in p_list:
        try:
            temp = Purchase.query.filter(and_(Purchase.cat_id == cat_id, Purchase.name == x)).first()
            db.session.delete(temp)
        except exc.SQLAlchemyError:
            return False

    db.session.commit()
    return True


def get_all_purchases():
    try:
        return [x.as_dict() for x in Purchase.query.all()]
    except exc.SQLAlchemyError:
        return {}


def get_purchase_by_id(cat_id):
    try:
        return [x.as_dict() for x in Purchase.query.filter_by(id=cat_id).all()]
    except exc.SQLAlchemyError:
        return {}


def get_purchases_by_cat(cat_name):
    try:
        cat_id = Category.query.filter_by(name=cat_name).first().id
        return [x.as_dict() for x in Purchase.query.filter_by(cat_id=cat_id).all()]
    except exc.SQLAlchemyError:
        return {}

###################################################
####////APPROUTES----APPROUTES----APPROUTES\\\\####
###################################################


@app.route("/")
def skeleton():
    return render_template('skeleton.html')


@app.route("/cats", methods=["GET", "DELETE", "POST"])
def cats():
    if request.method == 'GET':
        if 'cat' in request.args:
            return json.dumps({
                'category': get_category(request.args['cat'])
            })
        elif 'cat_page' in request.args:
            return render_template('purchaseList.html', info=get_category(request.args['cat_page']))
        else:
            return json.dumps({
                'categories': get_all_categories()
            })
    elif request.method == 'DELETE' and 'cat' in request.args:
        return json.dumps({
            'completed': remove_category(request.args.get('cat'))
        })
    elif request.method == 'POST' and 'new_cat_name' in request.json and 'new_cat_limit' in request.json:
        return json.dumps({
            'completed': add_category(request.json['new_cat_name'], request.json['new_cat_limit'])
        })


@app.route("/purchases", methods=["GET", "DELETE", "POST"])
def purchases():

    if request.method == 'GET':
        if 'cat' in request.args:
            return json.dumps({
                'purchases': get_purchases_by_cat(request.args.get('cat'))
            })
        elif 'id' in request.args:
            return json.dumps({
                'purchase': get_purchase_by_id(request.args.get('id'))
            })
        else:
            return json.dumps({
                'purchase': get_all_purchases()
            })
    elif request.method == 'DELETE' and 'id' in request.args:
        return json.dumps({
            'completed': remove_purchase(request.args.get('id'))
        })
    elif request.method == 'DELETE' and 'cat' in request.json and 'names' in request.json:
        return json.dumps({
            'completed': remove_purchases(request.json['cat'], request.json['names'])
        })
    elif request.method == 'POST' and 'name' in request.json and 'category' in request.json and \
                                      'cost' in request.json and 'date' in request.json:
        return json.dumps({
            'completed': add_purchase(request.json['name'], request.json['category'],
                                      request.json['cost'], request.json['date'])
        })


if __name__ == '__main__':
    app.run()
