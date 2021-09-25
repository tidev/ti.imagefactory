/**
 * Ti.Imagefactory
 * Copyright (c) 2011-2021 by Axway, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */
/* globals OS_ANDROID, OS_IOS */

'use strict';

const ImageFactory = require('ti.imagefactory');

let navigationWindow = null;
const imageMap = {
	'PNG - Opaque': 'assets/images/IconOpaque.png',
	'PNG - Transparent': 'assets/images/IconTransparent.png',
	'JPEG - Upright': 'assets/images/ExifUpright.jpeg',
	'JPEG - EXIF Rotate 90': 'assets/images/ExifRotate90.jpeg',
	'JPEG - EXIF Rotate 180': 'assets/images/ExifRotate180.jpeg',
	'JPEG - EXIF Rotate 270': 'assets/images/ExifRotate270.jpeg',
	'JPEG - EXIF Flip Horizontal': 'assets/images/ExifFlipHorizontal.jpeg',
	'JPEG - EXIF Flip Vertical': 'assets/images/ExifFlipVertical.jpeg',
	'JPEG - EXIF Transpose': 'assets/images/ExifTranspose.jpeg',
	'JPEG - EXIF Transverse': 'assets/images/ExifTransverse.jpeg',
};
const parentWindow = Ti.UI.createWindow({ title: 'Images' });
const tableView = Ti.UI.createTableView({
	data: Object.keys(imageMap).map((title) => {
		return Ti.UI.createTableViewRow({ title: title });
	}),
});
tableView.addEventListener('click', (e) => {
	// Load the selected image as a blob.
	let sourceBlob = null;
	if (OS_IOS) {
		sourceBlob = Ti.Filesystem.getAsset(imageMap[e.row.title]);
	}
	if (!sourceBlob) {
		sourceBlob = Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, imageMap[e.row.title]).read();
	}

	// Displays the given blob's image metadata/exif info in a child window.
	function showMetadataWindowFor(imageBlob) {
		const metadataDictionary = ImageFactory.metadataFrom(imageBlob);
		const windowSettings = { title: 'Metadata' };
		if (OS_IOS) {
			windowSettings.backgroundColor = 'white';
		}
		const metadataWindow = Ti.UI.createWindow(windowSettings);
		const scrollView = Ti.UI.createScrollView({
			layout: 'vertical',
			scrollType: 'vertical',
			showVerticalScrollIndicator: true,
			width: Ti.UI.FILL,
			height: Ti.UI.FILL,
		});
		scrollView.add(Ti.UI.createLabel({
			text: '\n' + JSON.stringify(metadataDictionary, null, 4) + '\n',
			left: 10,
			right: 10,
		}));
		metadataWindow.add(scrollView);
		navigationWindow.openWindow(metadataWindow, { animated: true });
	}

	// Displays the given blob's image in a child window.
	function showImageWindowFor(imageBlob) {
		const windowSettings = { title: 'Resulting Image' };
		if (OS_IOS && imageBlob) {
			const metadataButton = Ti.UI.createButton({
				systemButton: Ti.UI.iOS.SystemButton.INFO_LIGHT,
			});
			metadataButton.addEventListener('click', () => {
				showMetadataWindowFor(imageBlob);
			});
			windowSettings.backgroundColor = 'white';
			windowSettings.rightNavButton = metadataButton;
		}
		const imageWindow = Ti.UI.createWindow(windowSettings);
		if (imageBlob) {
			imageWindow.add(Ti.UI.createImageView({ image: imageBlob, autorotate: false }));
			if (OS_ANDROID) {
				imageWindow.activity.onCreateOptionsMenu = (e) => {
					const menuItem = e.menu.add({
						title: 'View Metadata',
						showAsAction: Ti.Android.SHOW_AS_ACTION_NEVER,
					});
					menuItem.addEventListener('click', () => {
						showMetadataWindowFor(imageBlob);
					});
				};
			}
		} else {
			imageWindow.add(Ti.UI.createLabel({ text: 'Image factory returned null.' }));
		}
		navigationWindow.openWindow(imageWindow, { animated: true });
	}

	// Show a list of ImageFactory transformations to apply to selected image.
	const settingsWindow = Ti.UI.createWindow({ title: 'Transformations' });
	const tableView = Ti.UI.createTableView();
	{
		const row = Ti.UI.createTableViewRow({ title: 'None' });
		row.addEventListener('click', () => {
			showImageWindowFor(sourceBlob);
		});
		tableView.appendRow(row);
	}
	if (OS_ANDROID) {
		const row = Ti.UI.createTableViewRow({ title: 'compress() PNG' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.compress(sourceBlob, 1.0, ImageFactory.PNG));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'compress() JPEG 5% quality' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.compress(sourceBlob, 0.05));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'compress() JPEG 100% quality' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.compress(sourceBlob, 1.0));
		});
		tableView.appendRow(row);
	}
	if (OS_ANDROID) {
		const row = Ti.UI.createTableViewRow({ title: 'compress() WebP 5% quality' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.compress(sourceBlob, 0.05, ImageFactory.WEBP));
		});
		tableView.appendRow(row);
	}
	if (OS_ANDROID) {
		const row = Ti.UI.createTableViewRow({ title: 'compress() WebP 100% quality' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.compress(sourceBlob, 1.0, ImageFactory.WEBP));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'compressToFile() PNG' });
		row.addEventListener('click', () => {
			const file = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, 'CompressTest.png');
			ImageFactory.compressToFile(sourceBlob, 1.0, file.nativePath);
			showImageWindowFor(file.read());
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'compressToFile() JPEG 5% quality' });
		row.addEventListener('click', () => {
			const file = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, 'CompressTest.jpg');
			ImageFactory.compressToFile(sourceBlob, 0.05, file.nativePath);
			showImageWindowFor(file.read());
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'compressToFile() JPEG 100% quality' });
		row.addEventListener('click', () => {
			const file = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, 'CompressTest.jpg');
			ImageFactory.compressToFile(sourceBlob, 1.0, file.nativePath);
			showImageWindowFor(file.read());
		});
		tableView.appendRow(row);
	}
	if (OS_ANDROID) {
		const row = Ti.UI.createTableViewRow({ title: 'compressToFile() WebP 5% quality' });
		row.addEventListener('click', () => {
			const file = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, 'CompressTest.webp');
			ImageFactory.compressToFile(sourceBlob, 0.05, file.nativePath);
			showImageWindowFor(file.read());
		});
		tableView.appendRow(row);
	}
	if (OS_ANDROID) {
		const row = Ti.UI.createTableViewRow({ title: 'compressToFile() WebP 100% quality' });
		row.addEventListener('click', () => {
			const file = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, 'CompressTest.webp');
			ImageFactory.compressToFile(sourceBlob, 1.0, file.nativePath);
			showImageWindowFor(file.read());
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageAsUpright()' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.imageAsUpright(sourceBlob));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageAsResized() 320x320' });
		row.addEventListener('click', () => {
			const options = {
				width: 320,
				height: 320,
			};
			showImageWindowFor(ImageFactory.imageAsResized(sourceBlob, options));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageAsCropped() 320x320 center' });
		row.addEventListener('click', () => {
			const options = {
				width: 320,
				height: 320,
			};
			showImageWindowFor(ImageFactory.imageAsCropped(sourceBlob, options));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageAsCropped() bottom-left quadrant' });
		row.addEventListener('click', () => {
			const options = {
				x: 0,
				y: sourceBlob.uprightHeight / 2,
				width: sourceBlob.uprightWidth / 2,
				height: sourceBlob.uprightHeight / 2,
			};
			showImageWindowFor(ImageFactory.imageAsCropped(sourceBlob, options));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageAsCropped() top-right quadrant' });
		row.addEventListener('click', () => {
			const options = {
				x: sourceBlob.uprightWidth / 2,
				y: 0,
				width: sourceBlob.uprightWidth / 2,
				height: sourceBlob.uprightHeight / 2,
			};
			showImageWindowFor(ImageFactory.imageAsCropped(sourceBlob, options));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageWithRoundedCorner()' });
		row.addEventListener('click', () => {
			const options = {
				borderSize: 4,
				cornerRadius: 64,
				format: ImageFactory.PNG,
			};
			showImageWindowFor(ImageFactory.imageWithRoundedCorner(sourceBlob, options));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageWithTransparentBorder()' });
		row.addEventListener('click', () => {
			const options = {
				borderSize: 50,
				format: ImageFactory.PNG,
			};
			showImageWindowFor(ImageFactory.imageWithTransparentBorder(sourceBlob, options));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageAsThumbnail() 320x320 circle' });
		row.addEventListener('click', () => {
			const options = {
				size: 320,
				cornerRadius: 160,
				format: ImageFactory.PNG,
			};
			showImageWindowFor(ImageFactory.imageAsThumbnail(sourceBlob, options));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageAsThumbnail() 320x320 square' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.imageAsThumbnail(sourceBlob, { size: 320 }));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageWithAlpha()' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.imageWithAlpha(sourceBlob, { format: ImageFactory.PNG }));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageWithRotation() 0 degrees' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.imageWithRotation(sourceBlob, { degrees: 0 }));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageWithRotation() 45 clockwise' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.imageWithRotation(sourceBlob, { degrees: 45 }));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageWithRotation() 90 clockwise' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.imageWithRotation(sourceBlob, { degrees: 90 }));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageWithRotation() 180 clockwise' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.imageWithRotation(sourceBlob, { degrees: 180 }));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageWithRotation() 270 clockwise' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.imageWithRotation(sourceBlob, { degrees: 270 }));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageWithRotation() 90 counter-clockwise' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.imageWithRotation(sourceBlob, { degrees: -90 }));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageWithRotation() 180 counter-clockwise' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.imageWithRotation(sourceBlob, { degrees: -180 }));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageWithRotation() 270 counter-clockwise' });
		row.addEventListener('click', () => {
			showImageWindowFor(ImageFactory.imageWithRotation(sourceBlob, { degrees: -270 }));
		});
		tableView.appendRow(row);
	}
	{
		const row = Ti.UI.createTableViewRow({ title: 'imageTransform() crop/rounded/rotate-90' });
		row.addEventListener('click', () => {
			const options = [
				{
					type: ImageFactory.TRANSFORM_CROP,
					width: 320,
					height: 320,
				},
				{
					type: ImageFactory.TRANSFORM_ROTATE,
					degrees: 90,
				},
				{
					type: ImageFactory.TRANSFORM_ROUNDEDCORNER,
					borderSize: 4,
					cornerRadius: 64,
				},
			];
			showImageWindowFor(ImageFactory.imageTransform(sourceBlob, ...options));
		});
		tableView.appendRow(row);
	}
	settingsWindow.add(tableView);
	navigationWindow.openWindow(settingsWindow, { animated: true });
});
parentWindow.add(tableView);
navigationWindow = Ti.UI.createNavigationWindow({
	window: parentWindow,
});
navigationWindow.open();
