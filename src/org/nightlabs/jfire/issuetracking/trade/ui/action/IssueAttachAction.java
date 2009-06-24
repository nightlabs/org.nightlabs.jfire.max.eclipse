package org.nightlabs.jfire.issuetracking.trade.ui.action;

import javax.jdo.JDOHelper;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssuePage;
import org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerAction;
import org.nightlabs.progress.NullProgressMonitor;


public class IssueAttachAction extends ArticleContainerAction
{
	public boolean calculateVisible()
	{
		return true;
	}

	@Override
	public boolean calculateEnabled()
	{
		return true;
	}

	@Override
	public void run()
	{
		ArticleContainer articleContainer = this.getArticleContainer();
		ObjectID objectID = (ObjectID)JDOHelper.getObjectId(articleContainer);

		AttachIssueToObjectWizard attachIssueToObjectWizard = new AttachIssueToObjectWizard(articleContainer);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(attachIssueToObjectWizard)
		{
			@Override
			protected Point getInitialSize()
			{
				return new Point(convertHorizontalDLUsToPixels(600), convertVerticalDLUsToPixels(450));
			}
		};
		dialog.open();

		// Update the table in the Section inside ShowLinkedIssuePage.
		// TODO Use proper listeners for refreshing the table.
		//      And then maybe find out the latest entry and highlight it. Kai
		if (dialog.getReturnCode() == Window.OK) { // != Window.CANCEL) {
			IEditorPart activeEditor = RCPUtil.getActiveWorkbenchPage().getActiveEditor();
			if (activeEditor instanceof ArticleContainerEditor) {
				ArticleContainerEditor articleContainerEditor = (ArticleContainerEditor) activeEditor;

				if (getArticleContainer().equals(articleContainerEditor.getArticleContainerEdit().getArticleContainer())) {
					IFormPage page = articleContainerEditor.setActivePage(ShowLinkedIssuePage.PAGE_ID);
					((ShowLinkedIssuePage)page).getPageController().doLoad(new NullProgressMonitor());
					((ShowLinkedIssuePage)page).highlightIssueEntry( attachIssueToObjectWizard.getSelectedIssue() );
				}
			}
		}
	}

}

