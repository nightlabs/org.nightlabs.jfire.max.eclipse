package org.nightlabs.jfire.issuetimetracking.admin.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditorPageController;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.dao.UserDAO;
import org.nightlabs.jfire.trade.ui.currency.CurrencyCombo;
import org.nightlabs.progress.NullProgressMonitor;

public class ProjectCostEditorPage 
extends EntityEditorPageWithProgress 
{
	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = ProjectCostEditorPage.class.getName();

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link ProjectCostEditorPage}. 
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new ProjectCostEditorPage(formEditor);
		}
		
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new ProjectEditorPageController(editor);
		}
	}

	/**
	 * <p>
	 * This constructor is used by the entity editor
	 * page extension system.
	 * 
	 * @param editor The editor for which to create this
	 * 		form page. 
	 */
	public ProjectCostEditorPage(FormEditor editor)
	{
		super(editor, ID_PAGE, "Project Cost");
	}

	private ProjectEditorPageController controller;
	private ScrolledComposite sc;
	
	@Override
	protected void addSections(Composite parent) {
		controller = (ProjectEditorPageController)getPageController();
		
		sc = new ScrolledComposite(parent, SWT.H_SCROLL |   
				  SWT.V_SCROLL);
		sc.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		final XComposite mainComposite = new XComposite(sc, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		GridLayout layout = (GridLayout)mainComposite.getLayout();
		layout.makeColumnsEqualWidth = true;

		XComposite costComposite = new XComposite(mainComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		costComposite.getGridLayout().numColumns = 2;
		
		new Label(costComposite, SWT.NONE).setText("Currency");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		CurrencyCombo currencyCombo = new CurrencyCombo(costComposite, SWT.NONE);
		currencyCombo.setLayoutData(gridData);
		
		new Label(costComposite, SWT.NONE).setText("Monthly Cost");
		Text costText = new Text(costComposite, SWT.SINGLE);
		costText.setLayoutData(gridData);
		
		new Label(costComposite, SWT.NONE).setText("Monthly Revenue");
		Text revenueText = new Text(costComposite, SWT.SINGLE);
		revenueText.setLayoutData(gridData);

		XComposite userComposite = new XComposite(mainComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		userComposite.getGridLayout().numColumns = 2;
		
		final ListComposite<User> userList = new ListComposite<User>(userComposite, SWT.NONE);
		userList.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof User) {
					User user = (User) element;
					return user.getName();
				}
				return "";
			}
		});
		
		Job job = new Job("Loading Users................") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					final java.util.List<User> users = UserDAO.sharedInstance().getUsers(
							Login.getLogin().getOrganisationID(),
							(String[]) null,
							new String[] {
								User.FETCH_GROUP_NAME
							},
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new NullProgressMonitor()
					);
					
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							userList.setInput(users);
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
		
		XComposite detailComposite = new XComposite(userComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		detailComposite.getGridLayout().numColumns = 2;
		
		new Label(detailComposite, SWT.NONE).setText("Currency");
		CurrencyCombo currencyCombo2 = new CurrencyCombo(detailComposite, SWT.NONE);
		currencyCombo2.setLayoutData(gridData);
		
		new Label(detailComposite, SWT.NONE).setText("Monthly Cost");
		Text costText2 = new Text(detailComposite, SWT.SINGLE);
		costText2.setLayoutData(gridData);
		
		new Label(detailComposite, SWT.NONE).setText("Monthly Revenue");
		Text revenueText2 = new Text(detailComposite, SWT.SINGLE);
		revenueText2.setLayoutData(gridData);

		sc.setContent(mainComposite);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		if (controller.isLoaded()) {
		}
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent();		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
			}
		});
	}
	
	@Override
	protected String getPageFormTitle() {
		return "Project Cost";
	}
	
	protected ProjectEditorPageController getController() {
		return (ProjectEditorPageController)getPageController();
	}
	
	@Override
	protected boolean includeFixForVerticalScrolling() {
		return false;
	}
}