/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.overview.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.jdo.JDOHelper;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.base.ui.action.WorkbenchPartSelectionAction;
import org.nightlabs.jfire.issue.id.IssueID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class AbstractIssueAction extends WorkbenchPartSelectionAction {

	private Collection<IssueID> selectedIssueIDs = Collections.emptyList();
	
	/**
	 * 
	 */
	public AbstractIssueAction() {
	}

	/**
	 * @param activePart
	 */
	public AbstractIssueAction(IWorkbenchPart activePart) {
		super(activePart);
	}

	/**
	 * @param text
	 */
	public AbstractIssueAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public AbstractIssueAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public AbstractIssueAction(String text, int style) {
		super(text, style);
	}

	@Override
	public void setSelection(ISelection selection) {
		super.setSelection(selection);
		if (!getSelection().isEmpty() && getSelection() instanceof StructuredSelection) {
			StructuredSelection sel = (StructuredSelection) getSelection();
			selectedIssueIDs = new ArrayList<IssueID>();
			for (Iterator it = sel.iterator(); it.hasNext(); ) {
				Object objectID = JDOHelper.getObjectId(it.next());
				if (objectID instanceof IssueID) {
					selectedIssueIDs.add((IssueID) objectID);
				}
			}
			
		}
		else if (getSelection().isEmpty()) {
			selectedIssueIDs = Collections.emptyList();
		}
	}
	
	public Collection<IssueID> getSelectedIssueIDs() {
		return selectedIssueIDs;
	}
	
	public void setSelectedIssueIDs(Collection<IssueID> issueIDs) {
		this.selectedIssueIDs = issueIDs;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.action.IUpdateActionOrContributionItem#calculateEnabled()
	 */
	public boolean calculateEnabled() {
		return getSelectedIssueIDs().size() > 0;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.action.IUpdateActionOrContributionItem#calculateVisible()
	 */
	public boolean calculateVisible() {
		return true;
	}

}
