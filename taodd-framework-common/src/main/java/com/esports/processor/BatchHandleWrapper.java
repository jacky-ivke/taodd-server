package com.esports.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public final class BatchHandleWrapper<E> {

	private LinkedBlockingQueue<E> queue;
	private boolean running = true;
	private ExecutorService exec;
	private int batchSize = 100;
	private int queueSize = Integer.MAX_VALUE;
	private int timeout = 5000;
	private int taskCount = 1;
	private final BatchHandler<E> batchHandler;
	private EnqueueHandler<E> enqueueHandler;

	public static <T> BatchHandleWrapper<T> batchHandler(
			BatchHandler<T> batchHandler) {
		return new BatchHandleWrapper<>(batchHandler);
	}

	public BatchHandleWrapper<E> batchSize(int size) {
		if (size > 0) {
			this.batchSize = size;
		}
		return this;
	}

	public BatchHandleWrapper<E> queueSize(int size) {
		if (size > 0) {
			this.queueSize = size;
		}
		return this;
	}

	public BatchHandleWrapper<E> timeout(int timeout, TimeUnit unit) {
		if (timeout > 0) {
			this.timeout = (int) unit.toMillis(timeout);
		}
		return this;
	}

	public BatchHandleWrapper<E> taskCount(int count) {
		if (count > 0) {
			this.taskCount = count;
		}
		return this;
	}

	public BatchHandleWrapper<E> enqueueHandler(EnqueueHandler<E> enqueueHandler) {
		this.enqueueHandler = enqueueHandler;
		return this;
	}

	public BatchHandleWrapper<E> start() {
		check();
		queue = new LinkedBlockingQueue<>(queueSize);
		exec = Executors.newFixedThreadPool(taskCount);
		BatchTask task = new BatchTask();
		for (int i = 0; i < taskCount; i++) {
			exec.execute(task);
		}
		return this;
	}

	private void check() {
		if (batchHandler == null) {
			throw new RuntimeException("BatchHandler不能为空");
		}
	}

	private BatchHandleWrapper(BatchHandler<E> batchHandler) {
		this.batchHandler = batchHandler;
		check();
	}

	/**
	 * 队列大小为{@link Integer#MIN_VALUE}的批量操作包装器
	 * 
	 * @param batchHandler
	 *            批量处理器
	 * @param batchSize
	 *            批量操作上限
	 * @param timeoutMs
	 *            超时时间(单位：毫秒)
	 * @param taskCount
	 *            处理线程数量
	 */
	public BatchHandleWrapper(BatchHandler<E> batchHandler, int batchSize,
			int timeoutMs, int taskCount) {
		this(batchHandler, Integer.MAX_VALUE, batchSize, timeoutMs, taskCount);
	}

	/**
	 * 具有指定队列大小的批量操作包装器
	 * 
	 * @param batchHandler
	 *            批量处理器
	 * @param queueSize
	 *            队列大小
	 * @param batchSize
	 *            批量操作大小
	 * @param timeoutMs
	 *            取出元素超时时间(单位：毫秒)
	 * @param taskCount
	 *            处理线程数量
	 */
	public BatchHandleWrapper(BatchHandler<E> batchHandler, int queueSize,
			int batchSize, int timeoutMs, int taskCount) {
		this(batchHandler, queueSize, batchSize, timeoutMs, taskCount, null);
	}

	/**
	 * 具有指定队列大小和入队失败处理器的批量操作包装器
	 * 
	 * @param batchHandler
	 *            批量处理器
	 * @param queueSize
	 *            队列大小
	 * @param batchSize
	 *            批量操作大小
	 * @param timeoutMs
	 *            超时时间(单位：毫秒)
	 * @param taskCount
	 *            处理线程数量
	 * @param enqueueHandler
	 *            入队处理器
	 */
	public BatchHandleWrapper(BatchHandler<E> batchHandler, int queueSize,
			int batchSize, int timeoutMs, int taskCount,
			EnqueueHandler<E> enqueueHandler) {
		if (batchHandler == null || batchSize < 1 || timeoutMs < 0
				|| taskCount < 0) {
			throw new RuntimeException(
					"参数错误：BatchHandler不能为null，batchSize必须大于0，timeout必须大于0，taskCount必须大于0");
		}
		this.queue = new LinkedBlockingQueue<>(queueSize);
		this.batchSize = batchSize;
		this.timeout = timeoutMs;
		this.batchHandler = batchHandler;
		this.enqueueHandler = enqueueHandler;
		exec = Executors.newFixedThreadPool(taskCount);
		BatchTask task = new BatchTask();
		for (int i = 0; i < taskCount; i++) {
			exec.execute(task);
		}
	}

	public void add(E data) {
		try {
			if (enqueueHandler != null) {
				enqueueHandler.handle(queue, data);
			} else {
				queue.put(data);
			}
		} catch (Exception e) {
		}
	}

	public void add(E data, int timeoutMs) {
		try {
			if (enqueueHandler != null) {
				enqueueHandler.handle(queue, data);
			} else {
				if (!queue.offer(data, timeoutMs, TimeUnit.MILLISECONDS)) {
				}
			}
		} catch (Exception e) {
		}
	}

	public void stop() {
		running = false;
		exec.shutdownNow();
	}

	/**
	 * 批量处理器
	 */
	public interface BatchHandler<E> {
		/**
		 * 批量方法
		 * 
		 * @param list
		 *            待处理的数据列表
		 * @param task
		 *            负责处理数据的线程
		 */
		Integer handle(List<E> list, Runnable task);
	}

	/**
	 * 数据入队处理器
	 */
	public interface EnqueueHandler<E> {
		/**
		 * 处理方法
		 * 
		 * @param queue
		 *            保存数据的队列
		 * @param obj
		 *            待保存数据
		 * @param e
		 *            异常
		 */
		void handle(BlockingQueue<E> queue, E obj);
	}

	/**
	 * 批量处理任务
	 */
	private class BatchTask implements Runnable {

		@Override
		public void run() {
			ArrayList<E> list = new ArrayList<>(batchSize);
			while (running && !Thread.interrupted()) {
				try {
					E data = queue.poll(timeout, TimeUnit.MILLISECONDS);
					if (data == null) {
						batch(list);
					} else {
						list.add(data);
						if (list.size() == batchSize) {
							batch(list);
						}
					}
				} catch (Exception e) {

				}
			}
			// 停止处理后，批量处理内存中存在的数据
			while (queue.size() > 0) {
				E data = queue.poll();
				if (data != null) {
					list.add(data);
					if (list.size() == batchSize) {
						batch(list);
					}
				}
			}
			batch(list);
		}

		private void batch(ArrayList<E> list) {
			if (list.size() > 0) {
				try {
					batchHandler.handle(list, this);
				} finally {
					// 处理数据后，不管成功与否，均清空当前数据
					list.clear();
				}
			} else {
				 System.out.println("当前队列无数据");
			}
		}
	}

	public static void main(String[] args) {
		BatchHandler<Object> batchHandler = new BatchHandler<Object>() {
			@Override
			public Integer handle(List<Object> list, Runnable task) {
				System.out.println("#size=" + list.size() + ",#data=" + list);
				return 0;
			}
		};
		EnqueueHandler<Object> enqueueHandler = new EnqueueHandler<Object>() {
			@Override
			public void handle(BlockingQueue<Object> queue, Object obj) {
				queue.offer(obj);
			}
		};
		final BatchHandleWrapper<Object> bhw = BatchHandleWrapper
				.batchHandler(batchHandler).batchSize(2)
				.timeout(1, TimeUnit.SECONDS).taskCount(2)
				.enqueueHandler(enqueueHandler).start();

		bhw.add(1231);
		bhw.add(1232);
		bhw.add(1233);
		bhw.add(1234);
		bhw.add(1235);
		

		// Thread t = new Thread(){
		// public void run() {
		// for(int i=1;i<=150;i++){
		// bhw.add("第"+i,50);
		// }
		// System.err.println("我放完了");
		// bhw.stop();
		// };
		// };
		// t.start();

		// Runnable runnable = new Runnable() {
		// public void run() {
		// // task to run goes here
		// System.out.println("Hello !!");
		// }
		// };
		//
		// ScheduledExecutorService service = Executors
		// .newSingleThreadScheduledExecutor();
		// // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
		// service.scheduleAtFixedRate(runnable, 10, 1, TimeUnit.SECONDS);

	}
}