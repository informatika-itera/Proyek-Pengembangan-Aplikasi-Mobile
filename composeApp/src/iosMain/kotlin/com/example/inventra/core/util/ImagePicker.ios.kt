package com.example.inventra.core.util

import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

actual class ImagePicker(private val viewController: UIViewController) {
    actual fun pickImage(onImagePicked: (ByteArray, String) -> Unit) {
        val picker = UIImagePickerController()
        picker.sourceType = UIImagePickerControllerSourceType
            .UIImagePickerControllerSourceTypePhotoLibrary
        val delegate = object : NSObject(),
            UIImagePickerControllerDelegateProtocol,
            UINavigationControllerDelegateProtocol {
            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                val image = didFinishPickingMediaWithInfo[
                    "UIImagePickerControllerOriginalImage"
                ] as? UIImage
                image?.let {
                    val data = UIImageJPEGRepresentation(it, 0.8)
                    val bytes = data?.bytes?.let { ptr ->
                        ByteArray(data.length.toInt()).also { arr ->
                            kotlinx.cinterop.usePinned { arr.copyFrom(ptr, arr.size) }
                        }
                    } ?: return@let
                    val fileName = "item_${kotlinx.datetime.Clock.System.now()
                        .toEpochMilliseconds()}.jpg"
                    onImagePicked(bytes, fileName)
                }
                picker.dismissViewControllerAnimated(true, null)
            }
        }
        picker.delegate = delegate
        viewController.presentViewController(picker, animated = true, completion = null)
    }
}