#!/usr/bin/python

import sys
import time
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
  
gcm = GCM(API_KEY)

try:
  reg_keys = json.loads(db['keys'])
except:
  reg_keys = []

  
if form.getvalue('add_reg_id') <> None:
  print "add reg id: " + form.getvalue('add_reg_id')
  try:
    index = reg_keys.index(form.getvalue('add_reg_id'))
  except: 
    reg_keys.append((form.getvalue('add_reg_id')))
    print "ok"
  
  
if form.getvalue('del_reg_id') <> None:
  print "del reg id: " + form.getvalue('del_reg_id')
  try:
    reg_keys.remove(form.getvalue('del_reg_id'))
    print "ok"
  except:
    print "not registered: " + form.getvalue('del_reg_id') 
    
  

db['keys'] = json.dumps(reg_keys) 
db.close()  

data = {}
  
if form.getvalue('messagetype') <> None:
  data['messageType'] = form.getvalue('messagetype')
  
 
if form.getvalue('message') <> None:
 data['message'] = form.getvalue('message')
 try:
   response = gcm.json_request(registration_ids=reg_keys, data=data) 
 except:
   print "something went wrong :( -- response: " + response
   
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
