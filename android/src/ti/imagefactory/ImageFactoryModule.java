/**
 * Ti.Imagefactory Module
 * Copyright (c) 2011-2021 by Axway, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.imagefactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiDrawableReference;

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
	@Kroll.constant
	public static final int WEBP = ImageFormatType.WEBP.toTitaniumIntegerId();

	public TiBlob localImageWithRotation(TiBlob blob, KrollDict args)
	{
		return ImageFactory.imageRotate(blob, args);
	}

	@Kroll.method
	public void imageWithRotation(KrollDict args) {
		final ImageFactoryModule that = this;
		TiBlob blob = TiConvert.toBlob(args.get("blob"));
		KrollFunction callback = (KrollFunction) args.get("success");
		boolean sync = TiConvert.toBoolean(args.get("sync"), false);

		if (sync) {
			TiBlob result = that.localImageWithRotation(blob, args);
			KrollDict map = new KrollDict();
			map.put("image", result);
			callback.call(getKrollObject(), map);
		} else {
			new Thread() {
				@Override
				public void run() {
					TiBlob result = that.localImageWithRotation(blob, args);
					KrollDict map = new KrollDict();
					map.put("image", result);
					callback.call(getKrollObject(), map);
				}
			}.start();
		}
	}

	public TiBlob localImageWithAlpha(TiBlob blob, KrollDict args)
	{
		return ImageFactory.imageAlpha(blob, args);
	}

	@Kroll.method
	public void ImageWithAlpha(KrollDict args) {
		final ImageFactoryModule that = this;
		TiBlob blob = TiConvert.toBlob(args.get("blob"));
		KrollFunction callback = (KrollFunction) args.get("success");
		boolean sync = TiConvert.toBoolean(args.get("sync"), false);

		if (sync) {
			TiBlob result = that.localImageWithAlpha(blob, args);
			KrollDict map = new KrollDict();
			map.put("image", result);
			callback.call(getKrollObject(), map);
		} else {
			new Thread() {
				@Override
				public void run() {
					TiBlob result = that.localImageWithAlpha(blob, args);
					KrollDict map = new KrollDict();
					map.put("image", result);
					callback.call(getKrollObject(), map);
				}
			}.start();
		}
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

	public TiBlob localImageAsThumbnail(TiBlob blob, KrollDict args)
	{
		return ImageFactory.imageThumbnail(blob, args);
	}

	@Kroll.method
	public void imageAsThumbnail(KrollDict args) {
		final ImageFactoryModule that = this;
		TiBlob blob = TiConvert.toBlob(args.get("blob"));
		KrollFunction callback = (KrollFunction) args.get("success");
		boolean sync = TiConvert.toBoolean(args.get("sync"), false);

		if (sync) {
			TiBlob result = that.localImageAsThumbnail(blob, args);
			KrollDict map = new KrollDict();
			map.put("image", result);
			callback.call(getKrollObject(), map);
		} else {
			new Thread() {
				@Override
				public void run() {
					TiBlob result = that.localImageAsThumbnail(blob, args);
					KrollDict map = new KrollDict();
					map.put("image", result);
					callback.call(getKrollObject(), map);
				}
			}.start();
		}
	}

	@Kroll.method
	public TiBlob localImageAsResized(TiBlob blob, KrollDict args)
	{
		return ImageFactory.imageResize(blob, args);
	}

	@Kroll.method
	public void imageAsResized(KrollDict args) {
		final ImageFactoryModule that = this;
		TiBlob blob = TiConvert.toBlob(args.get("blob"));
		KrollFunction callback = (KrollFunction) args.get("success");
		boolean sync = TiConvert.toBoolean(args.get("sync"), false);

		if (sync) {
			TiBlob result = that.localImageAsResized(blob, args);
			KrollDict map = new KrollDict();
			map.put("image", result);
			callback.call(getKrollObject(), map);
		} else {
			new Thread() {
				@Override
				public void run() {
					TiBlob result = that.localImageAsResized(blob, args);
					KrollDict map = new KrollDict();
					map.put("image", result);
					callback.call(getKrollObject(), map);
				}
			}.start();
		}
	}

	public TiBlob localImageAsCropped(TiBlob blob, KrollDict args)
	{
		return ImageFactory.imageCrop(blob, args);
	}

	@Kroll.method
	public void imageAsCropped(KrollDict args) {
		final ImageFactoryModule that = this;
		TiBlob blob = TiConvert.toBlob(args.get("blob"));
		KrollFunction callback = (KrollFunction) args.get("success");
		boolean sync = TiConvert.toBoolean(args.get("sync"), false);

		if (sync) {
			TiBlob result = that.localImageAsCropped(blob, args);
			KrollDict map = new KrollDict();
			map.put("image", result);
			callback.call(getKrollObject(), map);
		} else {
			new Thread() {
				@Override
				public void run() {
					TiBlob result = that.localImageAsCropped(blob, args);
					KrollDict map = new KrollDict();
					map.put("image", result);
					callback.call(getKrollObject(), map);
				}
			}.start();
		}
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
				ImageFormatType formatType = ImageFormatType.fromMimeType(blob.getMimeType());
				if (formatType != null) {
					newArgs.put(TiPropertyNames.FORMAT, formatType.toTitaniumIntegerId());
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


	public TiBlob localCompress(TiBlob blob, float quality, @Kroll.argument(optional = true) Object formatId)
	{
		// Do not continue if not given a blob.
		if (blob == null) {
			return null;
		}
		// Compress blob's image to JPEG with given quality. (Quality range: 0.0 - 1.0)
		// Calling imageRotate() with degrees 0 forces image to upright position in case it has EXIF orientation.
		KrollDict args = new KrollDict();
		args.put(TiPropertyNames.FORMAT, TiConvert.toInt(formatId, ImageFactoryModule.JPEG));
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
			// Make the image upright in case it has an EXIF orientation.
			// Note: Method will return same blob reference if image is already upright.
			KrollDict args = new KrollDict();
			args.put(TiPropertyNames.QUALITY, quality);
			blob = imageAsUpright(blob, args);

			// Write the image file to the requested format.
			args.put(TiPropertyNames.FORMAT, formatType.toTitaniumIntegerId());
			wasSuccessful = ImageFactory.compressToStream(blob.getImage(), args, outputStream);
		} catch (Throwable ex) {
			Log.e(TAG, "Failed to compress image to file.", ex);
		}
		return wasSuccessful;
	}

	@Kroll.method
	public void compress(KrollDict args) {
		final ImageFactoryModule that = this;

		TiBlob blob = TiConvert.toBlob(args.get("blob"));
		float quality = TiConvert.toFloat(args.get("quality"));
		int formatId = TiConvert.toInt(args.get("format"), ImageFactoryModule.JPEG);
		boolean sync = TiConvert.toBoolean(args.get("sync"), false);
		KrollFunction callback = (KrollFunction) args.get("success");

		if (sync) {
			TiBlob result = that.localCompress(blob, quality, formatId);
			KrollDict map = new KrollDict();
			map.put("image", result);
			callback.call(getKrollObject(), map);
		} else {
			new Thread() {
				@Override
				public void run() {
					TiBlob result = that.localCompress(blob, quality, formatId);
					KrollDict map = new KrollDict();
					map.put("image", result);
					callback.call(getKrollObject(), map);
				}
			}.start();
		}
	}

	@Kroll.method
	public KrollDict metadataFrom(TiBlob blob)
	{
		ImageMetadata metadata = ImageMetadata.from(blob);
		return (metadata != null) ? metadata.getEntries() : null;
	}

	public int[] localGetPixelArray(TiBlob blob)
	{
		TiDrawableReference ref = TiDrawableReference.fromBlob(getActivity(), blob);
		Bitmap bitmap = ref.getBitmap(false,false);

		int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

		return pixels;
	}

	@Kroll.method
	public void getPixelArray(KrollDict args) {
		final ImageFactoryModule that = this;
		TiBlob blob = TiConvert.toBlob(args.get("blob"));
		KrollFunction callback = (KrollFunction) args.get("success");
		boolean sync = TiConvert.toBoolean(args.get("sync"), false);

		if (sync) {
			int[] result = that.localGetPixelArray(blob);
			KrollDict map = new KrollDict();
			map.put("pixels", result);
			callback.call(getKrollObject(), map);
		} else {
			new Thread() {
				@Override
				public void run() {
					int[] result = that.localGetPixelArray(blob);
					KrollDict map = new KrollDict();
					map.put("pixels", result);
					callback.call(getKrollObject(), map);
				}
			}.start();
		}
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

	private TiBlob convertImageToBlob(Bitmap image) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte data[] = new byte[0];
		// NOTE: While "70" is indeed ignored by the "compress" method, it is there so that in the future if we let the
		// user decide the final output format and compression, we can remember what the default value is. For now, let
		// us keep things simple and compress to a png so that we get transparency. Because it is loseless, users can
		// then turn around and make a call to "compress" if they want it to be a smaller JPEG.
		if (image.compress(Bitmap.CompressFormat.PNG, 70, bos)) {
			data = bos.toByteArray();
		}

		TiBlob result = TiBlob.blobFromData(data, "image/png");
		coerceDimensionsIntoBlob(image, result);

		// [MOD-309] Free up memory to work around issue in Android
		image.recycle();
		image = null;

		return result;
	}

	public TiBlob localResampleImage(String fileName, KrollDict args)
	{
		final KrollDict argsDict = new KrollDict(args);
		final int inSampleSize = argsDict.optInt("inSampleSize", 1);
		final int inDensity = argsDict.optInt("inDensity", 1);
		final int inTargetDensity = argsDict.optInt("inTargetDensity", 1);

		final BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = inSampleSize;
		bitmapOptions.inDensity = inDensity;
		bitmapOptions.inTargetDensity = inTargetDensity;

		final Bitmap scaledBitmap = BitmapFactory.decodeFile(fileName, bitmapOptions);
		scaledBitmap.setDensity(Bitmap.DENSITY_NONE);

		return convertImageToBlob(scaledBitmap);
	}

	@Kroll.method
	public void resampleImage(KrollDict args) {
		final ImageFactoryModule that = this;
		String fileName = TiConvert.toString(args.get("file"));
		KrollFunction callback = (KrollFunction) args.get("success");
		boolean sync = TiConvert.toBoolean(args.get("sync"), false);

		if (sync) {
			TiBlob result = that.localResampleImage(fileName, args);
			KrollDict map = new KrollDict();
			map.put("image", result);
			callback.call(getKrollObject(), map);
		} else {
			new Thread() {
				@Override
				public void run() {
					TiBlob result = that.localResampleImage(fileName, args);
					KrollDict map = new KrollDict();
					map.put("image", result);
					callback.call(getKrollObject(), map);
				}
			}.start();
		}
	}
}
