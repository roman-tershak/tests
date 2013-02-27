package tests.jpainejb.jta.propagation;

import javax.ejb.Local;

@Local
public interface StatefulPropagationCallee {

	void doSomethingInCallee(String... names) throws Exception;

	void doSomethingInCallee(Long... ids) throws Exception;
}
