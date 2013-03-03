from flask import (Blueprint,request,render_template,
            redirect,url_for,flash,current_app,jsonify,make_response)

from werkzeug import Response
from flask.ext.login import (login_user,logout_user,current_user,login_required)
from PIL import Image
from StringIO import StringIO

from photoback.config.javascript_credentials import aviary_key,filepicker_key
from photoback import mongo
import requests
import gridfs
from gridfs.errors import NoFile
from bson import ObjectId

blueprint = Blueprint('photos',__name__,template_folder='templates')

#return JSON of photo ids for user
#@blueprint.route('/photos/list/<user>') ?
@blueprint.route('/photos/names/')
#@login_required
def index():
    file_list={}
    file_list['files']=[]
    for pic in mongo.db.fs.files.find():
        fid = pic.get('_id')
        file_list['files'].append((str(fid)))
    current_app.logger.error(file_list)
    return jsonify(file_list)

@blueprint.route('/photos/')
#@login_required
def list_photos():
    return render_template('list_photos.html')

#FIXME: Unless we switch to filepicker, you'll need basic form based upload
@blueprint.route('/photos/upload',methods=['GET','POST'])
#@login_required
def upload_photos():
    if request.method=='GET':
        #current_app.logger.error(url_for('general.index'))
        return render_template('upload_photos.html',aviary_key=aviary_key,filepicker_key=filepicker_key)
    elif request.method =='POST':
        #fetch filepicker or aviary URL, thumbnail it, etc.
        #this should be in a work queue
        #TODO: somehow allow the user to specify a filename?
        #FIXME: do some error handling if the URL fetch fails somehow
        im_url = request.form.get('url',None)
        current_app.logger.error(im_url)
        result=requests.get(im_url)
        
        if result.status_code!=200:
            flash("image upload failed!!!")
            return render_template('upload_photos.html',aviary_key=aviary_key,filepicker_key=filepicker_key)

        fs = gridfs.GridFS(mongo.db)
        thumb_io = StringIO()
        pil_im = Image.open(StringIO(result.content))
        #use PIL to detect image type prior to saving since content-type header could be manipulated
        #FIXME: use Flask-Uploads to handle format detection/validation
        image_type = 'image/'+pil_im.format

        tmp = pil_im.copy()
        tmp.thumbnail((128,128))#move thumbnail size out to app.config
        tmp.save(thumb_io,format='JPEG')
        thumb_id = fs.put(thumb_io.getvalue(),filename="",content_type="image/jpg")

        fid = fs.put(StringIO(result.content),filename="omglol.jpg",content_type=image_type,thumbnail_id=thumb_id)

        current_app.logger.error("pushed image to mongo ")
        current_app.logger.error(fid)
        current_app.logger.error(fs.exists(fid))
        return 'doing some thumbnai and gridfs stuff'

@blueprint.route('/photos/<photo_id>/',methods=['GET','POST'])
#@login_required
def get_photo(photo_id):
    #FIXME: you actually need to do fs.get_last_version(f) for f in fs.list()]
    fs = gridfs.GridFS(mongo.db)
    current_app.logger.error(photo_id)
    current_app.logger.error(fs.exists(ObjectId(photo_id)))
    try:
        the_file = fs.get(ObjectId(photo_id))
        #response = make_response(the_file.read())
        current_app.logger.error(the_file.content_type)
        mimetype = the_file.content_type
        if mimetype is None:
            #response.mimetype="image/png"
            mimetype="image/png"
        else:
            #response.mimetype = the_file.content_type
            pass
        #return response
        #this approach avoids reading the entire file into memory
        return Response(the_file,mimetype=mimetype,direct_passthrough=True)
    except NoFile:
        abort(404)

#FIXME: what URL should return image metadata and what should return the actual binary data?
@blueprint.route('/photos/detail_view/<photo_id>/',methods=['GET','POST'])
#@login_required
def show_photo(photo_id):
    return render_template('show_photo.html',photo_id=photo_id)

@blueprint.route('/photos/details/<photo_id>/',methods=['GET','POST'])
#@login_required
def describe_photo(photo_id):
    try: 
        the_file = mongo.db.fs.files.find_one({'_id':ObjectId(photo_id)})
        current_app.logger.error(the_file.get('uploadDate'))
        current_app.logger.error(type(the_file))
        details = {'uploadDate':str(the_file.get('uploadDate')),'contentType':the_file.get('contentType')}
        return jsonify(details)
    except NoFile:
        abort(404)

def thumbnail(file_obj):
    fs = gridfs.GridFS(mongo.db)#or put in main file?
    pil_im = Image.open(fs.get(fid))
    pil_im.thumbnail((128,128))#this modifies pil_im directly
    thumb_io = StringIO()
    #FIXME figure out how to detect format, or just always use JPG?
    pil_im.save(thumb_io,format='JPEG')
    thumb_id = fs.put(thumb_io.getvalue(),filename="",content_type="")

