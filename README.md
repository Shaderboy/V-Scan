# V-Scan

This is an android app that allows you to scan a product's upc code and will tell you if it's vegan or not.

# How to use:

1. Download latest release from the APK folder.
2. Extract file.
3. Connect Android device to PC with USB cable (make sure USB storage is turned on.
4. Copy file to device's storage.

OR

1. Navigate to this page on device
2. Go to Release tab
3. Download APK straight onto phone.

Then...

5. Launch .apk file from the File Manager/Downloads folder.
6. Once the barcode scanner is launched, hold the phones camera up to the UPC code on any food item.
7. Wait until the camera focuses and launches the loading screen.
8. If the item "may be vegan", or is not vegan, touch the highlighted ingredients to learn more about them. Then press anywhere on the background to go back.

# How does it work?

The program uses an open source barcode scanner (ZBar) to parse the UPC code. It then makes a call to a product database (Factual) and gets a list of ingredients.
It then cross references that list with a hand populated database of animal products, and that's it!
