package nc.ui.ui.ahu.aujz.action;

import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.AbstractAppModel;

/**
 * 生产入库单 业务动作
 * 创建日期:2026-02-10
 * @author Flynn Chen
 */
public class BusinessAction extends NCAction {

    private static final long serialVersionUID = 1L;
    private AbstractAppModel model;

    public BusinessAction(AbstractAppModel model) {
        super("业务操作");
        this.model = model;
        setCode("businessAction");
    }

    @Override
    public void doAction() {
        
    }

    @Override
    protected boolean isActionEnable() {
        return model.getSelectedData() != null;
    }
}
