package org.nightlabs.jfire.reporting.admin.parameter.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.reporting.admin.parameter.ui.resource.Messages;
import org.nightlabs.jfire.reporting.parameter.config.ReportParameterAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ReportParameterAcquisitionUseCase;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class UseCaseDialog 
extends CenteredDialog 
{
	public static final int EDIT_MODE = 1;
	public static final int NEW_MODE = 2;
	
	/**
	 * @param parentShell
	 */
	public UseCaseDialog(Shell parentShell, ReportParameterAcquisitionUseCase useCase, 
			ReportParameterAcquisitionSetup setup, int mode) 
	{
		super(parentShell);
		this.useCase = useCase;
		this.mode = mode;
		this.setup = setup;
	}

	@Override
	public void create() 
	{
		super.create();
		if (mode == EDIT_MODE)
			getShell().setText(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.dialog.UseCaseDialog.editMode.shell.text")); //$NON-NLS-1$
		if (mode == NEW_MODE)
			getShell().setText(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.dialog.UseCaseDialog.newMode.shell.text"));		 //$NON-NLS-1$
		
		setShellStyle(getShellStyle() | SWT.Resize);
		getShell().setSize(300, 200);
	}

	private int mode = EDIT_MODE;
	private ReportParameterAcquisitionSetup setup;
	private ReportParameterAcquisitionUseCase useCase;
	private I18nTextEditor nameEditor;
	private I18nTextEditor descriptionEditor;
	@Override
	protected Control createDialogArea(Composite parent) 
	{
		Composite wrapper = new XComposite(parent, SWT.NONE);
		nameEditor = new I18nTextEditor(wrapper, Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.dialog.UseCaseDialog.nameEditor.caption")); //$NON-NLS-1$
//		nameEditor.setEditMode(EditMode.BUFFERED);
		if (useCase != null)
			nameEditor.setI18nText(useCase.getName(), EditMode.BUFFERED);
		descriptionEditor = new I18nTextEditor(wrapper, Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.dialog.UseCaseDialog.descriptionEditor.caption")); //$NON-NLS-1$
//		descriptionEditor.setEditMode(EditMode.BUFFERED);
		if (useCase != null)
			descriptionEditor.setI18nText(useCase.getDescription(), EditMode.BUFFERED);
		
		nameEditor.setFocus();
		return wrapper;		
	}
	
	public ReportParameterAcquisitionUseCase getUseCase() {
		return useCase;
	}
	
	@Override
	protected void okPressed() 
	{
		if (mode == NEW_MODE) {
			String useCaseID = "" + IDGenerator.nextID(ReportParameterAcquisitionUseCase.class); //$NON-NLS-1$
			useCase = new ReportParameterAcquisitionUseCase(setup, useCaseID);
		}
//		if (mode == EDIT_MODE) {
			nameEditor.getI18nText().copyTo(useCase.getName());
			descriptionEditor.getI18nText().copyTo(useCase.getDescription());			
//		}
		super.okPressed();
	}	
	
	
}
