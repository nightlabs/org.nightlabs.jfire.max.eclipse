package org.nightlabs.jfire.reporting.admin.parameter.ui.command;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.nightlabs.jfire.reporting.admin.parameter.ui.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.admin.parameter.ui.util.ObjectIDProvider;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;
import org.nightlabs.jfire.reporting.parameter.config.IGraphicalInfoProvider;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class AutoLayoutCommand
extends Command
{

	public AutoLayoutCommand(ValueAcquisitionSetup setup, EditPartViewer viewer) {
		super();
		this.setup = setup;
		this.viewer = viewer;
	}

	private ValueAcquisitionSetup setup;
	private EditPartViewer viewer;
	private int distX = 75;
	private int distY = 75;

	@Override
	public void redo() {
		execute();
	}

	@Override
	public void undo() 
	{
		for (Map.Entry<IGraphicalInfoProvider, Point> entry : model2OldLocation.entrySet()) {
			IGraphicalInfoProvider graphicalInfoProvider = entry.getKey();
			graphicalInfoProvider.setX(entry.getValue().x);
			graphicalInfoProvider.setY(entry.getValue().y);
		}
		notifyEditParts();
	}

	@Override	
	public void execute()
	{
		List<AcquisitionParameterConfig> acquisitionParameterConfigs = setup.getParameterConfigs();
		// layout acquisitionParameterConfigs
		for (int i=0; i<acquisitionParameterConfigs.size(); i++) {
			AcquisitionParameterConfig acquisitionParameterConfig = acquisitionParameterConfigs.get(i);
			GraphicalEditPart ep = (GraphicalEditPart) viewer.getEditPartRegistry().get(acquisitionParameterConfig);
			if (ep != null) {
				IFigure figure = ep.getFigure();
				if (figure == null)
					continue;
//				int figureHeight = figure.getBounds().height;
				int figureHeight = figure.getPreferredSize().height;
				int y = i * distY + figureHeight;
				model2OldLocation.put(acquisitionParameterConfig, 
						new Point(acquisitionParameterConfig.getX(), acquisitionParameterConfig.getY()));				
				acquisitionParameterConfig.setX(getAcquisitionParameterConfigX());
				acquisitionParameterConfig.setY(y);				
			}
		}
		// sort valueProviderConfigs
		Set<ValueProviderConfig> valueProviderConfigs = setup.getValueProviderConfigs();		
		for (ValueProviderConfig valueProviderConfig : valueProviderConfigs) {
			int pageIndex = valueProviderConfig.getPageIndex();
			SortedSet<ValueProviderConfig> configs = pageIndex2PageOrderList.get(pageIndex);
			if (configs == null)
				configs = new TreeSet<ValueProviderConfig>(pageOrderComparator);			
			configs.add(valueProviderConfig);
			pageIndex2PageOrderList.put(pageIndex, configs);
		}
		// layouts valueProviderConfigs
		int counterPageIndex = 1;
		for (Map.Entry<Integer, SortedSet<ValueProviderConfig>> entry : pageIndex2PageOrderList.entrySet()) 
		{
			int counterPageOrder = 0;
			SortedSet<ValueProviderConfig> configs = entry.getValue();
			for (ValueProviderConfig valueProviderConfig : configs) {				
				GraphicalEditPart ep = (GraphicalEditPart) viewer.getEditPartRegistry().get(valueProviderConfig);
				if (ep != null) {
					IFigure figure = ep.getFigure();
					if (figure == null)
						continue;
					int figureHeight = figure.getPreferredSize().height;
//					int figureHeight = figure.getBounds().height;
					int y = counterPageOrder * distY + figureHeight;
					int dx = counterPageIndex * distX;
//					int figureWidth = figure.getBounds().width;
					int figureWidth = figure.getPreferredSize().width;
					int x = getAcquisitionParameterConfigX() - dx - counterPageIndex * figureWidth;
					model2OldLocation.put(valueProviderConfig, 
							new Point(valueProviderConfig.getX(), valueProviderConfig.getY()));
					valueProviderConfig.setX(x);
					valueProviderConfig.setY(y);					
					counterPageOrder++;					
				}
			}
			counterPageIndex++;
		}
		notifyEditParts();
	}

	private Comparator<ValueProviderConfig> pageOrderComparator = new Comparator<ValueProviderConfig>(){	
		public int compare(ValueProviderConfig config1, ValueProviderConfig config2) {
			return config1.getPageOrder() - config2.getPageOrder();
		}
	};

	private Comparator<Integer> revertedPageIndexComparator = new Comparator<Integer>(){	
		public int compare(Integer config1, Integer config2) {
			return config2 - config1;
		}
	};
	
	protected int getAcquisitionParameterConfigX() {
		// TODO determine right coordinate of graphicalViewer
		return 650;
	}
	
	private SortedMap<Integer, SortedSet<ValueProviderConfig>> pageIndex2PageOrderList = 
		new TreeMap<Integer, SortedSet<ValueProviderConfig>>(revertedPageIndexComparator);
	
	private Map<IGraphicalInfoProvider, Point> model2OldLocation = new HashMap<IGraphicalInfoProvider, Point>();
	
	protected void notifyEditParts() 
	{
		ModelNotificationManager.sharedInstance().notify(
				ObjectIDProvider.getObjectID(setup), 
				ModelNotificationManager.PROP_MOVE, 
				null, 
				setup);
		for (Map.Entry<IGraphicalInfoProvider, Point> entry : model2OldLocation.entrySet()) {
			IGraphicalInfoProvider model = entry.getKey();
			ModelNotificationManager.sharedInstance().notify(
					ObjectIDProvider.getObjectID(model), 
					ModelNotificationManager.PROP_MOVE, 
					null, 
					model);
		}			
	}
	
}
