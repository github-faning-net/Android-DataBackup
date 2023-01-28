package com.xayah.databackup.util

import com.xayah.databackup.data.AppInfoBackupMap
import com.xayah.databackup.data.AppInfoListSelectedNum
import com.xayah.databackup.data.AppInfoRestoreMap
import kotlinx.coroutines.flow.MutableStateFlow

class GlobalObject {
    object Instance {
        val instance = GlobalObject()
    }

    companion object {
        fun getInstance() = Instance.instance
    }

    // 应用备份哈希表
    val appInfoBackupMap by lazy {
        MutableStateFlow<AppInfoBackupMap>(hashMapOf())
    }

    // 应用备份哈希表计数
    val appInfoBackupMapNum
        get() = run {
            val num = AppInfoListSelectedNum(0, 0)
            for (i in appInfoBackupMap.value.values) {
                if ((i.detailBackup.selectApp || i.detailBackup.selectData) && i.isOnThisDevice) {
                    if (i.detailBase.isSystemApp) num.system++
                    else num.installed++
                }
            }
            num
        }

    // 应用恢复哈希表
    val appInfoRestoreMap by lazy {
        MutableStateFlow<AppInfoRestoreMap>(hashMapOf())
    }

    // 应用恢复哈希表计数
    val appInfoRestoreMapNum
        get() = run {
            val num = AppInfoListSelectedNum(0, 0)
            for (i in appInfoRestoreMap.value.values) {
                if (i.detailRestoreList.isNotEmpty()) {
                    if (i.detailRestoreList[i.restoreIndex].selectApp || i.detailRestoreList[i.restoreIndex].selectData) {
                        if (i.detailBase.isSystemApp) num.system++
                        else num.installed++
                    }
                }
            }
            num
        }
}