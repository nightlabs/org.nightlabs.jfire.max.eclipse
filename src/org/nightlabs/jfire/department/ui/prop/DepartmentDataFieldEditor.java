package org.nightlabs.jfire.department.ui.prop;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor;
import org.nightlabs.jfire.department.prop.DepartmentDataField;
import org.nightlabs.jfire.department.ui.DepartmentComboComposite;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.util.Util;

public class DepartmentDataFieldEditor extends AbstractDataFieldEditor<DepartmentDataField>
{
	public DepartmentDataFieldEditor(IStruct struct, DepartmentDataField data) {
		super(struct, data);
	}

	@Override
	public void doRefresh() {
		// update the UI (propertySet => UI)
		departmentComboComposite.setSelectedDepartment(getDataField().getDepartment());
	}
	
	private DepartmentComboComposite departmentComboComposite;

	@Override
	public Control createControl(Composite parent) {
		departmentComboComposite = new DepartmentComboComposite(parent);
		departmentComboComposite.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!Util.equals(departmentComboComposite.getSelectedDepartment(), getDataField().getDepartment()))
					getModifyListener().modifyData();
			}
		});
		return departmentComboComposite;
	}

	@Override
	public Control getControl() {
		return departmentComboComposite;
	}

	@Override
	public void updatePropertySet() {
		// update the property set (UI => property set)
		getDataField().setDepartment(departmentComboComposite.getSelectedDepartment());
	}

}
