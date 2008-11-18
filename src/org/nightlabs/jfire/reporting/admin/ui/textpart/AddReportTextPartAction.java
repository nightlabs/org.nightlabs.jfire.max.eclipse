/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.textpart;

import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.reporting.textpart.ReportTextPart;
import org.nightlabs.jfire.reporting.textpart.ReportTextPartConfiguration;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
class AddReportTextPartAction extends Action {
	
	static class Dialog extends ResizableTitleAreaDialog {

		private I18nTextBuffer buffer;
		
		public Dialog(Shell shell, ResourceBundle resourceBundle) {
			super(shell, resourceBundle);
			buffer = new I18nTextBuffer();
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			XComposite comp = new XComposite(parent, SWT.NONE);
			setTitle("New report text part");
			setMessage("Create a new report text part");
			I18nTextEditor editor = new I18nTextEditor(comp, "Report text part name");
			editor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			editor.setI18nText(buffer, EditMode.DIRECT);
			return super.createDialogArea(parent);
		}
		
		public I18nTextBuffer getI18nText() {
			return buffer;
		}
		
	}

	private ReportTextPartConfigurationEditor editor;
	
	/**
	 * 
	 */
	public AddReportTextPartAction(ReportTextPartConfigurationEditor editor) {
		super("Add");
		this.editor = editor;		
	}
	
	@Override
	public void run() {
		Dialog dlg = new Dialog(Display.getDefault().getActiveShell(), null);
		if (dlg.open() == Window.OK) {
			I18nText name = dlg.getI18nText();
			if (name.isEmpty())
				return;
			ReportTextPartConfiguration reportTextPartConfiguration = editor.getReportTextPartConfiguration();
			String id = ObjectIDUtil.makeValidIDString(name.getText());
			ReportTextPart part = new ReportTextPart(reportTextPartConfiguration, id);
			part.getName().copyFrom(name);
			reportTextPartConfiguration.addReportTextPart(part);
			editor.updateConfigurationEditComposite();
			editor.markDirty();
		}
	}
}
