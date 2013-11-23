package org.nightlabs.jfire.reporting.admin.parameter.ui.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.reporting.admin.parameter.ui.command.DeleteConnectionCommand;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpolicy.ConnectionEndpointEditPolicy;
import org.nightlabs.jfire.reporting.admin.parameter.ui.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueConsumerBindingPropertySource;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;
import org.nightlabs.jfire.reporting.parameter.config.id.ValueConsumerBindingID;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueConsumerBindingEditPart
extends AbstractConnectionEditPart
implements PropertyChangeListener
{
	private static final Logger logger = Logger.getLogger(ValueConsumerBindingEditPart.class);
	
	public ValueConsumerBindingEditPart(ValueConsumerBinding binding, ValueAcquisitionSetup setup) {
		super();
		setModel(binding);
		this.setup = setup;
	}
	
	private ValueAcquisitionSetup setup;
	public ValueConsumerBinding getValueConsumerBinding() {
		return (ValueConsumerBinding) getModel();
	}
	
//	public static final Color
//		alive = new Color(Display.getDefault(),0,74,168),
////		dead  = new Color(Display.getDefault(),0,0,0);
//		dead = new Color(Display.getDefault(),125,125,125);
	
	@Override
	protected void createEditPolicies()
	{
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {
			@Override
			protected Command getDeleteCommand(GroupRequest request) {
				return new DeleteConnectionCommand(getValueConsumerBinding(), setup);
			}
		});
	}
	
	@Override
	protected IFigure createFigure()
	{
		PolylineConnection connection = new PolylineConnection();
//		connection.setTargetDecoration(new PolygonDecoration());
		connection.setConnectionRouter(new ManhattanConnectionRouter());
//		connection.setConnectionRouter(new FanRouter());
//		GraphicalEditPart editPart = (GraphicalEditPart) getRoot().getViewer().getEditPartRegistry().get(setup);
//		IFigure figure = editPart.getFigure();
//		if (figure != null)
//			connection.setConnectionRouter(new ShortestPathConnectionRouter(figure));
		return connection;
	}
	
	@Override
	public void activateFigure()
	{
		super.activateFigure();
		/*Once the figure has been added to the ConnectionLayer,
		 * start listening for its router to change.
		 */
		getFigure().addPropertyChangeListener(Connection.PROPERTY_CONNECTION_ROUTER, this);
	}

	@Override
	public void deactivateFigure(){
		getFigure().removePropertyChangeListener(Connection.PROPERTY_CONNECTION_ROUTER, this);
		super.deactivateFigure();
	}
	
	public void propertyChange(PropertyChangeEvent event)
	{
		logger.info("Property "+event.getPropertyName() +" changed!"); //$NON-NLS-1$ //$NON-NLS-2$
		
		String property = event.getPropertyName();
		if (Connection.PROPERTY_CONNECTION_ROUTER.equals(property)){
			refreshBendpoints();
			refreshBendpointEditPolicy();
		}
		if ("value".equals(property))   //$NON-NLS-1$
			refreshVisuals();
		if ("bendpoint".equals(property))   //$NON-NLS-1$
			refreshBendpoints();
		
		refresh();
	}
	
	/**
	 * Updates the bendpoints, based on the model.
	 */
	protected void refreshBendpoints()
	{
		if (getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter)
			return;
//		List modelConstraint = getWire().getBendpoints();
//		List figureConstraint = new ArrayList();
//		for (int i=0; i<modelConstraint.size(); i++) {
//			WireBendpoint wbp = (WireBendpoint)modelConstraint.get(i);
//			RelativeBendpoint rbp = new RelativeBendpoint(getConnectionFigure());
//			rbp.setRelativeDimensions(wbp.getFirstRelativeDimension(),
//										wbp.getSecondRelativeDimension());
//			rbp.setWeight((i+1) / ((float)modelConstraint.size()+1));
//			figureConstraint.add(rbp);
//		}
//		getConnectionFigure().setRoutingConstraint(figureConstraint);
	}

	private void refreshBendpointEditPolicy()
	{
		if (getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter)
			installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, null);
//		else
//			installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new WireBendpointEditPolicy());
	}

	/**
	 * Refreshes the visual aspects of this, based upon the
	 * model (Wire). It changes the wire color depending on
	 * the state of Wire.
	 * 
	 */
	@Override
	protected void refreshVisuals() {
		refreshBendpoints();
//		if (getWire().getValue())
//			getWireFigure().setForegroundColor(alive);
//		else
//			getWireFigure().setForegroundColor(dead);
	}
	
	protected ObjectID getObjectID() {
		return ValueConsumerBindingID.create(getValueConsumerBinding());
	}
	
	@Override
	public void deactivate()
	{
		if (!isActive())
			return;
		super.deactivate();
		ModelNotificationManager.sharedInstance().removePropertyChangeListener(getObjectID(), this);
	}
	
	@Override
	public void activate()
	{
		if (isActive())
			return;
		super.activate();
		ModelNotificationManager.sharedInstance().addPropertyChangeListener(getObjectID(), this);
	}
	
	private IPropertySource propertySource;
	public IPropertySource getPropertySource() {
		if (propertySource == null) {
			propertySource = new ValueConsumerBindingPropertySource(getValueConsumerBinding());
		}
		return propertySource;
	}
	
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
