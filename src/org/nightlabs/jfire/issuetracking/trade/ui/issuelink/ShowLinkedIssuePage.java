package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
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
	private void createContextMenu() {
		menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager m) {
				menuMgr.add(new LinkToIssueAction(getController().getArticleContainer()));
			}
		});
		Menu menu = menuMgr.createContextMenu(showLinkedIssueSection.getIssueTable());
		showLinkedIssueSection.getIssueTable().setMenu(menu);
		getSite().registerContextMenu(menuMgr, showLinkedIssueSection.getIssueTable());
	}


	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (showLinkedIssueSection != null && !showLinkedIssueSection.getSection().isDisposed()) {
					showLinkedIssueSection.setLinkedIssues(getController().getLinkedIssues(), selectedIssue);
					selectedIssue = null;
				}
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
}