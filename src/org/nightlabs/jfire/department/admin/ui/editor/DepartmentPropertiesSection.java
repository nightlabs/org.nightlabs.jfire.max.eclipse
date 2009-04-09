package org.nightlabs.jfire.department.admin.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.jfire.department.Department;

public class DepartmentPropertiesSection extends RestorableSectionPart {

	private I18nTextEditor departmentNameText;
	private I18nTextEditor departmentDescriptionText;
	
	private Department department;
	
	ModifyListener dirtyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			markDirty();
		}
	};
	
	/**
	 * Create an instance of DepartmentPropertiesSection.
	 * @param parent The parent for this section
	 * @param toolkit The toolkit to use
	 */
	public DepartmentPropertiesSection(FormPage page, Composite parent, String sectionDescriptionText) {
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		createClient(getSection(), page.getEditor().getToolkit(), sectionDescriptionText);
	}

	private void createClient(Section section, FormToolkit toolkit, String sectionDescriptionText) {
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createDescriptionControl(section, toolkit, sectionDescriptionText);
		Composite container = EntityEditorUtil.createCompositeClient(toolkit, section, 1);
		GridLayout layout = (GridLayout) container.getLayout();
		layout.horizontalSpacing = 10;
		layout.numColumns = 3;
		
		createLabel(container,	"Department Name", 3);
		departmentNameText = new I18nTextEditor(container);
		departmentNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createLabel(container, "Description", 3);
		departmentDescriptionText = new I18nTextEditor(container, departmentNameText.getLanguageChooser());
		departmentDescriptionText.setLayoutData(getGridData(3));
	}
	
	private void createLabel(Composite container, String text, int span) {
		Label label = new Label(container, SWT.NONE);
		label.setText(text);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = span;
		label.setLayoutData(gd);
	}
	
	private GridData getGridData(int span) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = span;
		return gd;
	}
	
	private void createDescriptionControl(Section section, FormToolkit toolkit, String sectionDescriptionText) {
		if (sectionDescriptionText == null || "".equals(sectionDescriptionText)) //$NON-NLS-1$
			return;

		section.setText(sectionDescriptionText);
	}

	public void setDepartment(Department _department) {
		this.department = _department;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (departmentNameText.isDisposed())
					return;
				
				departmentNameText.removeModifyListener(dirtyListener);
				departmentDescriptionText.removeModifyListener(dirtyListener);
				if (department.getName() != null)
					departmentNameText.setI18nText(department.getName());
				if (department.getDescription() != null)
					departmentDescriptionText.setI18nText(department.getDescription());
				
				departmentNameText.addModifyListener(dirtyListener);
				departmentDescriptionText.addModifyListener(dirtyListener);
			}
		});
	}

	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		department.getDescription().copyFrom(departmentDescriptionText.getI18nText());
		department.getName().copyFrom(departmentNameText.getI18nText());
	}
}
