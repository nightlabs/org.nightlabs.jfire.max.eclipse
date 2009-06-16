package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
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
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class ShowLegalEntityLinkedTreeComposite 
extends AbstractTreeComposite
{
	private LegalEntityIssuesLinkNode rootlegalEntityIssuesLinkNode;
	private static final String[] FETCH_GROUPS = new String[] {
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
		Issue.FETCH_GROUP_ISSUE_MARKERS,
		IssueMarker.FETCH_GROUP_NAME,
		IssueMarker.FETCH_GROUP_ICON_16X16_DATA,	
		IssueSeverityType.FETCH_GROUP_NAME,
		IssuePriority.FETCH_GROUP_NAME,
		StateDefinition.FETCH_GROUP_NAME};

	private static final Object[] EMPTY_DATA = new Object[]{};
	private Collection<Image> iconImages = new ArrayList<Image>();

	public ShowLegalEntityLinkedTreeComposite(Composite parent, int style)
	{
		super(parent, style);
		init();
	}

	private class ContentProvider extends TreeContentProvider {

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#getChildren(java.lang.Object)
		 */
		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof LegalEntityIssuesLinkNode)
			{
				LegalEntityIssuesLinkNode legalEntityIssueLinkNode = (LegalEntityIssuesLinkNode)parentElement;			
				if(legalEntityIssueLinkNode.getChildNodes().size()>0)
					return legalEntityIssueLinkNode.getChildNodes().toArray();
				else
					return IssueLinkDAO.sharedInstance().getIssueLinksByOrganisationIDAndLinkedObjectID(legalEntityIssueLinkNode.getOrganisationID(), 
							legalEntityIssueLinkNode.getPersonID(), 
							FETCH_GROUPS, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
							new NullProgressMonitor()).toArray();
			}	

			if (parentElement instanceof IssueLink)
				return ((IssueLink)parentElement).getIssue().getIssueMarkers().toArray();

			return EMPTY_DATA;
		}

		
		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#hasChildren(java.lang.Object)
		 */
		@Override
		public boolean hasChildren(Object element) {	
			if (element instanceof IssueLink)
				return ((IssueLink)element).getIssue().getIssueMarkers().size() > 0;			
				if (element instanceof IssueMarker)
					return false;
				else
					return true;
		}
		


		@Override
		public void dispose() {
			for (Image image : iconImages)
				image.dispose();
			iconImages.clear();
		}

	}

	private class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			return getText(element);
		}


		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof IssueMarker)
			{
				ByteArrayInputStream in = new ByteArrayInputStream(((IssueMarker)element).getIcon16x16Data());
				Image icon = new Image(getDisplay(), in);
				iconImages.add(icon);
				return icon;	
			}		
			return null;
		}

		/**
		 * @see org.nightlabs.base.ui.table.TableLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if (element instanceof LegalEntityIssuesLinkNode)
				return ((LegalEntityIssuesLinkNode)element).getName();
			if (element instanceof IssueLink)
				return ((IssueLink)element).getIssue().getSubject().getText();
			if (element instanceof IssueMarker)
				return ((IssueMarker)element).getName().getText();		
			return ""; //$NON-NLS-1$
		}
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
		rootlegalEntityIssuesLinkNode= new LegalEntityIssuesLinkNode(partner.getOrganisationID(),
				propertySetID,"Issue List");
		rootlegalEntityIssuesLinkNode.addChildNode(new LegalEntityIssuesLinkNode(partner.getOrganisationID(),
				propertySetID,"Issue List"));
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getTreeViewer().setInput(rootlegalEntityIssuesLinkNode);
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
		if (rootlegalEntityIssuesLinkNode != null) {
			treeViewer.setInput(rootlegalEntityIssuesLinkNode);
		}
	}

	@Override
	public void createTreeColumns(Tree tree) {
		// TODO Auto-generated method stub

	}

}


