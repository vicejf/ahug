package nc.bs.ahu.aujz;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.vo.ui.ahu.aujz.AggAUJZVO;
import nc.vo.pub.BusinessException;

/**
 * 生产入库单 规则类
 * 创建日期:2026-02-10
 * @author Flynn Chen
 */
public class Rule {

    private static final long serialVersionUID = 1L;

    public void beforeInsert(AggAUJZVO billVO) throws BusinessException {
    }

    public void afterInsert(AggAUJZVO billVO) throws BusinessException {
    }

    public void beforeUpdate(AggAUJZVO billVO) throws BusinessException {
    }

    public void afterUpdate(AggAUJZVO billVO) throws BusinessException {
    }

    public void beforeDelete(String[] pkArray) throws BusinessException {
    }

    public void afterDelete(String[] pkArray) throws BusinessException {
    }
}
