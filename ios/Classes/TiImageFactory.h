/**
 * Ti.Imagefactory Module
 * Copyright (c) 2011-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

@interface TiImageFactory : NSObject {
}

+(UIImage*)imageCrop:(UIImage*)image withArgs:(NSDictionary*)args;
+(UIImage*)imageResize:(UIImage*)image withArgs:(NSDictionary*)args;
+(UIImage*)imageThumbnail:(UIImage*)image withArgs:(NSDictionary*)args;
+(UIImage*)imageRoundedCorner:(UIImage*)image withArgs:(NSDictionary*)args;
+(UIImage*)imageTransparentBorder:(UIImage*)image withArgs:(NSDictionary*)args;
+(UIImage*)imageAlpha:(UIImage*)image withArgs:(NSDictionary*)args;

@end
