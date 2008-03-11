package org.nightlabs.jfire.issuetracking.ui.issuelink;


import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public interface IssueLinkAdder 
{
	void init(IssueLinkHandlerFactory handlerFactory);

	IssueLinkHandlerFactory getIssueLinkHandlerFactory();
	
	/**
	 * Create the <tt>Composite</tt> which serves the functionality to add
	 * a {@link org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer}.
	 *
	 * @param parent The parent composite into all GUI elements should be created.
	 * @return The newly created <tt>Composite</tt>.
	 */
	Composite createComposite(Composite parent);

	Composite getComposite();

	void onDispose();

	void dispose();
	
	boolean isComplete();
	
	void addIssueLinkSelectionListener(IssueLinkSelectionListener listener);
	
	void removeIssueLinkSelectionListener(IssueLinkSelectionListener listener);
	
	void addIssueLinkDoubleClickListener(IssueLinkDoubleClickListener listener);
	
	void removeIssueLinkDoubleClickListener(IssueLinkDoubleClickListener listener);
	
	Set<ObjectID> getIssueLinkObjectIds();
	
	Set<IssueLink> createIssueLinks(
			Issue issue,
			IssueLinkType issueLinkType,
//			Set<ObjectID> linkedObjectIDs,
			ProgressMonitor monitor);
}