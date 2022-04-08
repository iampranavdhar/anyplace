package cy.ac.ucy.cs.anyplace.smas.ui.chat.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.webkit.MimeTypeMap
import cy.ac.ucy.cs.anyplace.lib.android.utils.LOG
import java.io.ByteArrayOutputStream
import java.io.File

// TODO:PM:ATH merge w/ android-lib
class ImageBase64 {

  private fun resize(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    var image = image
    return if (maxHeight > 0 && maxWidth > 0) {
      val width = image.width
      val height = image.height
      val ratioBitmap = width.toFloat() / height.toFloat()
      val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
      var finalWidth = maxWidth
      var finalHeight = maxHeight
      if (ratioMax > 1) {
        finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
      } else {
        finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
      }
      image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
      image
    } else {
      image
    }
  }

  fun encodeToBase64(imageUri: Uri?, context: Context): String {
    var encodedBase64 = ""

    if (imageUri != null) {
      val imageStream = context.contentResolver.openInputStream(imageUri!!)
      val selectedImage = BitmapFactory.decodeStream(imageStream)
      val baos = ByteArrayOutputStream()
      val compressedImage = resize(selectedImage, 700, 900)
      compressedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
      val b = baos.toByteArray()
      encodedBase64 = Base64.encodeToString(b, Base64.DEFAULT)
    }

    return encodedBase64
  }

  fun decodeFromBase64(encodedBase64: String): Bitmap? {
    var bytes = ByteArray(0)
    try {
      bytes = Base64.decode(encodedBase64, Base64.DEFAULT)
    } catch (e: IllegalArgumentException) {
      LOG.E("Image cannot be displayed.")
    }
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
  }

  fun getMimeType(imageUri: Uri, context: Context): String {

    val extension: String? = if (imageUri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
      val mime = MimeTypeMap.getSingleton()
      mime.getExtensionFromMimeType(context.contentResolver.getType(imageUri!!))
    } else {
      MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(imageUri?.path)).toString())
    }

    return extension ?: ""
  }
}