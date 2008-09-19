package org.nightlabs.jfire.simpletrade.ui.detail;

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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
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
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.simpletrade.store.prop.SimpleProductTypeStruct;
import org.nightlabs.jfire.simpletrade.ui.SimpletradePlugin;
import org.nightlabs.jfire.simpletrade.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
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
	private static final String ATTR_NAME_WEIGHT_2 = "weight_2";
	private static final String ATTR_NAME_WEIGHT_1 = "weight_1";
	private static final String ATTR_NAME_SASH_ORIENTATION = "sashOrientation";

	private static final int defaultImageHeight = 200;
	private static final int defaultImageWidth = 200;

	private Logger logger = Logger.getLogger(SimpleProductTypeDetailViewComposite.class);

	private Composite textWrapper;
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
		ProductType.FETCH_GROUP_NAME, ProductType.FETCH_GROUP_OWNER, ProductType.FETCH_GROUP_VENDOR,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_GROUPS};

	private volatile Job setProductTypeIDJob = null;
	public void setProductTypeID(final ProductTypeID productTypeID) {
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.simpletrade.ui.detail.SimpleProductTypeDetailViewComposite.loadProductTypeJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final SimpleProductType productType = (SimpleProductType) ProductTypeDAO.sharedInstance().getProductType(productTypeID,
						FETCH_GROUP_PRODUCT_TYPE_DETAIL, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
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
				IStruct struct = StructLocalDAO.sharedInstance().getStructLocal(
						SimpleProductType.class, props.getStructScope(), props.getStructLocalScope(), monitor);
				if (props != null)
					props.inflate(struct);

				final Job thisJob = this;
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (thisJob != setProductTypeIDJob)
							return;

						if (isDisposed())
							return;

						productTypeName.setText(productType.getName().getText());
						currImageData = null;

						ImageDataField smallImg = null;
						try {
							if (props != null)
								smallImg = (ImageDataField) props.getDataField(SimpleProductTypeStruct.IMAGES_SMALL_IMAGE);
						} catch (Exception e) {
							logger.warn("Loading image from propertySet failed!", e);
						}
						if (smallImg != null && smallImg.getContent() != null) {
//							ImageData id = null;
							InputStream in = new InflaterInputStream(new ByteArrayInputStream(smallImg.getContent()));
							try {
								currImageData = new ImageData(in);
							} finally {
								if (in != null)
									try {
										in.close();
									} catch (IOException e) {
										logger.error("", e);
									}
							}
						}

						displayImage();

						I18nTextDataField description = null;
						try {
							if (props != null)
								description = (I18nTextDataField) props.getDataField(SimpleProductTypeStruct.DESCRIPTION_LONG);
						} catch (Exception e) {
							logger.warn("Loading image from propertySet failed!", e);
						}
						productTypeDescription.setText(description == null ? "" : description.getI18nText().getText());
					}
				});
				return Status.OK_STATUS;
			}
		};
		setProductTypeIDJob = loadJob;
		loadJob.schedule();
	}

	private SashForm sashForm;

	protected void createComposite(XComposite parent)
	{
		parent.getGridLayout().numColumns = 2;
		parent.getGridLayout().makeColumnsEqualWidth = false;

		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		parent.setBackground(toolkit.getColors().getBackground());
		Form form = toolkit.createForm(parent);
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.setLayout(new GridLayout());
		Composite comp = form.getBody();
		comp.setLayout(new GridLayout());

//		sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm = new SashForm(comp, SWT.HORIZONTAL);
		sashForm.setLayout(new FillLayout());
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
//		textWrapper = new XComposite(sashForm, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		textWrapper = toolkit.createComposite(sashForm);
		textWrapper.setLayout(new GridLayout());
		textWrapper.setLayoutData(new GridData(GridData.FILL_BOTH));
		productTypeName = new ReadOnlyLabeledText(textWrapper, Messages.getString("org.nightlabs.jfire.simpletrade.ui.detail.SimpleProductTypeDetailViewComposite.productTypeName.caption"), SWT.BORDER); // | SWT.READ_ONLY); //$NON-NLS-1$
		productTypeDescription = new ReadOnlyLabeledText(textWrapper, Messages.getString("org.nightlabs.jfire.simpletrade.ui.detail.SimpleProductTypeDetailViewComposite.productTypeDescription.caption"), SWT.BORDER); // | SWT.MULTI | SWT.READ_ONLY); //$NON-NLS-1$
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
		imageWrapper.adaptToToolkit();
		imageWrapper.getGridData().grabExcessHorizontalSpace = false;
		imageWrapper.getGridData().heightHint = defaultImageHeight;
		imageWrapper.getGridData().widthHint = defaultImageWidth;
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
				int orientation = (restoredOrientation != null && restoredWeights != null) ? restoredOrientation : sashForm.getOrientation();
				if (getSize().x < 350 && orientation != SWT.VERTICAL) {
					setOrientation(SWT.VERTICAL);
				}
				if (getSize().x > 400 && orientation != SWT.HORIZONTAL) {
					setOrientation(SWT.HORIZONTAL);
				}
			}
		});
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		imageLabel.setLayoutData(gd);

		sashForm.setWeights(new int[] {10, 10});
		Display.getDefault().asyncExec(sash_setWeights_runnable);
	}

	private int sash_setWeights_counter = 0;

	private Runnable sash_setWeights_runnable = new Runnable() {
		public void run() {

			if (restoredOrientation != null && restoredWeights != null) {
				sashForm.setOrientation(restoredOrientation);
				sashForm.setWeights(restoredWeights);
				restoredOrientation = null;
				restoredWeights = null;
				return;
			}

			++sash_setWeights_counter;
			int leftWeight = (100 * (getSize().x - defaultImageWidth) / getSize().x); // first multiply, then divide!!! otherwise the integer-divisions are most of the time 0
			int rightWeight = (100 * defaultImageWidth / getSize().x);


			if (leftWeight > 0 && rightWeight > 0) {
				int[] weights = new int[] {leftWeight, rightWeight};
				logger.info("Setting weights to " + weights[0] + ", " + weights[1]);
				sashForm.setWeights(weights);
			}
			else {
				logger.warn("Weights are out of range! leftWeight=" + leftWeight + " rightWeight=" + rightWeight);

				if (sash_setWeights_counter < 10)
					Display.getDefault().asyncExec(sash_setWeights_runnable);
			}
		}
	};

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
		int imgMaxWidth = defaultImageWidth - 5;
		int maxThumbnailHeight = imageWrapper.getSize().y;
		int maxThumbnailWidth = Math.min(Math.max(imageWrapper.getSize().x / 2, imgMaxWidth), imgMaxWidth);
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

	public void setOrientation(int orientation) {
		sashForm.setOrientation(orientation);
		getDisplay().asyncExec(sash_setWeights_runnable);
	}

	public void saveState(IMemento memento) {
		int[] weights = sashForm.getWeights();
		if (weights.length != 2)
			return;
		IMemento element = memento.createChild(this.getClass().getSimpleName());
		element.putInteger(ATTR_NAME_SASH_ORIENTATION, sashForm.getOrientation());
		logger.info("Storing weights to " + weights[0] + ", " + weights[1]);
		element.putInteger(ATTR_NAME_WEIGHT_1, weights[0]);
		element.putInteger(ATTR_NAME_WEIGHT_2, weights[1]);
	}

	private Integer restoredOrientation = null;
	private int[] restoredWeights = null;

	public void restoreState(IMemento memento) {
		IMemento element = memento.getChild(this.getClass().getSimpleName());
		restoredOrientation = element.getInteger(ATTR_NAME_SASH_ORIENTATION);
		if (restoredOrientation != null) {
			Integer weight_1 = element.getInteger(ATTR_NAME_WEIGHT_1);
			Integer weight_2 = element.getInteger(ATTR_NAME_WEIGHT_2);
			if (weight_1 != null && weight_2 != null) {
				restoredWeights = new int[2];
				restoredWeights[0] = weight_1;
				restoredWeights[1] = weight_2;
			} else {
				restoredOrientation = null;
			}
		}
	}
}
