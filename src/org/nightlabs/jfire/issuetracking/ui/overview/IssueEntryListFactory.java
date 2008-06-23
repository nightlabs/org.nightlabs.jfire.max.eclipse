package org.nightlabs.jfire.issuetracking.ui.overview;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.overview.AbstractEntryFactory;
import org.nightlabs.jfire.base.ui.overview.DefaultEntry;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.EntryViewer;
import org.nightlabs.jfire.base.ui.overview.OverviewEntryEditorInput;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueEntryListFactory 
extends AbstractEntryFactory 
{
	public static final String ID = IssueEntryListFactory.class.getName();
	public IssueEntryListFactory() {
	}

	public Entry createEntry() {
		return new DefaultEntry (this) {

			public EntryViewer createEntryViewer() {
				return new IssueEntryListViewer(this);
			}

			public IWorkbenchPart handleActivation() {
				try {
					return RCPUtil.openEditor(
							new OverviewEntryEditorInput(this), 
							IssueEntryListEditor.EDITOR_ID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
			
		};
	}

}