/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.parameter.ui.figure;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class NodeConnectionAnchor extends AbstractConnectionAnchor {

	public static int TYPE_LEFT = 0;
	public static int TYPE_RIGHT = 1;
	
	/**
	 * The number (1-based) of the input anchor of the associated {@link AbstractNodeReportEditPart}
	 */
	private int anchorNumber;
	
	private int type;
	
	private Rectangle snapBox;
	
	/**
	 * 
	 */
	public NodeConnectionAnchor() {
	}

	/**
	 * @param owner
	 */
	public NodeConnectionAnchor(IFigure owner, int anchorNumber, int type) {
		super(owner);
		this.anchorNumber = anchorNumber;
		this.type = type;
		this.snapBox = new Rectangle();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ConnectionAnchor#getLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public Point getLocation(Point reference) {
		Rectangle rect = new Rectangle(getOwner().getBounds());
		rect.y = rect.y + AbstractInputNodeFigure.nameHeight + AbstractInputNodeFigure.spacerHeight + (anchorNumber * AbstractInputNodeFigure.anchorHeight) - AbstractInputNodeFigure.anchorHeight / 2;
		snapBox.y = rect.y - AbstractInputNodeFigure.anchorHeight / 2;
		snapBox.x = rect.x;
		snapBox.width = 50;
		snapBox.height = AbstractInputNodeFigure.anchorHeight;
		if (type == TYPE_RIGHT)
			snapBox.x = rect.x - 50;
		getOwner().translateToAbsolute(snapBox);
		
		if (type == TYPE_LEFT) {
			Point result = rect.getTopLeft();
			getOwner().translateToAbsolute(result);
			return result;
		}
		else {
			Point result = rect.getTopRight();
			getOwner().translateToAbsolute(result);
			return result;
		}
	}

	@Override
	public Point getReferencePoint() {
		Point result = getOwner().getBounds().getTopLeft();
		getOwner().translateToAbsolute(result);
		return result;
	}
	
	public boolean intercepts(Point location) {
		getLocation(getReferencePoint());
		return snapBox.contains(location);
	}
}
