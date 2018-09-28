package com.example.android.preciousplastic.permissions;

public interface PermissionResponseHandler {

    /**
     * Called upon being granted the permission.
     * @param permissionCode Value of enum permission request.
     */
    void permissionGranted(int permissionCode);

    /**
     * Called upon being denied the permission.
     * @param permissionCode Value of enum permission request.
     */
    void permissionDenied(int permissionCode);

    /**
     * Use the method during construction of a class implementing this.
     * Sets the permissionResponseHandler for the permission request.
     * When used from a fragment: "final BaseActivity baseActivity = (BaseActivity) getActivity();"
     * When used from an activity: just use the setter.
     * @param permissionResponseHandler implements the response hadling method.
     */
    void setPermissionResponseHandler(PermissionResponseHandler permissionResponseHandler);

}
