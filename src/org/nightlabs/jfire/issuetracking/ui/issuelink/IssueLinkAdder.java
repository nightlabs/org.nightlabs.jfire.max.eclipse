package org.nightlabs.jfire.issuetracking.ui.issuelink;


import java.util.Set;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.progress.ProgressMonitor;

/**
 * An interface for all issue link adder things.
 * 
 * <p>
 * Implementors are advised not to directly implement this interface but instead subclass {@link AbstractIssueLinkAdder}.
 * </p>
 *
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 */
public interface IssueLinkAdder extends ISelectionProvider
{
	/**
	 * 
	 * @param handlerFactory
	 */
	void init(IssueLinkHandlerFactory handlerFactory);

	/**
	 * 
	 * @return
	 */
	IssueLinkHandlerFactory getIssueLinkHandlerFactory();
	
	/**
	 * Create the <tt>Composite</tt> which serves the functionality to add
	 * a {@link org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer}.
	 *
	 * @param parent The parent composite into all GUI elements should be created
	 * @return The newly created <tt>Composite</tt>
	 */
	Composite createComposite(Composite parent);

	/**
	 * 
	 * @return The composite of the link adder
	 */
	Composite getComposite();

	/**
	 * This callback method must be called by a {@link DisposeListener} which is added to your
	 * composite (created in {@link #createComposite(Composite)}).
	 */
	void onDispose();

	/**
	 * 
	 * @param listener
	 */
	void addIssueLinkDoubleClickListener(IssueLinkDoubleClickListener listener);
	
	/**
	 * 
	 * @param listener
	 */
	void removeIssueLinkDoubleClickListener(IssueLinkDoubleClickListener listener);

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * Since this interface extends {@link ISelectionProvider}, this method needs to be implemented by classes
	 * implementing <code>IssueLinkAdder</code>. This method is required to return an {@link IStructuredSelection}
	 * with the same objects as returned by {@link #getLinkedObjectIDs()}.
	 * </p>
	 */
	@Override
	public IStructuredSelection getSelection();

	/**
	 * Get the selected object ids to be linked to the issue. These objects must be the same as returned by the
	 * {@link #getSelection()}.
	 *
	 * @return the currently selected {@link ObjectID} instances.
	 */
	Set<ObjectID> getLinkedObjectIDs();
	
	/**
	 * 
	 * @param issue
	 * @param issueLinkType
	 * @param monitor
	 * @return
	 */
	Set<IssueLink> createIssueLinks(
			Issue issue,
			IssueLinkType issueLinkType,
			ProgressMonitor monitor);
}