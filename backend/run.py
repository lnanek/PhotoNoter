#use this for external WSGI servers like: gunicorn run:app
from photoback import create_app
app = create_app(['photoback.config.debug'])
app.debug = True
