import openpool.*;
import SimpleOpenNI.*;

OpenPool op;

void setup() {
  //op = new OpenPool(this, "straight1.oni");
  op = new DummyPool(this);
  op.setConfigMode(true);
  size(840, 440);
}

void draw() {
  op.updateBalls();
  for (Ball ball : op.balls) {
    ball.draw(this);
  }
}
