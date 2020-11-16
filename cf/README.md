package com.mzc.boot;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SimpleBootApplication implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
	
    private static final int TIMEOUT = 50;

    private volatile Connector connector;
	
	public static void main(String[] args)  {
		SpringApplication.run(SimpleBootApplication.class, args);
	}

	@GetMapping("/")
	public String test() throws InterruptedException  {
		Thread.sleep(100000);
		return "Hi";
	}
	
	@GetMapping("/success")
	public String success() throws InterruptedException  {
		Thread.sleep(100000);
		return "success";
	}

	@GetMapping("/done")
	public String done() throws InterruptedException  {
		Thread.sleep(100000);
		return "done";
	}
	
	@PreDestroy
	public void preDestory() throws InterruptedException {
		System.out.println("##########################preDestory Start##################################");
		System.out.println("##########################Success##########################");
		System.out.println("##########################preDestory End##########################");
	}
	
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
	    this.connector.pause();
	    Executor executor = this.connector.getProtocolHandler().getExecutor();
	    if (executor instanceof ThreadPoolExecutor) {
	        try {
	            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
	            
	            System.out.println("=====================threadPoolExecutor Task Count=====================" + threadPoolExecutor.getTaskCount());
	            System.out.println("=====================threadPoolExecutor Active Count=====================" + threadPoolExecutor.getActiveCount());
	            System.out.println("=====================threadPoolExecutor getPoolSize=====================" + threadPoolExecutor.getPoolSize());
	            System.out.println("=====================threadPoolExecutor getQueue=====================" + threadPoolExecutor.getQueue());
	            System.out.println("=====================threadPoolExecutor getMaximumPoolSize=====================" + threadPoolExecutor.getMaximumPoolSize());
	            
	            threadPoolExecutor.shutdown();
	            if (!threadPoolExecutor.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {
	                System.out.println(("=====================Tomcat thread pool did not shut down gracefully within "
	                        + TIMEOUT + " seconds. Proceeding with forceful shutdown====================="));
	               
	                threadPoolExecutor.shutdownNow();
	                
	                if (!threadPoolExecutor.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {
	                	System.out.println("=====================Tomcat thread pool did not terminate=====================");
	                	System.out.println("Pool did not terminate");
                        throw new CommonException("===============================this thread interrupt1===================================================");
	                }
	                throw new CommonException("===============================this thread interrupt2===================================================");
	            }
	        } catch (Exception ex) {
	        	System.out.println("this thread interrupt3");
	        	ex.printStackTrace();
	        }
	    }
	}

	@Override
	public void customize(Connector connector) {
		this.connector = connector;
	}
	
	
	@GetMapping("/health")
	public String health() throws InterruptedException {
		return "ok";
	}
	
	@GetMapping("/oom")
	public void oom() throws Exception {
		try {
		int iteratorValue = 20;
		System.out.println("\n=================> OOM test started..\n");
		for (int outerIterator = 5; outerIterator < 20; outerIterator++) {
			System.out.println("Iteration " + outerIterator + " Free Mem: " + Runtime.getRuntime().freeMemory());
			int loop1 = 2;
			int[] memoryFillIntVar = new int[iteratorValue];
			// feel memoryFillIntVar array in loop..
			do {
				memoryFillIntVar[loop1] = 0;
				loop1--;
			} while (loop1 > 0);
			iteratorValue = iteratorValue * 5;
			System.out.println("\nRequired Memory for next loop: " + iteratorValue);
			Thread.sleep(1000);
		}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}



