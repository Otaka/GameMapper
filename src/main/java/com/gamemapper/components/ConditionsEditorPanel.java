package com.gamemapper.components;

import com.gamemapper.data.Condition;
import com.gamemapper.data.Condition.ConditionOperation;
import com.gamemapper.data.Conditions;
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
public class ConditionsEditorPanel extends JPanel {

    private Conditions conditions;
    private final JTable table;
    private AbstractTableModel tableModel;
    private DefaultCellEditor variablesCellEditor;
    private DefaultCellEditor operationTypeCellEditor;
    private DefaultCellEditor numberCellEditor;
    private final JScrollPane scroll;

    public ConditionsEditorPanel(Conditions conditions) {
        this.conditions = conditions;
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
                        return operationTypeCellEditor;
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
            createNewCondition();
        });
        JMenuItem deleteConditionMenu = new JMenuItem("Delete");
        deleteConditionMenu.setEnabled(rowSelected);
        deleteConditionMenu.addActionListener((ActionEvent e) -> {
            deleteCondition();
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

    private void createNewCondition() {
        if (VariablesStorage.get().getVariables().isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no variables configured. Cannot create any condition", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Condition newCondition = new Condition(VariablesStorage.get().getVariables().get(0), ConditionOperation.EQUAL, 0);
        conditions.getConditions().add(newCondition);
        tableModel.fireTableDataChanged();
    }

    private void deleteCondition() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            table.clearSelection();
            conditions.getConditions().remove(selectedRow);
            tableModel.fireTableDataChanged();
        }
    }

    private void prepareCellEditors() {
        variablesCellEditor = new DefaultCellEditor(new JComboBox<>());
        repopulateVariablesComboBox();

        JComboBox<ConditionOperation> operationsCombobox = new JComboBox<>();
        operationTypeCellEditor = new DefaultCellEditor(operationsCombobox);
        for (ConditionOperation operation : ConditionOperation.values()) {
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
                return conditions.getConditions().size();
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
                Condition condition = conditions.getConditions().get(rowIndex);
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
                Condition condition = conditions.getConditions().get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        condition.setVariable((Variable) aValue);
                        return;
                    case 1:
                        condition.setOperation((ConditionOperation) aValue);
                        return;
                    case 2:
                        try {
                        condition.setValue(Integer.parseInt(aValue.toString()));
                    } catch (NumberFormatException ex) {
                        //silently ignore
                    }
                    return;
                }
                throw new IllegalArgumentException("Cannot set table value for row " + rowIndex + " column " + columnIndex);
            }
        };
    }

    public Conditions getConditions() {
        return conditions;
    }
}
