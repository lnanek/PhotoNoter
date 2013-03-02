from flask import (Blueprint,request,render_template,
            redirect,url_for,flash)
from flask.ext.login import (login_user,logout_user,current_user,
        login_required,fresh_login_required)

from flask import current_app
from photoback import bcrypt 
from photoback import mongo
from photoback import login_manager
import requests

blueprint = Blueprint('settings',__name__,template_folder='templates')

