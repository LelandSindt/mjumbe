#!/usr/bin/python

import sys
import time
import calendar
import cgi
import anydbm
import json

sys.path.insert(0, '/mnt/sd/arduino/python/')

from gcm import GCM as GCM

form = cgi.FieldStorage() 

db = anydbm.open('/mnt/sd/arduino/anydbm/messenger.db', 'c')

if form.getvalue('api_key') <> None:
  db['api_key'] = form.getvalue('api_key')

print "Content-type: text/html\r\n\r\n";  

try:
  API_KEY = db['api_key']
except:
  print "<html> "
  print "  <head> <h1> mjumbe -- Configue API KEY </h1> </head>"
  print "  <body> "
  print "  <form method=\"get\" action=\"messenger.py\">" 
  print "  <div> <textarea rows=\"1\" name=\"api_key\" cols=\"128\" placeholder=\"GCM API KEY\"></textarea> </div>"
  print "  <div> " 
  print "  <div> <input type=\"submit\" value=\"Set API Key\" /></div> " 
  print "  </form>" 
  print "  </body>" 
  print "</html>"
  db.close()
  quit()
  
try:
  reg_keys = json.loads(db['keys'])
except:
  reg_keys = {}

  
if form.getvalue('add_reg_id') <> None:
  if form.getvalue('add_android_id') <> None:
    print "add andorid id: " + form.getvalue('add_android_id')  + " reg id: " + form.getvalue('add_reg_id')
    reg_keys[form.getvalue('add_android_id')] = form.getvalue('add_reg_id')
    print "ok"
  
  
if form.getvalue('del_android_id') <> None:
  print "del android id: " + form.getvalue('del_android_id')
  try:
    del reg_keys[form.getvalue('del_android_id')]
    print "ok"
  except:
    print "not registered: " + form.getvalue('del_android_id') 
    
  

db['keys'] = json.dumps(reg_keys) 
db.close()  

data = {}

reg_ids = []
android_ids = []

reg_ids = reg_keys.values()
android_ids = reg_keys.keys()

if len(reg_ids) == 0:
  print "<html> "
  print "  <head> <h1> mjumbe -- No devices registered... </h1> </head>"
  print "</html>"
  quit()

if form.getvalue('show_keys') <> None:
  print "<html> <body>"
  print str(reg_ids)
  print "<br>"
  print str(android_ids)
  print "</body> </html>"
  quit()
  
if form.getvalue('messagetype') <> None:
  data['messageType'] = form.getvalue('messagetype')
 
if form.getvalue('message') <> None:
 gcm = GCM(API_KEY)
 data['message'] = form.getvalue('message')
 data['ctm'] = calendar.timegm(time.gmtime()) * 1000 # Current Time Millis
 try:
   response = gcm.json_request(registration_ids=reg_ids, data=data) 
 except:
   print "something went wrong :( "
   
if form.getvalue('del_reg_id') == None and form.getvalue('add_reg_id') == None:   
  print "<html> "
  print "  <head> <h1> mjumbe </h1> </head>"
  print "  <body> "
  print "  <form method=\"get\" action=\"messenger.py\">"
  print "  <div> <textarea rows=\"2\" name=\"message\" cols=\"23\" placeholder=\"Message to send\"></textarea> </div>" 
  print "  <div> "
  print "    <select name=\"messagetype\">"
  print "    <option value=\"info\">Info</option>  "
  print "    <option value=\"alert\">Alert</option>  "
  print "    </select> "
  print "  <div> "
  print "  <div> <input type=\"submit\"  value=\"Send Message\" /></div> "
  print "  </form>"
  print "  </body>"
  print "</html>" 
