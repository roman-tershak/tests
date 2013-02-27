package tests.jpainejb.jta.simple;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Main {

	public static void main(String[] args) throws Exception {
		StatelessLocal statelessLocal = getOurStatelessBean();
//		statelessLocal.doSomethingSimple();
		
		statelessLocal.createPerson("person 1");
		statelessLocal.createPerson("person 2");
		statelessLocal.createPerson("person 3");
		statelessLocal.createPerson("person 4");
		statelessLocal.createPerson("person 5");
	}

	private static StatelessLocal getOurStatelessBean() throws IOException,
			NamingException {
		Context context = getInitialContext();
		
		StatelessLocal statelessLocal = (StatelessLocal) context.lookup("StatelessBeanLocal");
		return statelessLocal;
	}

	private static Context getInitialContext() throws IOException, NamingException {
		Properties properties = new Properties();
		properties.load(Main.class.getResourceAsStream("/container.properties"));
		
		return new InitialContext(properties);
	}

}
