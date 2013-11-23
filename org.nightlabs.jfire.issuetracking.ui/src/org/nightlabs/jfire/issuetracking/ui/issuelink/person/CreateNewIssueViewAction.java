package org.nightlabs.jfire.issuetracking.ui.issuelink.person;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class CreateNewIssueViewAction extends Action implements IViewActionDelegate
{
	public static final String ID = CreateNewIssueViewAction.class.getName();

	public CreateNewIssueViewAction () {
		super();
		setId(ID);
	}

	private IPersonIssueLinkView view;

	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view)
	{
		if (view instanceof IPersonIssueLinkView) {
			this.view = (IPersonIssueLinkView) view;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction arg0) {
		run();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// do nothing
	}

	@Override
	public void run()
	{
		if (view != null && view.getPerson() != null)
		{
			AttachIssueToObjectWizard attachIssueToObjectWizard = new AttachIssueToObjectWizard(view.getPerson());
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(view.getSite().getShell(), attachIssueToObjectWizard)
			{
				@Override
				protected Point getInitialSize()
				{
					return new Point(convertHorizontalDLUsToPixels(600), convertVerticalDLUsToPixels(450));
				}
			};
			dialog.open();
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SharedImages.ADD_16x16;
	}

	@Override
	public String getText() {
		return Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.CreateNewIssueViewAction.text"); //$NON-NLS-1$
	}

	@Override
	public String getToolTipText() {
		return Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.CreateNewIssueViewAction.tooltip");	 //$NON-NLS-1$
	}

}
