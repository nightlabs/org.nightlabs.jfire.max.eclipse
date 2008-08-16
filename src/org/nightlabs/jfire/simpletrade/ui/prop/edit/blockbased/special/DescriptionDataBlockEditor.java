package org.nightlabs.jfire.simpletrade.ui.prop.edit.blockbased.special;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditorFactoryRegistry;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditorNotFoundException;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.AbstractDataBlockEditor;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.AbstractDataBlockEditorComposite;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.AbstractDataBlockEditorFactory;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.DataBlockEditor;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.ExpandableBlocksEditor;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.IDataBlockEditorComposite;
import org.nightlabs.jfire.prop.DataBlock;
import org.nightlabs.jfire.prop.DataField;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.exception.DataFieldNotFoundException;
import org.nightlabs.jfire.prop.id.StructBlockID;
import org.nightlabs.jfire.prop.id.StructFieldID;
import org.nightlabs.jfire.simpletrade.store.prop.SimpleProductTypeStruct;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class DescriptionDataBlockEditor
extends AbstractDataBlockEditor
{
	public static class Factory extends AbstractDataBlockEditorFactory {
		/* (non-Javadoc)
		 * @see org.nightlabs.jfire.base.ui.prop.edit.blockbased.DataBlockEditorFactory#getProviderStructBlockID()
		 */
		@Override
		public StructBlockID getProviderStructBlockID() {
			return SimpleProductTypeStruct.DESCRIPTION;
		}

		@Override
		public DataBlockEditor createDataBlockEditor(IStruct struct, DataBlock dataBlock) {
			return new DescriptionDataBlockEditor(struct, dataBlock);
		}
	}

	private static class DescriptionDataBlockEditorComposite extends AbstractDataBlockEditorComposite {
		private static final Logger logger = Logger.getLogger(AbstractDataBlockEditorComposite.class);

		public DescriptionDataBlockEditorComposite(DataBlockEditor blockEditor,
				Composite parent, int style) {
			super(blockEditor, parent, style);

			setLayoutData(new GridData(GridData.FILL_BOTH));
			GridLayout thisLayout = new GridLayout(2, true);
			thisLayout.marginWidth = 0;
			thisLayout.marginHeight = 0;
			this.setLayout(thisLayout);

			createFieldEditors();
		}

		private void createFieldEditors() {
			addDataFieldEditor(SimpleProductTypeStruct.DESCRIPTION_SHORT, 2);
			addDataFieldEditor(SimpleProductTypeStruct.DESCRIPTION_LONG, 2);
		}

		private void addDataFieldEditor(StructFieldID structFieldID, int horizontalSpan)
		{
			DataField dataField = null;
			try {
				dataField = getDataBlock().getDataField(structFieldID);
			} catch (DataFieldNotFoundException e) {
				logger.error("addDataFieldEditor(StructFieldID fieldID) DataField not found for fieldID continuing: "+structFieldID.toString(),e); //$NON-NLS-1$
			}
			DataFieldEditor<DataField> editor = null;
			if (!hasFieldEditorFor(structFieldID)) {
				try {
					editor = DataFieldEditorFactoryRegistry.sharedInstance().getNewEditorInstance(
							getStruct(), ExpandableBlocksEditor.EDITORTYPE_BLOCK_BASED_EXPANDABLE,
							"", // TODO: Context ?!? //$NON-NLS-1$
							dataField
					);
				} catch (DataFieldEditorNotFoundException e1) {
					logger.error("addDataFieldEditor(StructFieldID fieldID) DataFieldEditor not found for fieldID continuing: "+structFieldID.toString(),e1); //$NON-NLS-1$
				}
				Control editorControl = editor.createControl(this);
				GridData editorLData = new GridData(GridData.FILL_BOTH);
				editorLData.horizontalSpan = horizontalSpan;
//				editorLData.grabExcessHorizontalSpace = true;
//				editorLData.horizontalAlignment = GridData.FILL;
				editorControl.setLayoutData(editorLData);
				addFieldEditor(structFieldID, editor);
			}
			else {
				editor = getFieldEditor(dataField);
			}
			editor.setData(getStruct(), dataField);
			editor.refresh();
		}
	}
	
	protected DescriptionDataBlockEditor(IStruct struct, DataBlock dataBlock) {
		super();
	}
	
	@Override
	protected IDataBlockEditorComposite createEditorComposite(Composite parent) {
		return new DescriptionDataBlockEditorComposite(this, parent, SWT.NONE);
	}
	
}