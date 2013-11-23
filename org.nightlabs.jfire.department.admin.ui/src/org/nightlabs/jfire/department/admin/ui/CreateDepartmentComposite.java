package org.nightlabs.jfire.department.admin.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.department.Department;
import org.nightlabs.jfire.idgenerator.IDGenerator;
/**
 * A composite that contains UIs for adding {@link Department}.
 *
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CreateDepartmentComposite
extends XComposite
{
	/**
	 * Contructs a composite used for adding {@link Department}.
	 *
	 * @param parent -the parent composite
	 * @param style - the SWT style flag
	 */
	public CreateDepartmentComposite(Department newDepartment, Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		this.newDepartment = newDepartment;

		if (newDepartment == null)
			this.newDepartment = new Department(Login.sharedInstance().getOrganisationID(), IDGenerator.nextID(Department.class));

		createComposite();
	}

	private I18nTextEditor nameText;
	private I18nTextEditorMultiLine descriptionText;

	private Department newDepartment;

	private void createComposite() {
		getGridLayout().numColumns = 1;
		getGridLayout().makeColumnsEqualWidth = false;
		getGridData().grabExcessHorizontalSpace = true;

		XComposite mainComposite = new XComposite(this, SWT.NONE,
				LayoutMode.TIGHT_WRAPPER);
		mainComposite.getGridLayout().numColumns = 4;

		XComposite nameAndDescriptionComposite = new XComposite(mainComposite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 5;
		nameAndDescriptionComposite.setLayoutData(gridData);

		Label subjectLabel = new Label(nameAndDescriptionComposite, SWT.WRAP);
		subjectLabel.setLayoutData(new GridData());
		subjectLabel.setText("Subject");

		nameText = new I18nTextEditor(nameAndDescriptionComposite);
		nameText.setI18nText(newDepartment.getName(), EditMode.DIRECT);

		Label descriptionLabel = new Label(nameAndDescriptionComposite, SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData());
		descriptionLabel.setText("Description");

		descriptionText = new I18nTextEditorMultiLine(nameAndDescriptionComposite, nameText.getLanguageChooser());
		descriptionText.setI18nText(newDepartment.getDescription(), EditMode.DIRECT);
	}

	public Department getCreatingDepartment() {
		return newDepartment;
	}
}