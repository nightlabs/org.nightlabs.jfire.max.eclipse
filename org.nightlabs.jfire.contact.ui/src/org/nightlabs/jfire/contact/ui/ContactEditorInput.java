package org.nightlabs.jfire.contact.ui;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.jfire.prop.id.PropertySetID;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class ContactEditorInput
extends JDOObjectEditorInput<PropertySetID>
{
	public ContactEditorInput(PropertySetID jdoObjectID)
	{
		this(jdoObjectID, false);
	}

	public ContactEditorInput(PropertySetID jdoObjectID, boolean createUniqueInput) {
		super(jdoObjectID, createUniqueInput);
	}
}
