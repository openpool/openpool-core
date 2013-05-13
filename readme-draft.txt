openpool-base-kinect
----------------------------------------------------------------

 This is the base function library for OpenPool project.
 For more detail, please visit http://www.open-pool.com/.

 Copyright (c) takashyx 2012-2013 ( http://takashyx.com )
 Copyright (c) arc@dmz 2012-2013 ( http://junkato.jp )
 Copyright (c) Dave Cahill 2012-2013 ( http://*************** )
 Copyright (c) Hideki Takeoka 2012-2013 ( http://*************** )

 Anybody else!!!!

 Copyright (c) Lab Production 2012-2013 ( http://lab-cafe.net )

 All rights reserved.
 This work is licensed under GPL v2.

----------------------------------------------------------------

Support System:
  windows 7 64-bit (reference environment)
  windows 8 64-bit (unstable)
  Mac OS X Lion    (unstable)

reference environment:
  In general, openpool base is tested only on windows 7 64bit machines.
  We use processing 2.0b with 64 bit java environment.

dependencies:
  processing 2.0b
  OpenCV 2.4.3 ( will be replaced with 2.4.4 ) 
  java JDK 1.7.0_10
  javacv 0.3 ( will be removed in the formal release )
  OpenNI 1.5.4.0 for windows 64-bit( will be replaced with the latest major release )
  PrimeSense NITE 1.5.2.21 for windows 64-bit
  PrimeSense Sensor KinectMod 5.1.2.1 for windows 64-bit
  
  For more "unofficially" supported combinations,see compatibility matrix at our HP:
  ((((link to the compatibility matrix ))))

How to setup the environment for runnning sample codes

  for windows 7 32 bit (untested)
  
  for windows 7 64 bit (reference environment)
    1.install latest java JRE or JDK
      tested: java JDK 1.7.0_10
      tested: java JDK 1.7.0_17

    2.install OpenCV
      tested: java JDK 1.7.0_10

    3.install OpenNI
      tested: java JDK 1.7.0_10

    4.install javacv 0.3
      tested: javacv 0.3

    5.install 

    6.clone the repo

    7.run the sample code
    
  for windows 8 32 bit (untested)
  for windows 8 64 bit (untested)
  
  Mac OSX ( tested but slow and have some troubles )

Steps taken:
Run ant build from resources/build.xml
Install SimpleOpenNI from Google (
Copy SimpleOpenNI directory from dependency directory to /Users/[username]/Processing/libraries
install opencv --> issues...
Install 32-bit OpenCV via homebrew
$ brew install --universal jpeg jasper libtiff libpng
$ brew install --env=std --32-bit opencv
However, there is currently an issue with 32-bit OpenCV via brew: 
Alternative method for installing OpenCV 32:
wget OpenCV-2.4.3.tar.bz2
tar xjvf OpenCV-2.4.3.tar.bz2
cd OpenCV-2.4.3
mkdir build; cd build
cmake -G "Unix Makefiles" -D CMAKE_OSX_ARCHITECTURES=i386 -D CMAKE_C_FLAGS=-m32 -D CMAKE_CXX_FLAGS=-m32 ..
make -j4
sudo make install
Open examples/OpenPoolExample/OpenPoolExample.pde in Processing
Run, success! 

    (((( Big thank you to Dave Cahill!!!!! ))))

    since we do not have much resource to check all the major environment, we welcome your report on other platforms.

----------------------------------------------------------------
FAQ
  Q. Are you busy
  A. Yes

  Q. Could you help us setup
  A. No

  Q. Can I mail you when I have trouble setting up the environment
  A. We have ML for dev please check it out

  Q. Does dolphines dream?
  A. Yes Coz they are mammals.

  Have a nice day period
