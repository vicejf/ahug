package nc.ui.ui.ahu.aujz.delegator;

import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.ActionContainer;
import nc.ui.uif2.actions.batch.BatchAddLineAction;
import nc.ui.uif2.actions.batch.BatchDelLineAction;
import nc.ui.uif2.actions.batch.BatchSaveAction;
import nc.ui.uif2.model.AbstractAppModel;

/**
 * 生产入库单 委托器
 * 创建日期:2026-02-10
 * @author Flynn Chen
 */
public class ClientDelegator {

    private AbstractAppModel model;

    public ClientDelegator(AbstractAppModel model) {
        this.model = model;
    }

    public void addActions(ActionContainer container) {
        container.addAction(new BatchAddLineAction());
        container.addAction(new BatchDelLineAction());
        container.addAction(new BatchSaveAction());
    }
}
