package com.jd.blockchain.util;

import com.jd.blockchain.SDKTest;
import org.junit.Test;

import java.util.concurrent.*;

public class PerformanceTest extends SDKTest {

    @Test
    public void insertDataMore() throws InterruptedException {
        for (int i = 0; i < 15; i++) {
            insertData();
            Thread.sleep(1000);
        }
    }

    /**
     * use the multiThread to insert date;
     */
    @Test
    public void pressureTest(){
        //1. 提供指定线程数量的线程池；
        ExecutorService service = Executors.newFixedThreadPool(100);
        //2. 执行指定的线程的操作，需要提供实现Runnable接口或Callable接口实现类的对象
        for(int i=0;i<100;i++){
            service.submit(new DataAccountThreadCall());//适用于Callable
        }
        try {
            Thread.sleep(1000*60*30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //3. 关闭连接池
        service.shutdown();
    }
}

// 1. 创建一个实现Callable的实现类
class DataAccountThreadCall extends SDKTest implements Callable {
    // 2. 实现call方法，将此线程需要执行的操作声明在call中
    @Override
    public Object call() throws InterruptedException {
        while (true){
            insertData();
            Thread.sleep(100);
        }
//        for(int i=0;i<100;i++){
//            insertData();
//            Thread.sleep(100);
//        }
//        return null;
    }
}
