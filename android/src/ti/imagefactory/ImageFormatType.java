/**
 * Ti.Imagefactory Module
 * Copyright (c) 2011-2021 by Axway, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.imagefactory;

import android.graphics.Bitmap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiMimeTypeHelper;

/** Enum type indicating an image format type such as PNG or JPEG. */
public enum ImageFormatType {
	JPEG(0, Bitmap.CompressFormat.JPEG, new String[] { "jpeg", "jpg" }),
	PNG(1, Bitmap.CompressFormat.PNG, new String[] { "png" }),
	WEBP(2, Bitmap.CompressFormat.WEBP, new String[] { "webp" });

	private final int titaniumIntegerId;
	private final Bitmap.CompressFormat compressFormat;
	private final String[] fileExtensions;
	private final String mimeType;

	private ImageFormatType(int titaniumIntegerId, Bitmap.CompressFormat compressFormat, String[] fileExtensions)
	{
		this.titaniumIntegerId = titaniumIntegerId;
		this.compressFormat = compressFormat;
		this.fileExtensions = fileExtensions;
		this.mimeType = TiMimeTypeHelper.getMimeTypeFromFileExtension(fileExtensions[0], "image/" + fileExtensions[0]);
	}

	/**
	 * Gets the unique integer ID that this Titanium module uses to represent this image format in JavaScript.
	 * @return Returns the image format's unique integer identifier.
	 */
	public int toTitaniumIntegerId()
	{
		return this.titaniumIntegerId;
	}

	/**
	 * Gets the Android "BitmapCompressFormat" enum ID for this image format.
	 * Used by the Java Bitmap.compress() method.
	 * @return Returns the Android compression format enum ID.
	 */
	public Bitmap.CompressFormat toCompressFormat()
	{
		return this.compressFormat;
	}

	/**
	 * Gets the mime type for this image format.
	 * @return Returns the mime type such as "image/png" or "image/jpeg".
	 */
	public String toMimeType()
	{
		return this.mimeType;
	}

	/**
	 * Determines if the image format supports an alpha channel.
	 * @return
	 * Returns true if image format supports alpha such as a PNG.
	 * <p/>
	 * Returns false if alpha is not supported such as with JPEGs.
	 */
	public boolean isAlphaSupported()
	{
		return (this != ImageFormatType.JPEG);
	}

	/**
	 * Gets the default file extension for the image format.
	 * @return Returns a file extension such as "png" or "jpg".
	 */
	public String getFileExtension()
	{
		return this.fileExtensions[0];
	}

	/**
	 * Gets the default file extension for the image format.
	 * @return Returns a file extension such as "png" or "jpg".
	 */
	@Override
	public String toString()
	{
		return getFileExtension();
	}

	/**
	 * Fetch an image format matching the given integer ID used by this Titanium module in JavaScript.
	 * @param value The unique integer ID used by the module in JavaScript.
	 * @return Returns the matching image format type. Returns null if given an invalid ID.
	 */
	public static ImageFormatType fromTitaniumIntegerId(int value)
	{
		for (ImageFormatType nextObject : ImageFormatType.values()) {
			if (nextObject.titaniumIntegerId == value) {
				return nextObject;
			}
		}
		return null;
	}

	/**
	 * Fetches an image format matching the given Android "CompressionFormat" enum ID.
	 * @param value The Anroid compression format constant. Can be null.
	 * @return Returns the matching image format type. Returns null if given an unknown ID or null.
	 */
	public static ImageFormatType from(Bitmap.CompressFormat value)
	{
		for (ImageFormatType nextObject : ImageFormatType.values()) {
			if (nextObject.compressFormat == value) {
				return nextObject;
			}
		}
		return null;
	}

	/**
	 * Fetches an image format matching the given file extension.
	 * @param fileExtension Image file extension such as "png", "jpg", or "jpeg". Not case sensitive. Can be null.
	 * @return
	 * Returns the matching image format type for the extension.
	 * <p/>
	 * Returns null if given an unknown extension or a null argument.
	 */
	public static ImageFormatType fromFileExtension(String fileExtension)
	{
		if (fileExtension == null) {
			return null;
		}

		fileExtension = fileExtension.toLowerCase();
		for (ImageFormatType nextType : ImageFormatType.values()) {
			for (String nextExtension : nextType.fileExtensions) {
				if (nextExtension.equals(fileExtension)) {
					return nextType;
				}
			}
		}
		return null;
	}

	/**
	 * Fetches an image format matching the given mime type.
	 * @param mimeType The mime type string such as "image/png", "image/jpeg", etc. Can be null.
	 * @return
	 * Returns the matching image format type for the given mime type.
	 * <p/>
	 * Returns null if given an unsupported mime type or a null argument.
	 */
	public static ImageFormatType fromMimeTye(String mimeType)
	{
		if (mimeType == null) {
			return null;
		}

		mimeType = mimeType.toLowerCase();
		for (ImageFormatType nextObject : ImageFormatType.values()) {
			if (nextObject.mimeType.equals(mimeType)) {
				return nextObject;
			}
		}
		return null;
	}

	/**
	 * Fetches an image format type from the given dictionary provided to this Titanium module's
	 * JavaScript methods. Will fetch the "format" entry from the dictionary and its value must
	 * be set to a value returned by this enum's toTitaniumIntegerId() method.
	 * @param options Dictionary containing a "format" entry. Can be null.
	 * @return
	 * Returns an image format type specified by the given "options" argument's "format" entry.
	 * <p/>
	 * Returns ImageFormatType.JPEG if given a null argument or if the "format" entry is missing.
	 */
	public static ImageFormatType from(KrollDict options)
	{
		return ImageFormatType.from(options, ImageFormatType.JPEG);
	}

	/**
	 * Fetches an image format type from the given dictionary provided to this Titanium module's
	 * JavaScript methods. Will fetch the "format" entry from the dictionary and its value must
	 * be set to a value returned by this enum's toTitaniumIntegerId() method.
	 * @param options Dictionary containing a "format" entry. Can be null.
	 * @param defaultType Image format type to return if "format" entry not found.
	 * @return
	 * Returns an image format type specified by the given "options" argument's "format" entry.
	 * <p/>
	 * Returns the "defaultType" argument if given argument is null or doesn not contain a "format" entry.
	 */
	public static ImageFormatType from(KrollDict options, ImageFormatType defaultType)
	{
		if (options == null) {
			return defaultType;
		}

		int titaniumIntegerId = TiConvert.toInt(options.get("format"), -1);
		ImageFormatType formatType = ImageFormatType.fromTitaniumIntegerId(titaniumIntegerId);
		if (formatType == null) {
			formatType = defaultType;
		}
		return formatType;
	}
}
