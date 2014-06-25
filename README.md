mjumbe
====

mjumbe is designed to make it easy to send messages from an Arduino Yun (or any platform with Python available) to your Android device via Google Cloud Messenger. 

--mjumbe is Swahili for messenger. 

Installation
----

- Yun

SD Card Directory Structure
``` bash
/arduino/
/arduino/cgi-bin/
/arduino/anydbm/
/arduino/python/
```


Download and Install Python scripts to the Yun
``` bash
curl -k https://raw.githubusercontent.com/geeknam/python-gcm/master/gcm/gcm.py -o /mnt/sd/arduino/python/gcm.py
curl -k https://raw.githubusercontent.com/LelandSindt/mjumbe/master/Yun/messenger.py -o /mnt/sd/arduino/cgi-bin/messenger.py
ln -s /mnt/sd/arduino/cgi-bin/messenger.py /www/cgi-bin/messenger.py
opkg update
opkg install python-openssl 
```

Set the API KEY in via messenger.py

Point your browser to http://<Yun IP Address>/cgi-bin/messenger.py you will be prompted to set the GCM API key.

- Android

Download, Compile and Install from srouce.

-or-

Download and sideload the apk https://docs.google.com/file/d/0ByRcDFX2gvmbcDFCcy1wRDhsU0E

Once installed you will be prompted to set the Project ID and the Registration URL.

Enter your project ID and the full URL of the messenger.py

example: http://192.168.1.10/cgi-bin/messenger.py

After the Project ID and Registration URL have been changed the app will register its self with Google Cloud Messenger and then register its self with the Arduino Yun via the messenger.py script. 

Messages can be sent via the messenger.py http web interface or via the command line. 

```bash
python /mnt/sd/arduino/cgi-bin/messenger.py message=Hello\ World\&messagetype=alert
```

Note: Registration between your Andorid device and the Yun (messenger.py) should take place over a local network. 


Todo
----
- Record all recieved messages and display them as a part of the mjumbe main activity.
- Implement message encryption.
- Publish mjumbe Android application on the google play market... 
- General code cleanup

References
----
- http://developer.android.com/google/gcm/gcm.html
- http://developer.android.com/google/gcm/client.html
- https://github.com/geeknam/python-gcm

