package org.nightlabs.jfire.personrelation.trade.ui.tucked;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeLabelProvider;

/**
 * An extension of the original {@link PersonRelationTreeLabelProvider}, but tweaked for font-color displays, depending on the
 * information carried inside a particular {@link TuckedPersonRelationTreeNode}.
 *  
 * @author khaireel
 */
public class TuckedPersonRelationTreeLabelProvider extends PersonRelationTreeLabelProvider<TuckedPersonRelationTreeNode> {
	private Display device;	
	private Color colorTextDim = null;	
	
	/**
	 * Creates a new instance of a TuckedPersonRelationTreeLabelProvider.
	 */
	public TuckedPersonRelationTreeLabelProvider(ColumnViewer columnViewer, Display device) {
		super(columnViewer);
		this.device = device;
		
		// Setup up operational colors.
		colorTextDim = new Color(this.device, 139, 119, 101); //205, 192, 176); //238, 233, 233);		
	}

	@Override
	protected void paint(Event event, Object element) {
		// Special tucked-node settings.
		if (element instanceof TuckedPersonRelationTreeNode) {
			TuckedPersonRelationTreeNode tuckedNode = (TuckedPersonRelationTreeNode) element;
			if (!tuckedNode.isNodePartOfTuckedPath())
				event.gc.setForeground(colorTextDim);
		}
		
		// Done playing with text colors.
		super.paint(event, element);
	}
}
