package org.nightlabs.jfire.reporting.admin.parameter.ui.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.reporting.admin.parameter.ui.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.parameter.config.IGraphicalInfoProvider;

/**
 * The base Report Parameter EditPart
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public abstract class AbstractReportParameterEditPart
extends AbstractGraphicalEditPart
implements PropertyChangeListener
{
	private static final Logger logger = Logger.getLogger(AbstractReportParameterEditPart.class);
	
	public AbstractReportParameterEditPart() {
		super();
	}

	// listen here for model changes
	public void propertyChange(PropertyChangeEvent evt) {
		logger.info("propertyChange "+evt.getPropertyName()+" "+evt.getNewValue()+ " for editPart "+this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//		refreshVisuals();
		refresh();
	}

	protected IGraphicalInfoProvider getGraphicalInfoProvider() {
		return (IGraphicalInfoProvider) getModel();
	}
	
	/**
	 * Updates the visual aspect of this.
	 */
	@Override
	protected void refreshVisuals()
	{
		if (getModel() instanceof IGraphicalInfoProvider) {
			int x = getGraphicalInfoProvider().getX();
			int y = getGraphicalInfoProvider().getY();
//			int width = getGraphicalInfoProvider().getWidth();
//			int height = getGraphicalInfoProvider().getHeight();
			int width = -1;
			int height = -1;
			Rectangle r = new Rectangle(x, y, width, height);

			((GraphicalEditPart) getParent()).setLayoutConstraint(
				this,
				getFigure(),
				r);
		}
		else {
			Rectangle r = new Rectangle(0, 0, -1, -1);
			((GraphicalEditPart) getParent()).setLayoutConstraint(
					this,
					getFigure(),
					r);
			
			getFigure().repaint();
			getFigure().revalidate();
		}
	}

	@Override
	protected void createEditPolicies()
	{
//		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ReportXYLayoutEditPolicy(
//				(XYLayout)getContentPane().getLayoutManager()));
	}

	protected abstract ObjectID getObjectID();
	
	@Override
	public void activate()
	{
		if (isActive())
			return;
		super.activate();
		ModelNotificationManager.sharedInstance().addPropertyChangeListener(getObjectID(), this);
	}

	@Override
	public void deactivate()
	{
		if (!isActive())
			return;
		super.deactivate();
		ModelNotificationManager.sharedInstance().removePropertyChangeListener(getObjectID(), this);
	}
	
	private IPropertySource propertySource;
	public IPropertySource getPropertySource() {
		if (propertySource == null)
			propertySource = createPropertySource();
		return propertySource;
	}
	protected abstract IPropertySource createPropertySource();
	
  /**
   * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
   */
  @Override
  public Object getAdapter(Class key)
  {
    /* override the default behavior defined in AbstractEditPart
     *  which would expect the model to be a property sourced.
     *  instead the editpart can provide a property source
     */
    if (IPropertySource.class == key) {
      return getPropertySource();
    }
    return super.getAdapter(key);
  }
}
