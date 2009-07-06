package org.nightlabs.jfire.personrelation.ui;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.jdo.ObjectID;

public abstract class PersonRelationTreeLabelProviderDelegate
{
	public abstract Class<? extends ObjectID> getJDOObjectIDClass();
	public abstract Class<?> getJDOObjectClass();

	public Image getJDOObjectImage(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		return null;
	}

	public abstract String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex);

	public abstract int[][] getJDOObjectColumnSpan(ObjectID jdoObjectID, Object jdoObject);
}
