package domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 遍历指定目录，获取所有 *.sql 文件的路径
 */
public class GetFileUtil {

    List<String> fileList = new ArrayList<String>();

    public List<String> getFileList(String fileDir) {
        File file = new File(fileDir);
        File[] files = file.listFiles();

        if (files == null) {
            System.out.println("目录为空");
        }
        // 递归遍历
        for (File f : files) {
            if (f.isFile()) {
                if (f.getPath().toLowerCase().contains(".sql")) {
                    fileList.add(f.getPath());
                }

            } else if (f.isDirectory()) {
                getFileList(f.getAbsolutePath());
            }
        }
        return fileList;
    }

}
