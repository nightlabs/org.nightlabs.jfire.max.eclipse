package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class ShowLinkedIssueSection
extends ToolBarSectionPart
{
	private ShowLinkedIssuePageController controller;
	private IFormPage page;
	private IssueTable issueTable;

	private AddIssueLinkAction addIssueLinkAction;

	/**
	 * @param page
	 * @param parent
	 * @param controller
	 */
	public ShowLinkedIssueSection(IFormPage page, Composite parent, final ShowLinkedIssuePageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR, Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssueSection.title")); //$NON-NLS-1$
		this.controller = controller;
		this.page = page;

		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1;


		addIssueLinkAction = new AddIssueLinkAction();
		getToolBarManager().add(addIssueLinkAction);

		getSection().setClient(client);
		updateToolBarManager();

		issueTable = new IssueTable(client, SWT.NONE);
		issueTable.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void setLinkedIssues(Collection<Issue> issues) {
		issueTable.setInput(issues);
	}

	public IssueTable getIssueTable() {
		return issueTable;
	}

	/**
	 * Highlights the entry in the {@link IssueTable} matching the given {@link Issue}.
	 * Table will contain no highlight if no match is found.
	 */
	public void highlightIssueEntry(Issue issue) {
		int index = -1;
		issueTable.getTableViewer().getTable().setSelection(index);

//		Collection<Issue> issues = CollectionUtil.castCollection( issueTable.getTableViewer().getInput() );	// <-- TODO Revise this when the related page is active.
//		                                                                                                    //     i.e. Use instead issueTable.getElements().

		Collection<Issue> issues = issueTable.getElements(); // TODO Something's not right. Cache elements dont seem updated!
		for(Issue issueElem : issues) {
			index++;
			if ( issueElem.equals(issue) ) {
				issueTable.getTableViewer().getTable().setSelection(index);
				break;
			}
		}

	}



	// -----------------------------------------------------------------------------------------------------------------------------------|
	/**
	 * Handles the action to add a new {@link IssueLink} to an {@link Issue}.
	 */
	private class AddIssueLinkAction extends Action {
		public AddIssueLinkAction() {
			setId(AddIssueLinkAction.class.getName());
			setImageDescriptor(SharedImages.ADD_16x16);
			setToolTipText("Add a link to an Issue");
			setText("Add a link to an Issue");
		}

		@Override
		public void run() {
			// TODO Finish this.
			Object linkedObject = controller.getArticleContainer();
			AttachIssueToObjectWizard attachIssueToObjectWizard = new AttachIssueToObjectWizard(linkedObject);
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(attachIssueToObjectWizard)
			{
				@Override
				protected Point getInitialSize()
				{
					return new Point(convertHorizontalDLUsToPixels(600), convertVerticalDLUsToPixels(450));
				}
			};
			dialog.open();

			// Update the table in the Section.
			// TODO Use proper listeners for refreshing the table.
			//      And then maybe find out the latest entry and highlight it. Kai
			if (dialog.getReturnCode() == Window.OK) { // != Window.CANCEL) {
				((ShowLinkedIssuePage)page).highlightIssueEntry( attachIssueToObjectWizard.getSelectedIssue() );
				controller.doLoad(new NullProgressMonitor());
			}

		}
	}

}
