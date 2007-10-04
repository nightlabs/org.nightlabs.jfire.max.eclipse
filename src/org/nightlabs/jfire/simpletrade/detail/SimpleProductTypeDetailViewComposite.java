package org.nightlabs.jfire.simpletrade.detail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.InflaterInputStream;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.ReadOnlyLabeledText;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.datafield.I18nTextDataField;
import org.nightlabs.jfire.prop.datafield.ImageDataField;
import org.nightlabs.jfire.prop.id.StructFieldID;
import org.nightlabs.jfire.simpletrade.SimpletradePlugin;
import org.nightlabs.jfire.simpletrade.resource.Messages;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.simpletrade.store.prop.SimpleProductTypeStruct;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.store.ProductTypeDAO;
import org.nightlabs.progress.ProgressMonitor;

/**
 * This is the Composite which is used by the {@link SimpleProductTypeDetailView}
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class SimpleProductTypeDetailViewComposite 
extends XComposite 
{

	private Logger logger = Logger.getLogger(SimpleProductTypeDetailViewComposite.class);
	
	private XComposite textWrapper;
	private ReadOnlyLabeledText productTypeName;	
	private ReadOnlyLabeledText productTypeDescription;	
	private XComposite imageWrapper;
	private Label imageLabel;
	private ImageData currImageData;
	
	
	public SimpleProductTypeDetailViewComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	public SimpleProductTypeDetailViewComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}
	
	public static final String[] FETCH_GROUP_PRODUCT_TYPE_DETAIL = new String[] {
		ProductType.FETCH_GROUP_NAME, ProductType.FETCH_GROUP_OWNER, 
		ProductType.FETCH_GROUP_PRODUCT_TYPE_GROUPS}; 
	
	public void setProductTypeID(final ProductTypeID productTypeID) {
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.simpletrade.detail.SimpleProductTypeDetailViewComposite.loadProductTypeJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final SimpleProductType productType = (SimpleProductType) ProductTypeDAO.sharedInstance().getProductType(productTypeID, 
						FETCH_GROUP_PRODUCT_TYPE_DETAIL, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						productTypeName.setText(productType.getName().getText());		
					}
				});
				Set<ProductTypeID> ids = new HashSet<ProductTypeID>(Arrays.asList(new ProductTypeID[] {productTypeID}));
				Set<StructFieldID> fields = new HashSet<StructFieldID>(Arrays.asList(new StructFieldID[] {
						SimpleProductTypeStruct.DESCRIPTION_LONG, SimpleProductTypeStruct.IMAGES_SMALL_IMAGE}
				));
				Map<ProductTypeID, PropertySet> propertySets = null;
				try {
					propertySets = SimpletradePlugin.getSimpleTradeManager().getSimpleProductTypesPropertySets(
							ids, fields, new String[] {PropertySet.FETCH_GROUP_FULL_DATA}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
						);
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				} 
				final PropertySet props = propertySets.get(productTypeID);
				IStruct struct = StructLocalDAO.sharedInstance().getStructLocal(SimpleProductType.class, productType.getStructLocalScope(), monitor);
				struct.explodePropertySet(props);
				if (props != null) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							ImageDataField smallImg;
							try {
								smallImg = (ImageDataField) props.getDataField(SimpleProductTypeStruct.IMAGES_SMALL_IMAGE);
							} catch (Exception e) {
								return;
							}
							if (smallImg.getContent() != null) { 
								ImageData id = null;
								InputStream in = new InflaterInputStream(new ByteArrayInputStream(smallImg.getContent()));
								try {
									currImageData = new ImageData(in);
								} finally {
									if (in != null)
										try {
											in.close();
										} catch (IOException e) {
											logger.error(e);
										}
								}
								displayImage();
							} else {
								currImageData = null;
								displayImage();
							}
							
							I18nTextDataField description;
							try {
								description = (I18nTextDataField) props.getDataField(SimpleProductTypeStruct.DESCRIPTION_LONG);
							} catch (Exception e) {
								return;
							}							
							productTypeDescription.setText(description.getI18nText().getText());
						}
					});
				}
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
	}
	
	protected void createComposite(XComposite parent) 
	{
		parent.getGridLayout().numColumns = 2;
		parent.getGridLayout().makeColumnsEqualWidth = false;
		
		final SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);		
		sashForm.setLayout(new FillLayout());
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		textWrapper = new XComposite(sashForm, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		productTypeName = new ReadOnlyLabeledText(textWrapper, Messages.getString("org.nightlabs.jfire.simpletrade.detail.SimpleProductTypeDetailViewComposite.productTypeName.caption"), SWT.BORDER); // | SWT.READ_ONLY); //$NON-NLS-1$
		productTypeDescription = new ReadOnlyLabeledText(textWrapper, Messages.getString("org.nightlabs.jfire.simpletrade.detail.SimpleProductTypeDetailViewComposite.productTypeDescription.caption"), SWT.BORDER); // | SWT.MULTI | SWT.READ_ONLY); //$NON-NLS-1$
		productTypeDescription.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		productTypeDescription.getTextControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		imageWrapper = new XComposite(sashForm, SWT.BORDER, LayoutMode.TIGHT_WRAPPER) {
			@Override
			public void layout(boolean arg0, boolean arg1) {
				layoutingImageWrapper = true;
				try {
					super.layout(arg0, arg1);
					displayImage();
				} finally {
					layoutingImageWrapper = false;
				}
			}
		};
		imageWrapper.getGridData().grabExcessHorizontalSpace = false;
		imageWrapper.getGridData().heightHint = 150;
		imageWrapper.getGridData().widthHint = 150;
		imageLabel = new Label(imageWrapper, SWT.NONE);
		imageLabel.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (imageLabel.getImage() != null)
					imageLabel.getImage().dispose();
			}
		});
		imageWrapper.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}
			public void controlResized(ControlEvent e) {
				displayImage();
			}
		});
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		imageLabel.setLayoutData(gd);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				int leftWeight = (int) ((float)((float)(getSize().x - 150))/(float)getSize().x * 100);
				int rightWeight = (int) ((float)((float)150)/(float)getSize().x * 100);;
				sashForm.setWeights(new int[] {leftWeight, rightWeight});
			}
		});
		sashForm.setWeights(new int[] {10, 4});
	}
	
	private boolean layoutingImageWrapper = false;
	
	private void displayImage() {
		if (imageLabel.getImage() != null) {
			imageLabel.getImage().dispose();
			imageLabel.setImage(null);
		}
		if (currImageData == null) {
			if (!layoutingImageWrapper)
				imageWrapper.layout(true, true);
			return;
		}
		
		int width = currImageData.width;
		int height = currImageData.height;
		double factor = 1.0;
		int maxThumbnailHeight = imageWrapper.getSize().y;
		int maxThumbnailWidth = Math.min(Math.max(imageWrapper.getSize().x / 2, 145), 145);
		if (maxThumbnailWidth > imageWrapper.getSize().x)
			maxThumbnailWidth = imageWrapper.getSize().x;
		
		if (width > maxThumbnailWidth || height > maxThumbnailHeight)
			factor = Math.min(
					height > maxThumbnailHeight ? 1.0*maxThumbnailHeight/height : 1.0, 
					width > maxThumbnailWidth ? 1.0*maxThumbnailWidth/width : 1.0
				);
			
		ImageData scaledData = currImageData.scaledTo((int) (factor*width), (int) (factor*height));
		Image image = new Image(Display.getDefault(), scaledData);
		imageLabel.setImage(image);
		if (!layoutingImageWrapper) { 
			imageLabel.getParent().layout(true, true);
			imageLabel.getParent().getParent().layout(true, true);
		}
	}
}
