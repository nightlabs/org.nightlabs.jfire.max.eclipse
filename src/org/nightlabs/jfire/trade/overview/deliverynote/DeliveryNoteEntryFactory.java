package org.nightlabs.jfire.trade.overview.deliverynote;

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
public class DeliveryNoteEntryFactory 
extends AbstractEntryFactory 
{

	public DeliveryNoteEntryFactory() {
	}

	public Entry createEntry() {
		return new DefaultEntry (this) {

			public EntryViewer createEntryViewer() {
				return new DeliveryNoteEntryViewer(this);
			}

			public void handleActivation() {
				try {
					RCPUtil.openEditor(
							new OverviewEntryEditorInput(this), 
							DeliveryNoteEntryEditor.EDITOR_ID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
			
		};
	}

}
