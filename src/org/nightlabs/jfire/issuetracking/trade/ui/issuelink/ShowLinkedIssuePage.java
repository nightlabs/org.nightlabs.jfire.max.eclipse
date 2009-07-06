package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class ShowLinkedIssuePage
extends EntityEditorPageWithProgress
{
	public static class Factory implements IEntityEditorPageFactory {
		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new ShowLinkedIssuePage(formEditor);
		}

		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new ShowLinkedIssuePageController(editor);
		}
	}

	private ShowLinkedIssueSection showLinkedIssueSection;

	public static final String PAGE_ID = ShowLinkedIssuePage.class.getName();

	/**
	 * @param editor
	 */
	public ShowLinkedIssuePage(FormEditor editor) {
		super(editor, PAGE_ID, Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssuePage.title")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#addSections(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addSections(Composite parent) {
		final ShowLinkedIssuePageController controller = (ShowLinkedIssuePageController) getPageController();

		showLinkedIssueSection = new ShowLinkedIssueSection(this, parent, controller);
		showLinkedIssueSection.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getManagedForm().addPart(showLinkedIssueSection);
		createContextMenu();

		if (controller.isLoaded()) {
			showLinkedIssueSection.setLinkedIssues(controller.getLinkedIssues());
		}
	}

	private MenuManager menuMgr;
	private IssueLinkActionRemove issueLinkActionRemove;
	private void createContextMenu() {
		issueLinkActionRemove = new IssueLinkActionRemove(showLinkedIssueSection.getIssueTable(), (ShowLinkedIssuePageController)getPageController(), this);

		menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager m) {
				menuMgr.add(new LinkToIssueAction(getController().getArticleContainer()));
				menuMgr.add(issueLinkActionRemove);
			}
		});

		Menu menu = menuMgr.createContextMenu(showLinkedIssueSection.getIssueTable());
		showLinkedIssueSection.getIssueTable().setMenu(menu);
		getSite().registerContextMenu(menuMgr, showLinkedIssueSection.getIssueTable());


		showLinkedIssueSection.getIssueTable().addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				handleRemoveAction();
			}
		});
	}

	protected void handleRemoveAction() {
		assert issueLinkActionRemove != null;
		issueLinkActionRemove.setEnabled( showLinkedIssueSection.getIssueTable().getSelectionIndex() >= 0 );
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (showLinkedIssueSection != null && !showLinkedIssueSection.getSection().isDisposed()) {
					// Update the IssueTable.
					Collection<Issue> issues = getController().getLinkedIssues();
					showLinkedIssueSection.setLinkedIssues( issues );

					// Now highlight the entry if a selectedIssue exists.
					if (selectedIssue != null) {
						IssueTable issueTable = showLinkedIssueSection.getIssueTable();

						int index = -1;
						issueTable.refresh(true);
						issueTable.getTableViewer().getTable().setSelection(index);

						issues = issueTable.getElements(); // <-- Doesnt seem to return the correct values here; retrieved them from cache... So how??
						for(Issue issueElem : issues) {
							index++;
							if ( issueElem.equals(selectedIssue) ) {
								issueTable.getTableViewer().getTable().setSelection(index);
								break;
							}
						}

						selectedIssue = null;
					}
				}

				handleRemoveAction();
			}
		});
	}

	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssuePage.pageFormTitle"); //$NON-NLS-1$
	}

	protected ShowLinkedIssuePageController getController() {
		return (ShowLinkedIssuePageController)getPageController();
	}


	private Issue selectedIssue = null;
	public void setHighlightIssueEntry(Issue issue) { selectedIssue = issue; }


	private class LinkToIssueAction extends Action {
		private Object linkedObject;

		public LinkToIssueAction(Object linkedObject) {
			this.linkedObject = linkedObject;
			setId(LinkToIssueAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingTradePlugin.getDefault(),
					ShowLinkedIssueSection.class,
			"Link")); //$NON-NLS-1$

			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssuePage.linkAction.tooltip")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssuePage.linkAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
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
			// --> See also: ShowLinkedIssueSection.AddIssueLinkAction, and IssueAttachAction.
			// --> These should all somehow be unified, since ALL of their behaviours are the same. Kai.
			//     --> On second thoughts, maybe not. Since they are, erm, different, even though they are the same. Kai. <--  Hääää??? WTF does this mean?? Kai.
			if (dialog.getReturnCode() == Window.OK) { // != Window.CANCEL) {
				setHighlightIssueEntry( attachIssueToObjectWizard.getSelectedIssue() ); // <-- Attempt to highlight the latest entry in the table.
//				getPageController().doLoad(new NullProgressMonitor()); // <-- This shall be delegated to the issueLinksLifeCycleListener.
			}
		}
	}




//	// -----------------------------------------------------------------------------------------------------------------------------------| TESTING |
//	/**
//	 * Handles the action to delete a new {@link IssueLink} to an {@link Issue}.
//	 */
//	private class RemoveIssueLinkAction extends Action {
//		public RemoveIssueLinkAction() {
//			setId(RemoveIssueLinkAction.class.getName());
//			setImageDescriptor(SharedImages.DELETE_16x16);
//			setToolTipText("Remove link to selected issue");
//			setText("Remove selected issue link(s)");
//		}
//
//		@Override
//		public void run() {
//			// Pick out those Issues selected from the IssueTable.
//			Collection<Issue> selectedIssues = showLinkedIssueSection.getIssueTable().getSelectedElements();
//			if (selectedIssues == null || selectedIssues.isEmpty()) return;
//
//			// Scroll through every single selected Issue, and ask for delete-confirmation.
//			for (Issue selectedIssue : selectedIssues) {
//				boolean result = MessageDialog.openConfirm(
//						getSite().getShell(),
//						"Remove IssueLink",
//						"Remove the link to Issue "
//						+ "(ID:" + ObjectIDUtil.longObjectIDFieldToString(selectedIssue.getIssueID()) + ") "
//						+ "\"" + selectedIssue.getSubject().getText() + "\"?");
//
//				if (result) {
//					// Setup the pre-delete sequence.
//					// Look for that related IssueLink to be deleted.
//					final Issue issue = selectedIssue;
//					final IssueLink issueLink = getController().removeRelatedIssueLink(selectedIssue);
//
//					Job job = new Job("Removing Issue link...") {
//						@Override
//						protected IStatus run(ProgressMonitor monitor) {
//							try {
//								Issue _issue = IssueDAO.sharedInstance().getIssue(
//										(IssueID)JDOHelper.getObjectId(issue), new String[] {FetchPlan.DEFAULT, Issue.FETCH_GROUP_ISSUE_LINKS},
//										NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 50));
//
//								_issue.removeIssueLink(issueLink);
//								IssueDAO.sharedInstance().storeIssue(
//										_issue, false, new String[] {FetchPlan.DEFAULT, Issue.FETCH_GROUP_ISSUE_LINKS},
//										NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 50));
//
//								return Status.OK_STATUS;
//							} finally {
//								monitor.done();
//							}
//						}
//					};
//
//					job.setPriority(Job.SHORT);
//					job.schedule();
//				}
//
//				// Note: There are listeners and these entries from the table will be removed.
//				//       Or, do we simply update the table here ourselves? <-- Double work? Let's leave it here. Kai.
//			}
//
//
//		}
//	}

}