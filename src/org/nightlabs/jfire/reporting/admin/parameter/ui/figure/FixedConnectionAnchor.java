package org.nightlabs.jfire.reporting.admin.parameter.ui.figure;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class FixedConnectionAnchor
extends AbstractConnectionAnchor
{
	private boolean leftToRight = true;
	private int offsetH;
	private int offsetV;
	private boolean topDown = false;

	public FixedConnectionAnchor(IFigure owner) {
		super(owner);
	}

	/**
	 * @see org.eclipse.draw2d.AbstractConnectionAnchor#ancestorMoved(IFigure)
	 */
	@Override
	public void ancestorMoved(IFigure figure) {
		if (figure instanceof ScalableFigure)
			return;
		super.ancestorMoved(figure);
	}

	public Point getLocation(Point reference) {
		Rectangle r = getOwner().getBounds();
		int x,y;
		if (topDown)
			y = r.y + offsetV;
		else
			y = r.bottom() - 1 - offsetV;

		if (leftToRight)
			x = r.x + offsetH;
		else
			x = r.right() - 1 - offsetH;

		Point p = new PrecisionPoint(x,y);
		getOwner().translateToAbsolute(p);
		return p;
	}

	@Override
	public Point getReferencePoint(){
		return getLocation(null);
	}

	/**
	 * @param offsetH The offsetH to set.
	 */
	public void setOffsetH(int offsetH) {
		this.offsetH = offsetH;
		fireAnchorMoved();
	}

	public int getOffsetH() {
		return offsetH;
	}
	
	/**
	 * @param offsetV The offsetV to set.
	 */
	public void setOffsetV(int offsetV) {
		this.offsetV = offsetV;
		fireAnchorMoved();
	}
	
	public int getOffsetV() {
		return offsetV;
	}
	
	public void setLeftToRight(boolean leftToRight) {
		this.leftToRight = leftToRight;
	}
	
	public void setTopDown(boolean topDown) {
		this.topDown = topDown;
	}
}