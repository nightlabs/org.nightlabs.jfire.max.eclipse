/**
 *
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reserve;

import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.action.SelectionAction;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeGroupTree.ProductTypeGroupNode;
import org.nightlabs.jfire.trade.ui.reserve.ReservationEditor;
import org.nightlabs.jfire.trade.ui.reserve.ReservationEditorInput;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class ReservationListAction
extends SelectionAction
{
	public static final String ID = ReservationListAction.class.getName();

	private ProductTypeID eventID;

	public ReservationListAction()
	{
		super();
		setId(ID);
		setText("Reservation list");
		setToolTipText("Opens the reservation list for the selected event");
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.action.IUpdateActionOrContributionItem#calculateEnabled()
	 */
	@Override
	public boolean calculateEnabled() {
		if (getSelectedObjects().size() == 1) {
			Object o = getSelectedObjects().iterator().next();
			if (o instanceof ProductTypeID) {
				eventID = (ProductTypeID) o;
				return true;
			}
			else if (o instanceof ProductTypeGroupNode) {
				ProductTypeGroupNode node = (ProductTypeGroupNode) o;
				eventID = (ProductTypeID) JDOHelper.getObjectId(node.getProductType());
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.action.IUpdateActionOrContributionItem#calculateVisible()
	 */
	@Override
	public boolean calculateVisible() {
		return true;
	}

	@Override
	public void run() {
		if (eventID != null) {
			try {
//				Editor2PerspectiveRegistry.sharedInstance().openEditor(
//						new ReservationEditorInput(eventID),
//						ReservationEditor.EDITOR_ID);
				RCPUtil.openEditor(new ReservationEditorInput(eventID), ReservationEditor.EDITOR_ID);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
