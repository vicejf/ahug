package nc.bs.ahu.aujz;

import nc.bs.framework.common.NCLocator;
import nc.itf.ahu.IAUJZ;
import nc.vo.ui.ahu.aujz.AggAUJZVO;
import nc.vo.pub.AggregatedValueObject;

/**
 * 生产入库单 插入动作
 * 创建日期:2026-02-10
 * @author Flynn Chen
 */
public class InsertAction {

    public AggregatedValueObject insert(AggregatedValueObject billVO) throws Exception {
        IAUJZ service = NCLocator.getInstance().lookup(IAUJZ.class);
        return service.insert(billVO);
    }
}
