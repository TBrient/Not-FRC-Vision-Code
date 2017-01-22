package com.vision.tracking.code;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.vision.VisionThread;

public class VisionTest {
	
	private static final int IMG_WIDTH = 320;
	private static final int IMG_HEIGHT = 240;
	
	private static VisionThread visionThread;
	private static double centerX = 0.0;
	private RobotDrive drive;
	
	private final static Object imgLock = new Object();
	
	public static void main(String args[]) {
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
        camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
        
        visionThread = new VisionThread(camera, new GripPipeline(), pipeline -> {
            if (!pipeline.findContoursOutput().isEmpty()) {
            	Rect  largestRectangle = Imgproc.boundingRect(pipeline.findContoursOutput().get(0));
            	for (int i = 1; i < pipeline.findContoursOutput().size(); i++) {
            		Rect testRec = Imgproc.boundingRect(pipeline.findContoursOutput().get(i));
            		if (largestRectangle.area() < testRec.area()) {
            			largestRectangle = testRec;
            		}
            	}
                synchronized (imgLock) {
                    centerX = largestRectangle.x + (largestRectangle.width / 2);
                    System.out.println(centerX);
                }
            }
        });
        visionThread.start();
	}
}
