package tests;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

public class Main {
	
	public static void main(String[] args) throws Exception {
		final Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		properties.put("java.naming.provider.url", "localhost:1300");
		
		final Object lock = new Object();
		
		Thread[] threads = new Thread[100];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread() {

				@Override
				public void run() {
					try {
						final InitialContext context = new InitialContext(properties);
//						final SbRemote slsbRemote = (SbRemote) context.lookup("sfsBeanB");
						
						for (int i = 0; i < 100; i++) {
							try {
//								synchronized (lock) {
//									System.out.println(this.getName() + "\n" + slsbRemote.opp1());
//								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
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
