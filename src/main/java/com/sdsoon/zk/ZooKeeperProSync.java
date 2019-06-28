package com.sdsoon.zk;


import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 监听数据变化：配置中心
 * 观察者模式
 * <p>
 * Created By Chr on 2019/4/28/0028.
 */
public class ZooKeeperProSync implements Watcher {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();

    public static void main(String args[]) throws Exception {
        //zk配置数据存放路径
        String path = "/username";

        //连接zk并且注册一个默认的监听器
        zk = new ZooKeeper("192.168.92.131:2181", 5000, new ZooKeeperProSync());

        //等待zk连接成功并且通知
        countDownLatch.await();

        //获取path目录的配置数据，并注册默认的监听器
        //从节点读取数据，watch-》true：监听
        System.out.println(new String(zk.getData(path, true, stat)));

        Thread.sleep(Integer.MAX_VALUE);
    }


    @Override
    public void process(WatchedEvent event) {

        if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {//zk连接成功通知事件
            if (Watcher.Event.EventType.None == event.getType() && null == event.getPath()) {
                countDownLatch.countDown();

            } else if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {//zk目录节点数据变化通知事件
                try {
                    System.out.println(" 配置已修改为，新值为：" + new String(zk.getData(event.getPath(), true, stat)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
