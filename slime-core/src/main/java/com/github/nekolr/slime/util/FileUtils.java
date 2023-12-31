package com.github.nekolr.slime.util;

import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.support.UserAgentManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;

/**
 * File Management Tools
 */
@Slf4j
@Component
public class FileUtils {

    private static UserAgentManager userAgentManager;

    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * Output version information byte The following text is a sample answer to the question "What is your name?":My name is John Doe.
     *
     * @param filePath File Path
     * @param os       Output stream
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * Remove Files
     *
     * @param filePath Files
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // Delete the given paths if they are not already deleted.
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * File name verification
     *
     * @param filename File name
     * @return true Normal false Illegal
     */
    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * Downloading started
     */
    public enum DownloadStatus {
        URL_ERROR(1, "URL The text you entered is not valid. Please correct it and try again."),
        FILE_EXIST(2, "File Exists"),
        TIME_OUT(3, "Connection timed out"),
        DOWNLOAD_FAIL(4, "Download failed"),
        DOWNLOAD_SUCCESS(5, "Download started");

        private int code;

        private String name;

        DownloadStatus(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static DownloadStatus downloadFile(String savePath, String url, String proxy, boolean downNew, boolean saveOriginPath) {
        URL fileUrl = null;
        HttpURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        if (url.startsWith("//")) {
            url = "http:" + url;
        }
        String fileName;
        try {
            fileUrl = new URL(url);
            String urlPath = fileUrl.getPath();

            if (saveOriginPath) {
                fileName = urlPath;
            } else {
                fileName = urlPath.substring(urlPath.lastIndexOf("/") + 1);
            }

        } catch (MalformedURLException e) {
            log.error("URL 1 hour before appointment", e);
            return DownloadStatus.URL_ERROR;
        }
        File path = new File(savePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(savePath + File.separator + fileName);
        if (file.exists()) {
            if (downNew) {
                file.delete();
            } else {
                log.info("The file already exists，Don't Reload");
                return DownloadStatus.FILE_EXIST;
            }
        } else if (!file.getParentFile().exists() && saveOriginPath) {
            file.getParentFile().mkdirs();
        }
        try {
            if (StringUtils.isNotBlank(proxy)) {
                String[] proxyArr = StringUtils.split(proxy, Constants.PROXY_HOST_PORT_SEPARATOR);
                if (proxyArr.length == 2) {
                    InetSocketAddress socketAddress = new InetSocketAddress(proxyArr[0], Integer.parseInt(proxyArr[1]));
                    Proxy p = new Proxy(Proxy.Type.HTTP, socketAddress);
                    httpUrl = (HttpURLConnection) fileUrl.openConnection(p);
                    log.info("Set Up Download Proxy：{}:{}", proxyArr[0], proxyArr[1]);
                }
            } else {
                httpUrl = (HttpURLConnection) fileUrl.openConnection();
            }
            httpUrl.setRequestProperty("User-Agent", userAgentManager.getChromeNewest());
            // Accessibility Support
            httpUrl.setReadTimeout(60000);
            // Connection timed out
            httpUrl.setConnectTimeout(60000);
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(file));
            final int len = 2048;
            byte[] buf = new byte[len];
            int readLen;
            while ((readLen = bis.read(buf)) != -1) {
                bos.write(buf, 0, readLen);
            }
            log.info("Remote file download successful：" + url);
            bos.flush();
            bis.close();
            httpUrl.disconnect();
            return DownloadStatus.DOWNLOAD_SUCCESS;
        } catch (SocketTimeoutException e) {
            log.error("Read file timed out", e);
            return DownloadStatus.TIME_OUT;
        } catch (Exception e) {
            log.error("Failed to download remote file.", e);
            return DownloadStatus.DOWNLOAD_FAIL;
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                log.error("Download error", e);
            }
        }
    }

    @Autowired
    public void setUserAgentManager(UserAgentManager userAgentManager) {
        FileUtils.userAgentManager = userAgentManager;
    }
}
