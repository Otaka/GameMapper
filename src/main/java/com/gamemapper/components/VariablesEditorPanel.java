package com.gamemapper.components;

import com.gamemapper.data.Variable;
import com.gamemapper.data.VariablesStorage;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.NumberFormatter;

/**
 * @author Dmitry
 */
public class VariablesEditorPanel extends JPanel {

    private VariablesStorage variablesStorage;
    private final JTable table;
    private AbstractTableModel tableModel;
    private DefaultCellEditor variableNameCellEditor;
    private DefaultCellEditor numberCellEditor;
    private final JScrollPane scroll;

    public VariablesEditorPanel(VariablesStorage variablesStorage) {
        this.variablesStorage = variablesStorage;
        tableModel = createTableModel();
        prepareCellEditors();
        table = new JTable(tableModel) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                switch (column) {
                    case 0:
                        return variableNameCellEditor;
                    case 1:
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
    }

    private void showPopupMenu(int x, int y, boolean rowSelected) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem createNewConditionMenu = new JMenuItem("New");
        createNewConditionMenu.addActionListener((ActionEvent e) -> {
            createNewVariable();
        });
        JMenuItem deleteConditionMenu = new JMenuItem("Delete");
        deleteConditionMenu.setEnabled(rowSelected);
        deleteConditionMenu.addActionListener((ActionEvent e) -> {
            deleteVariable();
        });
        menu.add(createNewConditionMenu);
        menu.add(deleteConditionMenu);
        menu.show(table, x, y);
    }

    private void createNewVariable() {
        Variable newVariable = new Variable(variablesStorage.chooseNewVariableName(), 0);
        variablesStorage.getVariables().add(newVariable);
        tableModel.fireTableDataChanged();
    }

    private void deleteVariable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            table.clearSelection();
            variablesStorage.getVariables().remove(selectedRow);
            tableModel.fireTableDataChanged();
        }
    }

    private void prepareCellEditors() {
        variableNameCellEditor = new DefaultCellEditor(new JTextField());
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
            final String[] columnsNames = new String[]{"Variable", "Value"};

            @Override
            public int getRowCount() {
                return variablesStorage.getVariables().size();
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
                Variable variable = variablesStorage.getVariables().get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return variable.getName();
                    case 1:
                        return variable.getValue();
                }
                throw new IllegalArgumentException("Cannot show table value for row " + rowIndex + " column " + columnIndex);
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                Variable variable = variablesStorage.getVariables().get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        variable.setName((String) aValue);
                        return;
                    case 1:
                        try {
                        variable.setValue(Integer.parseInt(aValue.toString()));
                    } catch (NumberFormatException ex) {
                        //silently ignore
                    }
                    return;
                }
                throw new IllegalArgumentException("Cannot set table value for row " + rowIndex + " column " + columnIndex);
            }
        };
    }
}
