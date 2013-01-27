import openpool.*;

OpenPool op;

void setup() {
  op = new OpenPool(this, "straight1.oni");
  op.setConfigMode(true);
}

void draw() {
  op.updateBalls();
  for (Ball ball : op.balls) {
    ball.draw(op);
  }
}
