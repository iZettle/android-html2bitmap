# android-html2bitmap
Generates a bitmap from html by rendering the content inside an off screen webview 

Usage: 
Html2Bitmap.getBitmap(context: Context, html: String, width: Int);

Context will be used to create webview.
Html is the generated html.
Width is the width of the generated Bitmap.

Will wait for remote resources to load before generating bitmap.
Can be used without being attached to a ui (i.e. generating the Bitmap in a background-thread/service)

Note: Some of the methods on the webview are required to be run on the mainThread 
of the application - so some operation will be performed on that thread.
