package nc.bs.ui.ahu.;

import nc.impl.dygpubapp.persitence.bill.BillQuery;
import nc.impl.dygpubapp.persitence.vo.VOQuery;
import nc.vo.ui.ahu..VO;
import nc.vo.ui.ahu..AggVO;
import nc.vo.pub.SuperVO;

public class QueryAction {
	public SuperVO[] queryByCondition(Class voClass, String whereSql) {
		SuperVO[] vos = null;
		if (voClass.getName().equals(VO.class.getName())) {
			vos = this.queryHeadData(whereSql, null);
		}
		return vos;
	}

	public VO[] queryHeadData(String whereSql, String order) {
		VOQuery<VO> querybo = new VOQuery<VO>(VO.class);
		VO[] headvos = querybo.queryWithWhereKeyWord(whereSql,order);
		return headvos;
	}

	public AggVO[] queryBillData(String[] keys) {
		BillQuery<AggVO> querybo = new BillQuery<AggVO>(AggVO.class);
		AggVO[] billvos = querybo.query(keys);
		return billvos;
	}

}
