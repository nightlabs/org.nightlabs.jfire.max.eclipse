package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItem;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItemChangeListener;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkItemChangeEvent;
import org.nightlabs.jfire.issuetracking.ui.issuelink.create.CreateIssueLinkWizard;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueLinkListSection extends AbstractIssueEditorGeneralSection{

	private IssueLinkAdderComposite issueLinkAdderComposite;
//	private Issue issue;	// <-- FIXME There is already an Issue in the super class. Do we need this duplicate? Kai

	private OpenLinkedObjectAction openLinkedObjectAction;
	private AddLinkAction addLinkAction;
	private RemoveLinkAction removeLinkAction;

	public IssueLinkListSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getSection().setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueLinkListSection.section.text")); //$NON-NLS-1$
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1;

		issueLinkAdderComposite = new IssueLinkAdderComposite(
				client, SWT.NONE, false, controller.getIssue());
		issueLinkAdderComposite.getGridData().grabExcessHorizontalSpace = true;
		issueLinkAdderComposite.getIssueLinkTable().addIssueLinkTableItemChangeListener(new IssueLinkTableItemChangeListener() {
			public void issueLinkItemChanged(final IssueLinkItemChangeEvent itemChangedEvent)
			{
				boolean expanded = controller.getIssue().getIssueLinks().size() > 0;
				getSection().setExpanded(expanded);
				markDirty();
			}
		});

		issueLinkAdderComposite.getIssueLinkTable().addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent e) {
				openLinkedObjectAction.run();
			}
		});

		issueLinkAdderComposite.getIssueLinkTable().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				if (e.getSelection() != null) {
					openLinkedObjectAction.setEnabled(true);
					removeLinkAction.setEnabled(true);
				}
			}
		});

		getSection().setClient(client);

		openLinkedObjectAction = new OpenLinkedObjectAction();
		openLinkedObjectAction.setEnabled(false);
		addLinkAction = new AddLinkAction();
		removeLinkAction = new RemoveLinkAction();
		removeLinkAction.setEnabled(false);

		getToolBarManager().add(openLinkedObjectAction);
		getToolBarManager().add(addLinkAction);
		getToolBarManager().add(removeLinkAction);

		updateToolBarManager();
	}

	@Override
	protected void doSetIssue(Issue issue) {
		issueLinkAdderComposite.getIssueLinkTable().setIssue(issue);
	}

//	@Override
//	public Issue getIssue() {
//		return issue;
//	}

	public class OpenLinkedObjectAction extends Action {
		public OpenLinkedObjectAction() {
			setId(OpenLinkedObjectAction.class.getName());
			setImageDescriptor(
					SharedImages.getSharedImageDescriptor(
							IssueTrackingPlugin.getDefault(),
							IssueLinkListSection.class,
							"Open" //$NON-NLS-1$
					)
			);
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueLinkListSection.OpenLinkedObjectAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueLinkListSection.OpenLinkedObjectAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			IssueLinkTable table = issueLinkAdderComposite.getIssueLinkTable();
			if (!table.getSelectedElements().isEmpty()) {
				for (IssueLinkTableItem issueLinkTableItem : table.getSelectedElements()) {
					if (issueLinkTableItem.getIssueLink() != null) {
						IssueLinkHandler<ObjectID, Object> handler = table.getIssueLinkHandler(issueLinkTableItem.getLinkedObjectID());
						handler.openLinkedObject(issueLinkTableItem.getIssueLink(), issueLinkTableItem.getLinkedObjectID());
					}
				}
			}
		}
	}

	public class AddLinkAction extends Action {
		public AddLinkAction() {
			setId(AddLinkAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(),
					IssueLinkListSection.class,
			"Add")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueLinkListSection.AddLinkAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueLinkListSection.AddLinkAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			IssueLinkTable table = issueLinkAdderComposite.getIssueLinkTable();
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(new CreateIssueLinkWizard(table, getIssue()));
			dialog.open();
		}
	}

	public class RemoveLinkAction extends Action {
		public RemoveLinkAction() {
			setId(RemoveLinkAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(),
					IssueLinkListSection.class,
			"Remove")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueLinkListSection.RemoveLinkAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueLinkListSection.RemoveLinkAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			issueLinkAdderComposite.getIssueLinkTable().removeIssueLinkTableItems(issueLinkAdderComposite.getIssueLinkTable().getSelectedElements());
		}
	}
}