package nc.vo.ui.ahu.aujz;

import java.util.Set;

import nc.vo.dygpubapp.appframe.entity.def.ISuperVO;
import nc.vo.dygpubapp.appframe.entity.util.SuperVOHelper;
import nc.vo.dygpubapp.appframe.meta.def.IVOMeta;
import nc.vo.dygpubapp.appframe.meta.impl.VOMetaFactory;
import nc.vo.pub.*;

/**
 * 生产入库单
 * 创建日期:2026-02-10
 * @author Flynn Chen
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")

public class AUJZHVO extends SuperVO implements ISuperVO{
	
	
	public static final String TABLENAME="ahu_aujz_h";
	

	private String.charAt(0).toUpperCase() + field.name.slice(1)  () {
		return pk_aujz;
	}   
	/**
	 * 属性pk_aujz的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newPk_aujz String
	 */
	public void setPk_aujz (String newPk_aujz ) {
	 	this.pk_aujz = newPk_aujz;
	} 	  

	private String.charAt(0).toUpperCase() + field.name.slice(1)  () {
		return billno;
	}   
	/**
	 * 属性billno的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newBillno String
	 */
	public void setBillno (String newBillno ) {
	 	this.billno = newBillno;
	} 	  

	private UFDate.charAt(0).toUpperCase() + field.name.slice(1)  () {
		return billdate;
	}   
	/**
	 * 属性billdate的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newBilldate UFDate
	 */
	public void setBilldate (UFDate newBilldate ) {
	 	this.billdate = newBilldate;
	} 	  

	private String.charAt(0).toUpperCase() + field.name.slice(1)  () {
		return corp;
	}   
	/**
	 * 属性corp的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newCorp String
	 */
	public void setCorp (String newCorp ) {
	 	this.corp = newCorp;
	} 	  

	private String.charAt(0).toUpperCase() + field.name.slice(1)  () {
		return vbusitype;
	}   
	/**
	 * 属性vbusitype的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newVbusitype String
	 */
	public void setVbusitype (String newVbusitype ) {
	 	this.vbusitype = newVbusitype;
	} 	  

	private String.charAt(0).toUpperCase() + field.name.slice(1)  () {
		return operatorid;
	}   
	/**
	 * 属性operatorid的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newOperatorid String
	 */
	public void setOperatorid (String newOperatorid ) {
	 	this.operatorid = newOperatorid;
	} 	  

	private String.charAt(0).toUpperCase() + field.name.slice(1)  () {
		return reviewer;
	}   
	/**
	 * 属性reviewer的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newReviewer String
	 */
	public void setReviewer (String newReviewer ) {
	 	this.reviewer = newReviewer;
	} 	  

	private String.charAt(0).toUpperCase() + field.name.slice(1)  () {
		return vstatus;
	}   
	/**
	 * 属性vstatus的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newVstatus String
	 */
	public void setVstatus (String newVstatus ) {
	 	this.vstatus = newVstatus;
	} 	  

	private String.charAt(0).toUpperCase() + field.name.slice(1)  () {
		return reviewnote;
	}   
	/**
	 * 属性reviewnote的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newReviewnote String
	 */
	public void setReviewnote (String newReviewnote ) {
	 	this.reviewnote = newReviewnote;
	} 	  

	private UFDate.charAt(0).toUpperCase() + field.name.slice(1)  () {
		return reviewdate;
	}   
	/**
	 * 属性reviewdate的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newReviewdate UFDate
	 */
	public void setReviewdate (UFDate newReviewdate ) {
	 	this.reviewdate = newReviewdate;
	} 	  

	private Integer.charAt(0).toUpperCase() + field.name.slice(1)  () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newDr Integer
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  

	private UFDateTime.charAt(0).toUpperCase() + field.name.slice(1)  () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newTs UFDateTime
	 */
	public void setTs (UFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  

	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2026-02-10
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newDr java.lang.Integer
	 */
	public void setDr (java.lang.Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2026-02-10
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2026-02-10
	 * @param newTs nc.vo.pub.lang.UFDateTime
	 */
	public void setTs (nc.vo.pub.lang.UFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
 
	/**
	  * <p>取得该VO父对象字段.
	  * <p>
	  * 创建日期:2026-02-10
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2026-02-10
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return PK_AUJZ;
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2026-02-10
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return TABLENAME;
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2026-02-10
	  */
     public AUJZHVO() {
		super();	
	}    
     
     public IVOMeta getMetaData() {
 		IVOMeta vOMeta = VOMetaFactory.getInstance().getVOMeta("aujz.AUJZHVO");
 		return vOMeta;
 	}

 	public Set<String> usedAttributeNames() {
 		return SuperVOHelper.usedAttributeNames(this);
 	}

 	public Object getAttributeValue(String attributeName) {
 		return SuperVOHelper.getAttributeValue(this, attributeName);
 	}


 	@Override
 	public void setAttributeValue(String attributeName, Object value) {
 		SuperVOHelper.setAttributeValue(this, attributeName, value);
 	}

 	@Override
 	public String getPrimaryKey() {
 		return SuperVOHelper.getPrimaryKey(this);
 	}

 	@Override
 	public void setPrimaryKey(String key) {
 		SuperVOHelper.setPrimaryKey(this, key);
 	}

 	@Override
 	public String[] getAttributeNames() {
 		return SuperVOHelper.getAttributeNames(this);
 	}
}
