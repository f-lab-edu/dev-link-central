package dev.linkcentral.presentation;

import javax.servlet.http.HttpServletRequest;

public class BaseUrlUtil {
    public static String getBaseUrl(HttpServletRequest request) {
        return String.format("%s://%s:%d", request.getScheme(), request.getServerName(),
                             request.getServerPort());
    }
}
