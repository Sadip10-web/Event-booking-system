import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventBookingSystemUI {
    private JFrame frame;
    private JTextArea eventListArea;
    private JTextField eventNameField;
    private JTextField eventLocationField;

    public EventBookingSystemUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Event Booking System");
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblAvailableEvents = new JLabel("Available Events:");
        lblAvailableEvents.setBounds(10, 11, 105, 14);
        frame.getContentPane().add(lblAvailableEvents);

        eventListArea = new JTextArea();
        eventListArea.setEditable(false);
        eventListArea.setBounds(10, 36, 414, 118);
        frame.getContentPane().add(eventListArea);

        JButton btnViewEvents = new JButton("View Events");
        btnViewEvents.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayEvents();
            }
        });
        btnViewEvents.setBounds(10, 165, 105, 23);
        frame.getContentPane().add(btnViewEvents);

        JLabel lblEventName = new JLabel("Event Name:");
        lblEventName.setBounds(10, 199, 105, 14);
        frame.getContentPane().add(lblEventName);

        eventNameField = new JTextField();
        eventNameField.setBounds(115, 196, 200, 20);
        frame.getContentPane().add(eventNameField);
        eventNameField.setColumns(10);

        JLabel lblEventLocation = new JLabel("Event Location:");
        lblEventLocation.setBounds(10, 224, 105, 14);
        frame.getContentPane().add(lblEventLocation);

        eventLocationField = new JTextField();
        eventLocationField.setBounds(115, 221, 200, 20);
        frame.getContentPane().add(eventLocationField);
        eventLocationField.setColumns(10);

        JButton btnBookEvent = new JButton("Book Event");
        btnBookEvent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bookEvent();
            }
        });
        btnBookEvent.setBounds(325, 220, 110, 23);
        frame.getContentPane().add(btnBookEvent);
    }

    public void displayEvents() {
        List<Event> events = fetchEventsFromDatabase();
        StringBuilder sb = new StringBuilder();
        for (Event event : events) {
            sb.append(event.toString()).append("\n");
        }
        eventListArea.setText(sb.toString());
    }

    private List<Event> fetchEventsFromDatabase() {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM events";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String location = rs.getString("location");
                Event event = new Event(id, name, location);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    private void bookEvent() {
        String eventName = eventNameField.getText().trim();
        String eventLocation = eventLocationField.getText().trim();
        if (eventName.isEmpty() || eventLocation.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both event name and location.");
            return;
        }

        String insertQuery = "INSERT INTO events (name, location) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, eventName);
            pstmt.setString(2, eventLocation);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Event booked successfully!");
            eventNameField.setText("");
            eventLocationField.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error booking event: " + e.getMessage());
        }
    }

    public void show() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    EventBookingSystemUI window = new EventBookingSystemUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
