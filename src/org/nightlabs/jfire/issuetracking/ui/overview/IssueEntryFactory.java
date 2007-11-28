package org.nightlabs.jfire.issuetracking.ui.overview;

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
public class IssueEntryFactory 
extends AbstractEntryFactory 
{

	public IssueEntryFactory() {
	}

	public Entry createEntry() {
		return new DefaultEntry (this) {

			public EntryViewer createEntryViewer() {
				return null;
//				return new IssueEntryViewer(this);
			}

			public void handleActivation() {
				try {
					RCPUtil.openEditor(
							new OverviewEntryEditorInput(this), 
							IssueEditor.EDITOR_ID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
			
		};
	}

}