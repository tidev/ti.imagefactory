/**
 * Ti.Imagefactory Module
 * Copyright (c) 2011-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

#import "TiImageFactory.h"
#import "TiUtils.h"
#import "TiUIImage+Resize.h"
#import "TiUIImage+RoundedCorner.h"
#import "TiUIImage+Alpha.h"

@implementation TiImageFactory

+(UIImage*)imageCrop:(UIImage*)image withArgs:(NSDictionary*) args
{
	if (image) {
		CGRect bounds;
		CGSize imageSize = [image size];
		bounds.size.width = [TiUtils floatValue:@"width" properties:args def:imageSize.width];
		bounds.size.height = [TiUtils floatValue:@"height" properties:args def:imageSize.height];
		bounds.origin.x = [TiUtils floatValue:@"x" properties:args def:(imageSize.width - bounds.size.width) / 2.0];
		bounds.origin.y = [TiUtils floatValue:@"y" properties:args def:(imageSize.height - bounds.size.height) / 2.0];
		
		return [TiUIImageResize croppedImage:bounds 
									 image:image];
	}
	
	return nil;
}

+(UIImage*)imageResize:(UIImage*)image withArgs:(NSDictionary*) args
{
	if (image) {
		CGSize imageSize = [image size];
		NSUInteger width = [TiUtils intValue:@"width" properties:args def:imageSize.width];
		NSUInteger height = [TiUtils intValue:@"height" properties:args def:imageSize.height];
		CGInterpolationQuality quality = MAX(MIN(kCGInterpolationDefault,[TiUtils intValue:@"quality" properties:args def:kCGInterpolationHigh]),kCGInterpolationHigh);
		BOOL hires = [TiUtils boolValue:@"hires" properties:args def:NO];
		
		return [TiUIImageResize resizedImage:CGSizeMake(width,height) 
					  interpolationQuality:quality 
									 image:image 
									 hires:hires];
	}
	
	return nil;
}

+(UIImage*)imageThumbnail:(UIImage*)image withArgs:(NSDictionary*)args
{
	if (image) {
		NSInteger size = [TiUtils intValue:@"size" properties:args def:48];
		NSUInteger borderSize = [TiUtils intValue:@"borderSize" properties:args def:1];
		NSUInteger cornerRadius = [TiUtils intValue:@"cornerRadius" properties:args def:0];
		CGInterpolationQuality quality = MAX(MIN(kCGInterpolationDefault,[TiUtils intValue:@"quality" properties:args def:kCGInterpolationHigh]),kCGInterpolationHigh);
		
		return [TiUIImageResize thumbnailImage:size
						   transparentBorder:borderSize
								cornerRadius:cornerRadius
						interpolationQuality:quality
									   image:image];
	}
	
	return nil;
}

+(UIImage*)imageRoundedCorner:(UIImage*)image withArgs:(NSDictionary*)args
{
	if (image) {
		NSUInteger borderSize = [TiUtils intValue:@"borderSize" properties:args def:1];
		NSUInteger cornerRadius = [TiUtils intValue:@"cornerRadius" properties:args def:0];
		
		return [TiUIImageRoundedCorner roundedCornerImage:cornerRadius
											 borderSize:borderSize
												  image:image];
	}
	
	return nil;
}

+(UIImage*)imageTransparentBorder:(UIImage*)image withArgs:(NSDictionary*)args
{
	if (image) {
		NSUInteger size = [TiUtils intValue:@"borderSize" properties:args def:1];
		
		return [TiUIImageAlpha transparentBorderImage:size
											  image:image];
	}
	
	return nil;
}

+(UIImage*)imageAlpha:(UIImage*)image withArgs:(NSDictionary*)args
{
	if (image) {
		return [TiUIImageAlpha imageWithAlpha:image];
	}
	
	return nil;
}

@end
