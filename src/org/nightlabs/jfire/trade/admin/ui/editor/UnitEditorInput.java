package org.nightlabs.jfire.trade.admin.ui.editor;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.jfire.store.id.UnitID;

public class UnitEditorInput 
extends JDOObjectEditorInput<UnitID>
{
	public UnitEditorInput(UnitID jdoObjectID) {
		super(jdoObjectID);
	}
}