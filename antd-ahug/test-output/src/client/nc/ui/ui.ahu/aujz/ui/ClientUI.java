package nc.ui.ui.ahu.;


import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pub.bill.BillItem;
import nc.ui.trade.bill.AbstractManageController;
import nc.ui.trade.bsdelegate.BusinessDelegator;
import nc.ui.trade.manage.BillManageUI;
import nc.ui.trade.manage.ManageEventHandler;
import nc.vo.ui.ahu..VO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.trade.field.IBillField;

@SuppressWarnings("serial")
public class ClientUI extends BillManageUI {

	
	public ClientUI() {
		super();
	}
	
	public ClientUI(String pk_corp, String pk_billType, String pk_busitype,
			String operater, String billId) {
		super(pk_corp, pk_billType, pk_busitype, operater, billId);
	}
	
	
	@Override
	protected AbstractManageController createController() {
		return new Controller();
	}
	
	@Override
	protected BusinessDelegator createBusinessDelegator() {
		return new Delegator();
	}
	
	@Override
	protected ManageEventHandler createEventHandler() {
		return new EventHandler(this, getUIControl());
	}

	@Override
	public void setBodySpecialData(CircularlyAccessibleValueObject[] vos) throws Exception {
		
	}

	@Override
	protected void setHeadSpecialData(CircularlyAccessibleValueObject vo, int intRow) throws Exception {
		
	}

	@Override
	protected void setTotalHeadSpecialData(CircularlyAccessibleValueObject[] vos) throws Exception {
		
	}

	@Override
	protected void initSelfData() {
		
	}

	@Override
	public void setDefaultData() throws Exception {
		
		IBillField billfield=getBillField();
		String[] itemkeys=new String[]{
				billfield.getField_Corp(),
				VO.CREATEOR,
				VO.CREATEDATE,
				VO.VCODE
		};
		
		Object[] values=new Object[]{
				_getCorp().getPrimaryKey(),
				_getOperator(),
				_getServerTime(),
				String.valueOf(getNewCode())
		};
		for(int i=0;i<itemkeys.length;i++){
			BillItem item=null;
			item=getBillCardPanel().getHeadItem(itemkeys[i]);
			if(item==null){
				item=getBillCardPanel().getTailItem(itemkeys[i]);
			}
			if(item!=null){
				item.setValue(values[i]);
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public int getNewCode()throws BusinessException{
		int count=1;
		String whereSql=" select * from _h where pk_corp='"+_getCorp().getPrimaryKey()+"'";
		List<Object> result=(ArrayList<Object>) queryUAPServer().executeQuery(whereSql, new BeanListProcessor(VO.class));
		if(result!=null&&result.size()>0){
			count=result.size()+1;
		}
		return count;
	}
	
	
	private IUAPQueryBS uapserver;
	
	private IUAPQueryBS queryUAPServer(){
		if(uapserver==null){
			uapserver=NCLocator.getInstance().lookup(IUAPQueryBS.class);
		}
		return uapserver;
	}
	
}
