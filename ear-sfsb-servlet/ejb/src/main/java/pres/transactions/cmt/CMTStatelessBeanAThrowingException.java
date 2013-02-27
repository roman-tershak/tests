package pres.transactions.cmt;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import pres.GenericBeanInterface;

@Stateless(mappedName="CMTStatelessBeanAThrowingException")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CMTStatelessBeanAThrowingException extends CMTStatelessBeanA {

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts) throws Exception {
		
		super.opp(params, nexts);
		
		throw new RuntimeException("Unconditional RuntimeException");
	}
}
