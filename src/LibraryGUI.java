import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LibraryGUI extends JFrame {

    JTextField idField, titleField, authorField, userField, searchField;
    DefaultTableModel model;

    public LibraryGUI() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        setTitle("Library Management System");
        setSize(800, 500);
        setLayout(new BorderLayout(10, 10));

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new GridLayout(2, 4, 10, 10));

        idField = new JTextField();
        titleField = new JTextField();
        authorField = new JTextField();
        userField = new JTextField();

        topPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));

        topPanel.add(new JLabel("Book ID"));
        topPanel.add(new JLabel("Title"));
        topPanel.add(new JLabel("Author"));
        topPanel.add(new JLabel("User ID"));

        topPanel.add(idField);
        topPanel.add(titleField);
        topPanel.add(authorField);
        topPanel.add(userField);

        // ===== BUTTON PANEL =====
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton issueBtn = new JButton("Issue");
        JButton returnBtn = new JButton("Return");
        JButton searchBtn = new JButton("Search");

        searchField = new JTextField();

        panel.setBorder(BorderFactory.createTitledBorder("Actions"));

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(issueBtn);

        panel.add(returnBtn);
        panel.add(searchField);
        panel.add(searchBtn);

        // ===== TABLE =====
        String[] cols = {"ID", "Title", "Author", "Available", "Issued To"};
        model = new DefaultTableModel(cols, 0);

        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        addBtn.addActionListener(e -> addBook());
        updateBtn.addActionListener(e -> updateBook());
        deleteBtn.addActionListener(e -> deleteBook());
        searchBtn.addActionListener(e -> searchBook());
        issueBtn.addActionListener(e -> issueBook());
        returnBtn.addActionListener(e -> returnBook());

        searchBook();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // ===== ADD =====
    void addBook() {
        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO books VALUES (?, ?, ?, true)"
            );

            ps.setInt(1, Integer.parseInt(idField.getText()));
            ps.setString(2, titleField.getText());
            ps.setString(3, authorField.getText());

            ps.executeUpdate();
            searchBook();

            JOptionPane.showMessageDialog(this, "Book Added!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ===== UPDATE =====
    void updateBook() {
        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "UPDATE books SET title=?, author=? WHERE id=?"
            );

            ps.setString(1, titleField.getText());
            ps.setString(2, authorField.getText());
            ps.setInt(3, Integer.parseInt(idField.getText()));

            ps.executeUpdate();
            searchBook();

            JOptionPane.showMessageDialog(this, "Updated!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ===== DELETE =====
    void deleteBook() {
        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "DELETE FROM books WHERE id=?"
            );

            ps.setInt(1, Integer.parseInt(idField.getText()));
            ps.executeUpdate();

            searchBook();
            JOptionPane.showMessageDialog(this, "Deleted!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ===== SEARCH (CLEAN FIX) =====
    void searchBook() {
        try (Connection con = DBConnection.getConnection()) {

            model.setRowCount(0);

            String text = searchField.getText().trim();

            PreparedStatement ps;

            if (text.isEmpty()) {
                ps = con.prepareStatement(
                    "SELECT b.id, b.title, b.author, b.available, t.user_id " +
                    "FROM books b LEFT JOIN transactions t " +
                    "ON b.id = t.book_id AND t.return_date IS NULL"
                );
            } else {
                ps = con.prepareStatement(
                    "SELECT b.id, b.title, b.author, b.available, t.user_id " +
                    "FROM books b LEFT JOIN transactions t " +
                    "ON b.id = t.book_id AND t.return_date IS NULL " +
                    "WHERE b.id = ? OR b.title LIKE ? OR b.author LIKE ?"
                );

                int id = -1;
                try { id = Integer.parseInt(text); } catch (Exception ignored) {}

                ps.setInt(1, id);
                String key = "%" + text + "%";
                ps.setString(2, key);
                ps.setString(3, key);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Integer uid = rs.getObject("user_id") != null ? rs.getInt("user_id") : null;

                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBoolean("available"),
                    uid == null ? "Available" : "User " + uid
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error!");
        }
    }

    // ===== ISSUE =====
    void issueBook() {
        try (Connection con = DBConnection.getConnection()) {

            int bookId = Integer.parseInt(idField.getText());
            int userId = Integer.parseInt(userField.getText());

            PreparedStatement check = con.prepareStatement(
                "SELECT available FROM books WHERE id=?"
            );
            check.setInt(1, bookId);
            ResultSet rs = check.executeQuery();

            if (rs.next() && rs.getBoolean("available")) {

                LocalDate today = LocalDate.now();

                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO transactions (book_id, user_id, issue_date, due_date) VALUES (?, ?, ?, ?)"
                );

                ps.setInt(1, bookId);
                ps.setInt(2, userId);
                ps.setDate(3, Date.valueOf(today));
                ps.setDate(4, Date.valueOf(today.plusDays(7)));

                ps.executeUpdate();

                PreparedStatement upd = con.prepareStatement(
                    "UPDATE books SET available=false WHERE id=?"
                );
                upd.setInt(1, bookId);
                upd.executeUpdate();

                searchBook();
                JOptionPane.showMessageDialog(this, "Issued!");

            } else {
                JOptionPane.showMessageDialog(this, "Not available!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Issue failed!");
        }
    }

    // ===== RETURN =====
    void returnBook() {
        try (Connection con = DBConnection.getConnection()) {

            int bookId = Integer.parseInt(idField.getText());

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM transactions WHERE book_id=? AND return_date IS NULL"
            );
            ps.setInt(1, bookId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                LocalDate due = rs.getDate("due_date").toLocalDate();
                LocalDate today = LocalDate.now();

                long fine = today.isAfter(due)
                        ? ChronoUnit.DAYS.between(due, today) * 10
                        : 0;

                PreparedStatement upd = con.prepareStatement(
                    "UPDATE transactions SET return_date=? WHERE id=?"
                );
                upd.setDate(1, Date.valueOf(today));
                upd.setInt(2, rs.getInt("id"));
                upd.executeUpdate();

                PreparedStatement bUpd = con.prepareStatement(
                    "UPDATE books SET available=true WHERE id=?"
                );
                bUpd.setInt(1, bookId);
                bUpd.executeUpdate();

                searchBook();
                JOptionPane.showMessageDialog(this, "Returned! Fine: ₹" + fine);

            } else {
                JOptionPane.showMessageDialog(this, "No record found!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Return failed!");
        }
    }

    public static void main(String[] args) {
        new LibraryGUI();
    }
}