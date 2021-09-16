/**
 * Ti.Imagefactory Module
 * Copyright (c) 2011-2021 by Axway, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.imagefactory;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.util.TiConvert;

public class ImageFactory
{
	private static final String TAG = "ImageFactory";

	public static TiBlob imageRotate(TiBlob blob, KrollDict args)
	{
		if (blob == null) {
			return null;
		}
		Bitmap oldBitmap = blob.getImage();
		Bitmap newBitmap = imageRotate(oldBitmap, args, TiExifOrientation.from(blob));
		return compressToBlob(newBitmap, args, (newBitmap != oldBitmap));
	}

	public static Bitmap imageRotate(Bitmap bitmap, KrollDict args, TiExifOrientation exifOrientation)
	{
		// Validate given bitmap.
		if (bitmap == null) {
			return null;
		}

		// Make sure the rest of the params are defined.
		if (args == null) {
			args = new KrollDict();
		}
		if (exifOrientation == null) {
			exifOrientation = TiExifOrientation.UPRIGHT;
		}

		// Create a new rotated bitmap.
		Matrix matrix = createUprightMatrixFrom(bitmap, exifOrientation);
		matrix.postRotate(args.optInt(TiPropertyNames.DEGREES, 90));
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	public static TiBlob imageCrop(TiBlob blob, KrollDict args)
	{
		if (blob == null) {
			return null;
		}
		Bitmap oldBitmap = blob.getImage();
		Bitmap newBitmap = imageCrop(oldBitmap, args, TiExifOrientation.from(blob));
		return compressToBlob(newBitmap, args, (newBitmap != oldBitmap));
	}

	public static Bitmap imageCrop(Bitmap bitmap, KrollDict args, TiExifOrientation exifOrientation)
	{
		// Validate given bitmap.
		if (bitmap == null) {
			return null;
		}

		// Make sure the rest of the params are defined.
		if (args == null) {
			args = new KrollDict();
		}
		if (exifOrientation == null) {
			exifOrientation = TiExifOrientation.UPRIGHT;
		}

		// Calculate the region to crop the given bitmap to.
		int oldUprightWidth = !exifOrientation.isSideways() ? bitmap.getWidth() : bitmap.getHeight();
		int oldUprightHeight = !exifOrientation.isSideways() ? bitmap.getHeight() : bitmap.getWidth();
		int newUprightWidth = args.optInt(TiPropertyNames.WIDTH, oldUprightWidth);
		int newUprightHeight = args.optInt(TiPropertyNames.HEIGHT, oldUprightHeight);
		int uprightX = args.optInt(TiPropertyNames.X, (oldUprightWidth - newUprightWidth) / 2);
		int uprightY = args.optInt(TiPropertyNames.Y, (oldUprightHeight - newUprightHeight) / 2);

		// Calculate the region to extract the image from the source bitmap.
		// Note: If an EXIF orientation is defined, then we need to compensate for it here.
		int sourceX;
		int sourceY;
		switch (exifOrientation.getDegreesCounterClockwise()) {
			case 90:
				sourceX = uprightY;
				if (exifOrientation.isMirrored()) {
					sourceY = uprightX;
				} else {
					sourceY = oldUprightWidth - (uprightX + newUprightWidth);
				}
				break;
			case 180:
				if (exifOrientation.isMirrored()) {
					sourceX = uprightX;
				} else {
					sourceX = oldUprightWidth - (uprightX + newUprightWidth);
				}
				sourceY = oldUprightHeight - (uprightY + newUprightHeight);
				break;
			case 270:
				sourceX = oldUprightHeight - (uprightY + newUprightHeight);
				if (exifOrientation.isMirrored()) {
					sourceY = oldUprightWidth - (uprightX + newUprightWidth);
				} else {
					sourceY = uprightX;
				}
				break;
			case 0:
			default:
				if (exifOrientation.isMirrored()) {
					sourceX = oldUprightWidth - (uprightX + newUprightWidth);
				} else {
					sourceX = uprightX;
				}
				sourceY = uprightY;
				break;
		}
		int sourceWidth = !exifOrientation.isSideways() ? newUprightWidth : newUprightHeight;
		int sourceHeight = !exifOrientation.isSideways() ? newUprightHeight : newUprightWidth;

		// Create a new cropped bitmap.
		Matrix matrix = createUprightMatrixFrom(bitmap, exifOrientation);
		matrix.postTranslate((float) uprightX, (float) uprightY);
		return Bitmap.createBitmap(bitmap, sourceX, sourceY, sourceWidth, sourceHeight, matrix, true);
	}

	public static TiBlob imageResize(TiBlob blob, KrollDict args)
	{
		if (blob == null) {
			return null;
		}
		Bitmap oldBitmap = blob.getImage();
		Bitmap newBitmap = imageResize(oldBitmap, args, TiExifOrientation.from(blob));
		return compressToBlob(newBitmap, args, (newBitmap != oldBitmap));
	}

	public static Bitmap imageResize(Bitmap bitmap, KrollDict args, TiExifOrientation exifOrientation)
	{
		// Validate given bitmap.
		if (bitmap == null) {
			return null;
		}

		// Make sure the rest of the params are defined.
		if (args == null) {
			args = new KrollDict();
		}
		if (exifOrientation == null) {
			exifOrientation = TiExifOrientation.UPRIGHT;
		}

		// Calculate how to scale the bitmap so it will stretch to fill the given width/height.
		float oldWidth = !exifOrientation.isSideways() ? bitmap.getWidth() : bitmap.getHeight();
		float oldHeight = !exifOrientation.isSideways() ? bitmap.getHeight() : bitmap.getWidth();
		float newWidth = TiConvert.toFloat(args.get(TiPropertyNames.WIDTH), oldWidth);
		float newHeight = TiConvert.toFloat(args.get(TiPropertyNames.HEIGHT), oldHeight);
		float scaleX = (oldWidth > 0) ? (newWidth / oldWidth) : 0.0f;
		float scaleY = (oldHeight > 0) ? (newHeight / oldHeight) : 0.0f;

		// Create a new resized bitmap.
		Matrix matrix = createUprightMatrixFrom(bitmap, exifOrientation);
		matrix.postScale(scaleX, scaleY);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	public static TiBlob imageThumbnail(TiBlob blob, KrollDict args)
	{
		if (blob == null) {
			return null;
		}
		Bitmap oldBitmap = blob.getImage();
		Bitmap newBitmap = imageThumbnail(oldBitmap, args, TiExifOrientation.from(blob));
		return compressToBlob(newBitmap, args, (newBitmap != oldBitmap));
	}

	public static Bitmap imageThumbnail(Bitmap bitmap, KrollDict args, TiExifOrientation exifOrientation)
	{
		// Validate given bitmap.
		if (bitmap == null) {
			return null;
		}

		// Make sure the rest of the params are defined.
		if (args == null) {
			args = new KrollDict();
		}
		if (exifOrientation == null) {
			exifOrientation = TiExifOrientation.UPRIGHT;
		}

		// Fetch thumbnail options.
		int size = args.optInt(TiPropertyNames.SIZE, 48);
		int borderSize = args.optInt(TiPropertyNames.BORDER_SIZE, 1);
		int cornerRadius = args.optInt(TiPropertyNames.CORNER_RADIUS, 0);

		// Create a bitmap crop-scaled to the given thumbnail size.
		Matrix matrix = createUprightMatrixFrom(bitmap, exifOrientation);
		float scale = (float) size / (float) Math.min(bitmap.getWidth(), bitmap.getHeight());
		matrix.postScale(scale, scale);
		Bitmap tempBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		// Create a new bitmap to draw the thumbnail image to.
		Bitmap newBitmap = Bitmap.createBitmap(size + borderSize * 2, size + borderSize * 2, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);

		// Apply rounded corners via a clipping path if configured.
		if (cornerRadius > 0) {
			final Rect rect = new Rect(borderSize, borderSize, size + borderSize, size + borderSize);
			final RectF rectF = new RectF(rect);
			Path path = new Path();
			path.addRoundRect(rectF, (float) cornerRadius, (float) cornerRadius, Path.Direction.CCW);
			canvas.clipPath(path);
		}

		// Draw the image to the canvas (clipped to the path) and centered.
		int x = borderSize - (tempBitmap.getWidth() - size) / 2;
		int y = borderSize - (tempBitmap.getHeight() - size) / 2;
		canvas.drawBitmap(tempBitmap, (float) x, (float) y, createBitmapPaintFrom(args));

		// Delete the temporary bitmap.
		tempBitmap.recycle();

		// Return the generated thumbnail bitmap.
		return newBitmap;
	}

	public static TiBlob imageRoundedCorner(TiBlob blob, KrollDict args)
	{
		// Do not continue if not given a blob.
		if (blob == null) {
			return null;
		}

		// Make sure to compress the new image to a format that supports an alpha channel.
		if (args == null) {
			args = new KrollDict();
		}
		if (ImageFormatType.from(args).isAlphaSupported() == false) {
			args.put(TiPropertyNames.FORMAT, ImageFactoryModule.PNG);
		}

		// Create a new image with rounded corners.
		Bitmap oldBitmap = blob.getImage();
		Bitmap newBitmap = imageRoundedCorner(oldBitmap, args, TiExifOrientation.from(blob));
		return compressToBlob(newBitmap, args, (newBitmap != oldBitmap));
	}

	public static Bitmap imageRoundedCorner(Bitmap bitmap, KrollDict args, TiExifOrientation exifOrientation)
	{
		// Validate given bitmap.
		if (bitmap == null) {
			return null;
		}

		// Make sure the rest of the params are defined.
		if (args == null) {
			args = new KrollDict();
		}
		if (exifOrientation == null) {
			exifOrientation = TiExifOrientation.UPRIGHT;
		}

		// Fetch rounded corner options.
		int borderSize = Math.max(TiConvert.toInt(args.get(TiPropertyNames.BORDER_SIZE), 1), 0);
		int cornerRadius = Math.max(TiConvert.toInt(args.get(TiPropertyNames.CORNER_RADIUS), 0), 0);
		int uprightWidth = !exifOrientation.isSideways() ? bitmap.getWidth() : bitmap.getHeight();
		int uprightHeight = !exifOrientation.isSideways() ? bitmap.getHeight() : bitmap.getWidth();

		// Create a new bitmap with rounded corners.
		Bitmap newBitmap = Bitmap.createBitmap(uprightWidth, uprightHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		if (cornerRadius > 0) {
			Path path = new Path();
			Rect rect = new Rect(borderSize, borderSize, uprightWidth - borderSize, uprightHeight - borderSize);
			path.addRoundRect(new RectF(rect), (float) cornerRadius, (float) cornerRadius, Path.Direction.CCW);
			canvas.clipPath(path);
		}
		canvas.drawBitmap(bitmap, createUprightMatrixFrom(bitmap, exifOrientation), createBitmapPaintFrom(args));
		return newBitmap;
	}

	public static TiBlob imageTransparentBorder(TiBlob blob, KrollDict args)
	{
		// Do not continue if a blob was not provided.
		if (blob == null) {
			return null;
		}

		// Make sure to compress the new image to a format that supports an alpha channel.
		if (args == null) {
			args = new KrollDict();
		}
		if (ImageFormatType.from(args).isAlphaSupported() == false) {
			args.put(TiPropertyNames.FORMAT, ImageFactoryModule.PNG);
		}

		// Create a new bitmap with an invisible border surrounding the given bitmap.
		Bitmap oldBitmap = blob.getImage();
		Bitmap newBitmap = imageTransparentBorder(oldBitmap, args, TiExifOrientation.from(blob));
		return compressToBlob(newBitmap, args, (newBitmap != oldBitmap));
	}

	public static Bitmap imageTransparentBorder(Bitmap bitmap, KrollDict args, TiExifOrientation exifOrientation)
	{
		// Validate given bitmap.
		if (bitmap == null) {
			return null;
		}

		// Make sure the rest of the params are defined.
		if (args == null) {
			args = new KrollDict();
		}
		if (exifOrientation == null) {
			exifOrientation = TiExifOrientation.UPRIGHT;
		}

		// Fetch border options.
		int borderSize = Math.max(TiConvert.toInt(args.get(TiPropertyNames.BORDER_SIZE), 1), 0);
		int newWidth = !exifOrientation.isSideways() ? bitmap.getWidth() : bitmap.getHeight();
		int newHeight = !exifOrientation.isSideways() ? bitmap.getHeight() : bitmap.getWidth();
		newWidth += borderSize * 2;
		newHeight += borderSize * 2;

		// Create a new bitmap with an invisible border surrounding the given bitmap.
		Bitmap newBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		Matrix matrix = createUprightMatrixFrom(bitmap, exifOrientation);
		matrix.postTranslate((float) borderSize, (float) borderSize);
		canvas.drawBitmap(bitmap, matrix, createBitmapPaintFrom(args));
		return newBitmap;
	}

	public static TiBlob imageAlpha(TiBlob blob, KrollDict args)
	{
		// Fetch the blob's bitmap.
		Bitmap bitmap = (blob != null) ? blob.getImage() : null;
		if (bitmap == null) {
			return null;
		}

		// If image already has an alpha channel, then return that blob. No need to create a new image/blob.
		if (bitmap.hasAlpha()) {
			return blob;
		}

		// Make sure to compress the new image to a format that supports an alpha channel.
		if (args == null) {
			args = new KrollDict();
		}
		if (ImageFormatType.from(args).isAlphaSupported() == false) {
			args.put(TiPropertyNames.FORMAT, ImageFactoryModule.PNG);
		}

		// Create a copy of the given image with an alpha channel.
		Bitmap oldBitmap = blob.getImage();
		Bitmap newBitmap = imageAlpha(oldBitmap, args, TiExifOrientation.from(blob));
		return compressToBlob(newBitmap, args, (newBitmap != oldBitmap));
	}

	public static Bitmap imageAlpha(Bitmap bitmap, KrollDict args, TiExifOrientation exifOrientation)
	{
		// Validate given bitmap.
		if (bitmap == null) {
			return null;
		}

		// Make sure the rest of the params are defined.
		if (exifOrientation == null) {
			exifOrientation = TiExifOrientation.UPRIGHT;
		}

		// If image already has an alpha channel, then return that blob. No need to create a new image/blob.
		if (bitmap.hasAlpha()) {
			return bitmap;
		}

		// Create a new bitmap with an alpha channel.
		Bitmap newBitmap = null;
		if (exifOrientation == TiExifOrientation.UPRIGHT) {
			newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
		} else {
			int uprightWidth = !exifOrientation.isSideways() ? bitmap.getWidth() : bitmap.getHeight();
			int uprightHeight = !exifOrientation.isSideways() ? bitmap.getHeight() : bitmap.getWidth();
			newBitmap = Bitmap.createBitmap(uprightWidth, uprightHeight, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(newBitmap);
			canvas.drawBitmap(bitmap, createUprightMatrixFrom(bitmap, exifOrientation), createBitmapPaintFrom(args));
		}
		return newBitmap;
	}

	public static TiBlob compressToBlob(Bitmap bitmap, KrollDict args, boolean isRecyclingBitmap)
	{
		// Validate bitmap.
		if (bitmap == null) {
			return null;
		}

		// Compress to image format (such as JPEG) and return it wrapped in a blob.
		byte[] compressedBytes = new byte[0];
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			if (compressToStream(bitmap, args, outputStream)) {
				compressedBytes = outputStream.toByteArray();
			}
		} catch (Throwable ex) {
			Log.e(TAG, "Failed to compress image to blob.", ex);
		}
		if (isRecyclingBitmap) {
			bitmap.recycle();
			bitmap = null;
		}
		return TiBlob.blobFromData(compressedBytes, ImageFormatType.from(args).toMimeType());
	}

	public static boolean compressToStream(Bitmap bitmap, KrollDict args, OutputStream outputStream)
	{
		// Validate bitmap.
		if ((bitmap == null) || (outputStream == null)) {
			return false;
		}

		// Determine which format type to compress the bitmap to.
		ImageFormatType formatType = ImageFormatType.from(args);

		// Fetch compression quality from arguments.
		// This modules defines it as a value ranging from 0-1, which we must convert to 0-100.
		int quality = 70;
		if (args != null) {
			float value = TiConvert.toFloat(args.get(TiPropertyNames.QUALITY), quality / 100.0f);
			if (value > 1.0f) {
				value = 1.0f;
			} else if (value < 0.0f) {
				value = 0.0f;
			}
			quality = (int) (value * 100.0f);
		}

		// Compress bitmap to requested image format (such as JPEG) to given stream.
		return bitmap.compress(formatType.toCompressFormat(), quality, outputStream);
	}

	private static Matrix createUprightMatrixFrom(TiBlob blob)
	{
		Bitmap bitmap = (blob != null) ? blob.getImage() : null;
		return createUprightMatrixFrom(bitmap, TiExifOrientation.from(blob));
	}

	private static Matrix createUprightMatrixFrom(Bitmap bitmap, TiExifOrientation exifOrientation)
	{
		Matrix matrix = new Matrix();
		if ((bitmap != null) && (exifOrientation != null) && (exifOrientation != TiExifOrientation.UPRIGHT)) {
			float width = !exifOrientation.isSideways() ? bitmap.getWidth() : bitmap.getHeight();
			float height = !exifOrientation.isSideways() ? bitmap.getHeight() : bitmap.getWidth();
			matrix.postRotate(exifOrientation.getDegreesCounterClockwise());
			switch (exifOrientation.getDegreesCounterClockwise()) {
				case 90:
					matrix.postTranslate(width, 0);
					break;
				case 180:
					matrix.postTranslate(width, height);
					break;
				case 270:
					matrix.postTranslate(0, height);
					break;
			}
			if (exifOrientation.isMirrored()) {
				matrix.postScale(-1.0f, 1.0f, width * 0.5f, 0.0f);
			}
		}
		return matrix;
	}

	private static Paint createBitmapPaintFrom(KrollDict args)
	{
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither((args != null) ? args.optBoolean(TiPropertyNames.DITHER, false) : false);
		paint.setFilterBitmap(true);
		return paint;
	}
}
