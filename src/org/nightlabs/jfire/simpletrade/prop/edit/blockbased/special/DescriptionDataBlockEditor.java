package org.nightlabs.jfire.simpletrade.prop.edit.blockbased.special;

import org.apache.log4j.Logger;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditorFactoryRegistry;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditorNotFoundException;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.AbstractDataBlockEditor;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.DataBlockEditorFactory;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.ExpandableBlocksEditor;
import org.nightlabs.jfire.prop.AbstractDataField;
import org.nightlabs.jfire.prop.DataBlock;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.exception.DataFieldNotFoundException;
import org.nightlabs.jfire.prop.id.StructBlockID;
import org.nightlabs.jfire.prop.id.StructFieldID;
import org.nightlabs.jfire.simpletrade.store.prop.SimpleProductTypeStruct;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DescriptionDataBlockEditor 
extends AbstractDataBlockEditor 
{
	public static class Factory implements DataBlockEditorFactory {  
		/**
		 * @see org.nightlabs.jfire.base.ui.person.edit.blockbased.PersonDataBlockEditorFactory#getProviderStructBlockID()
		 */
		public StructBlockID getProviderStructBlockID() {
			return SimpleProductTypeStruct.DESCRIPTION;
		}
		
		/**
		 * @see org.nightlabs.jfire.base.ui.person.edit.blockbased.PersonDataBlockEditorFactory#createPersonDataBlockEditor(org.nightlabs.jfire.base.ui.person.PersonDataBlock, org.eclipse.swt.widgets.Composite, int)
		 */
		public AbstractDataBlockEditor createPropDataBlockEditor(IStruct struct, DataBlock dataBlock, Composite parent, int style) {
			return new DescriptionDataBlockEditor(struct, dataBlock, parent, style);
		}
	}
	
	private static final Logger logger = Logger.getLogger(DescriptionDataBlockEditor.class);
	
	/**
	 * @param struct
	 * @param dataBlock
	 * @param parent
	 * @param style
	 */
	public DescriptionDataBlockEditor(IStruct struct, DataBlock dataBlock,
			Composite parent, int style) {
		super(struct, dataBlock, parent, style);
		
		setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout thisLayout = new GridLayout(2, true);
		this.setLayout(thisLayout);
		
		createFieldEditors();
	}

	@Override
	public void refresh(IStruct struct, DataBlock block) {
		this.dataBlock = block;
		createFieldEditors();
	}

	private void createFieldEditors() {
		addDataFieldEditor(SimpleProductTypeStruct.DESCRIPTION_SHORT, 2);
		addDataFieldEditor(SimpleProductTypeStruct.DESCRIPTION_LONG, 2);
	}	
	
	private void addDataFieldEditor(StructFieldID fieldID, int horizontalSpan) 
	{
		AbstractDataField field = null;
		try {
			field = dataBlock.getDataField(fieldID);
		} catch (DataFieldNotFoundException e) {
			logger.error("addDataFieldEditor(StructFieldID fieldID) DataField not found for fieldID continuing: "+fieldID.toString(),e); //$NON-NLS-1$
		}
		DataFieldEditor editor = null;
		if (!hasFieldEditorFor(field)) { 
			try {
				editor = DataFieldEditorFactoryRegistry.sharedInstance().getNewEditorInstance(
						getStruct(), ExpandableBlocksEditor.EDITORTYPE_BLOCK_BASED_EXPANDABLE,
						"", // TODO: Context ?!? //$NON-NLS-1$
						field					
				);
			} catch (DataFieldEditorNotFoundException e1) {
				logger.error("addDataFieldEditor(StructFieldID fieldID) DataFieldEditor not found for fieldID continuing: "+fieldID.toString(),e1); //$NON-NLS-1$
			}
			Control editorControl = editor.createControl(this);
			GridData editorLData = new GridData(GridData.FILL_BOTH);
			editorLData.horizontalSpan = horizontalSpan;
//			editorLData.grabExcessHorizontalSpace = true;
//			editorLData.horizontalAlignment = GridData.FILL;
			editorControl.setLayoutData(editorLData);
			addFieldEditor(field, editor);
		}
		else {
			editor = getFieldEditor(field);
		}
		editor.setData(getStruct(), field);
		editor.refresh();
	}	
}
