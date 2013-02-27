package tests.jpainejb.jta.manypersunits;

import javax.ejb.Local;

@Local
public interface StatelessManyUnitsLocal {

	void doSomethingSimple();

	void createEntitiesInManyUnits(String... names) throws Exception;
	
	Long[] createEntitiesInManyUnitsAndReturnIds(String... names) throws Exception;
}
