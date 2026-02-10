package nc.ui.ui.ahu.;

import nc.ui.trade.bill.AbstractManageController;
import nc.ui.trade.bill.ISingleController;
import nc.ui.trade.businessaction.IBusinessActionType;
import nc.ui.trade.button.IBillButton;
import nc.vo.ui.ahu..VO;
import nc.vo.ui.ahu..AggVO;
import nc.vo.ahu.pub.PubConst;

/**
 * 生产入库单 - 控制器
 * @author 
 * @since 
 */
public class Controller extends AbstractManageController implements ISingleController {

	private String billType = PubConst.AHU_BILLTYPE_;

	private String[] billVoName = new String[] {
			AggVO.class.getName(),
			VO.class.getName(),
			VO.class.getName()
		};

	public Controller() {
		super();
	}
	
	//是否自动管理树的增删改操作
	public boolean isAutoManageTree() {
		return false;
	}
	
	public String getBillType() {
		return billType;
	}

	public String[] getBillVoName() {
		return billVoName;
	}

	public int getBusinessActionType() {
		return IBusinessActionType.BD;
	}

	public int[] getCardButtonAry() {

		return new int[] {
				IBillButton.Query,
				IBillButton.ImportBill,
				IBillButton.Add,
				IBillButton.Edit,
				IBillButton.Delete,
				IBillButton.Save,
				IBillButton.Cancel,
				IBillButton.Refresh,
				IBillButton.Return
		};
	}

	public String[] getCardBodyHideCol() {
		return null;
	}

	public boolean isShowCardRowNo() {
		return true;
	}

	public boolean isShowCardTotal() {
		return false;
	}

	public String getBodyCondition() {
		return null;
	}

	public String getBodyZYXKey() {

		return null;
	}

	public String getPkField() {
		return null;
	}

	public String getChildPkField() {
		return null;
	}

	public String getHeadZYXKey() {
		return null;
	}

	public Boolean isEditInGoing() throws Exception {

		return null;
	}

	public boolean isExistBillStatus() {
		return false;
	}

	public boolean isLoadCardFormula() {
		return true;
	}

	/**
	 * 是否单表体,=true单表体，=false单表头。
	 */
	public boolean isSingleDetail() {
		return false;
	}


	public boolean isChildTree() {
		return false;
	}

	public boolean isTableTree() {
		return false;
	}

	public String[] getListBodyHideCol() {
		return null;
	}

	public int[] getListButtonAry() {
		return new int[] {
				IBillButton.Query,
				IBillButton.ImportBill,
				IBillButton.Add,
				IBillButton.Edit,
				IBillButton.Delete,
				IBillButton.Refresh,
				IBillButton.Print,
				IBillButton.Card
		};
	}

	public String[] getListHeadHideCol() {
		return null;
	}

	public boolean isShowListRowNo() {
		return false;
	}

	public boolean isShowListTotal() {
		return false;
	}

}
