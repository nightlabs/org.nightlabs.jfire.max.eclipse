package org.nightlabs.jfire.trade.ui.articlecontainer.header;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.recurring.RecurringRootTreeNode;

public abstract class CreateArticleContainerAction
extends Action
{
	public CreateArticleContainerAction() {
		super();
	}

	public CreateArticleContainerAction(String text) {
		super(text);
	}

	public CreateArticleContainerAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	public CreateArticleContainerAction(String text, int style) {
		super(text, style);
	}

	protected HeaderTreeNode getRootTreeNode(HeaderTreeNode node)
	{
		HeaderTreeNode result = node;
		while (result.getParent() != null)
			result = result.getParent();

		return result;
	}

	public void calculateEnabled(ISelection selection) {
		IStructuredSelection sel = (IStructuredSelection) selection;
		if (sel.isEmpty())
			this.setEnabled(false);
		else {
			boolean actionEnabled = false;
			Object firstElement = sel.getFirstElement();
			if (firstElement instanceof HeaderTreeNode) {
				HeaderTreeNode selectedNode = (HeaderTreeNode) firstElement;
				HeaderTreeNode root = getRootTreeNode(selectedNode);
				if (root instanceof RecurringRootTreeNode) {
					if (!(selectedNode instanceof RecurringRootTreeNode))
						actionEnabled = true;
				}
				else if (!(root instanceof EndCustomerRootTreeNode)) {
					actionEnabled = true;
				}
			}
			this.setEnabled(actionEnabled);
		}
	}
}
