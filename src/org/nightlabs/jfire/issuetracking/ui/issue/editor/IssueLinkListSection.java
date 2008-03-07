package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItemChangedListener;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkItemChangedEvent;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkWizard;

/* 
* @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
*/
public class IssueLinkListSection extends AbstractIssueEditorGeneralSection{

	private IssueLinkAdderComposite issueLinkAdderComposite;
	private Issue issue;
	
	private OpenLinkAction openLinkAction;
	private AddLinkAction addLinkAction;
	private RemoveLinkAction removeLinkAction;
	
	public IssueLinkListSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getSection().setText("Issue Links");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());
		
		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 
		
		issueLinkAdderComposite = new IssueLinkAdderComposite(
				client, SWT.NONE, false);
		issueLinkAdderComposite.getGridData().grabExcessHorizontalSpace = true;
		issueLinkAdderComposite.addIssueLinkTableItemListener(new IssueLinkTableItemChangedListener() {
			public void issueLinkItemChanged(
					IssueLinkItemChangedEvent itemChangedEvent) {
				controller.getIssue().clearLinkObjectIDs();
				for (ObjectID objectID : issueLinkAdderComposite.getItems()) {
					controller.getIssue().addLinkObjectID(objectID);	
				}
				markDirty();
			}
		});

		issueLinkAdderComposite.getIssueLinkTable().addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent e) {
				IssueLinkHandler linkHandler = 
					issueLinkAdderComposite.getIssueLinkTable().getIssueLinkHandler(issueLinkAdderComposite.getIssueLinkTable().getFirstSelectedElement());
				linkHandler.openLinkObject(issueLinkAdderComposite.getIssueLinkTable().getFirstSelectedElement());
			}
		});
		
		getSection().setClient(client);
		
		openLinkAction = new OpenLinkAction();
		addLinkAction = new AddLinkAction();
		removeLinkAction = new RemoveLinkAction();
		
		getToolBarManager().add(openLinkAction);
		getToolBarManager().add(addLinkAction);
		getToolBarManager().add(removeLinkAction);
		
		updateToolBarManager();
	}
	
	@Override
	protected void doSetIssue(Issue issue) {
		this.issue = issue;

		Set<ObjectID> objectIDs = issue.getLinkObjectIDs();
		issueLinkAdderComposite.setObjectIDs(objectIDs);
	}
	
	public Issue getIssue() {
		return issue;
	}
	
	public class OpenLinkAction extends Action {		
		public OpenLinkAction() {
			super();
			setId(OpenLinkAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					IssueLinkListSection.class, 
			"Open"));
			setToolTipText("Open Link(s)");
			setText("Open");
		}

		@Override
		public void run() {
			if (issueLinkAdderComposite.getIssueLinkTable().getSelectionIndex() != -1) {
				IssueLinkTable table = issueLinkAdderComposite.getIssueLinkTable();
				table.getIssueLinkHandler(table.getFirstSelectedElement()).openLinkObject(table.getFirstSelectedElement());
			}
		}		
	}
	
	public class AddLinkAction extends Action {		
		public AddLinkAction() {
			super();
			setId(AddLinkAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					IssueLinkListSection.class, 
			"Add"));
			setToolTipText("Add Link(s)");
			setText("Add");
		}

		@Override
		public void run() {
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(
					new IssueLinkWizard(issueLinkAdderComposite));
			dialog.open();
		}		
	}
	
	public class RemoveLinkAction extends Action {		
		public RemoveLinkAction() {
			super();
			setId(RemoveLinkAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					IssueLinkListSection.class, 
			"Remove"));
			setToolTipText("Remove Link(s)");
			setText("Remove");
		}

		@Override
		public void run() {
			issueLinkAdderComposite.removeItems(issueLinkAdderComposite.getIssueLinkTable().getSelectedElements());
		}		
	}
}