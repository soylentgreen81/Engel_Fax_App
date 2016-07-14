# Engel Fax

![Build status](https://travis-ci.org/soylentgreen81/Engel_Fax_App.svg?branch=master)


![Engel Fax](https://github.com/soylentgreen81/Engel_Fax_App/blob/master/app/src/main/res/mipmap-xxhdpi/ic_launcher.png)


# Table of Contents
1. [About](#about)
2. [Prerequisites](#prerequisites)
3. [How to get it](#how-to-get-it)
4. [Used libraries](#used-libraries--icons)

## About
A SMS application for Android, which offers several means of communication
* Sending clear text sms (plain & simple & pretty boring)
* Sending ascii art sms using a prefix. Three flavors available: 
   * #A + Index -> static Ascii Art 
   * #F + Index -> Figlet Fonts
   * #C + Index -> Cowsay quotes
* Sending "ascii"-bitmaps using base 64 encoded binary data. Uses currently 2bits per pixel in a 24x18 bitmap. You can even import and crop your favorite images using a simple brightness check to convert them into an ascii image!

For more information on the server side consult:
https://github.com/muellmatto/smsfax

For more information on the message format check the [Wiki](https://github.com/soylentgreen81/Engel_Fax_App/wiki)

## Prerequisites
* API-Level 16 (Android 4.1 "Jelly Bean")
* Permission to send sms
* Camera Permission
 

## How to get it
* Check if you can install APKs from Unknown Sources (Settings -> Security -> Unknown Sources) [via](http://developer.android.com/distribute/tools/open-distribution.html)
* Download the APK from the [Latest Releases](https://github.com/soylentgreen81/Engel_Fax_App/releases/latest) and install it

## Used libraries / Icons
* [jdamcd/android-crop](https://github.com/jdamcd/android-crop)
* [journeyapps/zxing-android-embedded](https://github.com/journeyapps/zxing-android-embedded)
* [futuresimple/android-floating-action-button](https://github.com/futuresimple/android-floating-action-button)
* [ckurtm/FabButton](https://github.com/ckurtm/FabButton)
* Icons made by [Daniel Bruce](http://www.flaticon.com/authors/daniel-bruce)  from [www.flaticon.com](http://www.flaticon.com) is licensed by [CC BY 3.0](http://creativecommons.org/licenses/by/3.0/) 
* Icons made by [Freepik](http://www.freepik.com)  from [www.flaticon.com](http://www.flaticon.com) is licensed by [CC BY 3.0](http://creativecommons.org/licenses/by/3.0/) 
* Icons made by [Iconmoon](http://www.icomoon.io)  from [www.flaticon.com](http://www.flaticon.com) is licensed by [CC BY 3.0](http://creativecommons.org/licenses/by/3.0/) 
