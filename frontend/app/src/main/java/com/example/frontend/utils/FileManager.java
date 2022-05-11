package com.example.frontend.utils;

import android.content.Context;
import android.os.Environment;

import com.example.frontend.info.UserInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager {
    private static volatile FileManager mInstance;

    private FileManager(){

    }
    public static FileManager getInstance(){
        FileManager inst = mInstance;
        if (inst == null) {
            synchronized (HttpRequestManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new FileManager();
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    public boolean isSDCardState(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public String getSDCardPath(){
        if(isSDCardState()){//如果SDCard存在并且可以读写
            return Environment.getExternalStorageDirectory().getPath();
        }else{
            return null;
        }
    }

    public File createDirection(String dirName) {
        File dir = new File(dirName);
        dir.mkdir();
        return dir;
    }

    public boolean deleteDirection(String dirName) {
        File dir = new File(dirName);
        return deleteDirection(dir);
    }

    public boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file == null || !file.exists() || file.isDirectory())
            return false;

        return file.delete();
    }

    public boolean renameFile(String oldfileName, String newFileName) {
        File oleFile = new File(oldfileName);
        File newFile = new File(newFileName);
        return oleFile.renameTo(newFile);
    }

    public boolean copyFileTo(String srcFileName, String destFileName) throws IOException {
        File srcFile = new File(srcFileName);
        File destFile = new File(destFileName);
        return copyFileTo(srcFile, destFile);
    }

    public boolean moveSDFileTo(String srcFileName, String destFileName) throws IOException {
        File srcFile = new File(srcFileName);
        File destFile = new File(destFileName);
        return moveFileTo(srcFile, destFile);
    }



    /**
     * 删除一个文件
     *
     * @param file
     * @return
     */

    public boolean deleteFile(File file) {
        if (file.isDirectory())
            return false;
        return file.delete();
    }

    /**
     * 删除一个目录（可以是非空目录）
     *
     * @param dir
     */

    public boolean deleteDirection(File dir) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteDirection(file);// 递归
            }
        }
        dir.delete();
        return true;
    }

    /**
     * 拷贝一个文件,srcFile源文件，destFile目标文件
     *
     * @param
     * @throws IOException
     */

    public boolean copyFileTo(File srcFile, File destFile) throws IOException {

        if (srcFile.isDirectory() || destFile.isDirectory())
            return false;// 判断是否是文件
        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destFile);
        int readLen = 0;
        byte[] buf = new byte[1024];
        while ((readLen = fis.read(buf)) != -1) {
            fos.write(buf, 0, readLen);
        }
        fos.flush();
        fos.close();
        fis.close();
        return true;
    }

    /**
     * 移动一个文件
     *
     * @param srcFile
     * @param destFile
     * @return
     * @throws IOException
     */

    public boolean moveFileTo(File srcFile, File destFile) throws IOException {

        boolean is_copy = copyFileTo(srcFile, destFile);

        if (!is_copy)
            return false;
        deleteFile(srcFile);
        return true;
    }

    public boolean getUserFileExists(Context context, String path) {
        String destDir = context.getExternalFilesDir(UserInfo.getInstance().getUsername()).getAbsolutePath();
        return isFileExist(destDir + '/' + path);
    }

    public String getUserFileAbsolutePath(Context context, String path) {
        return context.getExternalFilesDir(UserInfo.getInstance().getUsername()).getAbsolutePath()
                + "/" + path;
    }
}
