package org.nightlabs.jfire.prop.file.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.nightlabs.jfire.base.ui.prop.structedit.AbstractStructFieldFactory;
import org.nightlabs.jfire.prop.StructBlock;
import org.nightlabs.jfire.prop.file.FileStructField;

public class FileStructFieldFactory extends AbstractStructFieldFactory {

	public FileStructField createStructField(StructBlock block, WizardPage wizardPage) {
		FileStructField field = new FileStructField(block);
		field.setMaxSizeKB(10 * 1024); // 10 MB = 10 * 1024 KB
		field.addFileFormat("*"); //$NON-NLS-1$
		return field;
	}
}
