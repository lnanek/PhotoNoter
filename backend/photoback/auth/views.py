from flask import (Blueprint,request,render_template,
            redirect,url_for,flash)
from flask.ext.login import (login_user,logout_user,current_user,
        login_required,fresh_login_required)

from flask import current_app
from photoback import bcrypt 
from photoback import mongo
from photoback import login_manager
from photoback.users.models import User
from photoback.config.singly_credentials import singly_client_id,singly_client_secret 
import requests

blueprint = Blueprint('auth',__name__,template_folder='templates')

#FIXME: does this belong elsewhere?
@login_manager.user_loader
def load_user(userid):
    current_app.logger.error("called load_user with userid:")
    current_app.logger.error(userid)
    return User(userid,'blah')

#FIXME: set the LoginManager.login_view and use the login_required
#decorator in other views where it's needed, but don't use it here in auth
@blueprint.route('/signup/',defaults={'service_name':'none'},methods=['GET','POST'])
@blueprint.route('/signup/<service_name>/',methods=['GET','POST'])
def signup(service_name):
    #FIXME render a page that's basically the same as login.html
    #but with a confirm password textfield 
    #and check here that the confirm and original match
    if current_user.is_authenticated():
        return redirect(url_for('general.index'))

    current_app.logger.error(request.query_string)
    if service_name=='none':
        if request.method=='POST':
            username = request.form.get('email_address',None)
            password = request.form.get('password',None)
            #FIXME: make validation functions for username and password
            if(username is not None and password is not None):
                hashed = bcrypt.hashpw(password,bcrypt.gensalt())
                new_user={"account_id":username,"password_hash":hashed}
                mongo.db.users.insert(new_user,safe=True)
            else:
                #this should never happen unless the user had javascript
                #disabled and thus defeated the first line of validation
                return redirect(url_for('signup'))

            #FIXME: keep username and email as distinct things
            logged_in_user = User(username,hashed)
            login_user(logged_in_user)
            return redirect(url_for('general.index'))
        elif request.method == 'GET':
            return render_template('signup.html')
    else:#we are signing up via facebook,twitter,etc
        code = request.args.get('code')#code from singly 
        
        #exchange credentials + code for access token
        params={'client_id':singly_client_id,'client_secret':singly_client_secret,'code':code}
        response = (requests.post('https://api.singly.com/oauth/access_token',params=params))
        access_token = response.json().get('access_token',None)
        account  = response.json().get('account',None)

        hashed_token = bcrypt.generate_password_hash(access_token+account)
        new_user={"account_id":account,"secret":hashed_token,"services":[{"name":service_name,"access_token":access_token}]}

        mongo.db.users.insert(new_user,safe=True)
           
        hashed = bcrypt.generate_password_hash(access_token)#use Flask-Bcrypt

        #FIXME: figure out what should get stored in the auth token
        logged_in_user = User(account,hashed)
        #FIXME: check if the user already exists during the signup process
        login_user(logged_in_user)
        return redirect(url_for('general.index'))


@blueprint.route('/login/',methods=['GET','POST'])
def login():
    current_app.logger.error('next is: ')
    next_step = request.args.get('next',None)
    if next_step is None:
        next_step=""
    else:
        #url encode next_step
        next_step="?next="+next_step
    
    if request.method =='GET':
        if not current_user.is_authenticated():
            current_app.logger.error('request.path')
            current_app.logger.error(request.path)
            return render_template('login.html',next_step=next_step)
        else:
           return redirect(url_for('general.index'))
    elif request.method == 'POST':
        current_app.logger.error(request.form)
        
        username = request.form.get('email_address',None)
        password = request.form.get('password',None)
        remember_me = request.form.get('remember_me',None)
        if remember_me is None:
            remember_me = False
        else:
            remember_me = True
       
        #FIXME: define hash_from_database by actually retrieving the hash from the database 
        #for the given username then check that hashing the entered password matches
        hash_from_database = "FIX_ME"
        if (hash_from_database is None )or (bcrypt.check_password_hash(hash_from_database,password) == False):
            flash('User does not exist or password was incorrect');
            return redirect(url_for('login'))

        current_app.logger.error('Successful login!!!!')
        current_app.logger.error(username)
        current_app.logger.error(password)

        the_user = User(username,hash_from_database)
        login_user(the_user,remember=remember_me)
        next_step = request.args.get('next',None)
        if next_step is not None:
            return redirect(next_step)
        else:
            return redirect(url_for('general.index'))


@blueprint.route('/logout/',methods=['POST'])
def logout():
    if not current_user.is_authenticated():
        current_app.logger.error("user not logged in")
        return render_template('login.html')
    else:
        current_app.logger.error("user was logged in")
        logout_user()
        current_app.logger.error("called logout_user()")
        flash("Logged out successfully.")
        return render_template('login.html')


@blueprint.route('/oauth/authenticate/',defaults={'service_name':'none'},methods=['GET','POST'])
@blueprint.route('/oauth/authenticate/<service_name>/')
def authorize(service_name):
    #FIXME: supported_services should be stored elsewhere
    supported_services = [  'twitter', 'google', 'facebook']
    if service_name in supported_services:
        access_token = "fetch_from_mongo_using current_user.get_id()"
        return redirect("https://api.singly.com/oauth/authenticate?response_type=code&client_id=%s&redirect_uri=http://localhost/callback/%s&service=%s&access_token=%s"%(singly_client_id,service_name,service_name,access_token))
    else:
        flash("That service is not supported")
        return render_template('page_not_found.html'), 404


@blueprint.route('/callback/',defaults={'service_name':'none'})
@blueprint.route('/callback/<service_name>/')
def oauth_callback(service_name):
    code = request.args.get('code')

    #exchange credentials + code for access token
    params={'client_id':singly_client_id,'client_secret':singly_client_secret,'code':code}
    response = (requests.post('https://api.singly.com/oauth/access_token',params=params))
    #FIXME: check for an error response
    access_token = response.json().get('access_token',None)
    account  = response.json().get('account',None)

    results=mongo.db.users.find_one({'account_id':account})
    #if user does not already exist, add them to database and log them in
    if results is None:
        new_user={"account_id":account,"secret":bcrypt.generate_password_hash(access_token),"services":[]}
        mongo.db.users.insert(new_user,safe=True)
        the_user = User(account,bcrypt.generate_password_hash(access_token))
        login_user(the_user)
        current_app.logger.error('user is now logged in')
        current_app.logger.error(current_user)
    else:
        current_app.logger.error('user already exists')
        current_app.logger.error(current_user)
        if current_user.is_authenticated() == False:
            the_user = User(account,bcrypt.generate_password_hash(access_token))
            login_user(the_user)
            current_app.logger.error('logged in existing user')
    
    return redirect(request.args.get("next") or url_for('general.index'))


@blueprint.route('/reset_password/')
@fresh_login_required
def reset_password():
    return 'reset password here'

