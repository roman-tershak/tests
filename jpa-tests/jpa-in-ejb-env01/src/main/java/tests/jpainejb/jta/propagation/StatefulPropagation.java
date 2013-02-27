package tests.jpainejb.jta.propagation;

import javax.ejb.Local;

@Local
public interface StatefulPropagation {

	void doSomething(String... names) throws Exception;

	void doSomething(Long... ids) throws Exception;
}
