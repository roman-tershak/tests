package tests.jpainejb.jta.propagation;

import java.io.IOException;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import tests.jpainejb.jta.manypersunits.Main;
import tests.jpainejb.jta.manypersunits.StatelessManyUnitsLocal;

public class MainPropagation {

	public static void main(String[] args) throws Exception {
		StatelessManyUnitsLocal statelessManyUnitsLocal = (StatelessManyUnitsLocal) Main.getInitialContext().lookup(
				"StatelessManyUnitsBeanLocal");
		StatefulPropagation statefulPropagation = (StatefulPropagation) Main.getInitialContext().lookup(
				"StatefulPropagationImplLocal");
		
//		enlistContext("java:openejb");
		
		String suffix = "f8";
		String[] names = new String[] {"ent 1a " + suffix, "ent 1b " + suffix, "ent 1c " + suffix};
//		statelessManyUnitsLocal.createEntitiesInManyUnits(names);
		Long[] ids = statelessManyUnitsLocal.createEntitiesInManyUnitsAndReturnIds(names);
		
//		statefulPropagation.doSomething(names);
		statefulPropagation.doSomething(ids);
	}

	public static void enlistContext(String ctxName) throws NamingException, IOException {
		NamingEnumeration<NameClassPair> namingEnumeration = Main.getInitialContext().list(ctxName);
		System.out.println(ctxName + ":");
		while (namingEnumeration.hasMore()) {
			NameClassPair nameClassPair = namingEnumeration.next();
			System.out.println("\t" + nameClassPair.getName() + " - " + nameClassPair.getClassName());
		}
	}

}
