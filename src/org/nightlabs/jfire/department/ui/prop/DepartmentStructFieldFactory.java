package org.nightlabs.jfire.department.ui.prop;

import org.eclipse.jface.wizard.WizardPage;
import org.nightlabs.jfire.base.ui.prop.structedit.AbstractStructFieldFactory;
import org.nightlabs.jfire.department.prop.DepartmentStructField;
import org.nightlabs.jfire.prop.StructBlock;
import org.nightlabs.jfire.prop.StructField;

public class DepartmentStructFieldFactory extends AbstractStructFieldFactory {

	@Override
	public StructField createStructField(StructBlock block, WizardPage wizardPage) {
		return new DepartmentStructField(block);
	}

}
