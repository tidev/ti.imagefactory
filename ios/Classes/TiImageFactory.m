/**
 * Ti.Imagefactory Module
 * Copyright (c) 2011-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

#import "TiImageFactory.h"
#import "TiUIImage+Alpha.h"
#import "TiUIImage+Resize.h"
#import "TiUIImage+Rotate.h"
#import "TiUIImage+RoundedCorner.h"
#import "TiUtils.h"

@implementation TiImageFactory

+ (UIImage *)imageCrop:(UIImage *)image withArgs:(NSDictionary *)args
{
  image = [TiImageFactory imageUpright:image];

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

+ (UIImage *)imageResize:(UIImage *)image withArgs:(NSDictionary *)args
{
  image = [TiImageFactory imageUpright:image];

  if (image) {
    CGSize imageSize = [image size];
    NSUInteger width = [TiUtils intValue:@"width" properties:args def:imageSize.width];
    NSUInteger height = [TiUtils intValue:@"height" properties:args def:imageSize.height];
    CGInterpolationQuality quality = MAX(MIN(kCGInterpolationDefault, [TiUtils intValue:@"quality" properties:args def:kCGInterpolationHigh]), kCGInterpolationHigh);
    BOOL hires = [TiUtils boolValue:@"hires" properties:args def:NO];

    return [TiUIImageResize resizedImage:CGSizeMake(width, height)
                    interpolationQuality:quality
                                   image:image
                                   hires:hires];
  }

  return nil;
}

+ (UIImage *)imageThumbnail:(UIImage *)image withArgs:(NSDictionary *)args
{
  image = [TiImageFactory imageUpright:image];

  if (image) {
    NSInteger size = [TiUtils intValue:@"size" properties:args def:48];
    NSUInteger borderSize = [TiUtils intValue:@"borderSize" properties:args def:1];
    NSUInteger cornerRadius = [TiUtils intValue:@"cornerRadius" properties:args def:0];
    CGInterpolationQuality quality = MAX(MIN(kCGInterpolationDefault, [TiUtils intValue:@"quality" properties:args def:kCGInterpolationHigh]), kCGInterpolationHigh);

    return [TiUIImageResize thumbnailImage:size
                         transparentBorder:borderSize
                              cornerRadius:cornerRadius
                      interpolationQuality:quality
                                     image:image];
  }

  return nil;
}

+ (UIImage *)imageRoundedCorner:(UIImage *)image withArgs:(NSDictionary *)args
{
  image = [TiImageFactory imageUpright:image];

  if (image) {
    NSUInteger borderSize = [TiUtils intValue:@"borderSize" properties:args def:1];
    NSUInteger cornerRadius = [TiUtils intValue:@"cornerRadius" properties:args def:0];

    return [TiUIImageRoundedCorner roundedCornerImage:cornerRadius
                                           borderSize:borderSize
                                                image:image];
  }

  return nil;
}

+ (UIImage *)imageTransparentBorder:(UIImage *)image withArgs:(NSDictionary *)args
{
  image = [TiImageFactory imageUpright:image];

  if (image) {
    NSUInteger size = [TiUtils intValue:@"borderSize" properties:args def:1];

    return [TiUIImageAlpha transparentBorderImage:size
                                            image:image];
  }

  return nil;
}

+ (UIImage *)imageAlpha:(UIImage *)image withArgs:(NSDictionary *)args
{
  image = [TiImageFactory imageUpright:image];

  if (image) {
    return [TiUIImageAlpha imageWithAlpha:image];
  }

  return nil;
}

+ (UIImage *)imageRotate:(UIImage *)image withArgs:(NSDictionary *)args
{
  image = [TiImageFactory imageUpright:image];

  if (image) {
    NSInteger degrees = [TiUtils intValue:@"degrees" properties:args def:1];

    return [TiUIImageRotate rotateImage:degrees image:image];
  }

  return nil;
}

+ (UIImage *)imageUpright:(UIImage *)image
{
  if (!image) {
    return nil;
  }

  UIImageOrientation orientation = image.imageOrientation;
  if (orientation == UIImageOrientationUp) {
    return image;
  }

  BOOL isSideways = NO;
  CGImageRef imgRef = image.CGImage;
  CGFloat width = CGImageGetWidth(imgRef);
  CGFloat height = CGImageGetHeight(imgRef);
  CGAffineTransform transform = CGAffineTransformIdentity;
  CGRect bounds = CGRectMake(0, 0, width, height);
  CGFloat scaleRatio = bounds.size.width / width;
  CGSize imageSize = CGSizeMake(CGImageGetWidth(imgRef), CGImageGetHeight(imgRef));
  CGFloat boundHeight;
  switch (orientation) {
  case UIImageOrientationUpMirrored:
    transform = CGAffineTransformMakeTranslation(imageSize.width, 0.0);
    transform = CGAffineTransformScale(transform, -1.0, 1.0);
    break;

  case UIImageOrientationDown:
    transform = CGAffineTransformMakeTranslation(imageSize.width, imageSize.height);
    transform = CGAffineTransformRotate(transform, M_PI);
    break;

  case UIImageOrientationDownMirrored:
    transform = CGAffineTransformMakeTranslation(0.0, imageSize.height);
    transform = CGAffineTransformScale(transform, 1.0, -1.0);
    break;

  case UIImageOrientationLeft:
  case UIImageOrientationLeftMirrored:
    isSideways = YES;
    boundHeight = bounds.size.height;
    bounds.size.height = bounds.size.width;
    bounds.size.width = boundHeight;
    transform = CGAffineTransformMakeTranslation(0.0, imageSize.width);
    transform = CGAffineTransformRotate(transform, 3.0 * M_PI / 2.0);
    if (orientation == UIImageOrientationLeftMirrored) {
      transform = CGAffineTransformTranslate(transform, imageSize.width, 0.0);
      transform = CGAffineTransformScale(transform, -1.0, 1.0);
    }
    break;

  case UIImageOrientationRight:
  case UIImageOrientationRightMirrored:
    isSideways = YES;
    boundHeight = bounds.size.height;
    bounds.size.height = bounds.size.width;
    bounds.size.width = boundHeight;
    transform = CGAffineTransformMakeTranslation(imageSize.height, 0.0);
    transform = CGAffineTransformRotate(transform, M_PI / 2.0);
    if (orientation == UIImageOrientationRightMirrored) {
      transform = CGAffineTransformTranslate(transform, imageSize.width, 0.0);
      transform = CGAffineTransformScale(transform, -1.0, 1.0);
    }
    break;

  case UIImageOrientationUp:
  default:
    return image;
  }

  UIGraphicsBeginImageContext(bounds.size);
  CGContextRef context = UIGraphicsGetCurrentContext();
  if (isSideways) {
    CGContextScaleCTM(context, -scaleRatio, scaleRatio);
    CGContextTranslateCTM(context, -height, 0);
  } else {
    CGContextScaleCTM(context, scaleRatio, -scaleRatio);
    CGContextTranslateCTM(context, 0, -height);
  }
  CGContextConcatCTM(context, transform);
  CGContextDrawImage(UIGraphicsGetCurrentContext(), CGRectMake(0, 0, width, height), imgRef);
  UIImage *imageCopy = UIGraphicsGetImageFromCurrentImageContext();
  UIGraphicsEndImageContext();

  return imageCopy;
}

@end
