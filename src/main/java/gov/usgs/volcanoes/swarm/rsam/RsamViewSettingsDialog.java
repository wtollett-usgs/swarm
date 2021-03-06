package gov.usgs.volcanoes.swarm.rsam;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import gov.usgs.volcanoes.core.math.BinSize;
import gov.usgs.volcanoes.swarm.Icons;
import gov.usgs.volcanoes.swarm.SwarmModalDialog;
import gov.usgs.volcanoes.swarm.rsam.RsamViewSettings.ViewType;

/**
 * RSAM view settings dialog.
 * 
 * @author Tom Parker
 */
public class RsamViewSettingsDialog extends SwarmModalDialog {
  private static final long serialVersionUID = 1L;

  private JPanel dialogPanel;

  private static RsamViewSettingsDialog dialog;
  private RsamViewSettings settings;

  private ButtonGroup viewGroup;
  private JRadioButton valuesButton;
  private JRadioButton countsButton;

  private JCheckBox detrend;
  private JCheckBox despike;
  private JTextField despikePeriod;
  private JComboBox<ValuesPeriods> valuesPeriod;
  private JComboBox<CountsPeriods> countsPeriod;

  private JCheckBox autoScale;
  private JTextField scaleMax;
  private JTextField scaleMin;
  
  private JCheckBox alarm;
  
  private JCheckBox runningMedianButton;
  private JTextField runningMedianPeriod;
  private JCheckBox runningMeanButton;
  private JTextField runningMeanPeriod;

  private JTextField eventThreshold;
  private JTextField eventRatio;
  private JTextField eventMaxLength;
  private JComboBox<BinSize> binSize;

/*  private ButtonGroup filterGroup;
  private JCheckBox filterEnabled;
  private JRadioButton lowPass;
  private JRadioButton highPass;
  private JRadioButton bandPass;
  private JCheckBox zeroPhaseShift;
  private JTextField corner1;
  private JTextField corner2;
  private JSlider order;*/
  
  private RsamViewSettingsDialog() {
    super(applicationFrame, "RSAM Settings");
    this.setIconImage(Icons.rsam_values.getImage());
    createUi();
    setSizeAndLocation();
  }


  /**
   * Get instance of RSAM view settings dialog.
   * @param s RSAM view settings
   * @return RSAM view settings dialog
   */
  public static RsamViewSettingsDialog getInstance(RsamViewSettings s) {
    if (dialog == null) {
      dialog = new RsamViewSettingsDialog();
    }

    dialog.settings = s;
    dialog.setToCurrent();
    return dialog;
  }

  /**
   * Set to current.
   */
  public void setToCurrent() {
    switch (settings.getType()) {
      case VALUES:
        valuesButton.setSelected(true);
        break;
      case COUNTS:
        countsButton.setSelected(true);
        break;
      default:
        break;
    }

    detrend.setSelected(settings.detrend);
    despike.setSelected(settings.despike);
    despikePeriod.setText(Integer.toString(settings.despikePeriod));

    valuesPeriod.setSelectedItem(ValuesPeriods.fromS(settings.valuesPeriodS));
    countsPeriod.setSelectedItem(CountsPeriods.fromS(settings.countsPeriodS));
    runningMedianButton.setSelected(settings.runningMedian);
    runningMedianPeriod.setText(String.format("%.1f", settings.runningMedianPeriodS));
    runningMedianPeriod.setEnabled(settings.runningMedian);
    runningMeanButton.setSelected(settings.runningMean);
    runningMeanPeriod.setText(String.format("%.1f", settings.runningMeanPeriodS));
    runningMeanPeriod.setEnabled(settings.runningMean);
    eventThreshold.setText(String.format("%d", settings.eventThreshold));
    eventRatio.setText(String.format("%.1f", settings.eventRatio));
    eventMaxLength.setText(String.format("%.1f", settings.eventMaxLengthS));
    binSize.setSelectedItem(settings.binSize);
    autoScale.setSelected(settings.autoScale);
    alarm.setSelected(settings.alarm);
    scaleMax.setText("" + settings.scaleMax);
    scaleMax.setEnabled(!autoScale.isSelected());
    scaleMin.setText("" + settings.scaleMin);
    scaleMin.setEnabled(!autoScale.isSelected());
    
/*    filterEnabled.setSelected(settings.filterOn);

    switch (settings.filter.getType()) {
      case LOWPASS:
        lowPass.setSelected(true);
        corner1.setText("0.0");
        corner2.setText(String.format("%.1f", settings.filter.getCorner1()));
        break;
      case HIGHPASS:
        highPass.setSelected(true);
        corner1.setText(String.format("%.1f", settings.filter.getCorner1()));
        corner2.setText("0.0");
        break;
      case BANDPASS:
        bandPass.setSelected(true);
        corner1.setText(String.format("%.1f", settings.filter.getCorner1()));
        corner2.setText(String.format("%.1f", settings.filter.getCorner2()));
        break;
      default:
        break;
    }
    order.setValue(settings.filter.getOrder());*/
  }

  private void createComponents() {
    viewGroup = new ButtonGroup();
    valuesButton = new JRadioButton("RSAM values");
    countsButton = new JRadioButton("Event counts");
    viewGroup.add(valuesButton);
    viewGroup.add(countsButton);

    valuesPeriod = new JComboBox<ValuesPeriods>(ValuesPeriods.values());
    countsPeriod = new JComboBox<CountsPeriods>(CountsPeriods.values());

    detrend = new JCheckBox("Detrend (linear)");
    despike = new JCheckBox("Despike (mean)");
    despikePeriod = new JTextField(3);
    despikePeriod.setInputVerifier(new IntegerInputVerifier(true));

    runningMedianButton = new JCheckBox("Running median");
    runningMedianPeriod = new JTextField(4);
    runningMedianButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        runningMedianPeriod.setEnabled(runningMedianButton.isSelected());
      }
    });
    runningMeanButton = new JCheckBox("Running mean");
    runningMeanPeriod = new JTextField(4);
    runningMeanButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        runningMeanPeriod.setEnabled(runningMeanButton.isSelected());
      }
    });

    autoScale = new JCheckBox("Auto scale");
    autoScale.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        scaleMax.setEnabled(!autoScale.isSelected());
        scaleMin.setEnabled(!autoScale.isSelected());
      }
    });
    alarm = new JCheckBox("Alarm");

    scaleMax = new JTextField(4);
    scaleMax.setInputVerifier(new IntegerInputVerifier());
    scaleMin = new JTextField(4);
    scaleMin.setInputVerifier(new IntegerInputVerifier());

    eventThreshold = new JTextField(4);
    eventRatio = new JTextField(4);
    eventMaxLength = new JTextField(4);
    binSize = new JComboBox<BinSize>(BinSize.values());
    
/*    filterGroup = new ButtonGroup();
    filterEnabled = new JCheckBox("Enabled");
    lowPass = new JRadioButton("Low pass");
    highPass = new JRadioButton("High pass");
    bandPass = new JRadioButton("Band pass");
    filterGroup.add(lowPass);
    filterGroup.add(highPass);
    filterGroup.add(bandPass);
    zeroPhaseShift = new JCheckBox("Zero phase shift");
    corner1 = new JTextField(7);
    corner2 = new JTextField(7);
    order = new JSlider(2, 8, 4);
    order.setMajorTickSpacing(2);
    order.setSnapToTicks(true);
    order.createStandardLabels(2);
    order.setPaintLabels(true);*/
  }

  protected void createUi() {
    super.createUi();
    createComponents();
    FormLayout layout = new FormLayout(
        "left:65dlu, 1dlu, left:30dlu, 3dlu, left:70dlu, 3dlu, left:30dlu, 3dlu, left:30dlu", "");

    DefaultFormBuilder builder = new DefaultFormBuilder(layout).border(Borders.DIALOG);

    CellConstraints cc = new CellConstraints();

    builder.appendSeparator("View");
    builder.nextLine();
    builder.append(valuesButton);
    builder.nextColumn(2);
    builder.append(countsButton);
    builder.nextLine();

    // RSAM Options
    builder.appendSeparator("RSAM Options");
    
    builder.nextLine();
    builder.append(detrend);
    builder.nextColumn(2);
    builder.add(new JLabel("RSAM Period:"),
        cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
    builder.nextColumn(2);
    builder.add(valuesPeriod, cc.xyw(builder.getColumn(), builder.getRow(), 3));

    builder.nextLine();
    builder.append(despike);
    builder.nextColumn(2);
    builder.add(new JLabel("Despike Period:"),
        cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
    builder.nextColumn(2);
    builder.add(despikePeriod, cc.xyw(builder.getColumn(), builder.getRow(), 1));
    
    builder.nextLine();
    builder.append(autoScale);
    builder.nextColumn(2);
    builder.add(new JLabel("Scale Min:"),
        cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
    builder.nextColumn(2);
    builder.add(scaleMin, cc.xyw(builder.getColumn(), builder.getRow(), 1));
    builder.nextLine();
    builder.append(alarm);
    //builder.appendRow("center:18dlu");
    builder.nextColumn(2);
    builder.add(new JLabel("Scale Max:"),
        cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
    builder.nextColumn(2);
    builder.add(scaleMax, cc.xyw(builder.getColumn(), builder.getRow(), 1));

    builder.nextLine();
    
    // Event options
    builder.appendSeparator("Event Options");
    builder.nextLine();
    builder.appendRow("center:18dlu");
    builder.add(new JLabel("Event threshold:"),
        cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
    builder.nextColumn(2);
    builder.append(eventThreshold);
    builder.add(new JLabel("Period:"),
        cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
    builder.nextColumn(2);
    builder.add(countsPeriod, cc.xyw(builder.getColumn(), builder.getRow(), 3));
    builder.nextLine();

    builder.appendRow("center:18dlu");
    builder.add(new JLabel("Event ratio:"),
        cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
    builder.nextColumn(2);
    builder.append(eventRatio);
    builder.add(new JLabel("Bin size:"),
        cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
    builder.nextColumn(2);
    builder.add(binSize, cc.xyw(builder.getColumn(), builder.getRow(), 3));

    builder.nextLine();
    builder.appendRow("center:18dlu");
    builder.add(new JLabel("Event max length:"),
        cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
    builder.nextColumn(2);
    builder.append(eventMaxLength);
    builder.nextLine();

    // Filter options
/*    builder.appendSeparator("Butterworth Filter");
    
    builder.append(filterEnabled, 3);
    builder.append(zeroPhaseShift, 3);
    builder.nextLine();
    builder.append(lowPass, 3);
    builder.add(new JLabel("Min. frequency:"),
        cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
    builder.nextColumn(2);
    builder.append(corner1);
    builder.nextLine();
    builder.append(highPass, 3);
    builder.add(new JLabel("Max. frequency:"),
        cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
    builder.nextColumn(2);
    builder.append(corner2);
    builder.nextLine();
    builder.append(bandPass, 3);
    builder.add(new JLabel("Order"),
        cc.xyw(builder.getColumn(), builder.getRow(), 3, "center, center"));
    builder.nextLine();
    builder.appendRow("center:20dlu");
    builder.nextColumn(3);
    builder.append(order, 4);
    builder.nextLine();*/

    dialogPanel = builder.getPanel();
    mainPanel.add(dialogPanel, BorderLayout.CENTER);

  }

  /**
   * @see gov.usgs.volcanoes.swarm.SwarmModalDialog#allowOk()
   */
  public boolean allowOk() {
    String message = null;
    try {
      message = "Error in despike period.";
      int dp = Integer.valueOf(despikePeriod.getText());

      message = "Error in event threshold.";
      int et = Integer.parseInt(eventThreshold.getText());
      message = "Error in event ratio.";
      double er = Double.parseDouble(eventRatio.getText());
      message = "Error in event max length.";
      double eml = Double.parseDouble(eventMaxLength.getText());

      if (!autoScale.isSelected()) {
        message = "Error in Scale min";
        int sm = Integer.parseInt(scaleMin.getText());
        message = "Error in Scale max";
        int sn = Integer.parseInt(scaleMax.getText());
      }
      
      message = "Error in running median period.";
      //double rm = Double.parseDouble(runningMedianPeriod.getText());
      message = "Error in running mean period.";
      //double rmp = Double.parseDouble(runningMeanPeriod.getText());

      message = "Error in event threshold.";
      //double et = Double.parseDouble(eventThreshold.getText());
      message = "Error in event ratio.";
      //double er = Double.parseDouble(eventRatio.getText());
      message = "Error in event maximum length.";
      //double eml = Double.parseDouble(eventMaxLength.getText());


      return true;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, message, "Options Error", JOptionPane.ERROR_MESSAGE);
    }

    return false;
  }

  /**
   * @see gov.usgs.volcanoes.swarm.SwarmModalDialog#wasOk()
   */
  public void wasOk() {
    try {
      settings.valuesPeriodS = ((ValuesPeriods) valuesPeriod.getSelectedItem()).getPeriodS();
      settings.countsPeriodS = ((CountsPeriods) countsPeriod.getSelectedItem()).getPeriodS();
      settings.detrend = detrend.isSelected();
      settings.despike = despike.isSelected();
      settings.despikePeriod = Integer.valueOf(despikePeriod.getText());

      settings.runningMean = runningMeanButton.isSelected();
      settings.runningMeanPeriodS = Double.parseDouble(runningMeanPeriod.getText());
      settings.runningMedian = runningMedianButton.isSelected();
      settings.runningMedianPeriodS = Double.parseDouble(runningMedianPeriod.getText());

      settings.eventThreshold = Integer.parseInt(eventThreshold.getText());
      settings.eventRatio = Double.parseDouble(eventRatio.getText());
      settings.eventMaxLengthS = Double.parseDouble(eventMaxLength.getText());
      settings.binSize = (BinSize) binSize.getSelectedItem();

      settings.autoScale = autoScale.isSelected();
      if (!settings.autoScale) {
        settings.scaleMax = Integer.parseInt(scaleMax.getText());
        settings.scaleMin = Integer.parseInt(scaleMin.getText());
      }

      if (valuesButton.isSelected()) {
        settings.setType(ViewType.VALUES);
      } else {
        settings.setType(ViewType.COUNTS);
      }
      settings.alarm = alarm.isSelected();
/*      settings.filterOn = filterEnabled.isSelected();
      settings.zeroPhaseShift = zeroPhaseShift.isSelected();

      FilterType ft = null;
      double c1 = 0;
      double c2 = 0;
      if (lowPass.isSelected()) {
        ft = FilterType.LOWPASS;
        c1 = Double.parseDouble(corner2.getText());
        c2 = 0;
      } else if (highPass.isSelected()) {
        ft = FilterType.HIGHPASS;
        c1 = Double.parseDouble(corner1.getText());
        c2 = 0;
      } else if (bandPass.isSelected()) {
        ft = FilterType.BANDPASS;
        c1 = Double.parseDouble(corner1.getText());
        c2 = Double.parseDouble(corner2.getText());
      }
      settings.filter.set(ft, order.getValue(), 100.0, c1, c2);*/
      
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, "Illegal values.", "Options Error",
          JOptionPane.ERROR_MESSAGE);
    }

  }
  
  /**
   * Integer input verifier.
   */
  public class IntegerInputVerifier extends InputVerifier {

    private boolean positiveOnly = false;

    IntegerInputVerifier() {

    }

    IntegerInputVerifier(boolean positiveOnly) {
      this.positiveOnly = positiveOnly;
    }

    /**
     * Verify.
     * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
     */
    public boolean verify(JComponent input) {
      String text = ((JTextField) input).getText();
      try {
        int value = Integer.valueOf(text);
        if (positiveOnly && value < 0) {
          return false;
        } else {
          return true;
        }
      } catch (NumberFormatException e) {
        return false;
      }
    }
  }

}
