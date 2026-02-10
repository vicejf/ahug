package nc.vo.ui.ahu.aujz;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.VOUtils;

/**
 * 生产入库单 聚合VO
 * 创建日期:2026-02-10
 * @author Flynn Chen
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class AggAUJZVO extends AggregatedValueObject {

	private static final long serialVersionUID = 1L;

	public AggAUJZVO() {
		super();
	}

	@Override
	public CircularlyAccessibleValueObject[] getChildrenVO() {
		return super.getChildrenVO();
	}

	@Override
	public void setChildrenVO(CircularlyAccessibleValueObject[] childrenVO) {
		super.setChildrenVO(childrenVO);
	}

	@Override
	public CircularlyAccessibleValueObject getParentVO() {
		return super.getParentVO();
	}

	@Override
	public void setParentVO(CircularlyAccessibleValueObject parentVO) {
		super.setParentVO(parentVO);
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_aujz";
	}

	@Override
	public String getChildrenPKFieldName() {
		return "pk_aujz_b";
	}

	@Override
	public String getTableName() {
		return "ahu_aujz_h";
	}

	@Override
	public IVOMeta getMetaData() {
		return VOUtils.getVOMeta(this);
	}
}
