/**
 * Ti.Imagefactory Module
 * Copyright (c) 2011-2021 by Axway, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.imagefactory;

import androidx.exifinterface.media.ExifInterface;
import java.io.File;
import java.io.InputStream;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.util.TiConvert;

/** Reads and writes image EXIF metadata. */
public class ImageMetadata
{
	private static final String TAG = "ImageMetadata";

	private static EntryDescriptor[] entryDescriptors = {
		new DoubleEntryDescriptor(ExifInterface.TAG_APERTURE_VALUE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_ARTIST, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_BITS_PER_SAMPLE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_BODY_SERIAL_NUMBER, ""),
		new DoubleEntryDescriptor(ExifInterface.TAG_BRIGHTNESS_VALUE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_CAMERA_OWNER_NAME, ""),
		new StringEntryDescriptor(ExifInterface.TAG_CFA_PATTERN, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_COLOR_SPACE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_COMPONENTS_CONFIGURATION, ""),
		new DoubleEntryDescriptor(ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_COMPRESSION, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_CONTRAST, ExifInterface.CONTRAST_NORMAL),
		new StringEntryDescriptor(ExifInterface.TAG_COPYRIGHT, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_CUSTOM_RENDERED, ExifInterface.RENDERED_PROCESS_NORMAL),
		new StringEntryDescriptor(ExifInterface.TAG_DATETIME, ""),
		new StringEntryDescriptor(ExifInterface.TAG_DATETIME_DIGITIZED, ""),
		new StringEntryDescriptor(ExifInterface.TAG_DATETIME_ORIGINAL, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_DEFAULT_CROP_SIZE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION, ""),
		new DoubleEntryDescriptor(ExifInterface.TAG_DIGITAL_ZOOM_RATIO, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_DNG_VERSION, 0),
		new StringEntryDescriptor(ExifInterface.TAG_EXIF_VERSION, "0230"),
		new DoubleEntryDescriptor(ExifInterface.TAG_EXPOSURE_BIAS_VALUE, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_EXPOSURE_INDEX, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_EXPOSURE_MODE, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_EXPOSURE_PROGRAM, ExifInterface.EXPOSURE_PROGRAM_NOT_DEFINED),
		new DoubleEntryDescriptor(ExifInterface.TAG_EXPOSURE_TIME, 0),
		new StringEntryDescriptor(ExifInterface.TAG_FILE_SOURCE, Integer.toString(ExifInterface.FILE_SOURCE_DSC)),
		new IntegerEntryDescriptor(ExifInterface.TAG_FLASH, 0),
		new StringEntryDescriptor(ExifInterface.TAG_FLASHPIX_VERSION, "0100"),
		new DoubleEntryDescriptor(ExifInterface.TAG_FLASH_ENERGY, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_FOCAL_LENGTH, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_FOCAL_PLANE_RESOLUTION_UNIT, ExifInterface.RESOLUTION_UNIT_INCHES),
		new DoubleEntryDescriptor(ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_F_NUMBER, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_GAIN_CONTROL, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_GAMMA, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_GPS_ALTITUDE, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_GPS_ALTITUDE_REF, 0),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_AREA_INFORMATION, ""),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_DATESTAMP, ""),
		new DoubleEntryDescriptor(ExifInterface.TAG_GPS_DEST_BEARING, 0),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_DEST_BEARING_REF, ExifInterface.GPS_DIRECTION_TRUE),
		new DoubleEntryDescriptor(ExifInterface.TAG_GPS_DEST_DISTANCE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_DEST_DISTANCE_REF, ExifInterface.GPS_DISTANCE_KILOMETERS),
		new DoubleEntryDescriptor(ExifInterface.TAG_GPS_DEST_LATITUDE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_DEST_LATITUDE_REF, ""),
		new DoubleEntryDescriptor(ExifInterface.TAG_GPS_DEST_LONGITUDE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_DEST_LONGITUDE_REF, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_GPS_DIFFERENTIAL, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_GPS_DOP, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_GPS_H_POSITIONING_ERROR, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_GPS_IMG_DIRECTION, 0),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_IMG_DIRECTION_REF, ExifInterface.GPS_DIRECTION_TRUE),
		new DoubleEntryDescriptor(ExifInterface.TAG_GPS_LATITUDE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_LATITUDE_REF, ""),
		new DoubleEntryDescriptor(ExifInterface.TAG_GPS_LONGITUDE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_LONGITUDE_REF, ""),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_MAP_DATUM, ""),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_MEASURE_MODE, ""),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_PROCESSING_METHOD, ""),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_SATELLITES, ""),
		new DoubleEntryDescriptor(ExifInterface.TAG_GPS_SPEED, 0),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_SPEED_REF, ExifInterface.GPS_SPEED_KILOMETERS_PER_HOUR),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_STATUS, ""),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_TIMESTAMP, ""),
		new DoubleEntryDescriptor(ExifInterface.TAG_GPS_TRACK, 0),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_TRACK_REF, ExifInterface.GPS_DIRECTION_TRUE),
		new StringEntryDescriptor(ExifInterface.TAG_GPS_VERSION_ID, ""),
		new StringEntryDescriptor(ExifInterface.TAG_IMAGE_DESCRIPTION, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_IMAGE_LENGTH, 0, 1),
		new StringEntryDescriptor(ExifInterface.TAG_IMAGE_UNIQUE_ID, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_IMAGE_WIDTH, 0, 1),
		new StringEntryDescriptor(ExifInterface.TAG_INTEROPERABILITY_INDEX, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_ISO_SPEED, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_ISO_SPEED_LATITUDE_YYY, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_ISO_SPEED_LATITUDE_ZZZ, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH, 0, 1),
		new StringEntryDescriptor(ExifInterface.TAG_LENS_MAKE, ""),
		new StringEntryDescriptor(ExifInterface.TAG_LENS_MODEL, ""),
		new StringEntryDescriptor(ExifInterface.TAG_LENS_SERIAL_NUMBER, ""),
		new DoubleEntryDescriptor(ExifInterface.TAG_LENS_SPECIFICATION, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_LIGHT_SOURCE, ExifInterface.LIGHT_SOURCE_UNKNOWN, 1),
		new StringEntryDescriptor(ExifInterface.TAG_MAKE, ""),
		new StringEntryDescriptor(ExifInterface.TAG_MAKER_NOTE, ""),
		new DoubleEntryDescriptor(ExifInterface.TAG_MAX_APERTURE_VALUE, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_METERING_MODE, ExifInterface.METERING_MODE_UNKNOWN),
		new StringEntryDescriptor(ExifInterface.TAG_MODEL, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_NEW_SUBFILE_TYPE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_OECF, ""),
		new StringEntryDescriptor(ExifInterface.TAG_OFFSET_TIME, ""),
		new StringEntryDescriptor(ExifInterface.TAG_OFFSET_TIME_DIGITIZED, ""),
		new StringEntryDescriptor(ExifInterface.TAG_OFFSET_TIME_ORIGINAL, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_ORF_ASPECT_FRAME, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_ORF_PREVIEW_IMAGE_LENGTH, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_ORF_PREVIEW_IMAGE_START, 0),
		new BlobEntryDescriptor(ExifInterface.TAG_ORF_THUMBNAIL_IMAGE),
		new IntegerEntryDescriptor(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL, 1),
		new IntegerEntryDescriptor(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_PIXEL_X_DIMENSION, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_PIXEL_Y_DIMENSION, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_PLANAR_CONFIGURATION, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_PRIMARY_CHROMATICITIES, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_RECOMMENDED_EXPOSURE_INDEX, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_REFERENCE_BLACK_WHITE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_RELATED_SOUND_FILE, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_RESOLUTION_UNIT, ExifInterface.RESOLUTION_UNIT_INCHES),
		new IntegerEntryDescriptor(ExifInterface.TAG_ROWS_PER_STRIP, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_RW2_ISO, 0),
		new StringEntryDescriptor(ExifInterface.TAG_RW2_JPG_FROM_RAW, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_RW2_SENSOR_BOTTOM_BORDER, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_RW2_SENSOR_LEFT_BORDER, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_RW2_SENSOR_RIGHT_BORDER, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_RW2_SENSOR_TOP_BORDER, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_SAMPLES_PER_PIXEL, 3),
		new IntegerEntryDescriptor(ExifInterface.TAG_SATURATION, ExifInterface.SATURATION_NORMAL),
		new IntegerEntryDescriptor(ExifInterface.TAG_SCENE_CAPTURE_TYPE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_SCENE_TYPE, "1"),
		new IntegerEntryDescriptor(ExifInterface.TAG_SENSING_METHOD, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_SENSITIVITY_TYPE, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_SHARPNESS, ExifInterface.SHARPNESS_NORMAL),
		new DoubleEntryDescriptor(ExifInterface.TAG_SHUTTER_SPEED_VALUE, 0),
		new StringEntryDescriptor(ExifInterface.TAG_SOFTWARE, ""),
		new StringEntryDescriptor(ExifInterface.TAG_SPATIAL_FREQUENCY_RESPONSE, ""),
		new StringEntryDescriptor(ExifInterface.TAG_SPECTRAL_SENSITIVITY, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_STANDARD_OUTPUT_SENSITIVITY, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_STRIP_BYTE_COUNTS, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_STRIP_OFFSETS, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_SUBFILE_TYPE, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_SUBJECT_AREA, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_SUBJECT_DISTANCE, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_SUBJECT_DISTANCE_RANGE, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_SUBJECT_LOCATION, 0),
		new StringEntryDescriptor(ExifInterface.TAG_SUBSEC_TIME, ""),
		new StringEntryDescriptor(ExifInterface.TAG_SUBSEC_TIME_DIGITIZED, ""),
		new StringEntryDescriptor(ExifInterface.TAG_SUBSEC_TIME_ORIGINAL, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_THUMBNAIL_IMAGE_LENGTH, 0, 1),
		new IntegerEntryDescriptor(ExifInterface.TAG_THUMBNAIL_IMAGE_WIDTH, 0, 1),
		new StringEntryDescriptor(ExifInterface.TAG_TRANSFER_FUNCTION, ""),
		new StringEntryDescriptor(ExifInterface.TAG_USER_COMMENT, ""),
		new IntegerEntryDescriptor(ExifInterface.TAG_WHITE_BALANCE, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_WHITE_POINT, 0),
		new StringEntryDescriptor(ExifInterface.TAG_XMP, ""),
		new DoubleEntryDescriptor(ExifInterface.TAG_X_RESOLUTION, 72.0),
		new DoubleEntryDescriptor(ExifInterface.TAG_Y_CB_CR_COEFFICIENTS, 0),
		new IntegerEntryDescriptor(ExifInterface.TAG_Y_CB_CR_POSITIONING, ExifInterface.Y_CB_CR_POSITIONING_CENTERED),
		new IntegerEntryDescriptor(ExifInterface.TAG_Y_CB_CR_SUB_SAMPLING, 0),
		new DoubleEntryDescriptor(ExifInterface.TAG_Y_RESOLUTION, 72.0),
	};

	/** Dictionary of EXIF metadata entries. */
	private final KrollDict entries = new KrollDict();

	/** Creates a new object containing no m */
	public ImageMetadata()
	{
	}

	/**
	 * Gets a mutable dictionary of EXIF metadata. You can add/remove entries into this dictionary.
	 * Expected to use Google's "ExifInterface.TAG_*" constants as the keys.
	 * @return Returns a dicationary of EXIF metadata.
	 */
	public KrollDict getEntries()
	{
		return this.entries;
	}

	/**
	 * Inserts the metadata entries returned by getEntries() into the given image file.
	 * Will preserve image file's existing entries unless getEntries() overwrites them.
	 * @param file An existing image file to insert metadata to. Can be null.
	 * @return
	 * Returns true if successfully wrote metadata to the given file.
	 * Returns false if given a null argument, if file doesn't exist, or unable to write to file.
	 */
	public boolean insertInto(File file)
	{
		// Validate argument.
		if (file == null) {
			return false;
		}

		// Do not continue if there is no metadata to write.
		if (this.entries.size() <= 0) {
			return true;
		}

		// Insert all metadata entries into image file.
		boolean wasSuccessful = false;
		try {
			ExifInterface exif = new ExifInterface(file);
			for (KrollDict.Entry<String, Object> entry : this.entries.entrySet()) {
				exif.setAttribute(entry.getKey(), TiConvert.toString(entry.getValue(), null));
			}
			exif.saveAttributes();
			wasSuccessful = true;
		} catch (Exception ex) {
			Log.e(TAG, "Failed to write metadata to file: " + file, ex);
		}
		return wasSuccessful;
	}

	/**
	 * Reads EXIF from given blob's image and returns a new ImageMetadata object providing the read EXIF entries.
	 * @param blob A blob which references an image. Can be null.
	 * @return
	 * Returns a new ImageMetadata instance if successfully read EXIF from blob's image.
	 * Returns null if given a null argument, blob doesn't reference an image, or failed to read image's metadata.
	 */
	public static ImageMetadata from(TiBlob blob)
	{
		// Validate argument.
		if (blob == null) {
			return null;
		}

		// Read all metadata from blob's image file.
		try (InputStream stream = blob.getInputStream()) {
			return ImageMetadata.from(stream);
		} catch (Exception ex) {
			Log.e(TAG, "Failed to fetch image metadata from blob.", ex);
		}
		return null;
	}

	/**
	 * Reads EXIF from given image file stream and returns a new ImageMetadata object providing the read EXIF entries.
	 * The caller is expected to close the given stream.
	 * @param stream The input stream to read image EXIF metadata from. Can be null.
	 * @return
	 * Returns a new ImageMetadata instance if successfully read EXIF from image.
	 * Returns null if given a null argument, stream doesn't reference an image, or failed to read image's metadata.
	 */
	public static ImageMetadata from(InputStream stream)
	{
		// Validate argument.
		if (stream == null) {
			return null;
		}

		// Read all image metadata from given input stream.
		ImageMetadata metadata = null;
		try {
			ExifInterface exif = new ExifInterface(stream);
			metadata = new ImageMetadata();
			for (EntryDescriptor nextDescriptor : ImageMetadata.entryDescriptors) {
				nextDescriptor.copyToDictionary(exif, metadata.entries);
			}
		} catch (Exception ex) {
			Log.e(TAG, "Failed to fetch image metadata from stream.", ex);
		}
		return metadata;
	}

	/** Describes an EXIF entry and provides a mean to read its deta. */
	private static abstract class EntryDescriptor
	{
		private String tagName;

		protected EntryDescriptor(String tagName)
		{
			this.tagName = tagName;
		}

		public String getTagName()
		{
			return this.tagName;
		}

		public void copyToDictionary(ExifInterface exif, KrollDict entries)
		{
			try {
				onCopyToDictionary(exif, entries);
			} catch (Exception ex) {
				Log.e(TAG, "Failed to read image metadata tag: " + this.tagName, ex);
			}
		}

		protected abstract void onCopyToDictionary(ExifInterface exif, KrollDict entries);
	}

	/** Describes an EXIF entry with a string value. */
	private static class StringEntryDescriptor extends EntryDescriptor
	{
		private String defaultValue;

		public StringEntryDescriptor(String tagName, String defaultValue)
		{
			super(tagName);
			this.defaultValue = defaultValue;
		}

		protected void onCopyToDictionary(ExifInterface exif, KrollDict entries)
		{
			if (exif.hasAttribute(getTagName())) {
				String value = exif.getAttribute(getTagName());
				entries.put(getTagName(), (value != null) ? value : this.defaultValue);
			}
		}
	}

	/** Describes an EXIF entry with an integer value. */
	private static class IntegerEntryDescriptor extends EntryDescriptor
	{
		private int defaultValue;
		private int minValue;

		public IntegerEntryDescriptor(String tagName, int defaultValue)
		{
			this(tagName, defaultValue, Integer.MIN_VALUE);
		}

		public IntegerEntryDescriptor(String tagName, int defaultValue, int minValue)
		{
			super(tagName);
			this.defaultValue = defaultValue;
			this.minValue = minValue;
		}

		protected void onCopyToDictionary(ExifInterface exif, KrollDict entries)
		{
			if (exif.hasAttribute(getTagName())) {
				int value = exif.getAttributeInt(getTagName(), this.defaultValue);
				if (value >= this.minValue) {
					entries.put(getTagName(), value);
				}
			}
		}
	}

	/** Describes an EXIF entry with a rationale (aka: floating point) value. */
	private static class DoubleEntryDescriptor extends EntryDescriptor
	{
		private double defaultValue;

		public DoubleEntryDescriptor(String tagName, double defaultValue)
		{
			super(tagName);
			this.defaultValue = defaultValue;
		}

		protected void onCopyToDictionary(ExifInterface exif, KrollDict entries)
		{
			if (exif.hasAttribute(getTagName())) {
				double value = exif.getAttributeDouble(getTagName(), this.defaultValue);
				entries.put(getTagName(), value);
			}
		}
	}

	/** Describes an EXIF entry with a byte array value. */
	private static class BlobEntryDescriptor extends EntryDescriptor
	{
		public BlobEntryDescriptor(String tagName)
		{
			super(tagName);
		}

		protected void onCopyToDictionary(ExifInterface exif, KrollDict entries)
		{
			if (exif.hasAttribute(getTagName())) {
				byte[] binary = exif.getAttributeBytes(getTagName());
				if (binary != null) {
					entries.put(getTagName(), TiBlob.blobFromData(binary));
				}
			}
		}
	}
}
