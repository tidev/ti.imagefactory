/**
 * Ti.Imagefactory Module
 * Copyright (c) 2011-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.imagefactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.view.TiDrawableReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

@Kroll.module(name="ImageFactory", id="ti.imagefactory")
public class ImageFactoryModule extends KrollModule
{
	// Standard Debugging variables
	private static final String LCAT = "ImageFactoryModule";

	// Transform constants defined to match iOS values
	@Kroll.constant public static final int TRANSFORM_NONE = 0;
	@Kroll.constant public static final int TRANSFORM_CROP = 1;
	@Kroll.constant public static final int TRANSFORM_RESIZE = 2;
	@Kroll.constant public static final int TRANSFORM_THUMBNAIL = 3;
	@Kroll.constant public static final int TRANSFORM_ROUNDEDCORNER = 4;
	@Kroll.constant public static final int TRANSFORM_TRANSPARENTBORDER = 5;
	@Kroll.constant public static final int TRANSFORM_ALPHA = 6;

	// Quality constants defined to match iOS values
	// Not current used, but exposed for API parity
	@Kroll.constant public static final int QUALITY_DEFAULT = 0;
	@Kroll.constant public static final int QUALITY_NONE = 1;	
	@Kroll.constant public static final int QUALITY_LOW = 2;
	@Kroll.constant public static final int QUALITY_MEDIUM = 4;
	@Kroll.constant public static final int QUALITY_HIGH = 3;
	
	// Image format
	@Kroll.constant public static final int JPEG = 0;
	@Kroll.constant public static final int PNG = 1;
	
	public ImageFactoryModule() {
		super();
	}

	// Image Transform Helpers
	
	private Bitmap imageTransform(int type, Bitmap image, KrollDict args)
	{
		switch (type) {
			case TRANSFORM_CROP:
				return ImageFactory.imageCrop(image, args);
			case TRANSFORM_RESIZE:
				return ImageFactory.imageResize(image, args);
			case TRANSFORM_THUMBNAIL:
				return ImageFactory.imageThumbnail(image, args);
			case TRANSFORM_ROUNDEDCORNER:
				return ImageFactory.imageRoundedCorner(image, args);
			case TRANSFORM_TRANSPARENTBORDER:
				return ImageFactory.imageTransparentBorder(image, args);
			case TRANSFORM_ALPHA:
				return ImageFactory.imageAlpha(image, args);
		}
	
		return image;
	}
	
	private CompressFormat getFormat(KrollDict args) {
		int format = args.optInt("format", JPEG);
		switch (format) {
			case ImageFactoryModule.PNG:
				return CompressFormat.PNG;
			case ImageFactoryModule.JPEG:
				return CompressFormat.JPEG;
			default:
				Log.e(LCAT, "Unknown format provided! Defaulting to JPEG!");
				return CompressFormat.JPEG;
		}
	}
	
	private String getStringFormat(CompressFormat format) {
		switch (format) {
			case PNG:
				return "image/png";
			case JPEG:
			default:
				return "image/jpeg";
		}
	}
	
	private int getQuality(KrollDict args) {
		if (!args.containsKey("quality"))
			return 70;
		return (int)(Float.parseFloat(args.getString("quality")) * 100);
	}
	
	private void coerceDimensionsIntoBlob(Bitmap image, TiBlob blob) {
		try {
			Field field = TiBlob.class.getDeclaredField("width");
			field.setAccessible(true);
		    field.setInt(blob, image.getWidth());
			field = TiBlob.class.getDeclaredField("height");
			field.setAccessible(true);
		    field.setInt(blob, image.getHeight());
		} catch (Exception e) {
			// ** cry **
		}
	}

	private TiBlob imageTransform(int type, TiBlob blob, KrollDict args)
	{
		TiDrawableReference ref = TiDrawableReference.fromBlob(getActivity(), blob);
		Bitmap image = imageTransform(type, ref.getBitmap(), args);

		if (image == null)
			return null;
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte data[] = new byte[0];
		CompressFormat format = getFormat(args);
		if (image.compress(format, getQuality(args), bos)) {
			data = bos.toByteArray();
		}

		TiBlob result = TiBlob.blobFromData(data, getStringFormat(format));
		coerceDimensionsIntoBlob(image, result);

		// [MOD-309] Free up memory to work around issue in Android
		image.recycle();
		image = null;
		ref = null;
		
		return result;
	}

 	// Public Image Methods

	@Kroll.method
	public TiBlob imageWithAlpha(TiBlob blob, HashMap args)
	{
		return imageTransform(TRANSFORM_ALPHA, blob, new KrollDict(args));
	}

	@Kroll.method
	public TiBlob imageWithTransparentBorder(TiBlob blob, HashMap args)
	{
		return imageTransform(TRANSFORM_TRANSPARENTBORDER, blob, new KrollDict(args));
	}
	
	@Kroll.method
	public TiBlob imageWithRoundedCorner(TiBlob blob, HashMap args)
	{
		return imageTransform(TRANSFORM_ROUNDEDCORNER, blob, new KrollDict(args));
	}
	
	@Kroll.method
	public TiBlob imageAsThumbnail(TiBlob blob, HashMap args)
	{
		return imageTransform(TRANSFORM_THUMBNAIL, blob, new KrollDict(args));
	}
	
	@Kroll.method
	public TiBlob imageAsResized(TiBlob blob, HashMap args)
	{
		return imageTransform(TRANSFORM_RESIZE, blob, new KrollDict(args));
	}
	
	@Kroll.method
	public TiBlob imageAsCropped(TiBlob blob, HashMap args)
	{
		return imageTransform(TRANSFORM_CROP, blob, new KrollDict(args));
	}

	@Kroll.method
	public TiBlob imageTransform(Object[] args)
	{
	    Bitmap image = null;
	    if (args[0] instanceof TiBlob) {

            // Use the drawable reference to get the source bitmap
            TiDrawableReference ref = TiDrawableReference.fromBlob(getActivity(), (TiBlob)args[0]);
            image = ref.getBitmap();

            // Apply the transforms one at a time
            for (int i = 1; i < args.length; i++) {
                if (args[i] instanceof HashMap) {
                     KrollDict transform = new KrollDict((HashMap)args[i]);
                    int type = transform.optInt("type", TRANSFORM_NONE);
                    Bitmap newImage = imageTransform(type, image, transform);
                    image.recycle();
                    image = null;
                    image = newImage;
                    newImage = null;
                }
            }
            
            ref = null;
        }

	    if (image == null)
			return null;
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte data[] = new byte[0];
		// NOTE: While "70" is indeed ignored by the "compress" method, it is there so that in the future if we let the
		// user decide the final output format and compression, we can remember what the default value is. For now, let
		// us keep things simple and compress to a png so that we get transparency. Because it is loseless, users can
		// then turn around and make a call to "compress" if they want it to be a smaller JPEG.
		if (image.compress(CompressFormat.PNG, 70, bos)) {
			data = bos.toByteArray();
		}

		TiBlob result = TiBlob.blobFromData(data, "image/png");
		coerceDimensionsIntoBlob(image, result);

		// [MOD-309] Free up memory to work around issue in Android
		image.recycle();
		image = null;
		
		return result;
	}

	@Kroll.method
	public TiBlob compress(TiBlob blob, float compressionQuality)
	{
		TiBlob result = null;
		Bitmap image = null;
		ByteArrayOutputStream bos;
		TiDrawableReference ref;
		
		try {
			ref = TiDrawableReference.fromBlob(getActivity(), blob);
			image = ref.getBitmap();
			bos = new ByteArrayOutputStream();
			if (image.compress(CompressFormat.JPEG, (int)(compressionQuality * 100), bos)) {
				byte[] data = bos.toByteArray();
				BitmapFactory.Options bfOptions = new BitmapFactory.Options();
				bfOptions.inPurgeable = true;
				bfOptions.inInputShareable = true;
				result = TiBlob.blobFromData(data, "image/jpeg");
				coerceDimensionsIntoBlob(image, result);
			}
		} catch (OutOfMemoryError e) {
			Log.e(LCAT, "Received an OutOfMemoryError! The image is too big to compress all at once. Consider using the \"compressToFile\" method instead.");
		} 
		finally {
            // [MOD-309] Free up memory to work around issue in Android
			if (image != null) {
				image.recycle();
				image = null;
			}
			bos = null;
			ref = null;
		}
		
		return result;
	}

	@Kroll.method
	public boolean compressToFile(TiBlob blob, float compressionQuality, String fileURL) {
		TiDrawableReference ref = TiDrawableReference.fromBlob(getActivity(), blob);
		Bitmap image = ref.getBitmap();

		TiBaseFile tiFile = TiFileFactory.createTitaniumFile(fileURL, false);
		File file = tiFile.getNativeFile();

		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			if (image.compress(CompressFormat.JPEG, (int) (compressionQuality * 100), bos)) {
				return true;
			}
		} catch (FileNotFoundException e) {
			return false;
		} finally {
	        // [MOD-309] Free up memory to work around issue in Android
			if (image != null) {
				image.recycle();
				image = null;
			}
			bos = null;
			ref = null;
		}

		return false;
	}
}
