from flask import (Blueprint,request,render_template,
            redirect,url_for,flash)
from flask.ext.login import (login_user,logout_user,current_user,
        login_required,fresh_login_required)

from flask import current_app
from photoback import bcrypt 
from photoback import mongo
from photoback import login_manager
from photoback.users.models import User
import requests

blueprint = Blueprint('photos',__name__,template_folder='templates')

