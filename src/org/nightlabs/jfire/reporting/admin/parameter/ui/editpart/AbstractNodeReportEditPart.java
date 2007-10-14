package org.nightlabs.jfire.reporting.admin.parameter.ui.editpart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpolicy.ContainerHighlightEditPolicy;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpolicy.ReportNodeEditPolicy;
import org.nightlabs.jfire.reporting.admin.parameter.ui.figure.AbstractInputNodeFigure;
import org.nightlabs.jfire.reporting.admin.parameter.ui.figure.NodeConnectionAnchor;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractNodeReportEditPart 
extends AbstractReportParameterEditPart
implements NodeEditPart
{
	public AbstractNodeReportEditPart(ValueAcquisitionSetup setup) {
		super();
		this.setup = setup;
	}

	private ValueAcquisitionSetup setup;
	protected ValueAcquisitionSetup getValueAcquisitionSetup() {
		return setup;
	}
	
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new ReportNodeEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ContainerHighlightEditPolicy(this));
	}
	
	protected AbstractInputNodeFigure getNodeFigure(){
		return (AbstractInputNodeFigure) getFigure();
	}
	
	private ConnectionAnchor sourceAnchor;
	private Map<String, ConnectionAnchor> targetAnchors;
	private Map<ConnectionAnchor, INodeAnchorItem> anchors2Items = new HashMap<ConnectionAnchor, INodeAnchorItem>();
	private List<ConnectionAnchor> targetAnchorList; // for having the target anchors sorted like given in getNodeTargetItems()
	
	/**
	 * Returns (and creates if neccessary) the source {@link ConnectionAnchor} of this edit part.
	 */
	protected ConnectionAnchor getSourceConnectionAnchor() {
		if (sourceAnchor == null) {
			INodeAnchorItem item = getNodeSourceItem();
			if (item != null) {
				sourceAnchor = new NodeConnectionAnchor(getFigure(), 1, NodeConnectionAnchor.TYPE_RIGHT);
				anchors2Items.put(sourceAnchor, item);
			}
		}
		return sourceAnchor;
	}
	
	/**
	 * Should return the {@link INodeAnchorItem} that represents the
	 * source anchor for this edit part.
	 */
	protected abstract INodeAnchorItem getNodeSourceItem();
	
	/**
	 * Should return the list of {@link INodeAnchorItem} that will
	 * be target-anchors for this edit part.
	 */
	protected abstract List<INodeAnchorItem> getNodeTargetItems();
	
	/**
	 * Fills (if neccessary) the map of target anchors with {@link ConnectionAnchor}s
	 * according to the {@link INodeAnchorItem}s returned by {@link #getNodeTargetItems()}
	 * @return
	 */
	private Map<String, ConnectionAnchor> getTargetAnchors() {
		if (targetAnchors == null) {
			targetAnchors = new HashMap<String, ConnectionAnchor>();
			targetAnchorList = new ArrayList<ConnectionAnchor>();
			int i = 1;
			for (INodeAnchorItem item : getNodeTargetItems()) {
				ConnectionAnchor anchor = new NodeConnectionAnchor(getFigure(), i++, NodeConnectionAnchor.TYPE_LEFT);
				targetAnchors.put(item.getAnchorName(), anchor);
				anchors2Items.put(anchor, item);
				targetAnchorList.add(anchor);
			}
		}
		return targetAnchors;
	}
	
	/**
	 * Returns the {@link ConnectionAnchor} associated to the given parameterID.
	 * 
	 * @param parameterID The parameterID ({@link INodeAnchorItem#getAnchorName()}) the {@link ConnectionAnchor} should be searched for
	 * @return the {@link ConnectionAnchor} associated to the given parameterID.
	 */
	public ConnectionAnchor getTargetConnectionAnchor(String parameterID) {
		return getTargetAnchors().get(parameterID);
	}
	
	/**
	 * Returns the (ordered) List of target {@link ConnectionAnchor}s.
	 * 
	 * @return The (ordered) List of target {@link ConnectionAnchor}s.
	 */
	public List<ConnectionAnchor> getTargetConnectionAnchors() {
		getTargetAnchors();
		return targetAnchorList;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return getSourceConnectionAnchor();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return getSourceConnectionAnchor();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		if (connection instanceof ValueConsumerBindingEditPart) {
			ValueConsumerBindingEditPart vcbep = (ValueConsumerBindingEditPart) connection;
			return getTargetConnectionAnchor(vcbep.getValueConsumerBinding().getParameterID());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		if (request instanceof CreateConnectionRequest) {
			CreateConnectionRequest createRequest = (CreateConnectionRequest) request;
			for (ConnectionAnchor anchor : getTargetAnchors().values()) {
				if (anchor instanceof NodeConnectionAnchor) {
					if (((NodeConnectionAnchor)anchor).intercepts(createRequest.getLocation())) {
						return anchor;
					}
				}
			}
			return null;
		}
		return null;
	}	
	
	/**
	 * Returns the name (parameterID) of the {@link INodeAnchorItem} assiciated to the given anchor.
	 *
	 * @return  The name of the ConnectionAnchor as a String.
	 */
	public String mapConnectionAnchorToParameterID(ConnectionAnchor c) {
		INodeAnchorItem item = anchors2Items.get(c);
		if (item != null)
			return item.getAnchorName();
		return null;
	}	
	
	/**
	 * Returns the type of the {@link INodeAnchorItem} associated to the given anchor.
	 *
	 * @return  The name of the ConnectionAnchor as a String.
	 */
	public String mapConnectionAnchorToParameterType(ConnectionAnchor c){
		INodeAnchorItem item = anchors2Items.get(c);
		if (item != null)
			return item.getAnchorType();
		return null;
	}	
	
	
}
