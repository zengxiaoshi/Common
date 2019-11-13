/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zxs.common.utils.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author:  ZengLiang
 * Email: zengliang@huya.com
 * Date:  2019-10-30 20:39
 * Description:* This class contains static helper methods to work with
 * {@link AccessibilityNodeInfo}
 */
class AccessibilityNodeInfoHelper {

    /**
     * Returns the node's bounds clipped to the size of the display
     *
     * @param node
     * @param width  pixel width of the display
     * @param height pixel height of the display
     * @return null if node is null, else a Rect containing visible bounds
     */
    static Rect getVisibleBoundsInScreen(AccessibilityNodeInfo node, int width, int height) {
        if (node == null) {
            return null;
        } else {
            Rect nodeRect = new Rect();
            node.getBoundsInScreen(nodeRect);

            Rect displayRect = new Rect();
            displayRect.top = 0;
            displayRect.left = 0;
            displayRect.right = width;
            displayRect.bottom = height;

            nodeRect.intersect(displayRect);
            if (Build.VERSION.SDK_INT >= 21) {
                Rect window = new Rect();
                if (node.getWindow() != null) {
                    node.getWindow().getBoundsInScreen(window);
                    nodeRect.intersect(window);
                }
            }

            return nodeRect;
        }
    }

    /**
     * Returns all AccessibilityNodeInfo in multiWindows
     *
     * @param accessibilityService
     * @return
     */
    public static AccessibilityNodeInfo[] getWindowRoots(AccessibilityService accessibilityService) {
        Set<AccessibilityNodeInfo> roots = new HashSet();
        // Start with the active window, which seems to sometimes be missing from the list returned
        // by the UiAutomation.
        AccessibilityNodeInfo activeRoot = accessibilityService.getRootInActiveWindow();
        if (activeRoot != null) {
            roots.add(activeRoot);
        }
        // Support multi-window searches for API level 21 and up.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            List<AccessibilityWindowInfo> windows = accessibilityService.getWindows();
            if (windows != null && windows.size() > 0) {
                for (AccessibilityWindowInfo window : windows) {
                    AccessibilityNodeInfo root = window.getRoot();
                    if (root != null) {
                        roots.add(root);
                    }
                }
            }
        }
        return roots.toArray(new AccessibilityNodeInfo[roots.size()]);
    }

    /**
     * Returns the dimensions of the display in pixels.
     *
     * @param context The context that will be used to retrieve system information
     * @return The {@link Point} used to store the dimensions of the display
     */
    public static Point getScreenSize(Context context) {
        Point screenPoint = new Point();
        Display windowDisplay = getDisplay(context);
        if (windowDisplay != null) {
            windowDisplay.getSize(screenPoint);
        }
        return screenPoint;
    }

    private static Display getDisplay(Context context) {
        final WindowManager windowManager =
            (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            return windowManager.getDefaultDisplay();
        } else {
            return null;
        }
    }
}
