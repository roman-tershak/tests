package tests;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

//import tests.t01.Result01;

public class MainC {

	private static final int NUM_OF_THREADS = 10;
	private static final int NUM_OF_OPP_TIMES = 10;

	public static void main(String[] args) throws Exception {
		final Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		properties.put("java.naming.provider.url", "localhost:1300");
		
		final Object lock = new Object();
		
		Thread[] threads = new Thread[NUM_OF_THREADS];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread() {

				@Override
				public void run() {
					try {
						final InitialContext context = new InitialContext(properties);
//						final SbRemote remote01 = (SbRemote) context.lookup("SfsBeanCA");
						
//						List<Result01> results = new LinkedList<Result01>();
						for (int i = 0; i < NUM_OF_OPP_TIMES; i++) {
							try {
//								synchronized (lock) {
//								results.add(remote01.opp2());
//									System.out.println(this.getName() + "\n" + remote01.opp1());
//								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
//						System.out.println(this.getName() + "\n" + results);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		}
		
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
		
	}
}
