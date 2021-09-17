/**
 * Ti.Imagefactory Module
 * Copyright (c) 2011-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

#import "TiImagefactoryModule.h"
#import "TiBase.h"
#import "TiBlob.h"
#import "TiHost.h"
#import "TiImageFactory.h"
#import "TiUtils.h"

@implementation TiImagefactoryModule

#pragma mark Internal

// this is generated for your module, please do not change it
- (id)moduleGUID
{
  return @"0aab25d7-0486-40ab-94a3-ed4f9a293414";
}

// this is generated for your module, please do not change it
- (NSString *)moduleId
{
  return @"ti.imagefactory";
}

#pragma mark Lifecycle

- (void)startup
{
  // this method is called when the module is first loaded
  // you *must* call the superclass
  [super startup];

  NSLog(@"[DEBUG] %@ loaded", self);
}

#pragma system properties

typedef enum {
  kTransformNone = 0,
  kTransformCrop,
  kTransformResize,
  kTransformThumbnail,
  kTransformRoundedCorner,
  kTransformTransparentBorder,
  kTransformAlpha,
  kTransformRotate
} TransformType;

MAKE_SYSTEM_PROP(TRANSFORM_CROP, kTransformCrop);
MAKE_SYSTEM_PROP(TRANSFORM_RESIZE, kTransformResize);
MAKE_SYSTEM_PROP(TRANSFORM_THUMBNAIL, kTransformThumbnail);
MAKE_SYSTEM_PROP(TRANSFORM_ROUNDEDCORNER, kTransformRoundedCorner);
MAKE_SYSTEM_PROP(TRANSFORM_TRANSPARENTBORDER, kTransformTransparentBorder);
MAKE_SYSTEM_PROP(TRANSFORM_ALPHA, kTransformAlpha);
MAKE_SYSTEM_PROP(TRANSFORM_ROTATE, kTransformRotate);

MAKE_SYSTEM_PROP(QUALITY_DEFAULT, kCGInterpolationDefault);
MAKE_SYSTEM_PROP(QUALITY_NONE, kCGInterpolationNone);
MAKE_SYSTEM_PROP(QUALITY_LOW, kCGInterpolationLow);
MAKE_SYSTEM_PROP(QUALITY_MEDIUM, kCGInterpolationMedium);
MAKE_SYSTEM_PROP(QUALITY_HIGH, kCGInterpolationHigh);

#pragma mark Image Transform Helpers

+ (UIImage *)imageTransform:(TransformType)type image:(UIImage *)image withArgs:(id)args
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
  case kTransformRotate:
    return [TiImageFactory imageRotate:image withArgs:args];
  case kTransformNone:
  default:
    return image;
  }
}

+ (id)imageTransform:(TransformType)type withArgs:(id)args
{
  enum Args {
    kArgBlob = 0,
    kArgOptions,
    kArgCount
  };

  // Validate correct number of arguments
  ENSURE_ARG_COUNT(args, kArgCount);

  id blob = [args objectAtIndex:kArgBlob];
  ENSURE_TYPE(blob, TiBlob);
  UIImage *image = [TiImagefactoryModule imageTransform:type image:[(TiBlob *)blob image] withArgs:[args objectAtIndex:kArgOptions]];

  return image ? [[[TiBlob alloc] initWithImage:image] autorelease] : nil;
}

#pragma mark Public Image Methods

- (id)imageWithRotation:(id)args
{
  return [TiImagefactoryModule imageTransform:kTransformRotate withArgs:args];
}

- (id)imageWithAlpha:(id)args
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
  return [TiImagefactoryModule imageTransform:kTransformThumbnail withArgs:args];
}

- (id)imageAsResized:(id)args
{
  return [TiImagefactoryModule imageTransform:kTransformResize withArgs:args];
}

- (id)imageAsCropped:(id)args
{
  return [TiImagefactoryModule imageTransform:kTransformCrop withArgs:args];
}

- (id)imageAsUpright:(id)args
{
  ENSURE_SINGLE_ARG(args, TiBlob);
  TiBlob *blob = (TiBlob *)args;
  UIImage *sourceImage = [blob image];
  UIImage *newImage = [TiImageFactory imageUpright:sourceImage];
  if (newImage && (newImage != sourceImage)) {
    blob = [[[TiBlob alloc] initWithImage:newImage] autorelease];
  }
  return blob;
}

- (id)imageTransform:(id)args
{
  enum Args {
    kArgBlob = 0,
    kArgTransforms,
    kArgCount
  };

  // Validate correct number of arguments
  ENSURE_ARG_COUNT(args, kArgCount);

  id blob = [args objectAtIndex:kArgBlob];
  ENSURE_TYPE(blob, TiBlob);
  UIImage *image = [(TiBlob *)blob image];

  for (id transform in args) {
    if ([transform isKindOfClass:[NSDictionary class]]) {
      TransformType type = (TransformType)[TiUtils intValue:@"type" properties:transform def:kTransformNone];
      image = [TiImagefactoryModule imageTransform:type image:image withArgs:transform];
    }
  }

  return image ? [[[TiBlob alloc] initWithImage:image] autorelease] : nil;
}

- (id)compress:(id)args
{
  TiBlob *blob;
  NSNumber *qualityObject;
  ENSURE_ARG_AT_INDEX(blob, args, 0, TiBlob);
  ENSURE_ARG_AT_INDEX(qualityObject, args, 1, NSNumber);

  UIImage *image = [blob image];
  image = [TiImageFactory imageUpright:image];

  float qualityValue = [TiUtils floatValue:qualityObject def:1.0];
  return [[[TiBlob alloc] initWithData:UIImageJPEGRepresentation(image, qualityValue) mimetype:@"image/jpeg"] autorelease];
}

- (id)compressToFile:(id)args
{
  // Fetch arguments.
  TiBlob *blob;
  NSNumber *qualityObject;
  NSString *filePath;
  ENSURE_ARG_AT_INDEX(blob, args, 0, TiBlob);
  ENSURE_ARG_AT_INDEX(qualityObject, args, 1, NSNumber);
  ENSURE_ARG_AT_INDEX(filePath, args, 2, NSString);

  // Fetch blob's image in upright form.
  UIImage *image = [blob image];
  image = [TiImageFactory imageUpright:image];

  // Compress the image to the format specified by the given path's file extension.
  NSData *compressedData = nil;
  NSString *fileExtension = [filePath pathExtension];
  if ([fileExtension caseInsensitiveCompare:@"png"] == 0) {
    compressedData = UIImagePNGRepresentation(image);
  } else {
    float qualityValue = [TiUtils floatValue:qualityObject def:1.0];
    compressedData = UIImageJPEGRepresentation(image, qualityValue);
  }
  if (compressedData == nil) {
    return NUMBOOL(NO);
  }

  // Create the directory tree if it doesn't already exist.
  NSURL *fileUrl = [TiUtils toURL:filePath proxy:self];
  NSURL *parentDirUrl = [fileUrl URLByDeletingLastPathComponent];
  [NSFileManager.defaultManager createDirectoryAtURL:parentDirUrl withIntermediateDirectories:YES attributes:nil error:nil];

  // Write to compressed image data to file.
  NSError *err = nil;
  [compressedData writeToURL:fileUrl options:NSDataWritingAtomic error:&err];
  if (err != nil) {
    NSLog(@"[ERROR] ImageFactory.compressToFile() failed for path \"%@\" - reason: %@", fileUrl, err);
  }
  return NUMBOOL(err != nil);
}

@end
