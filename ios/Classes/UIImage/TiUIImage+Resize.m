// UIImage+Resize.m
// Created by Trevor Harmon on 8/5/09.
// Free for personal or commercial use, with or without modification.
// No warranty is expressed or implied.

#import "TiUIImage+Alpha.h"
#import "TiUIImage+Resize.h"
#import "TiUIImage+RoundedCorner.h"
#import "TiUtils.h"

@implementation TiUIImageResize

+ (BOOL)isIOS4OrGreater
{
  return [UIView instancesRespondToSelector:@selector(contentScaleFactor)];
}

// Returns a copy of the image that has been transformed using the given affine transform and scaled to the new size
// The new image's orientation will be UIImageOrientationUp, regardless of the current image's orientation
// If the new size is not integral, it will be rounded up
+ (UIImage *)resizedImage:(CGSize)newSize
                transform:(CGAffineTransform)transform
           drawTransposed:(BOOL)transpose
     interpolationQuality:(CGInterpolationQuality)quality
                    image:(UIImage *)image
                    hires:(BOOL)hires
{
  CGImageRef imageRef = image.CGImage;
  CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();

  CGFloat scale = 1.0;
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_4_0
  if ([TiUIImageResize isIOS4OrGreater]) {
    scale = [image scale];

    // Force scaling to 2x/3x
    if (hires) {
      if ([TiUtils is2xRetina]) {
        scale = 2.0;
      } else if ([TiUtils is3xRetina]) {
        scale = 3.0;
      }
    }
  }
#endif

  CGRect newRect = CGRectIntegral(CGRectMake(0, 0, newSize.width * scale, newSize.height * scale));
  CGRect transposedRect = CGRectMake(0, 0, newRect.size.height, newRect.size.width);

  // Build a context that's the same dimensions as the new size
  CGContextRef bitmap = CGBitmapContextCreate(NULL,
      newRect.size.width,
      newRect.size.height,
      8,
      0,
      colorSpace,
      kCGImageAlphaPremultipliedLast);

  // Rotate and/or flip the image if required by its orientation
  CGContextConcatCTM(bitmap, transform);

  // Set the quality level to use when rescaling
  CGContextSetInterpolationQuality(bitmap, quality);

  // Draw into the context; this scales the image
  CGContextDrawImage(bitmap, transpose ? transposedRect : newRect, imageRef);

  // Get the resized image from the context and a UIImage
  CGImageRef newImageRef = CGBitmapContextCreateImage(bitmap);
  UIImage *newImage = nil;
  if ([TiUIImageResize isIOS4OrGreater]) {
    newImage = [UIImage imageWithCGImage:newImageRef scale:scale orientation:UIImageOrientationUp];
  } else {
    newImage = [UIImage imageWithCGImage:newImageRef];
  }

  // Clean up
  CGContextRelease(bitmap);
  CGImageRelease(newImageRef);
  CGColorSpaceRelease(colorSpace);

  return newImage;
}

// Returns an affine transform that takes into account the image orientation when drawing a scaled image
+ (CGAffineTransform)transformForOrientation:(CGSize)newSize image:(UIImage *)image
{
  CGAffineTransform transform = CGAffineTransformIdentity;

  switch (image.imageOrientation) {
  case UIImageOrientationDown: // EXIF = 3
  case UIImageOrientationDownMirrored: // EXIF = 4
    transform = CGAffineTransformTranslate(transform, newSize.width, newSize.height);
    transform = CGAffineTransformRotate(transform, M_PI);
    break;

  case UIImageOrientationLeft: // EXIF = 6
  case UIImageOrientationLeftMirrored: // EXIF = 5
    transform = CGAffineTransformTranslate(transform, newSize.width, 0);
    transform = CGAffineTransformRotate(transform, M_PI_2);
    break;

  case UIImageOrientationRight: // EXIF = 8
  case UIImageOrientationRightMirrored: // EXIF = 7
    transform = CGAffineTransformTranslate(transform, 0, newSize.height);
    transform = CGAffineTransformRotate(transform, -M_PI_2);
    break;
  case UIImageOrientationUp:
    break;
  case UIImageOrientationUpMirrored:
    break;
  }

  switch (image.imageOrientation) {
  case UIImageOrientationUp:
    break;
  case UIImageOrientationDown:
    break;
  case UIImageOrientationLeft:
    break;
  case UIImageOrientationRight:
    break;
  case UIImageOrientationUpMirrored: // EXIF = 2
  case UIImageOrientationDownMirrored: // EXIF = 4
    transform = CGAffineTransformTranslate(transform, newSize.width, 0);
    transform = CGAffineTransformScale(transform, -1, 1);
    break;

  case UIImageOrientationLeftMirrored: // EXIF = 5
  case UIImageOrientationRightMirrored: // EXIF = 7
    transform = CGAffineTransformTranslate(transform, newSize.height, 0);
    transform = CGAffineTransformScale(transform, -1, 1);
    break;
  }

  return transform;
}

// Returns a copy of this image that is cropped to the given bounds.
// The bounds will be adjusted using CGRectIntegral.
// This method ignores the image's imageOrientation setting.
+ (UIImage *)croppedImage:(CGRect)bounds image:(UIImage *)image
{
  if (image == nil) {
    return nil;
  }
  CGImageRef imageRef = CGImageCreateWithImageInRect(image.CGImage, bounds);
  UIImage *croppedImage = [UIImage imageWithCGImage:imageRef];
  CGImageRelease(imageRef);
  return croppedImage;
}

// Returns a copy of this image that is squared to the thumbnail size.
// If transparentBorder is non-zero, a transparent border of the given size will be added around the edges of the thumbnail. (Adding a transparent border of at least one pixel in size has the side-effect of antialiasing the edges of the image when rotating it using Core Animation.)
+ (UIImage *)thumbnailImage:(NSInteger)thumbnailSize
          transparentBorder:(NSUInteger)borderSize
               cornerRadius:(NSUInteger)cornerRadius
       interpolationQuality:(CGInterpolationQuality)quality
                      image:(UIImage *)image
{
  UIImage *resizedImage = [TiUIImageResize resizedImageWithContentMode:UIViewContentModeScaleAspectFill
                                                                bounds:CGSizeMake(thumbnailSize, thumbnailSize)
                                                  interpolationQuality:quality
                                                                 image:image];

  // Crop out any part of the image that's larger than the thumbnail size
  // The cropped rect must be centered on the resized image
  // Round the origin points so that the size isn't altered when CGRectIntegral is later invoked
  CGRect cropRect = CGRectMake(round((resizedImage.size.width - thumbnailSize) / 2),
      round((resizedImage.size.height - thumbnailSize) / 2),
      thumbnailSize,
      thumbnailSize);
  UIImage *croppedImage = [TiUIImageResize croppedImage:cropRect image:resizedImage];

  UIImage *transparentBorderImage = borderSize ? [TiUIImageAlpha transparentBorderImage:borderSize image:croppedImage] : croppedImage;

  return [TiUIImageRoundedCorner roundedCornerImage:cornerRadius borderSize:borderSize image:transparentBorderImage];
}

// Returns a rescaled copy of the image, taking into account its orientation
// The image will be scaled disproportionately if necessary to fit the bounds specified by the parameter
+ (UIImage *)resizedImage:(CGSize)newSize
     interpolationQuality:(CGInterpolationQuality)quality
                    image:(UIImage *)image
                    hires:(BOOL)hires
{
  BOOL drawTransposed;

  switch (image.imageOrientation) {
  case UIImageOrientationLeft:
  case UIImageOrientationLeftMirrored:
  case UIImageOrientationRight:
  case UIImageOrientationRightMirrored:
    drawTransposed = YES;
    break;

  default:
    drawTransposed = NO;
  }

  return [TiUIImageResize resizedImage:newSize
                             transform:[TiUIImageResize transformForOrientation:newSize image:image]
                        drawTransposed:drawTransposed
                  interpolationQuality:quality
                                 image:image
                                 hires:hires];
}

// Resizes the image according to the given content mode, taking into account the image's orientation
+ (UIImage *)resizedImageWithContentMode:(UIViewContentMode)contentMode
                                  bounds:(CGSize)bounds
                    interpolationQuality:(CGInterpolationQuality)quality
                                   image:(UIImage *)image
{
  CGFloat horizontalRatio = bounds.width / image.size.width;
  CGFloat verticalRatio = bounds.height / image.size.height;
  CGFloat ratio;

  switch (contentMode) {
  case UIViewContentModeScaleAspectFill:
    ratio = MAX(horizontalRatio, verticalRatio);
    break;

  case UIViewContentModeScaleAspectFit:
    ratio = MIN(horizontalRatio, verticalRatio);
    break;

  default:
    [NSException raise:NSInvalidArgumentException format:@"Unsupported content mode: %ld", (long)contentMode];
  }

  CGSize newSize = CGSizeMake(image.size.width * ratio, image.size.height * ratio);

  return [TiUIImageResize resizedImage:newSize interpolationQuality:quality image:image hires:NO];
}

@end
