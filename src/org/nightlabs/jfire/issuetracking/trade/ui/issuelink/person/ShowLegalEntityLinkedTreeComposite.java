package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.GC;
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
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.IssueDescription;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueLinkDAO;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
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
		Issue.FETCH_GROUP_ISSUE_COMMENTS,
		Issue.FETCH_GROUP_ISSUE_MARKERS,
		Issue.FETCH_GROUP_DESCRIPTION,
		IssueMarker.FETCH_GROUP_NAME,
		IssueMarker.FETCH_GROUP_ICON_16X16_DATA,
		IssueComment.FETCH_GROUP_TEXT,
		IssueComment.FETCH_GROUP_TIMESTAMP};

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
				return ((LegalEntityIssuesLinkNode)parentElement).getChildNodes();
			if (parentElement instanceof IssueLink)
			{

				List<IssueComment> commentsList = ((IssueLink)parentElement).getIssue().getComments();
				Collections.sort(commentsList, new Comparator<IssueComment>() {
					public int compare(IssueComment o1, IssueComment o2) {
						// reverse chronological Order
						return o2.getCreateTimestamp().compareTo(o1.getCreateTimestamp());
					}
				});

				final List<IssueComment> testcommentsList = commentsList;
				final LegalEntityIssuesLinkNode subjectlegalEntityIssuesLinkNode= new LegalEntityIssuesLinkNode(null,
						null,"Comment"){
					@Override
					public Object[]  getChildNodes() {
						return testcommentsList.toArray();

					}	
				};	

				Object[] arrayDesc = new Object[] {((IssueLink)parentElement).getIssue().getDescription()};	
				return concat(arrayDesc, 
						new Object[] {subjectlegalEntityIssuesLinkNode});
			}
			return EMPTY_DATA;
		}

		protected Object[] concat(Object[] object, Object[] more) {
			Object[] both = new Object[object.length + more.length];
			System.arraycopy(object, 0, both, 0, object.length);
			System.arraycopy(more, 0, both, object.length, more.length);
			return both;
		}


		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#hasChildren(java.lang.Object)
		 */
		@Override
		public boolean hasChildren(Object element) {	
			if (element instanceof IssueLink)
				return ((IssueLink)element).getIssue().getIssueMarkers().size() > 0;			
				if (element instanceof IssueComment||element instanceof IssueDescription)
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
			if (element instanceof LegalEntityIssuesLinkNode)
				return ((LegalEntityIssuesLinkNode)element).getIcon();
			if (element instanceof IssueLink)
				return getCombiIssueMarkerImage(((IssueLink)element).getIssue());
			return null;
		}


		protected Image getCombiIssueMarkerImage(Issue issue)
		{
			int maxIssueMarkerCountPerIssue;		
			if (issue.getIssueMarkers().size() > 0)
				maxIssueMarkerCountPerIssue = issue.getIssueMarkers().size();
			else
				return null;
			Image combinedIcon = new Image(getDisplay(),
					IssueMarker.ISSUE_MARKER_IMAGE_DIMENSION.width * maxIssueMarkerCountPerIssue + maxIssueMarkerCountPerIssue - 1,
					IssueMarker.ISSUE_MARKER_IMAGE_DIMENSION.height);
			GC gc = new GC(combinedIcon);
			Image icon = null;
			int i = 0;
			try {
				for(IssueMarker issueMarker:issue.getIssueMarkers()) {
					if (issueMarker.getIcon16x16Data() != null) {
						ByteArrayInputStream in = new ByteArrayInputStream(issueMarker.getIcon16x16Data());
						icon = new Image(getDisplay(), in);
						gc.drawImage(icon, IssueMarker.ISSUE_MARKER_IMAGE_DIMENSION.width * i + i, 0);
						iconImages.add(icon);
					}
					i++;
				}
			} finally {
				gc.dispose();
			}			
			iconImages.add(combinedIcon);
			return combinedIcon;
		}


		/**
		 * @see org.nightlabs.base.ui.table.TableLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if (element instanceof LegalEntityIssuesLinkNode)
				return ((LegalEntityIssuesLinkNode)element).getName();
			if (element instanceof IssueLink)
				return String.format("%s/%s",((IssueLink)element).getIssue().getIssueIDAsString(), 
						((IssueLink)element).getIssue().getSubject().getText());		
			if (element instanceof IssueComment)
				return ((IssueComment)element).getText();		
			if (element instanceof IssueDescription)
				return ((IssueDescription)element).getText();
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


		final ObjectID propertySetID = (ObjectID) JDOHelper.getObjectId(partner.getPerson());			
		final LegalEntityIssuesLinkNode sublegalEntityIssuesLinkNode= new LegalEntityIssuesLinkNode(partner.getOrganisationID(),
				propertySetID,"Issue List"){
			@Override
			public Object[]  getChildNodes() {
				return IssueLinkDAO.sharedInstance().getIssueLinksByOrganisationIDAndLinkedObjectID(getOrganisationID(), 
						getPersonID(), 
						FETCH_GROUPS, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						new NullProgressMonitor()).toArray();
			}	
		};	

		rootlegalEntityIssuesLinkNode= new LegalEntityIssuesLinkNode(partner.getOrganisationID(),
				propertySetID,"Issue List"){
			@Override
			public Object[]  getChildNodes() {
				return new Object[] {sublegalEntityIssuesLinkNode};				
			}	
		};

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


