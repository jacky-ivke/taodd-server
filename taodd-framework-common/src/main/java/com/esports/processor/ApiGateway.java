package com.esports.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ApiGateway {

	private static final int INTERFACE_COUNT = 10;

	public static ExecutorService executorService = new ThreadPoolExecutor(INTERFACE_COUNT, INTERFACE_COUNT * 3, 3L,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.DiscardPolicy());
	
	public static <T> void start(List<FutureTask<T>> tasks){
		for(FutureTask<T> task : tasks){
			executorService.submit(task);
		}
	}

	public static <T> List<T> syncwait(final List<FutureTask<T>> tasks){
		final List<T> list = new ArrayList<T>();
		for(FutureTask<T> task : tasks){
			executorService.submit(task);
		}
		for (FutureTask<T> future : tasks) {
			try {
				T t = future.get();
				if(null != t){
					list.add(t);
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}
