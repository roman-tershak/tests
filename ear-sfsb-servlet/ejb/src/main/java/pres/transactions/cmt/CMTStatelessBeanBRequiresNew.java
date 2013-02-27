package pres.transactions.cmt;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import pres.GenericBeanInterface;

@Stateless(mappedName="CMTStatelessBeanBRequiresNew")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CMTStatelessBeanBRequiresNew extends CMTStatelessBeanB {

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Override
	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts) throws Exception {
		return super.opp(params, nexts);
	}
}
