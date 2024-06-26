package com.xayah.core.datastore

object ConstantUtil {
    const val DEFAULT_PATH_PARENT = "/storage/emulated/0"
    const val DEFAULT_PATH_CHILD = "DataBackup"
    const val DEFAULT_PATH = "${DEFAULT_PATH_PARENT}/${DEFAULT_PATH_CHILD}"
    const val DEFAULT_TIMEOUT = 30000
    const val CONFIGURATIONS_KEY_BLACKLIST = "blacklist"
    const val CONFIGURATIONS_KEY_CLOUD = "cloud"
    const val CONFIGURATIONS_KEY_FILE = "file"
    const val FTP_ANONYMOUS_USERNAME = "anonymous" // https://www.rfc-editor.org/rfc/rfc1635
    const val FTP_ANONYMOUS_PASSWORD = "guest"
    val SupportedExternalStorageFormat = listOf(
        "sdfat",
        "fuseblk",
        "exfat",
        "ntfs",
        "ext4",
        "f2fs",
        "texfat",
    )
    val DefaultMediaList = listOf(
        "Pictures" to "/storage/emulated/0/Pictures",
        "Music" to "/storage/emulated/0/Music",
        "DCIM" to "/storage/emulated/0/DCIM",
        "Download" to "/storage/emulated/0/Download",
    )
}
