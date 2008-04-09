package org.nightlabs.jfire.trade.ui.overview.invoice;

import org.eclipse.ui.IWorkbenchPart;
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
public class InvoiceEntryFactory
extends AbstractEntryFactory
{

	public InvoiceEntryFactory() {
		super();
	}

	public Entry createEntry() {
		return new DefaultEntry(this) {

			public EntryViewer createEntryViewer() {
				return new InvoiceEntryViewer(this);
			}

			public IWorkbenchPart handleActivation() {
				try {
					return RCPUtil.openEditor(
							new OverviewEntryEditorInput(this),
							InvoiceEntryEditor.EDITOR_ID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
			
		};
	}

}
