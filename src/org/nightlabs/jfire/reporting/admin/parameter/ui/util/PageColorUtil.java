package org.nightlabs.jfire.reporting.admin.parameter.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PageColorUtil 
{
	private static List<Color> pageColors = new ArrayList<Color>();
	private static Color paramColor = new Color(null, 163, 163, 163);
	private static List<Color> headerColors = new ArrayList<Color>();
	private static Color paramHeaderColor = new Color(null, 78, 77, 77);
	private static List<Color> borderColors = new ArrayList<Color>();
	private static Color paramBorderColor = ColorConstants.black;;
	private static List<Color> fontColors = new ArrayList<Color>();
	private static Color paramFontColor = ColorConstants.black;
	private static List<Color> headerFontColors = new ArrayList<Color>();
	private static Color paramHeaderFontColor = ColorConstants.white;
	
	static {
		fontColors.add(new Color(null, 57, 57, 57));
		borderColors.add(new Color(null, 114, 114, 114));
		headerFontColors.add(new Color(null, 57, 57, 57));
		
		headerColors.add(new Color(null, 170, 183, 156));
		pageColors.add(new Color(null, 216, 226, 206));
		
		headerColors.add(new Color(null, 162, 162, 192));
		pageColors.add(new Color(null, 213, 213, 227));
		
		headerColors.add(new Color(null, 186, 131, 131));
		pageColors.add(new Color(null, 228, 180, 180));
		
		headerColors.add(new Color(null, 209, 188, 125));
		pageColors.add(new Color(null, 238, 223, 177));
		
		headerColors.add(new Color(null, 170, 139, 139));
		pageColors.add(new Color(null, 212, 188, 226));
	}
	
	private static Color getColor(int colorIndex, List<Color> list) {
		if (colorIndex < 0) {
			if (list == pageColors)
				return paramColor;
			else if (list == headerColors)
				return paramHeaderColor;
			else if (list == borderColors)
				return paramBorderColor;
			else if (list == fontColors)
				return paramFontColor;
			else if (list == headerFontColors)
				return paramHeaderFontColor;
		}
		int index = colorIndex % list.size();
		return list.get(index);
	}
	
	public static Color getPageColor(int colorIndex) 
	{
		return getColor(colorIndex, pageColors);
	}
	
	public static Color getHeaderColor(int colorIndex) 
	{
		return getColor(colorIndex, headerColors);
	}
	
	public static Color getBorderColor(int colorIndex) 
	{
		return getColor(colorIndex, borderColors);
	}

	public static Color getFontColor(int colorIndex) 
	{
		return getColor(colorIndex, fontColors);
	}
	
	public static Color getHeaderFontColor(int colorIndex) 
	{
		return getColor(colorIndex, headerFontColors);
	}
	
}
