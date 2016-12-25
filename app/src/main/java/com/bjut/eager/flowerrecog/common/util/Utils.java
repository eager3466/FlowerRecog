package com.bjut.eager.flowerrecog.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Utils {

	/**
	 * check soft input status, if active, hide the soft input
	 * 
	 * @param context
	 */
	public static void checkSoftInput(Activity context) {
		if(context == null) {
			return;
		}
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && context.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(context.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

	/**
	 * 输入法显示隐藏开关
	 *
	 * @param context
	 * @param view
	 * @param isShowInputMethod
	 */
	public static void inputMethodToggle(Context context, View view, boolean isShowInputMethod) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (isShowInputMethod) {
			imm.showSoftInput(view, 0);
		} else {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * 获取状态栏高度
	 * @param context
	 * @return
     */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * 获取当前时间戳
	 * @return
     */
	public static long getNowTimestamp() {
		Date data = new Date();
		return data.getTime();
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		float m = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / m + 0.5f);
	}

	/**
	 * get action bar size
	 * @param context
	 * @return
     */
	public static int getActionBarSize(Context context) {
		TypedArray actionbarSizeTypedArray = context.obtainStyledAttributes(new int[] {
				android.R.attr.actionBarSize
		});

		return  (int) actionbarSizeTypedArray.getDimension(0, 0);
	}

//	/**
//	 * restart app
//	 * @param mContext
//     */
//	public static void restartApp(Context mContext) {
//		Intent intent = new Intent(mContext, MainActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		mContext.startActivity(intent);
//		android.os.Process.killProcess(android.os.Process.myPid());
//	}

	/**
	 * AES加密解密单元.
	 * 参考一下两篇文章.
	 * http://www.cnblogs.com/arix04/archive/2009/10/15/1511839.html
	 * http://aub.iteye.com/blog/1133494
	 * 加密
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String content, String token) throws Exception {
		byte[] raw = token.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC"); //"算法/模式/补码方式"
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(content.getBytes("utf-8"));
			return new String(Base64.encode(encrypted, Base64.DEFAULT));//此处使用BASE64做转码功能，同时能起到2次加密的作用。
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}

	/**
	 * 解密.
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String content, String token) throws Exception {
		try {
			byte[] raw = token.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted = Base64.decode(content, Base64.DEFAULT);
			try {
				byte[] original = cipher.doFinal(encrypted);
				String originalString = new String(original, "utf-8");
				return originalString;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
