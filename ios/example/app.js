var window = Ti.UI.createWindow({
    backgroundColor: 'white'
});

var imageView = Titanium.UI.createImageView({
    image: 'images/flower.jpg',
    top: 4,
    left: 4,
    width: Ti.UI.SIZE || 'auto',
    height: Ti.UI.SIZE || 'auto'
});

window.add(imageView);

var btnTransform = Titanium.UI.createButton({
    title: 'Next', zIndex: 1,
    bottom: 4, left: 4,
    width: '40%', height: 60
});
window.add(btnTransform);

var btnSave = Titanium.UI.createButton({
    title: 'Save', zIndex: 1,
    right: 4, bottom: 4,
    width: '40%', height: 60
});
window.add(btnSave);

var imageViewTransformed = Titanium.UI.createImageView({
    top: 221,
    left: 4,
    width: Ti.UI.SIZE || 'auto',
    height: Ti.UI.SIZE || 'auto'
});

window.add(imageViewTransformed);
window.open();

var ImageFactory = require('ti.imagefactory');

var f = Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'images', 'flower.jpg');
var blob = f.read();
imageViewTransformed.image = blob;
var type = 0;

btnTransform.addEventListener('click', function (e) {
    switch (type) {
        case 0:
            newBlob = ImageFactory.imageWithAlpha(blob, {});
            break;
        case 1:
            newBlob = ImageFactory.imageWithTransparentBorder(blob, { borderSize:10 });
            break;
        case 2:
            newBlob = ImageFactory.imageWithRoundedCorner(blob, { borderSize:4, cornerRadius:8 });
            break;
        case 3:
            newBlob = ImageFactory.imageAsThumbnail(blob, {size:64, borderSize:5, cornerRadius:10, quality:ImageFactory.QUALITY_HIGH });
            break;
        case 4:
            newBlob = ImageFactory.imageAsResized(blob, { width:140, height:140, quality:ImageFactory.QUALITY_LOW });
            break;
        case 5:
            newBlob = ImageFactory.imageAsResized(blob, { width:140, height:140, quality:ImageFactory.QUALITY_HIGH, hires:true });
            break;
        case 6:
            newBlob = ImageFactory.imageAsCropped(blob, { width:100, height:100, x:50, y:50 });
            break;
        case 7:
            newBlob = ImageFactory.imageTransform(blob,
                { type:ImageFactory.TRANSFORM_CROP, width:200, height:200 },
                { type:ImageFactory.TRANSFORM_ROUNDEDCORNER, borderSize:6, cornerRadius:20 }
            );
            break;
        case 8:
            newBlob = resizeKeepAspectRatioNewHeight(blob, 232, 213, 100);
            break;
        case 9:
            newBlob = resizeKeepAspectRatioNewWidth(blob, 232, 213, 100);
            break;
        case 10:
            newBlob = resizeKeepAspectRatioPercentage(blob, 232, 213, 110);
            break;
    }
    imageViewTransformed.image = newBlob;
    imageViewTransformed.size = { width:newBlob.width, height:newBlob.height };

    type = (type + 1) % 11;
});

btnSave.addEventListener('click', function (e) {
    newBlob = ImageFactory.compress(blob, 0.25);
    var filename = Titanium.Filesystem.applicationDataDirectory + "/newflower.jpg";
    f = Titanium.Filesystem.getFile(filename);
    f.write(newBlob);

    var alert = Ti.UI.createAlertDialog({
        title:'Image Factory',
        message:'Compressed image saved to newflower.jpg with compression quality of 25%'
    });
    alert.show();
});

function resizeKeepAspectRatioPercentage(blob, imageWidth, imageHeight, percentage) {
    // only run this function if suitable values have been entered
    if (imageWidth <= 0 || imageHeight <= 0 || percentage <= 0)
        return blob;

    var w = imageWidth * (percentage / 100);
    var h = imageHeight * (percentage / 100);

    Ti.API.info('w: ' + w);
    Ti.API.info('h: ' + h);

    return ImageFactory.imageAsResized(blob, { width:w, height:h });
}

function resizeKeepAspectRatioNewWidth(blob, imageWidth, imageHeight, newWidth) {
    // only run this function if suitable values have been entered
    if (imageWidth <= 0 || imageHeight <= 0 || newWidth <= 0)
        return blob;

    var ratio = imageWidth / imageHeight;

    var w = newWidth;
    var h = newWidth / ratio;

    Ti.API.info('ratio: ' + ratio);
    Ti.API.info('w: ' + w);
    Ti.API.info('h: ' + h);

    return ImageFactory.imageAsResized(blob, { width:w, height:h });
}

function resizeKeepAspectRatioNewHeight(blob, imageWidth, imageHeight, newHeight) {
    // only run this function if suitable values have been entered
    if (imageWidth <= 0 || imageHeight <= 0 || newHeight <= 0)
        return blob;

    var ratio = imageWidth / imageHeight;

    var w = newHeight * ratio;
    var h = newHeight;

    Ti.API.info('ratio: ' + ratio);
    Ti.API.info('w: ' + w);
    Ti.API.info('h: ' + h);

    return ImageFactory.imageAsResized(blob, { width:w, height:h });
}