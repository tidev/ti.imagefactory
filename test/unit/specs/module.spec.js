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
});
