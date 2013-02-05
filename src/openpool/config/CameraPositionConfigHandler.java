package openpool.config;

import java.awt.Point;

import openpool.BallDetector;
import openpool.OpenPool;
import processing.core.PImage;

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
		return "Specify the camera image area by dragging the top-left corners.";
	}

	@Override
	public void draw() {
		int depthWidth = ballDetector.getDepthWidth();
		int depthHeight = ballDetector.getDepthHeight();
		
		Point tl = op.getTopLeftCorner();
		Point br = op.getBottomRightCorner();

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

		cam1tl_ScreenAxis = ImagetoScreen(cam1tl_imageAxis,tl,br,depthWidth,depthHeight,1,ballDetector.getCam1Width());
		cam1br_ScreenAxis = ImagetoScreen(cam1br_imageAxis,tl,br,depthWidth,depthHeight,1,ballDetector.getCam1Width());
		
		ballDetector.rememberBackground();
		PImage diffImage = ballDetector.getDiffImage();
		op.pa.image(diffImage, tl.x, tl.y, br.x - tl.x, br.y - tl.y);

		if(camCount >= 2)
		{
			cam2tl_imageaxis.x = ballDetector.getCam1Width() + ballDetector.getX2();
			cam2tl_imageaxis.y = ballDetector.getY2();
			
			cam2br_imageaxis.x = cam2tl_imageaxis.x + ballDetector.getCam2Width();
			cam2br_imageaxis.y = cam2tl_imageaxis.y + ballDetector.getCam2Height();
			
			cam2tl_ScreenAxis = ImagetoScreen(cam2tl_imageaxis,tl,br,depthWidth,depthHeight,2,ballDetector.getCam1Width());
			cam2br_ScreenAxis = ImagetoScreen(cam2br_imageaxis,tl,br,depthWidth,depthHeight,2,ballDetector.getCam1Width());
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
		
		op.pa.fill(255, 255, 0);
		if (selectedCam >= 0) {
			op.pa.ellipse(op.pa.mouseX, op.pa.mouseY, 20, 20);
		}
		
		// draw the image bounding box
		op.pa.stroke(255, 255, 0);
		op.pa.line(tl.x, tl.y, br.x, tl.y);
		op.pa.line(br.x, tl.y, br.x, br.y);
		op.pa.line(br.x, br.y, tl.x, br.y);
		op.pa.line(tl.x, br.y, tl.x, tl.y);
		
		// draw an arrow
		op.pa.line(tl.x, tl.y, tl.x + 8, tl.y + 4);
		op.pa.line(tl.x, tl.y, tl.x + 4, tl.y + 8);
		op.pa.line(tl.x, tl.y, tl.x + 10, tl.y + 10);
		
		// draw xy
		op.pa.text("X:", tl.x + 20, tl.y + 20);
		op.pa.text(tl.x, tl.x + 30, tl.y + 20);
		op.pa.text("Y:", tl.x + 20, tl.y + 34);
		op.pa.text(tl.y, tl.x + 30, tl.y + 34);
		
		// draw an arrow
		op.pa.line(br.x, br.y, br.x - 8, br.y - 4);
		op.pa.line(br.x, br.y, br.x - 4, br.y - 8);
		op.pa.line(br.x, br.y, br.x - 10, br.y - 10);
		
		// draw xy
		op.pa.text("X:", br.x - 50, br.y - 24);
		op.pa.text(br.x, br.x - 40, br.y - 24);
		op.pa.text("Y:", br.x - 50, br.y - 10);
		op.pa.text(br.y, br.x - 40, br.y - 10);
		
		//draw cam1 image bounding
		op.pa.stroke(255, 0, 0);
		op.pa.line(cam1tl_ScreenAxis.x, cam1tl_ScreenAxis.y, cam1br_ScreenAxis.x, cam1tl_ScreenAxis.y);
		op.pa.line(cam1br_ScreenAxis.x, cam1tl_ScreenAxis.y, cam1br_ScreenAxis.x, cam1br_ScreenAxis.y);
		op.pa.line(cam1br_ScreenAxis.x, cam1br_ScreenAxis.y, cam1tl_ScreenAxis.x, cam1br_ScreenAxis.y);
		op.pa.line(cam1tl_ScreenAxis.x, cam1br_ScreenAxis.y, cam1tl_ScreenAxis.x, cam1tl_ScreenAxis.y);
		
		if(camCount >= 2)
		{
			//draw cam2 image bounding
			op.pa.stroke(0, 255, 0);
			op.pa.line(cam2tl_ScreenAxis.x, cam2tl_ScreenAxis.y, cam2br_ScreenAxis.x, cam2tl_ScreenAxis.y);
			op.pa.line(cam2br_ScreenAxis.x, cam2tl_ScreenAxis.y, cam2br_ScreenAxis.x, cam2br_ScreenAxis.y);
			op.pa.line(cam2br_ScreenAxis.x, cam2br_ScreenAxis.y, cam2tl_ScreenAxis.x, cam2br_ScreenAxis.y);
			op.pa.line(cam2tl_ScreenAxis.x, cam2br_ScreenAxis.y, cam2tl_ScreenAxis.x, cam2tl_ScreenAxis.y);
		}
		op.pa.noStroke();
	}
	
	private int getDistanceSq(int x1, int y1, int x2, int y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}

	/**
	 * Convert image xy to screen xy.
	 * @param pt Screen xy point to convert
	 * @param tl depthImage top-left corner xy in screen xy
	 * @param br depthImage bottom-right corner xy in screen xy
	 * @param depthWidth depthimage width in image xy
	 * @param depthHeight depthImage height in image xy
	 * @param camNumber id number of camera starts with 0
	 * @param cam1Width width of cam1 in image xy
	 */
	private Point ImagetoScreen(Point pt,Point tl,Point br,int depthWidth, int depthHeight,int camNumber,int cam1Width){
		
		Point retPt = new Point();
		
		if(camNumber >= 1){
			retPt.x += pt.x + cam1Width;
		}
		retPt.x = tl.x + pt.x*(br.x-tl.x)/depthWidth;
		retPt.y = tl.y + pt.y*(br.y-tl.y)/depthHeight;
		
		return retPt;
	}
	
	/**
	 * Convert screen xy to image xy.
	 * @param pt Screen xy point to convert
	 * @param tl depthImage top-left corner xy in screen xy
	 * @param br depthImage bottom-right corner xy in screen xy
	 * @param depthWidth depthimage width in image xy
	 * @param depthHeight depthImage height in image xy
	 * @param camNumber id number of camera starts with 0
	 * @param cam1Width width of cam1 in image xy
	 */
	private Point ScreentoImage(Point pt,Point tl,Point br,int depthWidth, int depthHeight,int camNumber,int cam1Width){
		Point retPt = new Point();
		retPt.x = (pt.x- tl.x)*(depthWidth  / (br.x-tl.x)) ;
		retPt.y = (pt.y - tl.y)*(depthHeight / (br.y-tl.y));
		
		if(camNumber >= 1){
			retPt.x -= retPt.x - cam1Width;
		}
		return retPt;
	}
}
