package cn.jwjg.jwpd.Utils;

import java.io.File;

public class FileUtils {
    public static boolean deleteFolder(File file) {

        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // 删除文件
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 实例化目录下所有的文件
                if (files != null) { // 遍历目录下所有的文件，并删除
                    for (int i = 0; i < files.length; i++) {
                        deleteFolder(files[i]);
                    }
                }
            }
            boolean isSuccess = file.delete();
            if (!isSuccess) {
                return false;
            }

        }
        return true;
    }

    public static boolean updateFileName(File Ofile,File Nfile){
        Ofile.renameTo(Nfile);
        return  true;
    }
}
