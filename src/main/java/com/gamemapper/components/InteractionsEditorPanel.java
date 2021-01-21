package com.gamemapper.components;

import com.gamemapper.data.Interaction;
import com.gamemapper.data.Interactions;
import com.gamemapper.data.Variable;
import com.gamemapper.data.VariablesStorage;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.NumberFormatter;

/**
 * @author Dmitry
 */
public class InteractionsEditorPanel extends JPanel {

    private Interactions interactions;
    private final JTable table;
    private AbstractTableModel tableModel;
    private DefaultCellEditor variablesCellEditor;
    private DefaultCellEditor operationCellEditor;
    private DefaultCellEditor numberCellEditor;
    private final JScrollPane scroll;

    public InteractionsEditorPanel(Interactions interactions) {
        this.interactions = interactions;
        tableModel = createTableModel();
        prepareCellEditors();
        table = new JTable(tableModel) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                switch (column) {
                    case 0:
                        repopulateVariablesComboBox();
                        return variablesCellEditor;
                    case 1:
                        return operationCellEditor;
                    case 2:
                        return numberCellEditor;
                    default:
                        throw new IllegalArgumentException("Do not know what cell editor to return at column " + column);
                }
            }
        };

        setLayout(new BorderLayout());
        scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 3) {
                    int rowAtPoint = table.rowAtPoint(e.getPoint());

                    if (rowAtPoint > -1) {
                        table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                    }

                    showPopupMenu(e.getX(), e.getY(), table.getSelectedRow() != -1);
                }
            }
        });
        scroll.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 3) {
                    showPopupMenu(e.getX(), e.getY() - table.getRowHeight(), table.getSelectedRow() != -1);
                }
            }
        });
        scroll.setPreferredSize(new Dimension(300, 100));
    }

    private void showPopupMenu(int x, int y, boolean rowSelected) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem createNewConditionMenu = new JMenuItem("New");
        createNewConditionMenu.addActionListener((ActionEvent e) -> {
            createNewInteraction();
        });
        JMenuItem deleteConditionMenu = new JMenuItem("Delete");
        deleteConditionMenu.setEnabled(rowSelected);
        deleteConditionMenu.addActionListener((ActionEvent e) -> {
            deleteInteraction();
        });
        menu.add(createNewConditionMenu);
        menu.add(deleteConditionMenu);
        menu.show(table, x, y);
    }

    private void repopulateVariablesComboBox() {
        JComboBox<Variable> variablesCombobox = (JComboBox<Variable>) variablesCellEditor.getComponent();
        variablesCombobox.removeAllItems();
        for (Variable var : VariablesStorage.get().getVariables()) {
            variablesCombobox.addItem(var);
        }
    }

    private void createNewInteraction() {
        if (VariablesStorage.get().getVariables().isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no variables configured. Cannot create any interaction", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Interaction newInteraction = new Interaction(VariablesStorage.get().getVariables().get(0), Interaction.InteractionOperation.ADD, 0);
        interactions.getInteractions().add(newInteraction);
        tableModel.fireTableDataChanged();
    }

    private void deleteInteraction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            table.clearSelection();
            interactions.getInteractions().remove(selectedRow);
            tableModel.fireTableDataChanged();
        }
    }

    private void prepareCellEditors() {
        variablesCellEditor = new DefaultCellEditor(new JComboBox<>());
        repopulateVariablesComboBox();

        JComboBox<Interaction.InteractionOperation> operationsCombobox = new JComboBox<>();
        operationCellEditor = new DefaultCellEditor(operationsCombobox);
        for (Interaction.InteractionOperation operation : Interaction.InteractionOperation.values()) {
            operationsCombobox.addItem(operation);
        }

        NumberFormat longFormat = NumberFormat.getIntegerInstance();
        NumberFormatter numberFormatter = new NumberFormatter(longFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setMinimum(Integer.MIN_VALUE);
        numberFormatter.setMaximum(Integer.MAX_VALUE);
        JFormattedTextField numberTextEditor = new JFormattedTextField(longFormat);
        numberCellEditor = new DefaultCellEditor(numberTextEditor);
    }

    private AbstractTableModel createTableModel() {
        return new AbstractTableModel() {
            final String[] columnsNames = new String[]{"Variable", "Operation", "Value"};

            @Override
            public int getRowCount() {
                return interactions.getInteractions().size();
            }

            @Override
            public int getColumnCount() {
                return columnsNames.length;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }

            @Override
            public String getColumnName(int column) {
                return columnsNames[column];
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Interaction condition = interactions.getInteractions().get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return condition.getVariable();
                    case 1:
                        return condition.getOperation();
                    case 2:
                        return condition.getValue();
                }
                throw new IllegalArgumentException("Cannot show table value for row " + rowIndex + " column " + columnIndex);
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                Interaction interaction = interactions.getInteractions().get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        interaction.setVariable((Variable) aValue);
                        return;
                    case 1:
                        interaction.setOperation((Interaction.InteractionOperation) aValue);
                        return;
                    case 2:
                        try {
                        interaction.setValue(Integer.parseInt(aValue.toString()));
                    } catch (NumberFormatException ex) {
                        //silently ignore
                    }
                    return;
                }
                throw new IllegalArgumentException("Cannot set table value for row " + rowIndex + " column " + columnIndex);
            }
        };
    }

    public Interactions getInteractions() {
        return interactions;
    }
}
