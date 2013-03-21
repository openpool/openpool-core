package openpool.config;

import java.awt.Point;
import java.awt.event.KeyEvent;

import openpool.BallDetector;
import openpool.OpenPool;

public class CameraPositionConfigHandler extends ConfigHandlerAbstractImpl {
	private final int minimumDistanceSq = 20 * 20;
	private OpenPool op;
	private BallDetector ballDetector;

	/**
	 * True when camera image corner is grabbed with mouse pointer.
	 */
	int selectedCam = -1;
	int camCount;

	public CameraPositionConfigHandler(OpenPool op,BallDetector ballDetector) {
		this.op = op;
		this.ballDetector = ballDetector;
		this.camCount = ballDetector.getCamCount();
	}
	
	@Override
	public String getTitle() {
		return "Specify the camera image area by dragging the top-left corners. / background is automatically defined when dragged. You can define it manually by pressing SPACE key.";
	}

	@Override
	public void draw() {
		int depthWidth = ballDetector.getDepthWidth();
		int depthHeight = ballDetector.getDepthHeight();
		
		Point tl = op.getCombinedImageTopLeft();
		Point br = op.getCombinedImageBottomRight();

		Point cam1tl_imageAxis = new Point();
		Point cam1br_imageAxis = new Point();
		Point cam2tl_imageaxis = new Point();
		Point cam2br_imageaxis = new Point();

		Point cam1tl_ScreenAxis = new Point();
		Point cam1br_ScreenAxis = new Point();
		Point cam2tl_ScreenAxis = new Point();
		Point cam2br_ScreenAxis = new Point();
		
		cam1tl_imageAxis.x = ballDetector.getX1();
		cam1tl_imageAxis.y = ballDetector.getY1();
		
		cam1br_imageAxis.x = cam1tl_imageAxis.x + ballDetector.getCam1Width();
		cam1br_imageAxis.y = cam1tl_imageAxis.y + ballDetector.getCam1Height();

		cam1tl_ScreenAxis = ImagetoScreen(cam1tl_imageAxis,tl,br,depthWidth,depthHeight,0,ballDetector.getCam1Width());
		cam1br_ScreenAxis = ImagetoScreen(cam1br_imageAxis,tl,br,depthWidth,depthHeight,0,ballDetector.getCam1Width());

		if(camCount >= 2)
		{
			cam2tl_imageaxis.x = ballDetector.getX2();
			cam2tl_imageaxis.y = ballDetector.getY2();
			
			cam2br_imageaxis.x = cam2tl_imageaxis.x + ballDetector.getCam2Width();
			cam2br_imageaxis.y = cam2tl_imageaxis.y + ballDetector.getCam2Height();
			
			cam2tl_ScreenAxis = ImagetoScreen(cam2tl_imageaxis,tl,br,depthWidth,depthHeight,1,ballDetector.getCam1Width());
			cam2br_ScreenAxis = ImagetoScreen(cam2br_imageaxis,tl,br,depthWidth,depthHeight,1,ballDetector.getCam1Width());
		}

		Point mp = new Point();
		mp.x = op.pa.mouseX;
		mp.y = op.pa.mouseY;
		
		if (op.pa.mousePressed && selectedCam >= 0) {
			ballDetector.setCamImageCorner(selectedCam,
					ScreentoImage(mp,tl,br,depthWidth,depthHeight,selectedCam,ballDetector.getCam1Width())
					);
		}
		else {
			selectedCam = -1;

			int mSq = minimumDistanceSq;
			
			for (int i = 0; i < camCount; i++){
				
					Point screenPt = ImagetoScreen(ballDetector.getCamImageCorner(i),
							tl,br,depthWidth,depthHeight,i,ballDetector.getCam1Width());
					
					int distanceSq = getDistanceSq(
						mp.x, mp.y,
						screenPt.x,
						screenPt.y
						);
					
					if (distanceSq < mSq) {
						mSq = distanceSq;
						selectedCam = i;
					;
				}
			}
		}
		
		if (selectedCam >= 0) {
			op.pa.ellipse(op.pa.mouseX, op.pa.mouseY, 20, 20);
			ballDetector.rememberBackground();
		}
		
		// draw the image bounding box
		op.pa.stroke(255, 255, 0);
		op.pa.line(tl.x, tl.y, br.x, tl.y);
		op.pa.line(br.x, tl.y, br.x, br.y);
		op.pa.line(br.x, br.y, tl.x, br.y);
		op.pa.line(tl.x, br.y, tl.x, tl.y);
		
		//draw cam1 image bounding
		op.pa.stroke(255, 0, 0);
		op.pa.line(cam1tl_ScreenAxis.x, cam1tl_ScreenAxis.y, cam1br_ScreenAxis.x, cam1tl_ScreenAxis.y);
		op.pa.line(cam1br_ScreenAxis.x, cam1tl_ScreenAxis.y, cam1br_ScreenAxis.x, cam1br_ScreenAxis.y);
		op.pa.line(cam1br_ScreenAxis.x, cam1br_ScreenAxis.y, cam1tl_ScreenAxis.x, cam1br_ScreenAxis.y);
		op.pa.line(cam1tl_ScreenAxis.x, cam1br_ScreenAxis.y, cam1tl_ScreenAxis.x, cam1tl_ScreenAxis.y);
		
		// draw an arrow
		op.pa.line(cam1tl_ScreenAxis.x, cam1tl_ScreenAxis.y, cam1tl_ScreenAxis.x + 8, cam1tl_ScreenAxis.y + 4);
		op.pa.line(cam1tl_ScreenAxis.x, cam1tl_ScreenAxis.y, cam1tl_ScreenAxis.x + 4, cam1tl_ScreenAxis.y + 8);
		op.pa.line(cam1tl_ScreenAxis.x, cam1tl_ScreenAxis.y, cam1tl_ScreenAxis.x + 10, cam1tl_ScreenAxis.y + 10);
		
		// draw xy
		op.pa.text("X:", cam1tl_ScreenAxis.x + 20, cam1tl_ScreenAxis.y + 20);
		op.pa.text(cam1tl_ScreenAxis.x, cam1tl_ScreenAxis.x + 30, cam1tl_ScreenAxis.y + 20);
		op.pa.text("Y:", cam1tl_ScreenAxis.x + 20, cam1tl_ScreenAxis.y + 34);
		op.pa.text(cam1tl_ScreenAxis.y, cam1tl_ScreenAxis.x + 30, cam1tl_ScreenAxis.y + 34);
		
		if(camCount >= 2)
		{
			//draw cam2 image bounding
			op.pa.stroke(0, 255, 0);
			op.pa.line(cam2tl_ScreenAxis.x, cam2tl_ScreenAxis.y, cam2br_ScreenAxis.x, cam2tl_ScreenAxis.y);
			op.pa.line(cam2br_ScreenAxis.x, cam2tl_ScreenAxis.y, cam2br_ScreenAxis.x, cam2br_ScreenAxis.y);
			op.pa.line(cam2br_ScreenAxis.x, cam2br_ScreenAxis.y, cam2tl_ScreenAxis.x, cam2br_ScreenAxis.y);
			op.pa.line(cam2tl_ScreenAxis.x, cam2br_ScreenAxis.y, cam2tl_ScreenAxis.x, cam2tl_ScreenAxis.y);
			
			// draw an arrow
			op.pa.line(cam2tl_ScreenAxis.x, cam2tl_ScreenAxis.y, cam2tl_ScreenAxis.x + 8, cam2tl_ScreenAxis.y + 4);
			op.pa.line(cam2tl_ScreenAxis.x, cam2tl_ScreenAxis.y, cam2tl_ScreenAxis.x + 4, cam2tl_ScreenAxis.y + 8);
			op.pa.line(cam2tl_ScreenAxis.x, cam2tl_ScreenAxis.y, cam2tl_ScreenAxis.x + 10, cam2tl_ScreenAxis.y + 10);
			
			// draw xy
			op.pa.text("X:", cam2tl_ScreenAxis.x + 20, cam2tl_ScreenAxis.y + 20);
			op.pa.text(cam2tl_ScreenAxis.x, cam2tl_ScreenAxis.x + 30, cam2tl_ScreenAxis.y + 20);
			op.pa.text("Y:", cam2tl_ScreenAxis.x + 20, cam2tl_ScreenAxis.y + 34);
			op.pa.text(cam2tl_ScreenAxis.y, cam2tl_ScreenAxis.x + 30, cam2tl_ScreenAxis.y + 34);
		}
		op.pa.noStroke();
	}
	
	private int getDistanceSq(int x1, int y1, int x2, int y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}

	/**
	 * Convert image xy to screen xy.
	 * @param imagePt Screen xy point to convert
	 * @param screen_tl depthImage top-left corner xy in screen xy
	 * @param screen_br depthImage bottom-right corner xy in screen xy
	 * @param combinedDepthImageWidth depthimage width in image xy
	 * @param combinedDepthImageHeight depthImage height in image xy
	 * @param camId id number of camera starts with 0
	 * @param cam1Width width of cam1 in image xy
	 */
	private Point ImagetoScreen(Point imagePt, Point screen_tl, Point screen_br, int combinedDepthImageWidth,
			int combinedDepthImageHeight, int camId, int cam1Width) {

		Point retPt = new Point();
		if (camId == 0) {
			retPt.x = (int)(screen_tl.x + imagePt.x * (double)(screen_br.x - screen_tl.x) / (double)combinedDepthImageWidth);
			retPt.y = (int)(screen_tl.y + imagePt.y * (double)(screen_br.y - screen_tl.y) / (double)combinedDepthImageHeight);
		} else {
			retPt.x = (int)(screen_tl.x + (imagePt.x + cam1Width) * (double)(screen_br.x - screen_tl.x) / (double)combinedDepthImageWidth);
			retPt.y = (int)(screen_tl.y + imagePt.y * (double)(screen_br.y - screen_tl.y) / (double)combinedDepthImageHeight);
		}
 		return retPt;
	}

	/**
	 * Convert screen xy to image xy.
	 * 
	 * @param screenPt
	 *            Screen xy point to convert
	 * @param screen_tl
	 *            depthImage top-left corner xy in screen xy
	 * @param screen_br
	 *            depthImage bottom-right corner xy in screen xy
	 * @param combinedDepthImageWidth
	 *            depthimage width in image xy
	 * @param combinedDepthImageHeight
	 *            depthImage height in image xy
	 * @param camId
	 *            id number of camera starts with 0
	 * @param cam1Width
	 *            width of cam1 in image xy
	 */
	private Point ScreentoImage(Point screenPt, Point screen_tl, Point screen_br, int combinedDepthImageWidth,
			int combinedDepthImageHeight, int camId, int cam1Width) {
		Point retPt = new Point();
		if (camId == 0) {
			retPt.x = (int)((screenPt.x - screen_tl.x) * ((double)combinedDepthImageWidth / (double)(screen_br.x - screen_tl.x)));
			retPt.y = (int)((screenPt.y - screen_tl.y) * ((double)combinedDepthImageHeight /(double) (screen_br.y - screen_tl.y)));
		} else {
			retPt.x = (int)((screenPt.x - screen_tl.x) * ((double)combinedDepthImageWidth / (double)(screen_br.x - screen_tl.x))) - cam1Width;
			retPt.y = (int)((screenPt.y - screen_tl.y) * ((double)combinedDepthImageHeight /(double)(screen_br.y - screen_tl.y)));
		}
		return retPt;
	}
	
	@Override
	public void keyEvent(KeyEvent e) {
		if (e.getID() != KeyEvent.KEY_RELEASED) {
			return;
		}
		if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
			ballDetector.rememberBackground();
			op.setMessage("Recorded the base image for background substraction.");
		}		
	}
}
