package pres;

import java.util.Map;

import javax.ejb.Remote;

@Remote
public interface GenericBeanInterface {

	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts) throws Exception;
}
