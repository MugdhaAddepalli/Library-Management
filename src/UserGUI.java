import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class UserGUI extends JFrame {

    JTextField idField, nameField, emailField, phoneField;
    DefaultTableModel model;

    public UserGUI() {

        setTitle("User Registration");
        setSize(700, 400);
        setLayout(new BorderLayout(10,10));

        // ===== TOP PANEL =====
        JPanel top = new JPanel(new GridLayout(2,4,10,10));

        idField = new JTextField();
        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();

        top.add(new JLabel("User ID"));
        top.add(new JLabel("Name"));
        top.add(new JLabel("Email"));
        top.add(new JLabel("Phone"));

        top.add(idField);
        top.add(nameField);
        top.add(emailField);
        top.add(phoneField);

        // ===== BUTTONS =====
        JPanel btnPanel = new JPanel();

        JButton addBtn = new JButton("Register User");
        JButton deleteBtn = new JButton("Delete User");

        btnPanel.add(addBtn);
        btnPanel.add(deleteBtn);

        // ===== TABLE =====
        String[] cols = {"User ID", "Name", "Email", "Phone"};

        model = new DefaultTableModel(cols,0);

        JTable table = new JTable(model);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        addBtn.addActionListener(e -> addUser());
        deleteBtn.addActionListener(e -> deleteUser());

        loadUsers();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    // ===== ADD USER =====
    void addUser() {

        try(Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users VALUES (?, ?, ?, ?)"
            );

            ps.setInt(1, Integer.parseInt(idField.getText()));
            ps.setString(2, nameField.getText());
            ps.setString(3, emailField.getText());
            ps.setString(4, phoneField.getText());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "User Registered!");

            loadUsers();

        } catch(Exception e) {

            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ===== DELETE USER =====
    void deleteUser() {

        try(Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "DELETE FROM users WHERE user_id=?"
            );

            ps.setInt(1, Integer.parseInt(idField.getText()));

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "User Deleted!");

            loadUsers();

        } catch(Exception e) {

            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ===== LOAD USERS =====
    void loadUsers() {

        try(Connection con = DBConnection.getConnection()) {

            model.setRowCount(0);

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users"
            );

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                model.addRow(new Object[] {
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone")
                });
            }

        } catch(Exception e) {

            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public static void main(String[] args) {

        new UserGUI();
    }
}
