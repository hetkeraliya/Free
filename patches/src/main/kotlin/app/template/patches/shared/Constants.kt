package app.template.patches.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Constants {
    val COMPATIBILITY_VWFNDR = Compatibility(
        name = "VWFNDR + MBL",
        packageName = "com.vwfndr.mbl",
        apkFileType = ApkFileType.APK,
        appIconColor = 0x1A1A1A,
        targets = listOf(
            AppTarget(version = null, isExperimental = true)
        )
    )
}
