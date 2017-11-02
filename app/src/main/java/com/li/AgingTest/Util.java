package com.li.AgingTest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gms on 17-8-18.
 */

public class Util {
    public static HashMap<String, String> mPackageName;

    public static void setPackageNameMap(HashMap<String, String> mCheckedMap) {
        Log.d("lixin","setPackageNameMap()");
        mPackageName =mCheckedMap;

    }

    public static List<PackageInfo> getPakageNameList(Context context){
        Log.d("lixin","getPakageNameList()");
        PackageManager packageManager =context.getPackageManager();
        List<PackageInfo> list = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        return list;
    }
    public static String getCommond(File whilelist, String stringcommand,
                                    boolean isAllModules) {
        StringBuilder commond = new StringBuilder();
        if (isAllModules) {
            commond.append("monkey");
        } else {
            commond.append("monkey --pkg-whitelist-file ").append(
                    whilelist.getPath());
        }
        commond.append(" --throttle 1000 ")
                .append(stringcommand)
// .append("--ignore-crashes --ignore-timeouts --ignore-security-exceptions --ignore-native-crashes")
                .append(" --monitor-native-crashes --kill-process-after-error -v -v -v ")
                .append("1000000");
        Log.i("", "----commond->" + commond.toString());
        return commond.toString();
    }
}
