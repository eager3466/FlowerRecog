package com.bjut.eager.flowerrecog.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

import java.util.List;

public class PackageUtil {

	/**
	 *  获取应用版本号
	 *  从DictApplication抽取过来
	 * @param context
	 * @return
	 */
	public static String getApplicationVersion(Context context) {
		String versionName = getVersionName(context);
		String ret = versionName;
		int index = versionName.indexOf('(');
		if (index != -1) {
			ret = versionName.substring(0, index);
		}
		ret = ret.trim();
		return ret;
	}

	public static String getVersionName(Context context) {
		try {
			PackageInfo pinfo = context.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
			return pinfo.versionName;
		} catch (NameNotFoundException e) {
			return "1.0.0";
		}
	}
	
	public static Boolean isInstalled(Context context, String packageName) {
		try {
			if (context.getPackageManager().getPackageInfo(packageName, 0) != null ) {
				return true;
			}
		} catch (NameNotFoundException e) {
		}
		return false;
	}

	public static String getPkgVersionName(Context context, String packageName) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			if (info != null) {
				return info.versionName;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static int getPkgVersionCode(Context context, String packageName) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			if (info != null) {
				return info.versionCode;
			}
		} catch (Exception e) {
		}
		return -1;
	}

	/**
	 * 获取默认启动器的包名
	 * @param context
	 * @return
	 */
	public static String getDefaultLauncherPackageName(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return resolveInfo.activityInfo.packageName;
	}

	/**
	 * 查询是否有这种类型的intent用
	 * @param activity
	 * @param intent
	 * @return
	 */
	public static boolean isIntentAvailable(Activity activity, Intent intent) {
		PackageManager pm = activity.getPackageManager(); // 获得PackageManager对象
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> currentResolveList = pm
				.queryIntentActivities(intent, 0);

		for (int i=currentResolveList.size()-1; i >= 0; i--) {
			ActivityInfo ai = currentResolveList.get(i).activityInfo;
			if (ai.exported) {
				return true;
			}
		}

		return false;
	}

	public static String getApplicationMetaData(Context context, String name) {
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
			String msg = appInfo.metaData.getString(name);
			return msg;
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}


}
