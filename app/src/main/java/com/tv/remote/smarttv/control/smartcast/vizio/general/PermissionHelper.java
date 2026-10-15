package com.tv.remote.smarttv.control.smartcast.vizio.general;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionHelper {
    private final Activity activity;
    private final String[] permissions;
    private final ActivityResultLauncher<String[]> permissionLauncher;
    private final Callback callback;

    public interface Callback {
        void onPermissionResult(boolean isGranted);
    }

    public PermissionHelper(Activity activity, String[] permissions, ActivityResultLauncher<String[]> permissionLauncher, Callback callback) {
        this.activity = activity;
        this.permissions = permissions;
        this.permissionLauncher = permissionLauncher;
        this.callback = callback;
    }

    public void checkAndRequestPermissions() {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }

        if (deniedPermissions.isEmpty()) {
            callback.onPermissionResult(true);
        } else {
            permissionLauncher.launch(deniedPermissions.toArray(new String[0]));
        }
    }

    public void handlePermissionsResult(Map<String, Boolean> result) {
        // An empty result means the request was cancelled/interrupted, not that everything was granted.
        if (result.isEmpty()) {
            callback.onPermissionResult(false);
            return;
        }
        boolean allGranted = true;
        for (Boolean isGranted : result.values()) {
            if (!isGranted) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) {
            callback.onPermissionResult(true);
        } else {
            boolean permanentlyDenied = false;
            for (String permission : permissions) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) &&
                        ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    permanentlyDenied = true;
                    break;
                }
            }

            if (permanentlyDenied) {
                showSettingsDialog();
            } else {
                callback.onPermissionResult(false);
            }
        }
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(activity)
                .setTitle("Permission Required")
                .setMessage("Some permissions are permanently denied. Please allow them from Settings.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                    activity.startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}