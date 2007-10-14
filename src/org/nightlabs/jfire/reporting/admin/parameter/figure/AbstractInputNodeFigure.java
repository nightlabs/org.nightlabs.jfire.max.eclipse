package org.nightlabs.jfire.reporting.admin.parameter.figure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.nightlabs.jfire.reporting.admin.parameter.editpart.AbstractNodeReportEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.util.PageColorUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractInputNodeFigure 
extends Figure 
implements HandleBounds
{
	
	public static final int nameHeight = 20;
	public static final int spacerHeight = 5;
	public static final int anchorHeight = 16;
	
	private AbstractNodeReportEditPart reportEditPart;
	
	public AbstractInputNodeFigure(AbstractNodeReportEditPart reportEditPart) 
	{
//		createInputConnectionAnchors(getInputAmount());	
		setOpaque(false);
		this.reportEditPart = reportEditPart; 
	}
	
	/**
	 * @see org.eclipse.gef.handles.HandleBounds#getHandleBounds()
	 */
	public Rectangle getHandleBounds() {
		return getBounds().getCropped(new Insets(2,0,2,0));
	}
		
	@Override
	protected void paintFigure(Graphics g) 
	{
		Rectangle bounds = getBounds();
		g.setBackgroundColor(PageColorUtil.getPageColor(colorIndex));
		g.setForegroundColor(PageColorUtil.getPageColor(colorIndex));
		g.fillRectangle(getBounds());
		g.setBackgroundColor(PageColorUtil.getHeaderColor(colorIndex));
		g.fillRectangle(bounds.x, bounds.y, bounds.width, nameHeight);
//		g.drawLine(bounds.x, bounds.y + nameHeight - spacerHeight, bounds.x + bounds.width, bounds.y + nameHeight - spacerHeight);
		String text = getName();
		int textWidth = FigureUtilities.getTextWidth(text, getFont());
		int space = bounds.width - textWidth;
		Font normalFont = g.getFont();
		try {
			g.setFont(getBoldFont(g));
			g.setForegroundColor(PageColorUtil.getHeaderFontColor(colorIndex));
			g.fillText(text, bounds.x + space / 2, bounds.y + 3);
		} finally {
			g.setFont(normalFont);
		}
		
		drawInputConnectors(g, getBounds());
	}		
	
	protected int connectorWidth = 6;
	protected int getConnectorWidth() {
		return connectorWidth;
	}	
	
	private Map<Integer, Point> index2InputAnchorLocation = new HashMap<Integer, Point>();
	protected Point getInputAnchorPoint(int index) {
		return index2InputAnchorLocation.get(index);
	}
	
	protected void drawInputConnectors(Graphics g, Rectangle rec) 
	{
		int x1 = rec.x;
		int y1 = rec.y;
//		int height = rec.height - nameHeight;
		List<ConnectionAnchor> anchors = reportEditPart.getTargetConnectionAnchors();
		ConnectionAnchor highlightAnchor = null; 
		if (highlightParameterID != null)
			highlightAnchor = reportEditPart.getTargetConnectionAnchor(highlightParameterID);
		
		for (int i = 0; i< anchors.size(); i++) {
			int additionalHeight = (i + 1) * anchorHeight;
			y1 = (rec.y + nameHeight + spacerHeight + additionalHeight - anchorHeight/2);
						
			// create connector
			int w = getConnectorWidth();
			PointList connector = new PointList();
			connector.addPoint(0, 1);
			connector.addPoint(w, w);
			connector.addPoint(0, 2*w - 2);
			connector.translate(x1, y1-w);

			
			Point connectionLocation = new Point(x1, y1);
			
			index2InputAnchorLocation.put(i, connectionLocation);
			
			// draw connector			
			g.setForegroundColor(PageColorUtil.getFontColor(colorIndex));			
			g.drawPolygon(connector);
			Color oldbgColor = PageColorUtil.getPageColor(colorIndex);
			g.setBackgroundColor(PageColorUtil.getFontColor(colorIndex));
			g.fillPolygon(connector);
			
			// draw text
			g.setBackgroundColor(oldbgColor);
			g.setForegroundColor(PageColorUtil.getFontColor(colorIndex));
			Font normalFont = null;
			if (highlightAnchor != null && anchors.get(i) == highlightAnchor) {
				normalFont = g.getFont();
				g.setFont(getBoldFont(g));
			}
			int fontHeight = FigureUtilities.getStringExtents("T", getFont()).height;
			g.fillText(getInputString(i), x1 + w + 5, y1 - fontHeight/2);
			if (highlightAnchor != null && anchors.get(i) == highlightAnchor) {
				g.setFont(normalFont);
			}
		}
	}
	
	protected void drawOutputConnector(Graphics g, Rectangle rec) 
	{
		int x1 = rec.x + rec.width - anchorHeight/3;
		int y1 = rec.y;
//		int height = rec.height;
		
//		int additionalHeight = height / 2;
		y1 = (rec.y + nameHeight + spacerHeight + anchorHeight/2);
					
		// create connector
		int w = getConnectorWidth();
		PointList connector = new PointList();
		connector.addPoint(anchorHeight/3, 0);
		connector.addPoint(anchorHeight/3, anchorHeight/3);
		connector.addPoint(0, anchorHeight/3);			
		connector.addPoint(0, 0);			
		connector.translate(x1, y1 -anchorHeight/6);
		
		// draw connector			
		g.setForegroundColor(PageColorUtil.getFontColor(colorIndex));			
		g.setBackgroundColor(PageColorUtil.getFontColor(colorIndex));
		g.drawPolygon(connector);
		Color oldbgColor = getBackgroundColor();
		g.fillPolygon(connector);
		
		int textWidth = FigureUtilities.getTextWidth(getOutputString(), getFont());
		// draw text
		g.setBackgroundColor(oldbgColor);
		g.setForegroundColor(PageColorUtil.getFontColor(colorIndex));
		int fontHeight = FigureUtilities.getStringExtents("T", getFont()).height;
//		getFont().getFontData()[0].height;
		g.fillText(getOutputString(), x1 - w - 5 - textWidth, y1 - fontHeight/2);
	}
	
	
	private Font boldFont = null;
	
	private Font getBoldFont(Graphics g) {
		if (boldFont == null) {
			boldFont = 	new Font(g.getFont().getDevice(), g.getFont().getFontData()[0].getName(), g.getFont().getFontData()[0].getHeight(), g.getFont().getFontData()[0].getStyle() | SWT.BOLD);
		}
		return boldFont;
	}
	
	public Dimension getPreferredSize(int w, int h) {
		Dimension prefSize = super.getPreferredSize(w, h);
		Dimension defaultSize = new Dimension(200, nameHeight + spacerHeight + Math.max(anchorHeight, anchorHeight * getInputAmount()) + spacerHeight);
		prefSize.union(defaultSize);
		return prefSize;
	}

	private String highlightParameterID = null;
	
	public void setInputHighlight(String parameterID) {
		highlightParameterID = parameterID;
		repaint();
	}
	

	private int colorIndex;
	
	public void setColorIndex(int colorIndex) {
		this.colorIndex = colorIndex;
		setBackgroundColor(PageColorUtil.getPageColor(colorIndex));
	}
	
	public abstract String getName();
	public abstract int getInputAmount();
	public abstract String getInputString(int index);
	protected abstract String getOutputString(); 

}
