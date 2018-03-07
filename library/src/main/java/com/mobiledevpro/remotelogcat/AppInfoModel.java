package com.mobiledevpro.remotelogcat;

/**
 * Mdel for app info
 * <p>
 * Created by Dmitriy V. Chernysh on 25.09.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://fb.me/mobiledevpro/
 * <p>
 * #MobileDevPro
 */

class AppInfoModel {
    private String name;
    private String version;
    private int build;

    /**
     * Constructor for getting entry from DB
     */
    AppInfoModel(String name, String version, int build) {
        this.name = name;
        this.version = version;
        this.build = build;
    }

    String getName() {
        return name;
    }

    String getVersion() {
        return version;
    }

    int getBuild() {
        return build;
    }
}
