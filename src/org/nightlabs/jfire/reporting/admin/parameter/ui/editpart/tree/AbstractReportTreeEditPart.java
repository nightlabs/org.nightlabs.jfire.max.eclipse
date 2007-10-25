package org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.tree;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractReportTreeEditPart 
extends AbstractTreeEditPart 
{

	/**
	 * @param model
	 */
	public AbstractReportTreeEditPart(Object model) {
		super(model);
	}
	
	private IPropertySource propertySource;
	public IPropertySource getPropertySource() {
		if (propertySource == null)
			propertySource = createPropertySource();
		return propertySource;
	}
	protected abstract IPropertySource createPropertySource();
	
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
