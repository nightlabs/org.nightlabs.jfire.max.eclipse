package org.nightlabs.jfire.dynamictrade.ui.template;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;

public class SelectDynamicProductTemplateDialog extends ResizableTrayDialog
{
	public SelectDynamicProductTemplateDialog(Shell parentShell) {
		super(parentShell, Messages.RESOURCE_BUNDLE);
	}

	public SelectDynamicProductTemplateDialog(IShellProvider parentShell) {
		super(parentShell, Messages.RESOURCE_BUNDLE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite contents = (Composite) super.createDialogArea(parent);
		new DynamicProductTemplateTree(contents);
		return contents;
	}
}
