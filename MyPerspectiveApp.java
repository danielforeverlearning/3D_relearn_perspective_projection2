

// /usr/lib/jvm/jdk-18/bin/javac -cp MyPerspectiveApp.java SortByZ.java Point.java Vector.java
// /usr/lib/jvm/jdk-18/bin/java  -cp MyPerspectiveApp

import java.util.ArrayList;
import java.util.Collections;
import java.lang.Math;
import java.awt.*;
import java.awt.Dimension;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;




public class MyPerspectiveApp extends JFrame implements KeyListener {

   final static Color bg = Color.white;
   final static Color fg = Color.black;
   final static Color side1_color = Color.red;
   final static Color side2_color = Color.pink;
   final static Color side3_color = Color.yellow;
   final static Color side4_color = Color.blue;
   final static Color side5_color = Color.green;
   final static Color side6_color = Color.black;

   //java-swing user-coords is X is positive to right, Y is positive down, 0,0 is top-left, center of window is 300,300
   
   //OpenGL camera-space, origin is (0,0,0), X is positive to right, Y is positive up, focal-length == e == 1 == (1/tan(alpha/2)), alpha==90degrees, camera/eye-ball at (0,0,-1)
   //clockwise , surface visible
   //counter-clockwise, surface hidden (not-visible)
   
   final static double f = 4; //far plane  is z=-4, we know -f <= Pz <= -n, all points inside view-frustrum in camera-space
   final static double n = 1; //near plane is z=-1, we know -f <= Pz <= -n, all points inside view-frustrum in camera-space
   final static double l = -1; //left plane is x=-1
   final static double r = 1;  //right plane is x=1
   final static double t = 1;  //top plane is y=1
   final static double b = -1; //bottom plane is y=-1   

   Point Center_Of_Cube_Point = new Point(0,0,-2.5);
   
   boolean do_X_rotate = false;
   boolean do_Y_rotate = false;
   boolean do_Z_rotate = false;

   //front start
   Point side1_vertex1 = new Point(-0.5,  0.5, -2);
   Point side1_vertex2 = new Point(-0.5, -0.5, -2);
   Point side1_vertex3 = new Point( 0.5, -0.5, -2);
   Point side1_vertex4 = new Point( 0.5,  0.5, -2);
   Vector side1_normal_vector = side1_vertex1.CalculateCrossProduct(side1_vertex2, side1_vertex3);

   //top start 
   Point side2_vertex1 = new Point(-0.5, 0.5, -2);
   Point side2_vertex2 = new Point( 0.5, 0.5, -2);
   Point side2_vertex3 = new Point( 0.5, 0.5, -3);
   Point side2_vertex4 = new Point(-0.5, 0.5, -3);
   Vector side2_normal_vector = side2_vertex1.CalculateCrossProduct(side2_vertex2, side2_vertex3);
   
   //bottom start
   Point side3_vertex1 = new Point(-0.5, -0.5, -2);
   Point side3_vertex2 = new Point(-0.5, -0.5, -3);
   Point side3_vertex3 = new Point( 0.5, -0.5, -3);
   Point side3_vertex4 = new Point( 0.5, -0.5, -2);
   Vector side3_normal_vector = side3_vertex1.CalculateCrossProduct(side3_vertex2, side3_vertex3);

   //right start
   Point side4_vertex1 = new Point(0.5,  -0.5, -2);
   Point side4_vertex2 = new Point(0.5,  -0.5, -3);
   Point side4_vertex3 = new Point(0.5,   0.5, -3);
   Point side4_vertex4 = new Point(0.5,   0.5, -2);
   Vector side4_normal_vector = side4_vertex1.CalculateCrossProduct(side4_vertex2, side4_vertex3);

   //left start
   Point side5_vertex1 = new Point(-0.5, -0.5, -2);
   Point side5_vertex2 = new Point(-0.5,  0.5, -2);
   Point side5_vertex3 = new Point(-0.5,  0.5, -3);
   Point side5_vertex4 = new Point(-0.5, -0.5, -3);
   Vector side5_normal_vector = side5_vertex1.CalculateCrossProduct(side5_vertex2, side5_vertex3);

   //back start
   Point side6_vertex1 = new Point(-0.5,  0.5, -3);
   Point side6_vertex2 = new Point( 0.5,  0.5, -3);
   Point side6_vertex3 = new Point( 0.5, -0.5, -3);
   Point side6_vertex4 = new Point(-0.5, -0.5, -3);
   Vector side6_normal_vector = side6_vertex1.CalculateCrossProduct(side6_vertex2, side6_vertex3);
   
   double CurrentScale = 1;
   
   //front start
   Point side1_vertex1_start = new Point(-0.5,  0.5, -2);
   Point side1_vertex2_start = new Point(-0.5, -0.5, -2);
   Point side1_vertex3_start = new Point( 0.5, -0.5, -2);
   Point side1_vertex4_start = new Point( 0.5,  0.5, -2);
   //top start 
   Point side2_vertex1_start = new Point(-0.5, 0.5, -2);
   Point side2_vertex2_start = new Point( 0.5, 0.5, -2);
   Point side2_vertex3_start = new Point( 0.5, 0.5, -3);
   Point side2_vertex4_start = new Point(-0.5, 0.5, -3);
   //bottom start
   Point side3_vertex1_start = new Point(-0.5, -0.5, -2);
   Point side3_vertex2_start = new Point(-0.5, -0.5, -3);
   Point side3_vertex3_start = new Point( 0.5, -0.5, -3);
   Point side3_vertex4_start = new Point( 0.5, -0.5, -2);
   //right start
   Point side4_vertex1_start = new Point(0.5,  -0.5, -2);
   Point side4_vertex2_start = new Point(0.5,  -0.5, -3);
   Point side4_vertex3_start = new Point(0.5,   0.5, -3);
   Point side4_vertex4_start = new Point(0.5,   0.5, -2);
   //left start
   Point side5_vertex1_start = new Point(-0.5, -0.5, -2);
   Point side5_vertex2_start = new Point(-0.5,  0.5, -2);
   Point side5_vertex3_start = new Point(-0.5,  0.5, -3);
   Point side5_vertex4_start = new Point(-0.5, -0.5, -3);
   //back start
   Point side6_vertex1_start = new Point(-0.5,  0.5, -3);
   Point side6_vertex2_start = new Point( 0.5,  0.5, -3);
   Point side6_vertex3_start = new Point( 0.5, -0.5, -3);
   Point side6_vertex4_start = new Point(-0.5, -0.5, -3);


   public MyPerspectiveApp(String name) {
        super(name);
   }


   private int  Convert_To_UserCoord_X(double dd) {
	    //for now origin of our java-swing-"viewing plane" is 300,300 in java-swing-user-coords
		//for java-swing-user-coords and camera-space X is positive to the right
		
		//after transformation from view-frustrum homogenous-clip-space which is a cube each edge [-1,+1]
		
	    int answer = (int) ((dd * 100) + 300);
        return answer;		
   }
   
   private int  Convert_To_UserCoord_Y(double dd) {
	    //for now origin of our java-swing-"viewing plane" is 300,300 in java-swing-user-coords
		//but camera-space Y is positive going up
		//but java-swing-user-coords Y is positive going down
		
		//after transformation from view-frustrum homogenous-clip-space which is a cube each edge [-1,+1]
		
		int answer = (int) (300 - (dd * 100));
		return answer;
   }

   
   private void DrawObject(Graphics2D g2) {
			
			if (do_X_rotate) {
				do_X_rotate = false;
				PerspectiveProjection_RotateCube_X_Axis(15);
			}
			if (do_Y_rotate) {
				do_Y_rotate = false;
				PerspectiveProjection_RotateCube_Y_Axis(15);
			}
			if (do_Z_rotate) {
				do_Z_rotate = false;
				PerspectiveProjection_RotateCube_Z_Axis(15);
			}
			
			//So if we rotated the cube in camera-space
			//If we just want to look at perspective-projection of cube as if it was rotating around its own center
			//translate vertexes to camera-space-origin then
			//translate vertexes to (0,0,-2.5) then
			//transform view-frustrum to clipspace
			
			//Using a normal_threshold like below does not work too good, people may think this is dumb
			//but i think if you sort the normal vectors by z-coordinate, then draw lowest to highest
			//double num = 2;
			//int exp    = -14;
			//double fix_artifact = Math.pow(num, exp);
		
			SortByZ mysort = new SortByZ();
			mysort.add(1, side1_normal_vector.z);
			mysort.add(2, side2_normal_vector.z);
			mysort.add(3, side3_normal_vector.z);
			mysort.add(4, side4_normal_vector.z);
			mysort.add(5, side5_normal_vector.z);
			mysort.add(6, side6_normal_vector.z);
			mysort.DebugPrint();
			
			int count = mysort.GetCount();
			int[] draw_side = mysort.GetDrawSides();
			
			for (int ii=0; ii < count; ii++) {
				
				if (draw_side[ii] == 1) {
				
					Point translated_side1_vertex1 = new Point(side1_vertex1.x - Center_Of_Cube_Point.x, side1_vertex1.y - Center_Of_Cube_Point.y, side1_vertex1.z - Center_Of_Cube_Point.z);
					Point translated_side1_vertex2 = new Point(side1_vertex2.x - Center_Of_Cube_Point.x, side1_vertex2.y - Center_Of_Cube_Point.y, side1_vertex2.z - Center_Of_Cube_Point.z);
					Point translated_side1_vertex3 = new Point(side1_vertex3.x - Center_Of_Cube_Point.x, side1_vertex3.y - Center_Of_Cube_Point.y, side1_vertex3.z - Center_Of_Cube_Point.z);
					Point translated_side1_vertex4 = new Point(side1_vertex4.x - Center_Of_Cube_Point.x, side1_vertex4.y - Center_Of_Cube_Point.y, side1_vertex4.z - Center_Of_Cube_Point.z);
					translated_side1_vertex1.z -= 2.5;
					translated_side1_vertex2.z -= 2.5;
					translated_side1_vertex3.z -= 2.5;
					translated_side1_vertex4.z -= 2.5;
					
					translated_side1_vertex1.DebugPrint("Translated_side1_vertex1 = ");
					translated_side1_vertex2.DebugPrint("Translated_side1_vertex2 = ");
					translated_side1_vertex3.DebugPrint("Translated_side1_vertex3 = ");
					translated_side1_vertex4.DebugPrint("Translated_side1_vertex4 = ");
					
					Point clipspace_side1_vertex1 = translated_side1_vertex1.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side1_vertex2 = translated_side1_vertex2.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side1_vertex3 = translated_side1_vertex3.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side1_vertex4 = translated_side1_vertex4.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					
					int [] side1_x = { Convert_To_UserCoord_X(clipspace_side1_vertex1.x), Convert_To_UserCoord_X(clipspace_side1_vertex2.x), Convert_To_UserCoord_X(clipspace_side1_vertex3.x), Convert_To_UserCoord_X(clipspace_side1_vertex4.x) };
					int [] side1_y = { Convert_To_UserCoord_Y(clipspace_side1_vertex1.y), Convert_To_UserCoord_Y(clipspace_side1_vertex2.y), Convert_To_UserCoord_Y(clipspace_side1_vertex3.y), Convert_To_UserCoord_Y(clipspace_side1_vertex4.y) };

					g2.setPaint(fg);
					g2.drawPolygon(side1_x, side1_y, 4);
					g2.setColor(side1_color);
					g2.fillPolygon(side1_x, side1_y, 4);
					
					//clipspace_side1_vertex1.DebugPrint("clipspace_side1_vertex1 = ");
					//clipspace_side1_vertex2.DebugPrint("clipspace_side1_vertex2 = ");
					//clipspace_side1_vertex3.DebugPrint("clipspace_side1_vertex3 = ");
					//clipspace_side1_vertex4.DebugPrint("clipspace_side1_vertex4 = ");
				}
		
				if (draw_side[ii] == 2) {
					
					Point translated_side2_vertex1 = new Point(side2_vertex1.x - Center_Of_Cube_Point.x, side2_vertex1.y - Center_Of_Cube_Point.y, side2_vertex1.z - Center_Of_Cube_Point.z);
					Point translated_side2_vertex2 = new Point(side2_vertex2.x - Center_Of_Cube_Point.x, side2_vertex2.y - Center_Of_Cube_Point.y, side2_vertex2.z - Center_Of_Cube_Point.z);
					Point translated_side2_vertex3 = new Point(side2_vertex3.x - Center_Of_Cube_Point.x, side2_vertex3.y - Center_Of_Cube_Point.y, side2_vertex3.z - Center_Of_Cube_Point.z);
					Point translated_side2_vertex4 = new Point(side2_vertex4.x - Center_Of_Cube_Point.x, side2_vertex4.y - Center_Of_Cube_Point.y, side2_vertex4.z - Center_Of_Cube_Point.z);
					translated_side2_vertex1.z -= 2.5;
					translated_side2_vertex2.z -= 2.5;
					translated_side2_vertex3.z -= 2.5;
					translated_side2_vertex4.z -= 2.5;
					
					Point clipspace_side2_vertex1 = translated_side2_vertex1.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side2_vertex2 = translated_side2_vertex2.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side2_vertex3 = translated_side2_vertex3.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side2_vertex4 = translated_side2_vertex4.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					
					int [] side2_x = { Convert_To_UserCoord_X(clipspace_side2_vertex1.x), Convert_To_UserCoord_X(clipspace_side2_vertex2.x), Convert_To_UserCoord_X(clipspace_side2_vertex3.x), Convert_To_UserCoord_X(clipspace_side2_vertex4.x) };
					int [] side2_y = { Convert_To_UserCoord_Y(clipspace_side2_vertex1.y), Convert_To_UserCoord_Y(clipspace_side2_vertex2.y), Convert_To_UserCoord_Y(clipspace_side2_vertex3.y), Convert_To_UserCoord_Y(clipspace_side2_vertex4.y) };
					
					g2.setPaint(fg);
					g2.drawPolygon(side2_x, side2_y, 4);
					g2.setColor(side2_color);
					g2.fillPolygon(side2_x, side2_y, 4);
					
					//side2 supposed to be top pink
					//clipspace_side2_vertex1.DebugPrint("clipspace_side2_vertex1 top pink = ");
					//clipspace_side2_vertex2.DebugPrint("clipspace_side2_vertex2 top pink = ");
					//clipspace_side2_vertex3.DebugPrint("clipspace_side2_vertex3 top pink = ");
					//clipspace_side2_vertex4.DebugPrint("clipspace_side2_vertex4 top pink = ");
					
					//side2_vertex1.DebugPrint("side2_vertex1 top pink = ");
					//side2_vertex2.DebugPrint("side2_vertex2 top pink = ");
					//side2_vertex3.DebugPrint("side2_vertex3 top pink = ");
					//side2_vertex4.DebugPrint("side2_vertex4 top pink = ");
				}
			
				if (draw_side[ii] == 3) {
					
					Point translated_side3_vertex1 = new Point(side3_vertex1.x - Center_Of_Cube_Point.x, side3_vertex1.y - Center_Of_Cube_Point.y, side3_vertex1.z - Center_Of_Cube_Point.z);
					Point translated_side3_vertex2 = new Point(side3_vertex2.x - Center_Of_Cube_Point.x, side3_vertex2.y - Center_Of_Cube_Point.y, side3_vertex2.z - Center_Of_Cube_Point.z);
					Point translated_side3_vertex3 = new Point(side3_vertex3.x - Center_Of_Cube_Point.x, side3_vertex3.y - Center_Of_Cube_Point.y, side3_vertex3.z - Center_Of_Cube_Point.z);
					Point translated_side3_vertex4 = new Point(side3_vertex4.x - Center_Of_Cube_Point.x, side3_vertex4.y - Center_Of_Cube_Point.y, side3_vertex4.z - Center_Of_Cube_Point.z);
					translated_side3_vertex1.z -= 2.5;
					translated_side3_vertex2.z -= 2.5;
					translated_side3_vertex3.z -= 2.5;
					translated_side3_vertex4.z -= 2.5;
					
					Point clipspace_side3_vertex1 = translated_side3_vertex1.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side3_vertex2 = translated_side3_vertex2.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side3_vertex3 = translated_side3_vertex3.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side3_vertex4 = translated_side3_vertex4.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					
					int [] side3_x = { Convert_To_UserCoord_X(clipspace_side3_vertex1.x), Convert_To_UserCoord_X(clipspace_side3_vertex2.x), Convert_To_UserCoord_X(clipspace_side3_vertex3.x), Convert_To_UserCoord_X(clipspace_side3_vertex4.x) };
					int [] side3_y = { Convert_To_UserCoord_Y(clipspace_side3_vertex1.y), Convert_To_UserCoord_Y(clipspace_side3_vertex2.y), Convert_To_UserCoord_Y(clipspace_side3_vertex3.y), Convert_To_UserCoord_Y(clipspace_side3_vertex4.y) };

					g2.setPaint(fg);
					g2.drawPolygon(side3_x, side3_y, 4);
					g2.setColor(side3_color);
					g2.fillPolygon(side3_x, side3_y, 4);
					
					//side3 supposed to be bottom yellow
					//clipspace_side3_vertex1.DebugPrint("clipspace_side3_vertex1 bottom yellow = ");
					//clipspace_side3_vertex2.DebugPrint("clipspace_side3_vertex2 bottom yellow = ");
					//clipspace_side3_vertex3.DebugPrint("clipspace_side3_vertex3 bottom yellow = ");
					//clipspace_side3_vertex4.DebugPrint("clipspace_side3_vertex4 bottom yellow = ");
					
					//side3_vertex1.DebugPrint("side3_vertex1 bottom yellow = ");
					//side3_vertex2.DebugPrint("side3_vertex2 bottom yellow = ");
					//side3_vertex3.DebugPrint("side3_vertex3 bottom yellow = ");
					//side3_vertex4.DebugPrint("side3_vertex4 bottom yellow = ");				
				}
			
				if (draw_side[ii] == 4) {
					
					Point translated_side4_vertex1 = new Point(side4_vertex1.x - Center_Of_Cube_Point.x, side4_vertex1.y - Center_Of_Cube_Point.y, side4_vertex1.z - Center_Of_Cube_Point.z);
					Point translated_side4_vertex2 = new Point(side4_vertex2.x - Center_Of_Cube_Point.x, side4_vertex2.y - Center_Of_Cube_Point.y, side4_vertex2.z - Center_Of_Cube_Point.z);
					Point translated_side4_vertex3 = new Point(side4_vertex3.x - Center_Of_Cube_Point.x, side4_vertex3.y - Center_Of_Cube_Point.y, side4_vertex3.z - Center_Of_Cube_Point.z);
					Point translated_side4_vertex4 = new Point(side4_vertex4.x - Center_Of_Cube_Point.x, side4_vertex4.y - Center_Of_Cube_Point.y, side4_vertex4.z - Center_Of_Cube_Point.z);
					translated_side4_vertex1.z -= 2.5;
					translated_side4_vertex2.z -= 2.5;
					translated_side4_vertex3.z -= 2.5;
					translated_side4_vertex4.z -= 2.5;
					
					Point clipspace_side4_vertex1 = translated_side4_vertex1.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side4_vertex2 = translated_side4_vertex2.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side4_vertex3 = translated_side4_vertex3.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side4_vertex4 = translated_side4_vertex4.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					
					int [] side4_x = { Convert_To_UserCoord_X(clipspace_side4_vertex1.x), Convert_To_UserCoord_X(clipspace_side4_vertex2.x), Convert_To_UserCoord_X(clipspace_side4_vertex3.x), Convert_To_UserCoord_X(clipspace_side4_vertex4.x) };
					int [] side4_y = { Convert_To_UserCoord_Y(clipspace_side4_vertex1.y), Convert_To_UserCoord_Y(clipspace_side4_vertex2.y), Convert_To_UserCoord_Y(clipspace_side4_vertex3.y), Convert_To_UserCoord_Y(clipspace_side4_vertex4.y) };
					
					g2.setPaint(fg);
					g2.drawPolygon(side4_x, side4_y, 4);
					g2.setColor(side4_color);
					g2.fillPolygon(side4_x, side4_y, 4);
					
					//side4 supposed to be right blue
					//clipspace_side4_vertex1.DebugPrint("clipspace_side4_vertex1 right blue = ");
					//clipspace_side4_vertex2.DebugPrint("clipspace_side4_vertex2 right blue = ");
					//clipspace_side4_vertex3.DebugPrint("clipspace_side4_vertex3 right blue = ");
					//clipspace_side4_vertex4.DebugPrint("clipspace_side4_vertex4 right blue = ");

					//side4_vertex1.DebugPrint("side4_vertex1 right blue = ");
					//side4_vertex2.DebugPrint("side4_vertex2 right blue = ");
					//side4_vertex3.DebugPrint("side4_vertex3 right blue = ");
					//side4_vertex4.DebugPrint("side4_vertex4 right blue = ");	
				}
			
				if (draw_side[ii] == 5) {
					
					Point translated_side5_vertex1 = new Point(side5_vertex1.x - Center_Of_Cube_Point.x, side5_vertex1.y - Center_Of_Cube_Point.y, side5_vertex1.z - Center_Of_Cube_Point.z);
					Point translated_side5_vertex2 = new Point(side5_vertex2.x - Center_Of_Cube_Point.x, side5_vertex2.y - Center_Of_Cube_Point.y, side5_vertex2.z - Center_Of_Cube_Point.z);
					Point translated_side5_vertex3 = new Point(side5_vertex3.x - Center_Of_Cube_Point.x, side5_vertex3.y - Center_Of_Cube_Point.y, side5_vertex3.z - Center_Of_Cube_Point.z);
					Point translated_side5_vertex4 = new Point(side5_vertex4.x - Center_Of_Cube_Point.x, side5_vertex4.y - Center_Of_Cube_Point.y, side5_vertex4.z - Center_Of_Cube_Point.z);
					translated_side5_vertex1.z -= 2.5;
					translated_side5_vertex2.z -= 2.5;
					translated_side5_vertex3.z -= 2.5;
					translated_side5_vertex4.z -= 2.5;
					
					Point clipspace_side5_vertex1 = translated_side5_vertex1.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side5_vertex2 = translated_side5_vertex2.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side5_vertex3 = translated_side5_vertex3.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side5_vertex4 = translated_side5_vertex4.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					
					int [] side5_x = { Convert_To_UserCoord_X(clipspace_side5_vertex1.x), Convert_To_UserCoord_X(clipspace_side5_vertex2.x), Convert_To_UserCoord_X(clipspace_side5_vertex3.x), Convert_To_UserCoord_X(clipspace_side5_vertex4.x) };
					int [] side5_y = { Convert_To_UserCoord_Y(clipspace_side5_vertex1.y), Convert_To_UserCoord_Y(clipspace_side5_vertex2.y), Convert_To_UserCoord_Y(clipspace_side5_vertex3.y), Convert_To_UserCoord_Y(clipspace_side5_vertex4.y) };

					g2.setPaint(fg);
					g2.drawPolygon(side5_x, side5_y, 4);
					g2.setColor(side5_color);
					g2.fillPolygon(side5_x, side5_y, 4);
					
					//side5 supposed to be left green
					//clipspace_side5_vertex1.DebugPrint("clipspace_side5_vertex1 left green = ");
					//clipspace_side5_vertex2.DebugPrint("clipspace_side5_vertex2 left green = ");
					//clipspace_side5_vertex3.DebugPrint("clipspace_side5_vertex3 left green = ");
					//clipspace_side5_vertex4.DebugPrint("clipspace_side5_vertex4 left green = ");
					
					//side5_vertex1.DebugPrint("side5_vertex1 left green = ");
					//side5_vertex2.DebugPrint("side5_vertex2 left green = ");
					//side5_vertex3.DebugPrint("side5_vertex3 left green = ");
					//side5_vertex4.DebugPrint("side5_vertex4 left green = ");
				}
			
				if (draw_side[ii] == 6) {

					Point translated_side6_vertex1 = new Point(side6_vertex1.x - Center_Of_Cube_Point.x, side6_vertex1.y - Center_Of_Cube_Point.y, side6_vertex1.z - Center_Of_Cube_Point.z);
					Point translated_side6_vertex2 = new Point(side6_vertex2.x - Center_Of_Cube_Point.x, side6_vertex2.y - Center_Of_Cube_Point.y, side6_vertex2.z - Center_Of_Cube_Point.z);
					Point translated_side6_vertex3 = new Point(side6_vertex3.x - Center_Of_Cube_Point.x, side6_vertex3.y - Center_Of_Cube_Point.y, side6_vertex3.z - Center_Of_Cube_Point.z);
					Point translated_side6_vertex4 = new Point(side6_vertex4.x - Center_Of_Cube_Point.x, side6_vertex4.y - Center_Of_Cube_Point.y, side6_vertex4.z - Center_Of_Cube_Point.z);
					translated_side6_vertex1.z -= 2.5;
					translated_side6_vertex2.z -= 2.5;
					translated_side6_vertex3.z -= 2.5;
					translated_side6_vertex4.z -= 2.5;
					
					Point clipspace_side6_vertex1 = translated_side6_vertex1.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side6_vertex2 = translated_side6_vertex2.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side6_vertex3 = translated_side6_vertex3.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					Point clipspace_side6_vertex4 = translated_side6_vertex4.TransformViewFrustrumToClipSpace(f,n,l,r,t,b);
					
					int [] side6_x = { Convert_To_UserCoord_X(clipspace_side6_vertex1.x), Convert_To_UserCoord_X(clipspace_side6_vertex2.x), Convert_To_UserCoord_X(clipspace_side6_vertex3.x), Convert_To_UserCoord_X(clipspace_side6_vertex4.x) };
					int [] side6_y = { Convert_To_UserCoord_Y(clipspace_side6_vertex1.y), Convert_To_UserCoord_Y(clipspace_side6_vertex2.y), Convert_To_UserCoord_Y(clipspace_side6_vertex3.y), Convert_To_UserCoord_Y(clipspace_side6_vertex4.y) };
					
					g2.setPaint(fg);
					g2.drawPolygon(side6_x, side6_y, 4);
					g2.setColor(side6_color);
					g2.fillPolygon(side6_x, side6_y, 4);
					
					//clipspace_side6_vertex1.DebugPrint("clipspace_side6_vertex1 = ");
					//clipspace_side6_vertex2.DebugPrint("clipspace_side6_vertex2 = ");
					//clipspace_side6_vertex3.DebugPrint("clipspace_side6_vertex3 = ");
					//clipspace_side6_vertex4.DebugPrint("clipspace_side6_vertex4 = ");
				}
			}//for
   }//DrawObject

   public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
		Dimension d = getSize();
		g2.clearRect(0,0,d.width,d.height);
		DrawObject(g2);
    }

    public static void main(String s[]) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        MyPerspectiveApp frame = new MyPerspectiveApp("PerspectiveProjection Test - x,y,z for rotate and up,down arrow for scaling");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        frame.pack();
        frame.setBackground(bg);
        frame.setForeground(fg);
        frame.setSize(new Dimension(600,600));
        frame.addKeyListener(frame);
        frame.setVisible(true);
    }



    public void keyTyped(KeyEvent e) {
        displayInfo(e, "KEY TYPED: ");
    }
     
    public void keyPressed(KeyEvent e) {
        displayInfo(e, "KEY PRESSED: ");
    }
     
    public void keyReleased(KeyEvent e) {
        displayInfo(e, "KEY RELEASED: ");

        HandleKeyPress(e);
    }
	
	private void PerspectiveProjection_RotateCube_X_Axis(double angle_in_degrees) {
		//X rotate
		double angle_in_radians = Math.toRadians(angle_in_degrees);
		Center_Of_Cube_Point.Rotate_Around_X_Axis(angle_in_radians);

		side1_vertex1.Rotate_Around_X_Axis(angle_in_radians);
		side1_vertex2.Rotate_Around_X_Axis(angle_in_radians);
		side1_vertex3.Rotate_Around_X_Axis(angle_in_radians);
		side1_vertex4.Rotate_Around_X_Axis(angle_in_radians);
		side1_normal_vector = side1_vertex1.CalculateCrossProduct(side1_vertex2, side1_vertex3);

		side2_vertex1.Rotate_Around_X_Axis(angle_in_radians);
		side2_vertex2.Rotate_Around_X_Axis(angle_in_radians);
		side2_vertex3.Rotate_Around_X_Axis(angle_in_radians);
		side2_vertex4.Rotate_Around_X_Axis(angle_in_radians);
		side2_normal_vector = side2_vertex1.CalculateCrossProduct(side2_vertex2, side2_vertex3);

		side3_vertex1.Rotate_Around_X_Axis(angle_in_radians);
		side3_vertex2.Rotate_Around_X_Axis(angle_in_radians);
		side3_vertex3.Rotate_Around_X_Axis(angle_in_radians);
		side3_vertex4.Rotate_Around_X_Axis(angle_in_radians);
		side3_normal_vector = side3_vertex1.CalculateCrossProduct(side3_vertex2, side3_vertex3);

		side4_vertex1.Rotate_Around_X_Axis(angle_in_radians);
		side4_vertex2.Rotate_Around_X_Axis(angle_in_radians);
		side4_vertex3.Rotate_Around_X_Axis(angle_in_radians);
		side4_vertex4.Rotate_Around_X_Axis(angle_in_radians);
		side4_normal_vector = side4_vertex1.CalculateCrossProduct(side4_vertex2, side4_vertex3);

		side5_vertex1.Rotate_Around_X_Axis(angle_in_radians);
		side5_vertex2.Rotate_Around_X_Axis(angle_in_radians);
		side5_vertex3.Rotate_Around_X_Axis(angle_in_radians);
		side5_vertex4.Rotate_Around_X_Axis(angle_in_radians);
		side5_normal_vector = side5_vertex1.CalculateCrossProduct(side5_vertex2, side5_vertex3);

		side6_vertex1.Rotate_Around_X_Axis(angle_in_radians);
		side6_vertex2.Rotate_Around_X_Axis(angle_in_radians);
		side6_vertex3.Rotate_Around_X_Axis(angle_in_radians);
		side6_vertex4.Rotate_Around_X_Axis(angle_in_radians);
		side6_normal_vector = side6_vertex1.CalculateCrossProduct(side6_vertex2, side6_vertex3);
	}//PerspectiveProjection_RotateCube_X_Axis
	
	
	private void PerspectiveProjection_RotateCube_Y_Axis(double angle_in_degrees) {
		//Y rotate
		double angle_in_radians = Math.toRadians(angle_in_degrees);
		Center_Of_Cube_Point.Rotate_Around_Y_Axis(angle_in_radians);

		side1_vertex1.Rotate_Around_Y_Axis(angle_in_radians);
		side1_vertex2.Rotate_Around_Y_Axis(angle_in_radians);
		side1_vertex3.Rotate_Around_Y_Axis(angle_in_radians);
		side1_vertex4.Rotate_Around_Y_Axis(angle_in_radians);
		side1_normal_vector = side1_vertex1.CalculateCrossProduct(side1_vertex2, side1_vertex3);      

		side2_vertex1.Rotate_Around_Y_Axis(angle_in_radians);
		side2_vertex2.Rotate_Around_Y_Axis(angle_in_radians);
		side2_vertex3.Rotate_Around_Y_Axis(angle_in_radians);
		side2_vertex4.Rotate_Around_Y_Axis(angle_in_radians);
		side2_normal_vector = side2_vertex1.CalculateCrossProduct(side2_vertex2, side2_vertex3);      

		side3_vertex1.Rotate_Around_Y_Axis(angle_in_radians);
		side3_vertex2.Rotate_Around_Y_Axis(angle_in_radians);
		side3_vertex3.Rotate_Around_Y_Axis(angle_in_radians);
		side3_vertex4.Rotate_Around_Y_Axis(angle_in_radians);
		side3_normal_vector = side3_vertex1.CalculateCrossProduct(side3_vertex2, side3_vertex3);      

		side4_vertex1.Rotate_Around_Y_Axis(angle_in_radians);
		side4_vertex2.Rotate_Around_Y_Axis(angle_in_radians);
		side4_vertex3.Rotate_Around_Y_Axis(angle_in_radians);
		side4_vertex4.Rotate_Around_Y_Axis(angle_in_radians);
		side4_normal_vector = side4_vertex1.CalculateCrossProduct(side4_vertex2, side4_vertex3);      

		side5_vertex1.Rotate_Around_Y_Axis(angle_in_radians);
		side5_vertex2.Rotate_Around_Y_Axis(angle_in_radians);
		side5_vertex3.Rotate_Around_Y_Axis(angle_in_radians);
		side5_vertex4.Rotate_Around_Y_Axis(angle_in_radians);
		side5_normal_vector = side5_vertex1.CalculateCrossProduct(side5_vertex2, side5_vertex3);      

		side6_vertex1.Rotate_Around_Y_Axis(angle_in_radians);
		side6_vertex2.Rotate_Around_Y_Axis(angle_in_radians);
		side6_vertex3.Rotate_Around_Y_Axis(angle_in_radians);
		side6_vertex4.Rotate_Around_Y_Axis(angle_in_radians);
		side6_normal_vector = side6_vertex1.CalculateCrossProduct(side6_vertex2, side6_vertex3); 
	}//PerspectiveProjection_RotateCube_Y_Axis
	
	
	private void PerspectiveProjection_RotateCube_Z_Axis(double angle_in_degrees) {
		//Z rotate
		double angle_in_radians = Math.toRadians(angle_in_degrees);
		Center_Of_Cube_Point.Rotate_Around_Z_Axis(angle_in_radians);

		side1_vertex1.Rotate_Around_Z_Axis(angle_in_radians);
		side1_vertex2.Rotate_Around_Z_Axis(angle_in_radians);
		side1_vertex3.Rotate_Around_Z_Axis(angle_in_radians);
		side1_vertex4.Rotate_Around_Z_Axis(angle_in_radians);
		side1_normal_vector = side1_vertex1.CalculateCrossProduct(side1_vertex2, side1_vertex3);      

		side2_vertex1.Rotate_Around_Z_Axis(angle_in_radians);
		side2_vertex2.Rotate_Around_Z_Axis(angle_in_radians);
		side2_vertex3.Rotate_Around_Z_Axis(angle_in_radians);
		side2_vertex4.Rotate_Around_Z_Axis(angle_in_radians);
		side2_normal_vector = side2_vertex1.CalculateCrossProduct(side2_vertex2, side2_vertex3);      

		side3_vertex1.Rotate_Around_Z_Axis(angle_in_radians);
		side3_vertex2.Rotate_Around_Z_Axis(angle_in_radians);
		side3_vertex3.Rotate_Around_Z_Axis(angle_in_radians);
		side3_vertex4.Rotate_Around_Z_Axis(angle_in_radians);
		side3_normal_vector = side3_vertex1.CalculateCrossProduct(side3_vertex2, side3_vertex3);      

		side4_vertex1.Rotate_Around_Z_Axis(angle_in_radians);
		side4_vertex2.Rotate_Around_Z_Axis(angle_in_radians);
		side4_vertex3.Rotate_Around_Z_Axis(angle_in_radians);
		side4_vertex4.Rotate_Around_Z_Axis(angle_in_radians);
		side4_normal_vector = side4_vertex1.CalculateCrossProduct(side4_vertex2, side4_vertex3);      

		side5_vertex1.Rotate_Around_Z_Axis(angle_in_radians);
		side5_vertex2.Rotate_Around_Z_Axis(angle_in_radians);
		side5_vertex3.Rotate_Around_Z_Axis(angle_in_radians);
		side5_vertex4.Rotate_Around_Z_Axis(angle_in_radians);
		side5_normal_vector = side5_vertex1.CalculateCrossProduct(side5_vertex2, side5_vertex3);      

		side6_vertex1.Rotate_Around_Z_Axis(angle_in_radians);
		side6_vertex2.Rotate_Around_Z_Axis(angle_in_radians);
		side6_vertex3.Rotate_Around_Z_Axis(angle_in_radians);
		side6_vertex4.Rotate_Around_Z_Axis(angle_in_radians);
		side6_normal_vector = side6_vertex1.CalculateCrossProduct(side6_vertex2, side6_vertex3);      
	}//PerspectiveProjection_RotateCube_Z_Axis
	
	
	private void UniformScale(boolean is_uniform_scale_up) {
		
		//So if we rotated the cube in camera-space
		//If we just want to look at perspective-projection of cube as if it was rotating around its own center
		
		if (is_uniform_scale_up)
			CurrentScale += 0.1;
		else
			CurrentScale -= 0.1;
		
		side1_vertex1.x = CurrentScale * side1_vertex1_start.x;
		side1_vertex1.y = CurrentScale * side1_vertex1_start.y;
		side1_vertex1.z = CurrentScale * side1_vertex1_start.z;
		
		side1_vertex2.x = CurrentScale * side1_vertex2_start.x;
		side1_vertex2.y = CurrentScale * side1_vertex2_start.y;
		side1_vertex2.z = CurrentScale * side1_vertex2_start.z;
		
		side1_vertex3.x = CurrentScale * side1_vertex3_start.x;
		side1_vertex3.y = CurrentScale * side1_vertex3_start.y;
		side1_vertex3.z = CurrentScale * side1_vertex3_start.z;
		
		side1_vertex4.x = CurrentScale * side1_vertex4_start.x;
		side1_vertex4.y = CurrentScale * side1_vertex4_start.y;
		side1_vertex4.z = CurrentScale * side1_vertex4_start.z;
		
		side2_vertex1.x = CurrentScale * side2_vertex1_start.x;
		side2_vertex1.y = CurrentScale * side2_vertex1_start.y;
		side2_vertex1.z = CurrentScale * side2_vertex1_start.z;
		
		side2_vertex2.x = CurrentScale * side2_vertex2_start.x;
		side2_vertex2.y = CurrentScale * side2_vertex2_start.y;
		side2_vertex2.z = CurrentScale * side2_vertex2_start.z;
		
		side2_vertex3.x = CurrentScale * side2_vertex3_start.x;
		side2_vertex3.y = CurrentScale * side2_vertex3_start.y;
		side2_vertex3.z = CurrentScale * side2_vertex3_start.z;
		
		side2_vertex4.x = CurrentScale * side2_vertex4_start.x;
		side2_vertex4.y = CurrentScale * side2_vertex4_start.y;
		side2_vertex4.z = CurrentScale * side2_vertex4_start.z;
		
		side3_vertex1.x = CurrentScale * side3_vertex1_start.x;
		side3_vertex1.y = CurrentScale * side3_vertex1_start.y;
		side3_vertex1.z = CurrentScale * side3_vertex1_start.z;
		
		side3_vertex2.x = CurrentScale * side3_vertex2_start.x;
		side3_vertex2.y = CurrentScale * side3_vertex2_start.y;
		side3_vertex2.z = CurrentScale * side3_vertex2_start.z;
		
		side3_vertex3.x = CurrentScale * side3_vertex3_start.x;
		side3_vertex3.y = CurrentScale * side3_vertex3_start.y;
		side3_vertex3.z = CurrentScale * side3_vertex3_start.z;
		
		side3_vertex4.x = CurrentScale * side3_vertex4_start.x;
		side3_vertex4.y = CurrentScale * side3_vertex4_start.y;
		side3_vertex4.z = CurrentScale * side3_vertex4_start.z;
		
		side4_vertex1.x = CurrentScale * side4_vertex1_start.x;
		side4_vertex1.y = CurrentScale * side4_vertex1_start.y;
		side4_vertex1.z = CurrentScale * side4_vertex1_start.z;
		
		side4_vertex2.x = CurrentScale * side4_vertex2_start.x;
		side4_vertex2.y = CurrentScale * side4_vertex2_start.y;
		side4_vertex2.z = CurrentScale * side4_vertex2_start.z;
		
		side4_vertex3.x = CurrentScale * side4_vertex3_start.x;
		side4_vertex3.y = CurrentScale * side4_vertex3_start.y;
		side4_vertex3.z = CurrentScale * side4_vertex3_start.z;
		
		side4_vertex4.x = CurrentScale * side4_vertex4_start.x;
		side4_vertex4.y = CurrentScale * side4_vertex4_start.y;
		side4_vertex4.z = CurrentScale * side4_vertex4_start.z;
		
		side5_vertex1.x = CurrentScale * side5_vertex1_start.x;
		side5_vertex1.y = CurrentScale * side5_vertex1_start.y;
		side5_vertex1.z = CurrentScale * side5_vertex1_start.z;
		
		side5_vertex2.x = CurrentScale * side5_vertex2_start.x;
		side5_vertex2.y = CurrentScale * side5_vertex2_start.y;
		side5_vertex2.z = CurrentScale * side5_vertex2_start.z;
		
		side5_vertex3.x = CurrentScale * side5_vertex3_start.x;
		side5_vertex3.y = CurrentScale * side5_vertex3_start.y;
		side5_vertex3.z = CurrentScale * side5_vertex3_start.z;
		
		side5_vertex4.x = CurrentScale * side5_vertex4_start.x;
		side5_vertex4.y = CurrentScale * side5_vertex4_start.y;
		side5_vertex4.z = CurrentScale * side5_vertex4_start.z;
		
		side6_vertex1.x = CurrentScale * side6_vertex1_start.x;
		side6_vertex1.y = CurrentScale * side6_vertex1_start.y;
		side6_vertex1.z = CurrentScale * side6_vertex1_start.z;
		
		side6_vertex2.x = CurrentScale * side6_vertex2_start.x;
		side6_vertex2.y = CurrentScale * side6_vertex2_start.y;
		side6_vertex2.z = CurrentScale * side6_vertex2_start.z;
		
		side6_vertex3.x = CurrentScale * side6_vertex3_start.x;
		side6_vertex3.y = CurrentScale * side6_vertex3_start.y;
		side6_vertex3.z = CurrentScale * side6_vertex3_start.z;
		
		side6_vertex4.x = CurrentScale * side6_vertex4_start.x;
		side6_vertex4.y = CurrentScale * side6_vertex4_start.y;
		side6_vertex4.z = CurrentScale * side6_vertex4_start.z;
		
		side1_normal_vector = side1_vertex1.CalculateCrossProduct(side1_vertex2, side1_vertex3);
		side2_normal_vector = side2_vertex1.CalculateCrossProduct(side2_vertex2, side2_vertex3);      
		side3_normal_vector = side3_vertex1.CalculateCrossProduct(side3_vertex2, side3_vertex3);
		side4_normal_vector = side4_vertex1.CalculateCrossProduct(side4_vertex2, side4_vertex3);      
		side5_normal_vector = side5_vertex1.CalculateCrossProduct(side5_vertex2, side5_vertex3);
		side6_normal_vector = side6_vertex1.CalculateCrossProduct(side6_vertex2, side6_vertex3);

        Center_Of_Cube_Point.x = 0;
		Center_Of_Cube_Point.y = 0;
		Center_Of_Cube_Point.z = (side1_vertex1.z + side6_vertex1.z) / 2; 		
	}//UniformScale	
	
    private void HandleKeyPress(KeyEvent e) {
       int keyCode = e.getKeyCode();
       if (keyCode == 65) { //a
          System.out.println("a pressed");
          Dimension d = getSize();
          System.out.println("width  = " + d.width);
          System.out.println("height = " + d.height);

          side1_normal_vector.DebugPrint("front red side1_normal_vector = ");
          side2_normal_vector.DebugPrint("top pink side2_normal_vector = ");
          side3_normal_vector.DebugPrint("bottom yellow side3_normal_vector = ");
          side4_normal_vector.DebugPrint("right blue side4_normal_vector = ");
          side5_normal_vector.DebugPrint("left green side5_normal_vector = ");
          side6_normal_vector.DebugPrint("back black side6_normal_vector = ");
       }
	   else if (keyCode == 38) { //up-arrow
	      UniformScale(true);
		  this.repaint();
	   }
	   else if (keyCode == 40) { //down-arrow
	      UniformScale(false);
		  this.repaint();
	   }
       else if (keyCode == 88) { //x
		  do_X_rotate = true;
		  this.repaint();
       }//x
	   else if (keyCode == 89) { //y
	      do_Y_rotate = true;
		  this.repaint();
       }//y
	   else if (keyCode == 90) { //z
	      do_Z_rotate = true;
		  this.repaint();
       }//z
    }//HandleKeyPress
	
     
    private void displayInfo(KeyEvent e, String keyStatus) {
         
        //You should only rely on the key char if the event
        //is a key typed event.

        int id = e.getID();
        String keyString;
        if (id == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            keyString = "key character = '" + c + "'";
        } else {
            int keyCode = e.getKeyCode();
            keyString = "key code = " + keyCode
                    + " ("
                    + KeyEvent.getKeyText(keyCode)
                    + ")";
        }
         
        int modifiersEx = e.getModifiersEx();
        String modString = "extended modifiers = " + modifiersEx;
        String tmpString = KeyEvent.getModifiersExText(modifiersEx);
        if (tmpString.length() > 0) {
            modString += " (" + tmpString + ")";
        } else {
            modString += " (no extended modifiers)";
        }
         
        String actionString = "action key? ";
        if (e.isActionKey()) {
            actionString += "YES";
        } else {
            actionString += "NO";
        }
         
        String locationString = "key location: ";
        int location = e.getKeyLocation();
        if (location == KeyEvent.KEY_LOCATION_STANDARD) {
            locationString += "standard";
        } else if (location == KeyEvent.KEY_LOCATION_LEFT) {
            locationString += "left";
        } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
            locationString += "right";
        } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
            locationString += "numpad";
        } else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
            locationString += "unknown";
        }
         
        System.out.println(keyStatus);
        System.out.println(keyString);
        System.out.println(modString);
        System.out.println(actionString);
        System.out.println(locationString);
        System.out.println();
        System.out.println();
    }
}

