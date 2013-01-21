/**
 * Ti.Imagefactory Module
 * Copyright (c) 2011-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.imagefactory;

import org.appcelerator.kroll.KrollDict;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageFactory {
	
	public static Bitmap imageCrop(Bitmap image, KrollDict args)
	{
		if (image != null) {
			int width = args.optInt("width", image.getWidth());
			int height = args.optInt("height", image.getHeight());
			int x = args.optInt("x", (image.getWidth() - width) / 2);
			int y = args.optInt("y", (image.getHeight() - height) / 2);
			
			return Bitmap.createBitmap(image, x, y, width, height);
		}
		
		return null;
	}

	public static Bitmap imageResize(Bitmap image, KrollDict args)
	{
		if (image != null) {
			int width = args.optInt("width", image.getWidth());
			int height = args.optInt("height", image.getHeight());
						
			return Bitmap.createScaledBitmap(image, width, height, true);
		}
		
		return null;
	}
	
	public static Bitmap imageThumbnail(Bitmap image, KrollDict args)
	{
		if (image != null) {
			int size = args.optInt("size", 48);
			int borderSize = args.optInt("borderSize", 1);
			int cornerRadius = args.optInt("cornerRadius", 0);
			Boolean dither = args.optBoolean("dither", true);
			
			// Create a temporary bitmap that maintains the aspect ratio. Note that one of either the height or width
			// may be larger than the thumbnail size if it is not a 1:1 aspect ratio. This is necessary to provide
			// parity with iOS since the Android scale-to-fit options will scale the image so that both width and
			// height fit within the specified rectangle, whereas the iOS scale-to-fit options will scale so that
			// the smaller size fits the specified rectangle
			
			float scale = (image.getWidth() < image.getHeight()) ? (float)image.getWidth() / (float)size : (float)image.getHeight() / (float)size;
			int width = (int)(image.getWidth() / scale);
			int height = (int)(image.getHeight() / scale);
			Bitmap tempBitmap = Bitmap.createScaledBitmap(image, width, height, true);
			
			// Create a new bitmap that is the thumbnail size
			Bitmap resultBitmap = Bitmap.createBitmap(size + borderSize * 2, size + borderSize * 2, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(resultBitmap);
			
			// Create a clipping path with rounded corners
			if (cornerRadius > 0) {
		        final Rect rect = new Rect(borderSize, borderSize, size + borderSize, size + borderSize);
		        final RectF rectF = new RectF(rect);
				Path path = new Path();
				path.addRoundRect(rectF, (float)cornerRadius, (float)cornerRadius, Path.Direction.CCW);
				canvas.clipPath(path);
			}
			
			// Draw the image to the canvas (clipped to the path) and centered
			int x = borderSize - (tempBitmap.getWidth() - size) / 2;
			int y = borderSize - (tempBitmap.getHeight() - size) / 2;	
			Paint paint = new Paint();
			paint.setDither(dither);
			canvas.drawBitmap(tempBitmap, (float)x, (float)y, paint);
			
			// We're done with the tempBitmap
			tempBitmap.recycle();
			tempBitmap = null;

		    return resultBitmap;			
		}
		
		return null;
	}
	
	public static Bitmap imageRoundedCorner(Bitmap image, KrollDict args)
	{
		if (image != null) {
			int borderSize = args.optInt("borderSize", 1);
			int cornerRadius = args.optInt("cornerRadius", 0);
			
			// Create a new bitmap that is the same size as the source bitmap
			Bitmap resultBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(resultBitmap);
			
			// Create a clipping path with rounded corners
			if (cornerRadius > 0) {
				Path path = new Path();
		        final Rect rect = new Rect(borderSize, borderSize, image.getWidth() - borderSize, image.getHeight() - borderSize);
		        final RectF rectF = new RectF(rect);
				path.addRoundRect(rectF, (float)cornerRadius, (float)cornerRadius, Path.Direction.CCW);
				canvas.clipPath(path);
			}
			
			// Draw the image to the canvas (clipped to the path)
			canvas.drawBitmap(image, 0, 0, null);

		    return resultBitmap;
		}
		
		return null;
	}
	
	public static Bitmap imageTransparentBorder(Bitmap image, KrollDict args)
	{
		if (image != null) {
			int borderSize = args.optInt("borderSize", 1);
			
			// Create a bitmap that is the current size plus the size needed for the border
			Bitmap resultBitmap = Bitmap.createBitmap(image.getWidth() + borderSize * 2, image.getHeight() + borderSize * 2, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(resultBitmap);
			
			// Draw the image to the canvas (offset by the border size)
			canvas.drawBitmap(image, (float)borderSize, (float)borderSize, null);
			
			return resultBitmap;
		}
		
		return null;
	}
	
	public static Bitmap imageAlpha(Bitmap image, KrollDict args)
	{
		if (image != null) {
			// If the image already has an alpha channel then just return
			if (image.hasAlpha()) {
				return image;
			}
			
			// Create a new bitmap that supports alpha
			Bitmap resultBitmap = image.copy(Bitmap.Config.ARGB_8888, false);
			
			return resultBitmap;
		}
		
		return null;
	}
}
