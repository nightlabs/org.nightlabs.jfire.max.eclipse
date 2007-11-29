package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.overview.AbstractEntryFactory;
import org.nightlabs.jfire.base.ui.overview.DefaultEntry;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.EntryViewer;
import org.nightlabs.jfire.base.ui.overview.OverviewEntryEditorInput;

public class IssueTypeNewFactory extends AbstractEntryFactory {

	public IssueTypeNewFactory(){
		
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
							IssueTypeNewEditor.EDITOR_ID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
			
		};
	}

}
