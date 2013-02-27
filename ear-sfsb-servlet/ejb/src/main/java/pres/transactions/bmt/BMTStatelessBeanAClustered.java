package pres.transactions.bmt;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.jboss.ejb3.annotation.Clustered;

@Stateless(mappedName="BMTStatelessBeanAClustered")
@TransactionManagement(TransactionManagementType.BEAN)
@Clustered
public class BMTStatelessBeanAClustered extends BMTStatelessBeanA {
}
