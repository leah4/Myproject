package task;

import java.io.File;

public interface ScanCallBack {

    //对于文件夹的扫描任务进行回调：处理文件夹下一级子文件夹和子文件
    void callback(File dir);
}
