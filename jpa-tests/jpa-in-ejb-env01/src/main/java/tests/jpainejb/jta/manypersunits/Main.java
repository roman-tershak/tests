package tests.jpainejb.jta.manypersunits;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import oracle.jdbc.xa.client.OracleXADataSource;

import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

public class Main {

	private static InitialContext initialContext;

	public static void main(String[] args) throws Exception {
//		bindDataSources();
		
		StatelessManyUnitsLocal statelessLocal = getOurStatelessBean();
//		statelessLocal.doSomethingSimple();
		
		statelessLocal.createEntitiesInManyUnits("entity oracle", "entity ms sql", "entity mysql");
	}

	private static StatelessManyUnitsLocal getOurStatelessBean() throws IOException,
			NamingException {
		Context context = getInitialContext();
		
		StatelessManyUnitsLocal statelessLocal = (StatelessManyUnitsLocal) context.lookup("StatelessManyUnitsBeanLocal");
		return statelessLocal;
	}

	public static Context getInitialContext() throws IOException, NamingException {
		if (initialContext == null) {
			Properties properties = new Properties();
			properties.load(Main.class.getResourceAsStream("/container.properties"));
			initialContext = new InitialContext(properties);
		}
		return initialContext;
	}

}
