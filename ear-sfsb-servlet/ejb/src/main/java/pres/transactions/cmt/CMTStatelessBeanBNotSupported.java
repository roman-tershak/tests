package pres.transactions.cmt;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import pres.GenericBeanInterface;

@Stateless(mappedName="CMTStatelessBeanBNotSupported")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CMTStatelessBeanBNotSupported extends CMTStatelessBeanB {

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts) throws Exception {
		return super.opp(params, nexts);
	}
}
