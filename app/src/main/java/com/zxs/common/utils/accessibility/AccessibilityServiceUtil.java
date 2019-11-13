package com.zxs.common.utils.accessibility;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * User:  ZengLiang
 * Email: zengliang@huya.com
 * Date:  2019/8/29
 * Description:
 */
public class AccessibilityServiceUtil {
    private static final String TAG = AccessibilityServiceUtil.class.getSimpleName();

    /**
     * 检查Accessibility权限
     */
    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }
        return false;
    }

    /**
     * 设置Accessibility权限
     */
    public static boolean setAccessibilitySettings(Context context, Boolean isOpen) {
        if (isOpen) {
            Settings.Secure.putString(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                String.format("%s/%s", context.getPackageName(), ".accessibility.service.AccessibilityServiceImpl"));
            Settings.Secure.putString(context.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED, "1");
        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Application.getInstance().getAccessibilityService().disableSelf();
//            } else {
                Settings.Secure.putString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                    String.format("%s/%s", context.getPackageName(), ".accessibility.service.AccessibilityServiceImpl"));
                Settings.Secure.putString(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED, "0");
//            }
        }
        return isAccessibilitySettingsOn(context);
    }
}
