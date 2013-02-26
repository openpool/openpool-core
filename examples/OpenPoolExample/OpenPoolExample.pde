/*
 This is an example sketch for OpenPool library.
 This sketch requires following libraries to run properly.

  - OpenPool: http://www.open-pool.com/.
  - SimpleOpenNI: http://code.google.com/p/simple-openni/wiki/Installation

 Copyright (c) takashyx 2012-2013 ( http://takashyx.com )
 Copyright (c) arc@dmz 2012-2013 ( http://junkato.jp )

 All rights reserved.
 This work is licensed under GPL v2.
*/

import openpool.*;
import SimpleOpenNI.*;

OpenPool op;

void setup() {
  size(840, 440);
  //op = new OpenPool(this, "straight1.oni");
  op = new DummyPool(this);
  op.loadConfig("config.txt");
  op.setConfigMode(true);
}

void draw() {
  op.updateBalls();
  for (Ball ball : op.balls) {
    ball.draw(this);
    if (ball.prev != null) {
      line(ball.x, ball.y, ball.prev.x, ball.prev.y);
    }
  }
}
