package org.nightlabs.jfire.trade.ui.overview.moneytransfer;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.overview.AbstractEntryFactory;
import org.nightlabs.jfire.base.ui.overview.DefaultEntry;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.EntryViewer;
import org.nightlabs.jfire.base.ui.overview.OverviewEntryEditorInput;

public class MoneyTranferEntryFactory 
extends AbstractEntryFactory
{
	public MoneyTranferEntryFactory() {
	}

	public Entry createEntry() {
		return new DefaultEntry(this) {

			public EntryViewer createEntryViewer() {
				return new MoneyTransferEntryViewer(this);
			}

			public IWorkbenchPart handleActivation() {
				try {
					return RCPUtil.openEditor(
							new OverviewEntryEditorInput(this), MoneyTransferEntryEditor.EDITOR_ID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
}