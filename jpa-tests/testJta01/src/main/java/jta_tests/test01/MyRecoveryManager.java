package jta_tests.test01;

import com.arjuna.ats.arjuna.recovery.RecoveryManager;
import com.arjuna.ats.arjuna.recovery.RecoveryScan;
import com.arjuna.ats.arjuna.tools.RecoveryMonitor;

public class MyRecoveryManager {

	public static void main(String[] args) {
		RecoveryManager recoveryManager = RecoveryManager.manager();
		recoveryManager.scan(new RecoveryScan() {
			@Override
			public void completed() {
				System.out.println("Completed!");
			}
		});
	}
}
