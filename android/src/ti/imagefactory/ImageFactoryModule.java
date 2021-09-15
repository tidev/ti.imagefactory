/**
 * Ti.Imagefactory Module
 * Copyright (c) 2011-2021 by Axway, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.imagefactory;

import android.graphics.Bitmap;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.util.TiConvert;

@Kroll.module(name = "ImageFactory", id = "ti.imagefactory")
public class ImageFactoryModule extends KrollModule
{
	// Standard Debugging variables
	private static final String TAG = "ImageFactoryModule";

	// Transform constants defined to match iOS values
	@Kroll.constant
	public static final int TRANSFORM_NONE = 0;
	@Kroll.constant
	public static final int TRANSFORM_CROP = 1;
	@Kroll.constant
	public static final int TRANSFORM_RESIZE = 2;
	@Kroll.constant
	public static final int TRANSFORM_THUMBNAIL = 3;
	@Kroll.constant
	public static final int TRANSFORM_ROUNDEDCORNER = 4;
	@Kroll.constant
	public static final int TRANSFORM_TRANSPARENTBORDER = 5;
	@Kroll.constant
	public static final int TRANSFORM_ALPHA = 6;
	@Kroll.constant
	public static final int TRANSFORM_ROTATE = 7;

	// Quality constants defined to match iOS values
	// Not current used, but exposed for API parity
	@Kroll.constant
	public static final int QUALITY_DEFAULT = 0;
	@Kroll.constant
	public static final int QUALITY_NONE = 1;
	@Kroll.constant
	public static final int QUALITY_LOW = 2;
	@Kroll.constant
	public static final int QUALITY_MEDIUM = 4;
	@Kroll.constant
	public static final int QUALITY_HIGH = 3;

	// Image format
	@Kroll.constant
	public static final int JPEG = ImageFormatType.JPEG.toTitaniumIntegerId();
	@Kroll.constant
	public static final int PNG = ImageFormatType.PNG.toTitaniumIntegerId();

	@Kroll.method
	public TiBlob imageWithRotation(TiBlob blob, HashMap args)
	{
		return ImageFactory.imageRotate(blob, new KrollDict(args));
	}

	@Kroll.method
	public TiBlob imageWithAlpha(TiBlob blob, HashMap args)
	{
		return ImageFactory.imageAlpha(blob, new KrollDict(args));
	}

	@Kroll.method
	public TiBlob imageWithTransparentBorder(TiBlob blob, HashMap args)
	{
		return ImageFactory.imageTransparentBorder(blob, new KrollDict(args));
	}

	@Kroll.method
	public TiBlob imageWithRoundedCorner(TiBlob blob, HashMap args)
	{
		return ImageFactory.imageRoundedCorner(blob, new KrollDict(args));
	}

	@Kroll.method
	public TiBlob imageAsThumbnail(TiBlob blob, HashMap args)
	{
		return ImageFactory.imageThumbnail(blob, new KrollDict(args));
	}

	@Kroll.method
	public TiBlob imageAsResized(TiBlob blob, HashMap args)
	{
		return ImageFactory.imageResize(blob, new KrollDict(args));
	}

	@Kroll.method
	public TiBlob imageAsCropped(TiBlob blob, HashMap args)
	{
		return ImageFactory.imageCrop(blob, new KrollDict(args));
	}

	@Kroll.method
	public TiBlob imageAsUpright(TiBlob blob, @Kroll.argument(optional = true) HashMap args)
	{
		// If given image has a non-upright EXIF orientation, then create a new pre-rotated image.
		// Otherwise, return the blob as-is if already upright.
		// Note: New image must have same "format" as original image unless configured differently.
		TiExifOrientation exifOrientation = TiExifOrientation.from(blob);
		if ((exifOrientation != null) && (exifOrientation != TiExifOrientation.UPRIGHT)) {
			KrollDict newArgs = (args != null) ? new KrollDict(args) : new KrollDict();
			newArgs.put(TiPropertyNames.DEGREES, 0);
			if (!newArgs.containsKey(TiPropertyNames.FORMAT)) {
				String mimeType = blob.getMimeType();
				if (mimeType != null) {
					mimeType = mimeType.toLowerCase();
					if (mimeType.endsWith("png")) {
						newArgs.put(TiPropertyNames.FORMAT, ImageFactoryModule.PNG);
					} else if (mimeType.endsWith("jpg") || mimeType.endsWith("jpeg")) {
						newArgs.put(TiPropertyNames.FORMAT, ImageFactoryModule.JPEG);
					}
				}
			}
			TiBlob newBlob = ImageFactory.imageRotate(blob, newArgs);
			if (newBlob != null) {
				return newBlob;
			}
		}
		return blob;
	}

	@Kroll.method
	public TiBlob imageTransform(Object[] args)
	{
		// Validate arguments.
		if ((args == null) || (args.length < 1) || !(args[0] instanceof TiBlob)) {
			return null;
		}

		// Fetch the 1st argument's blob and image. This is required.
		TiBlob blob = (TiBlob) args[0];
		TiExifOrientation exifOrientation = TiExifOrientation.from(blob);
		Bitmap bitmap = blob.getImage();
		if (bitmap == null) {
			return blob;
		}

		// Apply all given transformations to the source bitmap.
		Bitmap newBitmap = bitmap;
		for (int index = 1; index < args.length; index++) {
			// Fetch the next argument's options dictionary.
			KrollDict options;
			if (args[index] instanceof KrollDict) {
				options = (KrollDict) args[index];
			} else if (args[index] instanceof HashMap) {
				options = new KrollDict((HashMap) args[index]);
			} else {
				continue;
			}

			// Apply transformation to the image.
			Bitmap nextBitmap = null;
			int transformationType = TiConvert.toInt(options.get(TiPropertyNames.TYPE), TRANSFORM_NONE);
			switch (transformationType) {
				case TRANSFORM_ALPHA:
					nextBitmap = ImageFactory.imageAlpha(newBitmap, options, exifOrientation);
					break;
				case TRANSFORM_CROP:
					nextBitmap = ImageFactory.imageCrop(newBitmap, options, exifOrientation);
					break;
				case TRANSFORM_RESIZE:
					nextBitmap = ImageFactory.imageResize(newBitmap, options, exifOrientation);
					break;
				case TRANSFORM_ROTATE:
					nextBitmap = ImageFactory.imageRotate(newBitmap, options, exifOrientation);
					break;
				case TRANSFORM_ROUNDEDCORNER:
					nextBitmap = ImageFactory.imageRoundedCorner(newBitmap, options, exifOrientation);
					break;
				case TRANSFORM_THUMBNAIL:
					nextBitmap = ImageFactory.imageThumbnail(newBitmap, options, exifOrientation);
					break;
				case TRANSFORM_TRANSPARENTBORDER:
					nextBitmap = ImageFactory.imageTransparentBorder(newBitmap, options, exifOrientation);
					break;
			}

			// Keep bitmap created above (if successful) and delete the previous bitmap.
			// Note: Do not delete 1st original source bitmap that was given to this module method.
			if (nextBitmap != null) {
				if (newBitmap != bitmap) {
					newBitmap.recycle();
				}
				newBitmap = nextBitmap;
				exifOrientation = TiExifOrientation.UPRIGHT;
			}
		}

		// If a new bitmap was created above, compress it to PNG and wrap it in a new blob.
		if (newBitmap != bitmap) {
			KrollDict compressOptions = new KrollDict();
			compressOptions.put(TiPropertyNames.FORMAT, ImageFactoryModule.PNG);
			blob = ImageFactory.compressToBlob(newBitmap, compressOptions, true);
		}

		// Return the resulting image blob.
		return blob;
	}

	@Kroll.method
	public TiBlob compress(TiBlob blob, float quality)
	{
		// Do not continue if not given a blob.
		if (blob == null) {
			return null;
		}

		// Compress blob's image to JPEG with given quality. (Quality range: 0.0 - 1.0)
		// Calling imageRotate() with degrees 0 forces image to upright position in case it has EXIF orientation.
		KrollDict args = new KrollDict();
		args.put(TiPropertyNames.FORMAT, ImageFactoryModule.JPEG);
		args.put(TiPropertyNames.QUALITY, quality);
		args.put(TiPropertyNames.DEGREES, 0);
		return ImageFactory.imageRotate(blob, args);
	}

	@Kroll.method
	public boolean compressToFile(TiBlob blob, float quality, String fileUrl)
	{
		// Validate arguments.
		if ((blob == null) || (fileUrl == null) || fileUrl.isEmpty()) {
			return false;
		}

		// Create a file object from the given URL or file system path.
		TiBaseFile tiFile = TiFileFactory.createTitaniumFile(fileUrl, false);
		File file = (tiFile != null) ? tiFile.getNativeFile() : null;
		if (file == null) {
			return false;
		}

		// Extract the extension from the given file URL.
		String fileExtension = "";
		int periodIndex = fileUrl.lastIndexOf('.');
		if ((periodIndex >= 0) && ((periodIndex + 1) < fileUrl.length())) {
			int slashIndex = fileUrl.lastIndexOf('/');
			if (slashIndex < periodIndex) {
				fileExtension = fileUrl.substring(periodIndex + 1);
			}
		}

		// Determine the image format based on the given file extension.
		ImageFormatType formatType = ImageFormatType.fromFileExtension(fileExtension);
		if (formatType == null) {
			formatType = ImageFormatType.JPEG;
		}

		// Write blob's bitmap to file with requested compression format.
		boolean wasSuccessful = false;
		try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
			KrollDict args = new KrollDict();
			args.put(TiPropertyNames.FORMAT, formatType.toTitaniumIntegerId());
			args.put(TiPropertyNames.QUALITY, quality);
			blob = imageAsUpright(blob, args);
			wasSuccessful = ImageFactory.compressToStream(blob.getImage(), args, outputStream);
		} catch (Throwable ex) {
			Log.e(TAG, "Failed to compress image to file.", ex);
		}
		return wasSuccessful;
	}
}
