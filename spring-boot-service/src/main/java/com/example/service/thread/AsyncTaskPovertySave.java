package com.example.service.thread;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.example.service.black.CustomerBlackDO;
import com.example.service.black.CustomerBlackDOMapper;
import com.example.service.customer.CustomerTagRelationDO;
import com.example.service.poverty.CustomerPovertyDO;
import com.example.service.poverty.CustomerPovertyDOMapper;
import com.example.service.resident.ResidentDO;
import com.example.service.resident.ResidentDOMapper;

@Service
//线程执行任务类
public class AsyncTaskPovertySave {
	Random random = new Random();// 默认构造方法
    @Async
    // 表明是异步方法
    // 无返回值
    public void executeAsyncTask(List<CustomerPovertyDO>  list, CustomerPovertyDOMapper customerPovertyDOMapper) {
    	 System.out.println(Thread.currentThread().getName()+"开启新线程执行" + list.size());
    	 customerPovertyDOMapper.batchSave(list);
    }

    /**
     * 异常调用返回Future
     *
     * @param i
     * @return
     * @throws InterruptedException
     */
    @Async
    public Future<String> asyncInvokeReturnFuture(int i) throws InterruptedException {
        System.out.println("input is " + i);
        Thread.sleep(1000 * random.nextInt(i));

        Future<String> future = new AsyncResult<String>("success:" + i);// Future接收返回值，这里是String类型，可以指明其他类型

        return future;
    }

}
