package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueLinkDAO;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class ShowLegalEntityLinkedTreeComposite 
extends AbstractTreeComposite
{
	LegalEntityIssuesLinkNode legalEntityIssuesLinkNode;
	
	private static class ContentProvider extends TreeContentProvider {

		public static final String[] FETCH_GROUPS = new String[] {
			FetchPlan.DEFAULT,
			IssueLink.FETCH_GROUP_ISSUE,
			Issue.FETCH_GROUP_ISSUE_TYPE,
			Issue.FETCH_GROUP_SUBJECT,
			Issue.FETCH_GROUP_DESCRIPTION,
			Issue.FETCH_GROUP_ISSUE_SEVERITY_TYPE,
			Issue.FETCH_GROUP_ISSUE_PRIORITY,
			Statable.FETCH_GROUP_STATE,
			Issue.FETCH_GROUP_ISSUE_LOCAL,
			StatableLocal.FETCH_GROUP_STATE,
			State.FETCH_GROUP_STATE_DEFINITION,
			IssueType.FETCH_GROUP_NAME,
			IssueSeverityType.FETCH_GROUP_NAME,
			IssuePriority.FETCH_GROUP_NAME,
			StateDefinition.FETCH_GROUP_NAME};

		private static final Object[] EMPTY_DATA = new Object[]{};

		public Object[] getElements(Object inputElement) {

			if (inputElement instanceof LegalEntityIssuesLinkNode)
			{
				LegalEntityIssuesLinkNode legalEntityIssueLinkNode = (LegalEntityIssuesLinkNode)inputElement;

				return
				IssueLinkDAO.sharedInstance().getIssueLinksByOrganisationIDAndLinkedObjectID(legalEntityIssueLinkNode.getOrganisationID(), 
						legalEntityIssueLinkNode.getPersonID(), 
						FETCH_GROUPS, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						new NullProgressMonitor()).toArray();

			}

			return EMPTY_DATA;
		}

		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#getChildren(java.lang.Object)
		 */
		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof LegalEntityIssuesLinkNode)
			{
				LegalEntityIssuesLinkNode legalEntityIssueLinkNode = (LegalEntityIssuesLinkNode)parentElement;
				return
				IssueLinkDAO.sharedInstance().getIssueLinksByOrganisationIDAndLinkedObjectID(legalEntityIssueLinkNode.getOrganisationID(), 
						legalEntityIssueLinkNode.getPersonID(), 
						FETCH_GROUPS, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						new NullProgressMonitor()).toArray();
			}	
			return EMPTY_DATA;
		}

		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#hasChildren(java.lang.Object)
		 */
		@Override
		public boolean hasChildren(Object element) {
			return true;
		}

		@Override
		public void dispose() {
		}

	}

	private static class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			return getText(element);
		}

		/**
		 * @see org.nightlabs.base.ui.table.TableLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if (element instanceof IssueLink)
				return ((IssueLink)element).toString();
			return ""; //$NON-NLS-1$
		}
	}


	public ShowLegalEntityLinkedTreeComposite(Composite parent, int style)
	{
		super(parent, style);
		init();

	}




	public void setPersonID(AnchorID partnerID)
	{
		if (partnerID == null)
			return;

		if (Display.getCurrent() != null)
			throw new IllegalStateException("This method must *not* be called on the SWT UI thread! Use a Job!"); //$NON-NLS-1$

		final LegalEntity partner = partnerID == null ? null : LegalEntityDAO.sharedInstance().getLegalEntity(
				partnerID, 
				new String[] {
						FetchPlan.DEFAULT,
						LegalEntity.FETCH_GROUP_PERSON,
						PropertySet.FETCH_GROUP_FULL_DATA
				}, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
				new NullProgressMonitor());
		ObjectID propertySetID = (ObjectID) JDOHelper.getObjectId(partner.getPerson());
		legalEntityIssuesLinkNode= new LegalEntityIssuesLinkNode(partner.getOrganisationID(),propertySetID);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getTreeViewer().setInput(legalEntityIssuesLinkNode);
			}
		});
	}
	/**
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new ContentProvider());
		treeViewer.setLabelProvider(new LabelProvider());
		if (legalEntityIssuesLinkNode != null) {
					treeViewer.setInput(legalEntityIssuesLinkNode);
		}
	}

	@Override
	public void createTreeColumns(Tree tree) {
		// TODO Auto-generated method stub

	}

}


