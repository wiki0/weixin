package top.siki.weixin.docker.thread;

/**
 * @author: wiki
 * @date: 2018/5/22
 * @description: 下载线程
 */
public class DownloadThread extends Thread {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is running.");
        try {
            wait(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " is end.");
    }
}