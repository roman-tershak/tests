package pres;

import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.Clustered;

@Stateless(mappedName="CleaningBean")
@Clustered
public class CleaningBean implements GenericBeanInterface {

	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts) throws Exception {
		System.out.println("\t\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\t");
		return null;
	}

}
