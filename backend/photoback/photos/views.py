from flask import (Blueprint,request,render_template,
            redirect,url_for,flash,current_app,jsonify,make_response)

from werkzeug import Response
from flask.ext.login import (login_user,logout_user,current_user,login_required)
from PIL import Image
from PIL.ExifTags import TAGS
from StringIO import StringIO

from photoback.config.javascript_credentials import aviary_key,filepicker_key
from photoback import mongo
import requests
import gridfs
from gridfs.errors import NoFile
from bson import ObjectId
from werkzeug import secure_filename
import os

blueprint = Blueprint('photos',__name__,template_folder='templates')

#return JSON of photo ids for user
#@blueprint.route('/photos/list/<user>') ?
@blueprint.route('/photos/names/')
#@login_required
#return only names of front side of fullsize/non-thumbnail photos
def index():
    file_list={}
    file_list['files']=[]
    for pic in mongo.db.fs.files.find({'size':{'$ne':'thumb'},'side':'front'}):
        fid = pic.get('_id')
        file_list['files'].append((str(fid)))
    current_app.logger.error(file_list)
    return jsonify(file_list)

@blueprint.route('/photos/')
#@login_required
def list_photos():
    return render_template('list_photos.html')

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.',1)[1] in ['jpg'] 

#FIXME: Unless we switch to filepicker, you'll need basic form based upload
@blueprint.route('/photos/upload',methods=['GET','POST'])
def upload_form_photos():
    if request.method=='GET':
        return render_template('upload_photos.html',aviary_key=aviary_key,filepicker_key=filepicker_key)
    elif request.method=='POST':
        current_app.logger.error("image tags are ")
        image_tags = request.form.get('tags',None)
        #side = request.form.get('side',None)
        #filename_values = []
        #FIXME: assume that the first image is the front
        #and the second image is the back
        image_index=0
        photo_id=""
        for f in request.files.getlist('file'):
             if f and allowed_file(f.filename):
                current_app.logger.error("type of f is ******") 
                current_app.logger.error(type(f))

                fs = gridfs.GridFS(mongo.db)
                thumb_io = StringIO()
                pil_im = Image.open(f)

                #GPS data seems to be structured like:
                #'GPSInfo': {1: 'N', 2: ((37, 1), (46, 1), (22, 1)), 3: 'W', 4: ((122, 1), (24, 1), (7, 1)), 7: ((21, 1), (53, 1), (7, 1))
                #Lat, lon,altitude
                exif_details = pil_im._getexif()
                exif_tags={}
                if exif_details:
                    for tag,value in exif_details.items():
                        decoded = TAGS.get(tag, tag)
                        exif_tags[decoded] = value

                if 'GPSInfo' in exif_tags: 
                    coordinates = exif_tags['GPSInfo']
                    latitude = coordinates.get(2,None)
                    longitude = coordinates.get(4,None)
                    if(latitude and longitude):
                        latsign = coordinates.get(1,None)
                        lonsign = coordinates.get(3,None) 
                        if latsign=='N':
                            latsign=1
                        else:
                            latsign=-1
                        if lonsign=='W':
                            lonsign=-1
                        else:
                            lonsign=1
                        latitude = latsign*(float(latitude[0][0])+float(latitude[1][0])/60.+float(latitude[2][0])/3600)
                        longitude = lonsign*(float(longitude[0][0])+float(longitude[1][0])/60.+float(longitude[2][0])/3600)
                        current_app.logger.error("latitude")
                        current_app.logger.error(latitude)
                        current_app.logger.error(longitude)
                        current_app.logger.error("GPS info")
                        current_app.logger.error(exif_tags['GPSInfo'])
                else:
                    latitude=None
                    longitude=None


                #use PIL to detect image type prior to saving since content-type header could be manipulated
                #FIXME: use Flask-Uploads to handle format detection/validation
                image_type = 'image/'+pil_im.format

                thumb_copy = pil_im.copy()
                thumb_copy.thumbnail((128,128))#move thumbnail size out to app.config
                thumbio=StringIO()
                thumb_copy.save(thumbio,format='JPEG')
                #grab the ObjectId of the thumbnail, so you can associate it 
                #with the full-size image later
                thumb_id = fs.put(thumbio.getvalue(),filename="",content_type="image/jpg",size="thumb")

                current_app.logger.error("content-type ")
                current_app.logger.error(image_type)

                tmp = pil_im.copy()
                tmpio =StringIO()
                tmp.save(tmpio,format='JPEG')
                #use an extra identifier(photo_id) to associate the two sides of a photo with each other
                if(image_index==0):
                    side='front'
                    fid = fs.put(tmpio.getvalue(),filename=f.filename,content_type=image_type,thumbnail_id=thumb_id,lat=latitude,lon=longitude,side=side,hashtags=image_tags)
                    photo_id=fid
                else:
                    side='back'
                    fid = fs.put(tmpio.getvalue(),filename=f.filename,content_type=image_type,thumbnail_id=thumb_id,lat=latitude,lon=longitude,side=side,hashtags=image_tags,photo_id=photo_id)
                

                current_app.logger.error("pushed image to mongo ")
                current_app.logger.error(fid)
                current_app.logger.error(fs.exists(fid))
                image_index+=1
    return redirect(url_for("photos.list_photos"))

@blueprint.route('/photos/uploadfp',methods=['GET','POST'])
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
            mimetype="image/jpg"
        else:
            #response.mimetype = the_file.content_type
            pass
        #return response
        #this approach avoids reading the entire file into memory
        return Response(the_file,mimetype=mimetype,direct_passthrough=True)
    except NoFile:
        abort(404)

@blueprint.route('/photos/<photo_id>/thumb',methods=['GET','POST'])
#@login_required
def get_thumb(photo_id):
    #FIXME: you actually need to do fs.get_last_version(f) for f in fs.list()]
    fs = gridfs.GridFS(mongo.db)
    try:
        #Two DB queries just to fetch a thumbnail... 
        #the_file = fs.get(ObjectId(photo_id))
        #the_file = fs.get(the_file.thumbnail_id)
        #is this any better?
        thumbnail_id = mongo.db.fs.files.find_one({"_id":ObjectId(photo_id)},{"thumbnail_id":1})['thumbnail_id']
        the_file = fs.get(thumbnail_id)
        current_app.logger.error(the_file.content_type)
        mimetype = the_file.content_type
        if mimetype is None:
            #response.mimetype="image/png"
            mimetype="image/jpg"
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
    back_id = mongo.db.fs.files.find_one({'photo_id':ObjectId(photo_id)})
    back_id = back_id.get('_id',None)
    return render_template('show_photo.html',photo_id=photo_id,back_id=back_id)

@blueprint.route('/photos/details/<photo_id>/',methods=['GET','POST'])
#@login_required
def describe_photo(photo_id):
    try: 
        the_file = mongo.db.fs.files.find_one({'_id':ObjectId(photo_id)})
        current_app.logger.error(the_file.get('uploadDate'))
        current_app.logger.error(type(the_file))
        details = {'uploadDate':str(the_file.get('uploadDate')),
                'contentType':the_file.get('contentType'),
                'latitude':the_file.get('lat'),
                'longitude':the_file.get('lon'),
                'side':the_file.get('side')}
        return jsonify(details)
    except NoFile:
        abort(404)

def thumbnail(file_obj):
    fs = gridfs.GridFS(mongo.db)#or put in main file?
    pil_im = Image.open(fs.get(fid))
    #Note: thumbnail preserves aspect ratio, so you
    #won't necessarily get the size you specify
    #use pil.resize() if you want to force an exact size
    pil_im.thumbnail((128,128))#this modifies pil_im directly
    thumb_io = StringIO()
    #FIXME figure out how to detect format, or just always use JPG?
    pil_im.save(thumb_io,format='JPEG')
    thumb_id = fs.put(thumb_io.getvalue(),filename="",content_type="")

