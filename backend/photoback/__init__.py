from flask import Flask,render_template,g
from flask.ext.bcrypt import Bcrypt
from flask.ext.login import LoginManager
from flask_pymongo import PyMongo

bcrypt = Bcrypt()
login_manager = LoginManager()
mongo = PyMongo()


def create_app(config_objects):
    app = Flask(__name__)

    for config_object in config_objects:
        app.config.from_object(config_object)

    bcrypt.init_app(app)
    login_manager.init_app(app)
    #login view can be specified in settings but defaults to auth.login
    login_manager.login_view = app.config.get('AUTH_LOGIN_VIEW','auth.login')
    login_manager.login_message = app.config.get('AUTH_LOGIN_MESSAGE',None)
    mongo.init_app(app)
    
    from photoback.auth import views as auth
    from photoback.general import views as general
    from photoback.photos import views as photos
    from photoback.calendar import views as calendar
    from photoback.settings import views as settings


    app.register_blueprint(auth.blueprint)
    app.register_blueprint(general.blueprint)
    app.register_blueprint(photos.blueprint)
    app.register_blueprint(calendar.blueprint)
    app.register_blueprint(settings.blueprint)
    return app
    
