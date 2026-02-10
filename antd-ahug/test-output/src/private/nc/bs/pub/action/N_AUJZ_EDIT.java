package nc.bs.ahu.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.itf.ahu.IAUJZ;
import nc.vo.ui.ahu.aujz.AggAUJZVO;
import nc.vo.pub.AggregatedValueObject;

/**
 * 生产入库单 编辑动作
 * 创建日期:2026-02-10
 * @author Flynn Chen
 */
public class N_AUJZ_EDIT {

    public AggregatedValueObject edit(String pk) throws Exception {
        IAUJZ service = NCLocator.getInstance().lookup(IAUJZ.class);
        return service.query(new String[]{pk})[0];
    }
}
