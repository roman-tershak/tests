package jta_tests.test02;

import java.util.List;

import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

public class Util {

	public static void enlist(Transaction trans, XAResource xaRes) throws Exception {
		System.out.println("enlisting " + xaRes + " to " + trans + " - " + trans.enlistResource(xaRes));
	}
	
	public static void enlist(Transaction trans, List<XAResource> xaResources) throws Exception {
		for (XAResource xaResource : xaResources) {
			enlist(trans, xaResource);
		}
	}
	
	public static void delistSuccess(Transaction trans, XAResource xaRes) throws Exception {
		System.out.println("delisting for success " + xaRes + " from " + trans + " - " + 
				trans.delistResource(xaRes, XAResource.TMSUCCESS));
	}
	
	public static void delistSuspend(Transaction trans, XAResource xaRes) throws Exception {
		System.out.println("suspending " + trans + " in " + xaRes + " - " + 
				trans.delistResource(xaRes, XAResource.TMSUSPEND));
	}
	
	public static void delistFail(Transaction trans, XAResource xaRes) throws Exception {
		System.out.println("delisting for fail " + xaRes + " from " + trans + " - " + 
				trans.delistResource(xaRes, XAResource.TMFAIL));
	}

	public static void delistSuccess(Transaction trans, List<XAResource> xaResources) throws Exception {
		for (XAResource xaResource : xaResources) {
			delistSuccess(trans, xaResource);
		}
	}
	
	public static void delistSuspend(Transaction trans, List<XAResource> xaResources) throws Exception {
		for (XAResource xaResource : xaResources) {
			delistSuspend(trans, xaResource);
		}
	}
	
	public static void delistFail(Transaction trans, List<XAResource> xaResources) throws Exception {
		for (XAResource xaResource : xaResources) {
			delistFail(trans, xaResource);
		}
	}
	
	public static void startThreads(Thread... threads) {
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
	}

	public static void joinThreads(Thread... threads) throws InterruptedException {
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
	}
}
