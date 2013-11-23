package org.nightlabs.jfire.personrelation.ui.tree;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.jdo.ObjectID;

public abstract class AbstractPersonRelationTreeLabelProviderDelegate implements IPersonRelationTreeLabelProviderDelegate {
	public Image getJDOObjectImage(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		return null;
	}

	public void onDispose() { }
	public void clear() { }
}
