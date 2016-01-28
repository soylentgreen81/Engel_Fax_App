# Engel Fax
![Build status](https://travis-ci.org/soylentgreen81/Engel_Fax_App.svg?branch=master)


![Engel Fax](https://github.com/soylentgreen81/Engel_Fax_App/blob/master/app/src/main/res/mipmap-xxhdpi/ic_launcher.png)

## Android App 

Offers several means of communication
* sending clear text sms (plain & simple & pretty boring)
* sending ascii art sms using a prefix. Three flavors available: 
   * #A + Index -> static Ascii Art 
   * #F + Index -> Figlet Fonts
   * #C + Index -> Cowsay quotes
* sending "ascii"-bitmaps using base 64 encoded binary data. uses currently 2bits per pixel in a 24x18 bitmap. You can even import and crop your favorite images using a simple brightness check to convert them into an ascii image!

For more information on the server side consult:

https://github.com/muellmatto/smsfax

## Prerequisites
* API-Level 16 (Android 4.1 "Jelly Bean")
* Permission to send sms
