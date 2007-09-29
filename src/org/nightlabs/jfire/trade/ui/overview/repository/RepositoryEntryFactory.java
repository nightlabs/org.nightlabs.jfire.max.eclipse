package org.nightlabs.jfire.trade.ui.overview.repository;

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
public class RepositoryEntryFactory 
extends AbstractEntryFactory 
{

	public RepositoryEntryFactory() {
	}

	public Entry createEntry() {
		return new DefaultEntry(this) {

			public EntryViewer createEntryViewer() {
				return new RepositoryEntryViewer(this);
			}

			public void handleActivation() {
				try {
					RCPUtil.openEditor(
							new OverviewEntryEditorInput(this), RepositoryEntryEditor.EDITOR_ID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

}
