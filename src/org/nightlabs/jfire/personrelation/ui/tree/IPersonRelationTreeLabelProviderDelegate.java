package org.nightlabs.jfire.personrelation.ui.tree;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.jdo.ObjectID;

public interface IPersonRelationTreeLabelProviderDelegate {
	public Class<? extends ObjectID> getJDOObjectIDClass();
	public Class<?> getJDOObjectClass();

	public Image getJDOObjectImage(ObjectID jdoObjectID, Object jdoObject, int spanColIndex);

	public String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex);

	public int[][] getJDOObjectColumnSpan(ObjectID jdoObjectID, Object jdoObject);

	public void onDispose();
	public void clear();

}
