from flask import (Blueprint,request,render_template,
            redirect,url_for,flash,current_app,jsonify,make_response)

from werkzeug import Response
from flask.ext.login import (login_user,logout_user,current_user,login_required)

from photoback import mongo
import requests
import gridfs
from gridfs.errors import NoFile
from bson import ObjectId
import os
from photoback.config.javascript_credentials import tokbox_key

blueprint = Blueprint('opentok',__name__,template_folder='templates')

@blueprint.route('/opentok')
def video():
    return render_template("opentok.html",tokbox_key=tokbox_key)
