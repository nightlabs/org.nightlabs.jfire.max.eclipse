package org.nightlabs.jfire.simpletrade.admin.ui.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.inheritance.FieldMetaData;
import org.nightlabs.jfire.base.ui.prop.ValidationUtil;
import org.nightlabs.jfire.base.ui.prop.edit.ValidationResultHandler;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.BlockBasedEditor;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.DataBlockEditorChangedEvent;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.DataBlockEditorChangedListener;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.validation.ValidationResult;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;

/**
 * Section part utilised by {@link SimpleProductTypePropertySetPage} including action for inheritance of properties.
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class SimpleProductTypePropertiesSection extends ToolBarSectionPart {

	private static String VALIDATION_RESULT_MESSAGE_KEY = "validationResultMessageKey"; //$NON-NLS-1$
	private BlockBasedEditor blockBasedEditor;
	private Control blockBasedEditorControl;
	private InheritanceAction inheritanceAction;
	private SimpleProductType simpleProductType;

	/**
	 * Create an instance of SimpleProductTypePropertiesSection.
	 * @param page
	 * @param parent The parent for this section.
	 * @param sectionType
	 * @param sectionDescriptionText
	 */
	public SimpleProductTypePropertiesSection(final IFormPage page, final Composite parent, final int sectionType, final String sectionDescriptionText) {
		super(page, parent, sectionType, "Title"); //$NON-NLS-1$
		inheritanceAction = new InheritanceAction(){
			@Override
			public void run() {
				inheritPressed();
			}
		};
		inheritanceAction.setEnabled(true);
		getToolBarManager().add(inheritanceAction);
		updateToolBarManager();
		createClient(getSection(), page.getEditor().getToolkit(), sectionDescriptionText);
	}

	private void inheritPressed() {
		FieldMetaData fieldMetaData = simpleProductType.getFieldMetaData(SimpleProductType.FieldName.propertySet);
		fieldMetaData.setValueInherited(inheritanceAction.isChecked());
		if (fieldMetaData.isValueInherited()) {
			// TODO Asynchronously obtain the extended product type and copy the data here to have a preview!
		}
		markDirty();
	}

	void setSimpleProductType(final SimpleProductType simpleProductType) {
		this.simpleProductType = simpleProductType;
		FieldMetaData fieldMetaData = this.simpleProductType.getFieldMetaData(SimpleProductType.FieldName.propertySet);
		inheritanceAction.setChecked(fieldMetaData.isValueInherited() ? true : false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.AbstractFormPart#commit(boolean)
	 */
	@Override
	public void commit(final boolean onSave) {
		super.commit(onSave);
		blockBasedEditor.updatePropertySet();
	}

	private BlockBasedEditor createBlockBasedEditor() {
		return new BlockBasedEditor(true);
	}

	/**
	 * Create the content for this section.
	 * @param section The section to fill.
	 * @param toolkit The toolkit to use.
	 * @param sectionDescriptionText
	 */
	private void createClient(final Section section, final FormToolkit toolkit, final String sectionDescriptionText) {
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		setSectionDescriptionText(section, sectionDescriptionText);
		Composite container = EntityEditorUtil.createCompositeClient(toolkit, section, 1);

		blockBasedEditor = createBlockBasedEditor();
		blockBasedEditor.setValidationResultHandler(new ValidationResultHandler() {
			/**
			 * Used to cache the validation result because MessageManager
			 * updates UI every time which is quite expensive. Marc
			 */
			private ValidationResult lastValidationResult = null;

			private boolean needUpdate(ValidationResult validationResult) {
				if((lastValidationResult == null && validationResult != null) ||
						(lastValidationResult != null && !lastValidationResult.equals(validationResult))) {
					lastValidationResult = validationResult;
					return true;
				}
				return false;
			}

			@Override
			public void handleValidationResult(ValidationResult validationResult) {
				if(!needUpdate(validationResult))
					return;
				IMessageManager messageManager = getManagedForm().getMessageManager();
				if (validationResult == null) {
					messageManager.removeMessage(VALIDATION_RESULT_MESSAGE_KEY);
				} else {
					int type = ValidationUtil.getIMessageProviderType(validationResult.getType());
					messageManager.addMessage(VALIDATION_RESULT_MESSAGE_KEY, validationResult.getMessage(), null, type);
				}
			}
		});
		blockBasedEditorControl = blockBasedEditor.createControl(container, false);
		blockBasedEditorControl.setLayoutData(new GridData(GridData.FILL_BOTH));
		blockBasedEditor.addChangeListener(new DataBlockEditorChangedListener() {
			public void dataBlockEditorChanged(DataBlockEditorChangedEvent changedEvent) {
				markDirty();
			}
		});
		blockBasedEditor.addAdditionalDataChangedListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				markDirty();
			}
		});
	}

	public void setPropertySet(final PropertySet property) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(property == null)
					return;
				blockBasedEditor.setPropertySet(property, true);
			}
		});
	}

	private void setSectionDescriptionText(final Section section, final String sectionDescriptionText) {
		if (sectionDescriptionText == null || "".equals(sectionDescriptionText)) //$NON-NLS-1$
			return;
		section.setText(sectionDescriptionText);
	}
}
