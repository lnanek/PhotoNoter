#if you use SqlAlchemy you can make this
#inherit from db.Model AND UserMixin (from Flask-Login)

class User():
    "encapsulate a user_id for Flask-Login"
    def __init__(self,username,password_hash):
        self.username = username
        self.password_hash = password_hash
    def is_authenticated(self):
        #FIXME: implement is_authenticated
        return True
    def is_active(self):
        #FIXME: implement is_active
        return True
    def is_anonymous(self):
        #FIXME: implement is_anonymous
        return False
    def get_id(self):
        #FIXME: implement get_id
        return unicode(self.username) 
    def get_auth_token():
        #FIXME this needs to be random for every session?
        #and should invalidate if the user changes password
        return make_secure_token(username,password_hash)
    #should you keep the interactions
    #with the database outside of here?
    #in that case, just use the existing constructor
    #and handle fetching user info,etc. outside

