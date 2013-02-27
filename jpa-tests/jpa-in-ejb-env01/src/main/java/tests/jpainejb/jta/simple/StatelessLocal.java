package tests.jpainejb.jta.simple;

import javax.ejb.Local;

@Local
public interface StatelessLocal {

	void doSomethingSimple();

	void createPerson(String name);
}
