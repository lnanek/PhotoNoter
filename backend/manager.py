#!/usr/bin/env python

import sys
from flask.ext.script import Manager,prompt,prompt_bool,prompt_pass
from photoback import create_app,mongo
#from init_db import services

#Debug settings are distinct from test settings
#use simple database, debugger enabled,etc
app = create_app(['photoback.config.debug'])
manager = Manager(app)

#In case you mainly want to use development settings but
#still toggle debugger quickly
@manager.command
def debug(toolbar=False,port=8000):
    app.debug = True

    if toolbar:
        from flask_debugtoolbar import DebugToolbarExtension
        DebugToolbarExtension(app)
        app.logger.debug("toolbar enabled")

    app.run(port=int(port))

@manager.command
def database(action):
    if action == 'create':
        mongo.db.services.insert(services,safe=True)
    elif action == 'drop':
        pass

@manager.shell
def make_shell_context():
    context = {
            'app':app,
            'mongo':mongo
    }
    return context

if __name__ == '__main__':
    manager.run()
