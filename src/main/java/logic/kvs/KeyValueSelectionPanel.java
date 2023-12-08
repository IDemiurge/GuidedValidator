package logic.kvs;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import logic.kvs.datasource.ValidatorDataSource;
import logic.kvs.validator.CombinationValidator;
import logic.kvs.validator.ICombinationValidator;
import util.MapUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class KeyValueSelectionPanel extends JPanel {

    // Constants for implementing de-toggle functionality on a button group
    private static final String NOT_SELECTED = "NOT_SELECTED";
    private static final String SELECTED = "SELECTED";
    // Constants for coloring text on selection to show impossible combinations
    public static final Color TEXT_COLOR_COLORIZED = Color.red;
    public static final Color TEXT_COLOR_DEFAULT = Color.black;
    // Constants for display default text until Validator has been initialized
    private static final String DEFAULT_STATUS_TEXT = "Waiting for Validator init...";

    // The model for the panel
    private KeyValueSelectionSet keyValueSet;
    private ICombinationValidator validator;

    // The label used to provide feedback to the user on whether or not a valid combination was selected
    private JLabel statusLabel;
    // A ButtonGroup per key (column)
    private BiMap<String, ButtonGroup> buttonGroups;
    // All JRadioButton on the panel, alongside their associated key-value combination
    private BiMap<Entry<String, String>, JRadioButton> radioButtons;

    // Catch-all action listener for all JRadioButtons on the panel
    private ActionListener optionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectionChanged(e);
        }
    };

    public KeyValueSelectionPanel(KeyValueSelectionSet keyValueSet, ValidatorDataSource source) {
        this.keyValueSet = keyValueSet;

        buttonGroups = HashBiMap.create();
        radioButtons = HashBiMap.create();

        this.initValidator(source);
        this.initGrid();
    }

    /*
     * GUI CONSTRUCTION CODE
     */
    private void initGrid() {
        GridBagLayout gridBagLayout = createLayout();
        setLayout(gridBagLayout);

        // For each property
        for (int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++) {
            // Create a label ("column header")
            String property = getText(columnIndex, 0);

            JLabel keyLabel = new JLabel(property);
            add(keyLabel, createConstraints(columnIndex, 0));

            ButtonGroup columnButtonGroup = new ButtonGroup();
            // For each value in this property
            for (int valueIndex = 0; valueIndex < getRowCount(property); valueIndex++) {
                // Create a radio button
                String keyValue = getText(columnIndex, valueIndex + 1);

                JRadioButton keyValueButton = new JRadioButton(keyValue);

                add(keyValueButton, createConstraints(columnIndex, valueIndex + 1));
                columnButtonGroup.add(keyValueButton);
                radioButtons.put(MapUtils.newEntry(property, keyValue), keyValueButton);

                keyValueButton.setActionCommand(NOT_SELECTED);
                keyValueButton.addActionListener(optionListener);
            }

            buttonGroups.put(property, columnButtonGroup);
        }

        statusLabel = new JLabel(DEFAULT_STATUS_TEXT);
        add(statusLabel, createConstraints(0, getMaxRowCount() + 2));
    }

    private GridBagLayout createLayout() {
        GridBagLayout gridBagLayout = new GridBagLayout();

        gridBagLayout.columnWidths = new int[getColumnCount() * 2];

        gridBagLayout.rowHeights = new int[getMaxRowCount() + 3];

        gridBagLayout.columnWeights = new double[gridBagLayout.columnWidths.length];
        for (int columnIndex = 0; columnIndex < gridBagLayout.columnWeights.length; columnIndex += 2) {
            gridBagLayout.columnWeights[columnIndex] = 1.0;
        }
        gridBagLayout.columnWeights[gridBagLayout.columnWeights.length - 1] = Double.MIN_VALUE;

        gridBagLayout.rowWeights = new double[gridBagLayout.rowHeights.length];
        gridBagLayout.rowWeights[gridBagLayout.rowWeights.length - 1] = Double.MIN_VALUE;

        return gridBagLayout;
    }

    private GridBagConstraints createConstraints(int columnIndex, int rowIndex) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.gridx = columnIndex * 2;
        constraints.gridy = rowIndex;
        return constraints;
    }


    private int getColumnCount() {
        return keyValueSet.getKeys().size();
    }

    private int getMaxRowCount() {
        int rowCount = 0;
        for (String key : keyValueSet.getKeys()) {
            rowCount = Math.max(rowCount, getRowCount(key));
        }
        return rowCount;
    }

    private int getRowCount(String key) {
        return keyValueSet.getValues(key).size();
    }

    private String getText(int columnIndex, int rowIndex) {
        String text;

        String property = keyValueSet.getKeys().get(columnIndex);

        boolean columnHeaderRequested = 0 == rowIndex;
        if (columnHeaderRequested) {
            text = property;
        } else {
            text = keyValueSet.getValues(property).get(rowIndex - 1);
        }

        return text;
    }

    /*
     * DISPLAY LOGIC CODE
     */
    private void selectionChanged(ActionEvent e) {
        JRadioButton source = (JRadioButton) e.getSource();
        Entry<String, String> associatedOption = radioButtons.inverse().get(source);

        // Update toggling behaviour information
        if (NOT_SELECTED.equals(source.getActionCommand())) {
            // Mark as selected
            source.setActionCommand(SELECTED);
        } else if (SELECTED.equals(source.getActionCommand())) {
            // Selected and clicked again means deselect
            buttonGroups.get(associatedOption.getKey()).clearSelection();
            source.setActionCommand(NOT_SELECTED);
        }

        // Deselect buttons in same column
        Enumeration<AbstractButton> columnButtons = buttonGroups.get(associatedOption.getKey()).getElements();
        while (columnButtons.hasMoreElements()) {
            AbstractButton button = columnButtons.nextElement();

            boolean buttonsAreNotSame = button != source;
            if (buttonsAreNotSame) {
                button.setActionCommand(NOT_SELECTED);
                button.setSelected(false);
            }
        }

        resetAndColorizeText();

        resetStatusText();

    }

    /**
     * Schedules the reset of status label text according to application logic on non-GUI thread
     *
     * @see CombinationValidator#getValidationText(Map)  getValidationText
     */
    private void resetStatusText() {
        if (SwingUtilities.isEventDispatchThread()) {
            new Thread(this::resetStatusText).start();
        } else {
            String text = getValidatorOrMock().getValidationText(
                    MapUtils.newHashMap(getSelectedCombination()));
            SwingUtilities.invokeLater(() -> statusLabel.setText(text));
        }
    }

    /**
     * Schedules the reset of label colors according to application logic on non-GUI thread
     *
     * @see CombinationValidator#getColorizePredicate(Map)  getColorizePredicate
     */
    @SuppressWarnings("all")
    private void resetAndColorizeText() {
        if (SwingUtilities.isEventDispatchThread()) {
            new Thread(this::resetAndColorizeText).start();
        } else {
            Predicate<Entry<String, String>> predicate = getValidatorOrMock().getColorizePredicate(
                    MapUtils.newHashMap(getSelectedCombination()));

            SwingUtilities.invokeLater(() -> radioButtons.inverse().forEach(
                    (button, entry) -> button.setForeground(getColor(predicate.apply(entry)))));
        }
    }

    private Set<Entry<String, String>> getSelectedCombination() {
        return Sets.newHashSet(
                Maps.filterValues(
                        radioButtons,
                        new Predicate<JRadioButton>() {
                            @Override
                            public boolean apply(JRadioButton input) {
                                return input.isSelected();
                            }
                        }
                ).keySet()
        );
    }

    /**
     * @param colorize whether to return {@link KeyValueSelectionPanel#TEXT_COLOR_COLORIZED} or
     *                 {@link KeyValueSelectionPanel#TEXT_COLOR_DEFAULT}
     * @return appropriate text color for a label
     */
    private Color getColor(boolean colorize) {
        if (colorize) {
            return TEXT_COLOR_COLORIZED;
        }
        return TEXT_COLOR_DEFAULT;
    }


    /**
     * starts the initialization of the {@link ICombinationValidator} to be used by this panel on non-GUI thread
     *
     * @param source an implementation of {@link ValidatorDataSource} to be used
     */
    public void initValidator(ValidatorDataSource source) {
        if (SwingUtilities.isEventDispatchThread()) {
            new Thread(() -> initValidator(source)).start();
        } else {
            this.validator = new CombinationValidator(source.initValidatorData(), keyValueSet);
            resetAndColorizeText();
            resetStatusText();
        }
    }

    /**
     * @return the {@link ICombinationValidator} to be used by this panel. Note that if the validator wasn't initialized
     * yet, this method returns a mock that won't validate or colorize anything
     */
    @SuppressWarnings("all")
    public ICombinationValidator getValidatorOrMock() {
        if (validator == null) {
            return new ICombinationValidator() {

                @Override
                public Predicate<Entry<String, String>> getColorizePredicate(Map<String, String> selection) {
                    return e -> false;
                }

                @Override
                public String getValidationText(Map<String, String> selection) {
                    return DEFAULT_STATUS_TEXT;
                }
            };
        }
        return validator;
    }

}
