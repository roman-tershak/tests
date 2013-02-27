package pres.transactions.cmt;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import pres.GenericBeanInterface;

@Stateless(mappedName="CMTStatelessBeanBMandatory")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CMTStatelessBeanBMandatory extends CMTStatelessBeanB {

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	@Override
	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts) throws Exception {
		return super.opp(params, nexts);
	}
}
