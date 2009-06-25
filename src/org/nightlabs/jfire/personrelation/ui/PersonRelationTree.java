package org.nightlabs.jfire.personrelation.ui;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOObjectLazyTreeContentProvider;
import org.nightlabs.jfire.prop.id.PropertySetID;

public class PersonRelationTree extends AbstractTreeComposite<PersonRelationTreeNode>
{
	protected static class PersonRelationTreeContentProvider
	extends JDOObjectLazyTreeContentProvider<ObjectID, Object, PersonRelationTreeNode>
	{
	}

	public PersonRelationTree(Composite parent) {
		super(parent);
	}

	@Override
	public void createTreeColumns(Tree tree) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		// TODO Auto-generated method stub

	}

	public void setPersonIDs(Collection<PropertySetID> personIDs)
	{

	}

	/**
	 * @deprecated Do not call this method! It's only inherited and used
	 * internally - use {@link #setPersonIDs(Collection)} instead.
	 */
	@Deprecated
	@Override
	public void setInput(Object input) {
		super.setInput(input);
	}
}
