package org.nightlabs.jfire.trade.ui.overview.order;

import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.overview.AbstractEntryFactory;
import org.nightlabs.jfire.base.ui.overview.DefaultEntry;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.EntryViewer;
import org.nightlabs.jfire.base.ui.overview.OverviewEntryEditorInput;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class OrderEntryFactory 
extends AbstractEntryFactory 
{

	public OrderEntryFactory() {
	}

	public Entry createEntry() {
		return new DefaultEntry(this) {

			public EntryViewer createEntryViewer() {
				return new OrderEntryViewer(this);
			}

			public void handleActivation() {
				try {
					RCPUtil.openEditor(
							new OverviewEntryEditorInput(this), 
							OrderEntryEditor.EDITOR_ID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

}
