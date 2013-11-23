package org.nightlabs.jfire.department.ui.prop;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.base.ui.prop.structedit.AbstractStructFieldEditor;
import org.nightlabs.jfire.base.ui.prop.structedit.AbstractStructFieldEditorFactory;
import org.nightlabs.jfire.base.ui.prop.structedit.StructFieldEditor;
import org.nightlabs.jfire.department.prop.DepartmentStructField;

public class DepartmentStructFieldEditor extends AbstractStructFieldEditor<DepartmentStructField> {
	public static class DepartmentStructFieldEditorFactory extends AbstractStructFieldEditorFactory {
		@Override
		public StructFieldEditor createStructFieldEditor() {
			return new DepartmentStructFieldEditor();
		}
	}

	@Override
	protected Composite createSpecialComposite(Composite parent, int style) {
		Composite comp = new XComposite(parent, style);
		new Label(comp, SWT.NONE).setText("Please edit the departments in the 'System administration' perspective.");
		return comp;
	}

	@Override
	protected void setSpecialData(DepartmentStructField field) {
	}
}
