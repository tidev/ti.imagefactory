let imagefactory;

const IOS = (Ti.Platform.osname === 'iphone' || Ti.Platform.osname === 'ipad');
const ANDROID = (Ti.Platform.osname === 'android');

describe('ti.imagefactory', function () {

	it('can be required', () => {
		imagefactory = require('ti.imagefactory');
		expect(imagefactory).toBeDefined();
	});

	describe('constants', () => {
		describe('TRANSFORM_*', () => {
			it('TRANSFORM_CROP', () => {
				expect(imagefactory.TRANSFORM_CROP).toEqual(jasmine.any(Number));
			});

			it('TRANSFORM_RESIZE', () => {
				expect(imagefactory.TRANSFORM_RESIZE).toEqual(jasmine.any(Number));
			});

			it('TRANSFORM_THUMBNAIL', () => {
				expect(imagefactory.TRANSFORM_THUMBNAIL).toEqual(jasmine.any(Number));
			});

			it('TRANSFORM_ROUNDEDCORNER', () => {
				expect(imagefactory.TRANSFORM_ROUNDEDCORNER).toEqual(jasmine.any(Number));
			});

			it('TRANSFORM_TRANSPARENTBORDER', () => {
				expect(imagefactory.TRANSFORM_TRANSPARENTBORDER).toEqual(jasmine.any(Number));
			});

			it('TRANSFORM_ALPHA', () => {
				expect(imagefactory.TRANSFORM_ALPHA).toEqual(jasmine.any(Number));
			});

			it('TRANSFORM_ROTATE', () => {
				expect(imagefactory.TRANSFORM_ROTATE).toEqual(jasmine.any(Number));
			});
		});

		describe('QUALITY_*', () => {
			it('QUALITY_DEFAULT', () => {
				expect(imagefactory.QUALITY_DEFAULT).toEqual(jasmine.any(Number));
			});

			it('QUALITY_NONE', () => {
				expect(imagefactory.QUALITY_NONE).toEqual(jasmine.any(Number));
			});

			it('QUALITY_LOW', () => {
				expect(imagefactory.QUALITY_LOW).toEqual(jasmine.any(Number));
			});

			it('QUALITY_MEDIUM', () => {
				expect(imagefactory.QUALITY_MEDIUM).toEqual(jasmine.any(Number));
			});

			it('QUALITY_HIGH', () => {
				expect(imagefactory.QUALITY_HIGH).toEqual(jasmine.any(Number));
			});
		});
	});

	describe('methods', () => {
		describe('imageWithRotation', () => {
			it('to rotate the image context',  function () {
				Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'flower.jpg.keep', function (fileSystem) {
					var image = fileSystem.read();
					var degree = 10.0;
					imagefactory.imageWithRotation(image, degree, function (newImage) {
						expect(newImage).toEqual(jasmine.any(Object));
					});
				});
			});
		});

		describe('imageWithAlpha',  () => {
			it('should have an alpha layer',  function () {
				Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'flower.jpg.keep', function (fileSystem) {
					var image = fileSystem.read();
					var newParam = { format: imagefactory.PNG };
					imagefactory.imageWithAlpha(image, newParam, function (newImage) {
						expect(newImage).toEqual(jasmine.any(Object));
					});
				});
			});
		});

		describe('imageWithTransparentBorder',  () => {
			it('to returns a copy of the image with a transparent border of the given size added around its edges',  function () {
				Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'flower.jpg.keep', function (fileSystem) {
					var image = fileSystem.read();
					var borderSize = 5;
					imagefactory.imageWithTransparentBorder(image, borderSize, function (newImage) {
						expect(newImage).toEqual(jasmine.any(Object));
					});
				});
			});
		});

		describe('imageAsCropped',  () => {
			it('to crop image as per given coordinates',  function () {
				Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'flower.jpg.keep', function (fileSystem) {
					var image = fileSystem.read();
					var newParam = { width: 100, height: 100, x: 50, y: 50 };
					imagefactory.imageAsCropped(image, newParam, function (newImage) {
						expect(newImage).toEqual(jasmine.any(Object));
					});
				});
			});
		});

		describe('imageAsResized',  () => {
			it('to resize the image for given width anf height', function () {
				Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'flower.jpg.keep', function (fileSystem) {
					var image = fileSystem.read();
					var newParam = { width: 140, height: 140 };
					imagefactory.imageAsResized(image, newParam, function (newImage) {
						expect(newImage).toEqual(jasmine.any(Object));
					});
				});
			});
		});

		describe('imageAsThumbnail',  () => {
			it('to create image thumbnail',  function () {
				Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'flower.jpg.keep', function (fileSystem) {
					var image = fileSystem.read();
					var newParam = { size: 64, borderSize: 5, cornerRadius: 10, format: imagefactory.PNG };
					imagefactory.imageAsThumbnail(image, newParam, function (newImage) {
						expect(newImage).toEqual(jasmine.any(Object));
					});
				});
			});
		});

		describe('imageWithRoundedCorner',  () => {
			it('to make the rounded corner image',  function () {
				Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'flower.jpg.keep', function (fileSystem) {
					var image = fileSystem.read();
					var newParam = { borderSize: 4, cornerRadius: 8, format: imagefactory.PNG };
					imagefactory.imageAsThumbnail(image, newParam, function (newImage) {
						expect(newImage).toEqual(jasmine.any(Object));
					});
				});
			});
		});
	});
});
