package nc.bs.ahu.aujz;

import nc.bs.framework.common.NCLocator;
import nc.itf.ahu.IAUJZ;
import nc.vo.ui.ahu.aujz.AggAUJZVO;
import nc.vo.pub.AggregatedValueObject;

/**
 * 生产入库单 更新动作
 * 创建日期:2026-02-10
 * @author Flynn Chen
 */
public class UpdateAction {

    public AggregatedValueObject update(AggregatedValueObject billVO) throws Exception {
        IAUJZ service = NCLocator.getInstance().lookup(IAUJZ.class);
        return service.update(billVO);
    }
}
