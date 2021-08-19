# CatchCounter
### Demo Video
https://youtu.be/SSTXHXLsrTM

### Description
This is my university project for a course on mobile computing and IoT. The Android app is able to count juggling catches 
based on accelerometer data streamed to the phone from a Garmin watch which is worn while juggling. 

### Communication between Connect IQ App and Android App
The Connect IQ app on the Garmin watch communicates with the Android app through Garmin's own Garmin Connect app. Most users of 
Garmin devices have this app installed and it takes care of pairing the watch with the phone. Once a bluetooth connection between phone and
watch is established by the Garmin Connect app, the app can act as a proxy to the web or to another phone application.
For android development Garmin provides a java library to handle communication with and receive information about Garmin devices and Connect IQ apps on these devices.

### Garmin Connect IQ Compatibility
Although most of the newer Garmin watches come with Connect IQ Version 3 and above 
and there are already version 4 deprecation notices for some version 1 features, I wanted my app to be compatible all the way
back to Connect IQ version 1.0, the primary reason being that the watch I own (Forerunner 235) only has version 1.4.

### Catch Detection
On my watch (and I believe this is true for mosst other devices running Connect IQ 1.x) the accelerometer data can be read
at a frequency up to 10/s. Anything more frequent results in repeated sensor values. This is what I based the catch detection on.
I have recorded myself juggling 3 balls at different speeds and heights, as well as 4 and 5 balls. The corresponding accelerometer 
data together with some python visualization scripts can be found in the Data folder. On newer Garmin devices and Connect IQ versions
accelerometer data can be read at a frequency up to 25/s. This would probably make catch detection quite a bit easier, especially for 
a higher number of balls.

The actual catch detection in the android app uses the fact that there is roughly one very short and very steep drop in the Z axis per catch.

