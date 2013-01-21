/**
 * Ti.Imagefactory Module
 * Copyright (c) 2011-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

#import "TiImagefactoryModule.h"
#import "TiBase.h"
#import "TiHost.h"
#import "TiUtils.h"
#import "TiImageFactory.h"

@implementation TiImagefactoryModule

#pragma mark Internal

// this is generated for your module, please do not change it
-(id)moduleGUID
{
	return @"0aab25d7-0486-40ab-94a3-ed4f9a293414";
}

// this is generated for your module, please do not change it
-(NSString*)moduleId
{
	return @"ti.imagefactory";
}

#pragma mark Lifecycle

-(void)startup
{
	// this method is called when the module is first loaded
	// you *must* call the superclass
	[super startup];
	
	NSLog(@"[INFO] %@ loaded",self);
}

-(void)shutdown:(id)sender
{
	// this method is called when the module is being unloaded
	// typically this is during shutdown. make sure you don't do too
	// much processing here or the app will be quit forceably
	
	// you *must* call the superclass
	[super shutdown:sender];
}

#pragma mark Cleanup 

-(void)dealloc
{
	// release any resources that have been retained by the module
	[super dealloc];
}

#pragma mark Internal Memory Management

-(void)didReceiveMemoryWarning:(NSNotification*)notification
{
	// optionally release any resources that can be dynamically
	// reloaded once memory is available - such as caches
	[super didReceiveMemoryWarning:notification];
}

#pragma system properties

typedef enum {
	kTransformNone = 0,
	kTransformCrop,
	kTransformResize,
	kTransformThumbnail,
	kTransformRoundedCorner,
	kTransformTransparentBorder,
	kTransformAlpha
} TransformType;

MAKE_SYSTEM_PROP(TRANSFORM_CROP,kTransformCrop);
MAKE_SYSTEM_PROP(TRANSFORM_RESIZE,kTransformResize);
MAKE_SYSTEM_PROP(TRANSFORM_THUMBNAIL,kTransformThumbnail);
MAKE_SYSTEM_PROP(TRANSFORM_ROUNDEDCORNER,kTransformRoundedCorner);
MAKE_SYSTEM_PROP(TRANSFORM_TRANSPARENTBORDER,kTransformTransparentBorder);
MAKE_SYSTEM_PROP(TRANSFORM_ALPHA,kTransformAlpha);

MAKE_SYSTEM_PROP(QUALITY_DEFAULT,kCGInterpolationDefault);
MAKE_SYSTEM_PROP(QUALITY_NONE,kCGInterpolationNone);
MAKE_SYSTEM_PROP(QUALITY_LOW,kCGInterpolationLow);
MAKE_SYSTEM_PROP(QUALITY_MEDIUM,kCGInterpolationMedium);
MAKE_SYSTEM_PROP(QUALITY_HIGH,kCGInterpolationHigh);

#pragma mark Image Transform Helpers

+(UIImage*)imageTransform:(TransformType)type image:(UIImage*)image withArgs:(id)args
{
	switch (type) {
		case kTransformCrop:
			return [TiImageFactory imageCrop:image withArgs:args];
		case kTransformResize:
			return [TiImageFactory imageResize:image withArgs:args];
		case kTransformThumbnail:
			return [TiImageFactory imageThumbnail:image withArgs:args];
		case kTransformRoundedCorner:
			return [TiImageFactory imageRoundedCorner:image withArgs:args];
		case kTransformTransparentBorder:
			return [TiImageFactory imageTransparentBorder:image withArgs:args];
		case kTransformAlpha:
			return [TiImageFactory imageAlpha:image withArgs:args];
	}
	
	return image;
}

+(id)imageTransform:(TransformType)type withArgs:(id)args
{
	enum Args {
		kArgBlob = 0,
		kArgOptions,
		kArgCount
	};
	
	// Validate correct number of arguments
	ENSURE_ARG_COUNT(args, kArgCount);
	
	id blob = [args objectAtIndex:kArgBlob];
	ENSURE_TYPE(blob,TiBlob);	
	UIImage* image = [TiImagefactoryModule imageTransform:type image:[(TiBlob *)blob image] withArgs:[args objectAtIndex:kArgOptions]];
	
	return image ? [[[TiBlob alloc] initWithImage:image] autorelease] : nil;
}


#pragma mark Public Image Methods

-(id)imageWithAlpha:(id)args
{
	return [TiImagefactoryModule imageTransform:kTransformAlpha withArgs:args];
}

- (id)imageWithTransparentBorder:(id)args
{
	return [TiImagefactoryModule imageTransform:kTransformTransparentBorder withArgs:args];
}

- (id)imageWithRoundedCorner:(id)args
{
	return [TiImagefactoryModule imageTransform:kTransformRoundedCorner withArgs:args];
}

- (id)imageAsThumbnail:(id)args
{
	return [TiImagefactoryModule imageTransform:kTransformThumbnail withArgs:args];}

-(id)imageAsResized:(id)args
{
	return [TiImagefactoryModule imageTransform:kTransformResize withArgs:args];
}

-(id)imageAsCropped:(id)args
{
	return [TiImagefactoryModule imageTransform:kTransformCrop withArgs:args];
}

-(id)imageTransform:(id)args
{
	enum Args {
		kArgBlob = 0,
		kArgTransforms,
		kArgCount
	};
	
	// Validate correct number of arguments
	ENSURE_ARG_COUNT(args, kArgCount);
	
	id blob = [args objectAtIndex:kArgBlob];
	ENSURE_TYPE(blob,TiBlob);
	UIImage* image = [(TiBlob*)blob image];
	
	for (id transform in args) {
		if ([transform isKindOfClass:[NSDictionary class]]) {
			TransformType type = (TransformType)[TiUtils intValue:@"type" properties:transform def:kTransformNone];
			image = [TiImagefactoryModule imageTransform:type image:image withArgs:transform];
		}
	}
	
	return image ? [[[TiBlob alloc] initWithImage:image] autorelease] : nil;
}

-(id)compress:(id)args
{
	enum Args {
		kArgBlob = 0,
		kArgCompressionQuality,
		kArgCount
	};
	
	// Validate correct number of argumetns
	ENSURE_ARG_COUNT(args, kArgCount);
	
	id blob = [args objectAtIndex:kArgBlob];
	ENSURE_TYPE(blob,TiBlob);
	UIImage* image = [(TiBlob*)blob image];
	
	float compressionQuality = [TiUtils floatValue:[args objectAtIndex:kArgCompressionQuality] def:1.0];
	return [[[TiBlob alloc] initWithData:UIImageJPEGRepresentation(image,compressionQuality) mimetype:@"image/jpeg"] autorelease];
}

@end
