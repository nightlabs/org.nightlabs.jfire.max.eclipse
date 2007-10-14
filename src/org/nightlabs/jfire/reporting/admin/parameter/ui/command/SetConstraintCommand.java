package org.nightlabs.jfire.reporting.admin.parameter.ui.command;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.reporting.admin.parameter.ui.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.admin.parameter.ui.resource.Messages;
import org.nightlabs.jfire.reporting.admin.parameter.ui.util.ObjectIDProvider;
import org.nightlabs.jfire.reporting.parameter.config.IGraphicalInfoProvider;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SetConstraintCommand 
extends Command 
{
	private Point newPos;
//	private Dimension newSize;
	private Point oldPos;
//	private Dimension oldSize;
	private IGraphicalInfoProvider part;
	
	public SetConstraintCommand() {
		super();
		setLabel(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.command.SetConstraintCommand.changeBounds.label")); //$NON-NLS-1$
	}

	public void execute() {
//		oldSize = part.getSize();
//		oldPos  = part.getLocation();
		oldPos  = new Point(part.getX(), part.getY());
		redo();		
	}

	public String getLabel() {
//		if (oldSize.equals(newSize))
			return Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.command.SetConstraintCommand.changeLocation.label"); //$NON-NLS-1$
//		return "Resize";
	}

	public void redo() {
//		part.setSize(newSize);
		part.setX(newPos.x);
		part.setY(newPos.y);
		
		ModelNotificationManager.sharedInstance().notify(
				getObjectID(part), 
				IGraphicalInfoProvider.PROP_X, 
				oldPos.x, 
				newPos.x);
	}

	protected ObjectID getObjectID(Object o) {
		return ObjectIDProvider.getObjectID(o);
	}
	
	public void setLocation(Rectangle r) {
		setLocation(r.getLocation());
//		setSize(r.getSize());
	}

	public void setLocation(Point p) {
		newPos = p;
	}

	public void setModel(IGraphicalInfoProvider part) {
		this.part = part;
	}

//	public void setSize(Dimension p) {
//		newSize = p;
//	}

	public void undo() {
//		part.setSize(oldSize);
//		part.setLocation(oldPos);
		part.setX(oldPos.x);
		part.setY(oldPos.y);
		ModelNotificationManager.sharedInstance().notify(
				getObjectID(part), 
				IGraphicalInfoProvider.PROP_X, 
				oldPos.x, 
				newPos.x);		
	}
}
