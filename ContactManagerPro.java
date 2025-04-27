import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

// Contact class
class Contact implements Comparable<Contact> {
    String firstName, lastName, phone, email, company, jobTitle, address, birthday, notes;

    public Contact(String firstName, String lastName, String phone, String email,
                   String company, String jobTitle, String address, String birthday, String notes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.jobTitle = jobTitle;
        this.address = address;
        this.birthday = birthday;
        this.notes = notes;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + "\t" + phone + "\t" + email + "\t" + company +
                "\t" + jobTitle + "\t" + address + "\t" + birthday + "\t" + notes;
    }

    @Override
    public int compareTo(Contact o) {
        return this.lastName.compareToIgnoreCase(o.lastName);
    }
}

// Custom singly linked list
class SimpleLinkedList<T> {
    private class Node {
        T data;
        Node next;
        Node(T data) { this.data = data; }
    }

    private Node head;
    private int size = 0;

    public void add(T data) {
        Node newNode = new Node(data);
        if (head == null) head = newNode;
        else {
            Node temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
        }
        size++;
    }

    public T get(int index) {
        Node current = head;
        for (int i = 0; i < index && current != null; i++) current = current.next;
        return current != null ? current.data : null;
    }

    public int size() { return size; }

    public void clear() {
        head = null;
        size = 0;
    }

    public T[] toArray(T[] a) {
        int i = 0;
        Node current = head;
        while (current != null && i < a.length) {
            a[i++] = current.data;
            current = current.next;
        }
        return a;
    }
}

// Binary Search Tree for Contact search by last name
class ContactBST {
    private class Node {
        Contact contact;
        Node left, right;
        Node(Contact contact) { this.contact = contact; }
    }

    private Node root;

    public void insert(Contact c) {
        root = insertRec(root, c);
    }

    private Node insertRec(Node root, Contact c) {
        if (root == null) return new Node(c);
        if (c.compareTo(root.contact) < 0)
            root.left = insertRec(root.left, c);
        else
            root.right = insertRec(root.right, c);
        return root;
    }

    public Contact search(String lastName) {
        return searchRec(root, lastName);
    }

    private Contact searchRec(Node root, String lastName) {
        if (root == null) return null;
        int cmp = lastName.compareToIgnoreCase(root.contact.lastName);
        if (cmp == 0) return root.contact;
        return cmp < 0 ? searchRec(root.left, lastName) : searchRec(root.right, lastName);
    }
}

// File handler
class FileHandler {
    public static void writeToFile(String filename, SimpleLinkedList<Contact> contacts) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write("First Name\tLast Name\tPhone\tEmail\tCompany\tJob Title\tAddress\tBirthday\tNotes\n");
        for (int i = 0; i < contacts.size(); i++) {
            writer.write(contacts.get(i).toString());
            writer.newLine();
        }
        writer.close();
    }

    public static SimpleLinkedList<Contact> readFromFile(String filename) throws IOException {
        SimpleLinkedList<Contact> contacts = new SimpleLinkedList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        reader.readLine(); // skip header
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split("\t");
            if (fields.length == 9) {
                contacts.add(new Contact(fields[0], fields[1], fields[2], fields[3], fields[4],
                        fields[5], fields[6], fields[7], fields[8]));
            }
        }
        reader.close();
        return contacts;
    }
}

// Welcome screen
class WelcomeScreen extends JFrame {
    public WelcomeScreen() {
        setTitle("Welcome");
        setSize(420, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome to Contact Manager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        ImageIcon icon = new ImageIcon("contact-book-icon.png");
        Image image = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        add(imagePanel, BorderLayout.CENTER);

        JButton openButton = new JButton("Get Started");
        openButton.setFont(new Font("Arial", Font.PLAIN, 16));
        openButton.setPreferredSize(new Dimension(100, 40));
        openButton.addActionListener(e -> {
            dispose();
            new ContactManagerPro().setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}

// Main App
public class ContactManagerPro extends JFrame {
    private JTextField firstNameField, lastNameField, phoneField, emailField, companyField, jobTitleField,
            addressField, birthdayField, notesField;
    private JTextArea textArea;
    private JButton addButton, saveButton, loadButton, sortButton;
    private SimpleLinkedList<Contact> contacts;
    private Stack<Contact> contactHistory;
    private ContactBST contactTree;
    private HashMap<String, Contact> contactMap;
    private static final String FILE_NAME = "contacts.txt";

    public ContactManagerPro() {
        setTitle("Contact Manager Pro");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Image icon = Toolkit.getDefaultToolkit().getImage("contact-book-icon.png");
        setIconImage(icon);

        JPanel inputPanel = new JPanel(new GridLayout(9, 2, 5, 5));
        firstNameField = new JTextField(); lastNameField = new JTextField(); phoneField = new JTextField();
        emailField = new JTextField(); companyField = new JTextField(); jobTitleField = new JTextField();
        addressField = new JTextField(); birthdayField = new JTextField(); notesField = new JTextField();

        inputPanel.add(new JLabel("First Name:")); inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Last Name:")); inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("Phone:")); inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Email:")); inputPanel.add(emailField);
        inputPanel.add(new JLabel("Company:")); inputPanel.add(companyField);
        inputPanel.add(new JLabel("Job Title:")); inputPanel.add(jobTitleField);
        inputPanel.add(new JLabel("Address:")); inputPanel.add(addressField);
        inputPanel.add(new JLabel("Birthday:")); inputPanel.add(birthdayField);
        inputPanel.add(new JLabel("Notes:")); inputPanel.add(notesField);
        add(inputPanel, BorderLayout.NORTH);

        textArea = new JTextArea();
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        addButton = new JButton("Add Contact");
        saveButton = new JButton("Save Contacts");
        loadButton = new JButton("Load Contacts");
        sortButton = new JButton("Sort by Last Name");
        JButton searchNameButton = new JButton("Search by Last Name");
        JButton searchPhoneButton = new JButton("Search by Phone");

        panel.add(addButton); panel.add(saveButton); panel.add(loadButton); panel.add(sortButton);
        panel.add(searchNameButton); panel.add(searchPhoneButton);
        add(panel, BorderLayout.SOUTH);

        contacts = new SimpleLinkedList<>();
        contactHistory = new Stack<>();
        contactTree = new ContactBST();
        contactMap = new HashMap<>();

        loadSampleContacts();

        addButton.addActionListener(e -> addContact());
        saveButton.addActionListener(e -> saveContacts());
        loadButton.addActionListener(e -> loadContacts());
        sortButton.addActionListener(e -> sortContacts());

        searchNameButton.addActionListener(e -> {
            String lastName = JOptionPane.showInputDialog("Enter last name:");
            Contact found = contactTree.search(lastName);
            JOptionPane.showMessageDialog(this, found != null ? found.toString() : "Not found");
        });

        searchPhoneButton.addActionListener(e -> {
            String phone = JOptionPane.showInputDialog("Enter phone number:");
            Contact found = contactMap.get(phone);
            JOptionPane.showMessageDialog(this, found != null ? found.toString() : "Not found");
        });
    }

    private void loadSampleContacts() {
        Contact[] samples = new Contact[] {
            new Contact("John", "Smith", "555-123-4567", "john.smith@email.com", "TechCorp", "Engineer", "123 Main St, NY", "01/15/1990", "Loves biking"),
            new Contact("Emily", "Davis", "555-234-5678", "emily.davis@email.com", "FinBank", "Accountant", "456 Oak St, LA", "02/20/1985", "Allergic to peanuts"),
            new Contact("Michael", "Jordan", "555-345-6789", "michael.jordan@email.com", "Sportify", "Coach", "789 Pine St, CHI", "03/10/1975", "Basketball fan"),
            new Contact("Sarah", "Lee", "555-456-7890", "sarah.lee@email.com", "MediaHub", "Writer", "321 Elm St, SF", "04/25/1993", "Published author"),
            new Contact("David", "Kim", "555-567-8901", "david.kim@email.com", "HealthNet", "Doctor", "654 Cedar St, HOU", "05/12/1980", "Vegan"),
            new Contact("Jessica", "Perez", "555-678-9012", "jessica.perez@email.com", "AutoZone", "Manager", "987 Birch St, SEA", "06/30/1988", "Loves cars"),
            new Contact("Robert", "White", "555-789-0123", "robert.white@email.com", "BuildIt", "Architect", "159 Walnut St, BOS", "07/22/1970", "Enjoys travel"),
            new Contact("Laura", "Martins", "555-890-1234", "laura.martins@email.com", "DesignPro", "Artist", "753 Spruce St, PHX", "08/14/1995", "Sketches in free time"),
            new Contact("Daniel", "Nguyen", "555-901-2345", "daniel.nguyen@email.com", "Foodies", "Chef", "852 Maple St, MIA", "09/05/1982", "Makes sushi"),
            new Contact("Olivia", "Taylor", "555-012-3456", "olivia.taylor@email.com", "SchoolFirst", "Teacher", "951 Aspen St, DEN", "10/11/1999", "Teaches math")
        };

        for (Contact c : samples) {
            contacts.add(c);
            contactTree.insert(c);
            contactMap.put(c.phone, c);
        }
        updateTextArea();
    }

    private void addContact() {
        Contact contact = new Contact(
                firstNameField.getText(), lastNameField.getText(), phoneField.getText(),
                emailField.getText(), companyField.getText(), jobTitleField.getText(),
                addressField.getText(), birthdayField.getText(), notesField.getText()
        );
        contacts.add(contact);
        contactHistory.push(contact);
        contactTree.insert(contact);
        contactMap.put(contact.phone, contact);
        updateTextArea();
        clearFields();
    }

    private void saveContacts() {
        try {
            FileHandler.writeToFile(FILE_NAME, contacts);
            JOptionPane.showMessageDialog(this, "Contacts saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving contacts.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadContacts() {
        try {
            contacts = FileHandler.readFromFile(FILE_NAME);
            for (int i = 0; i < contacts.size(); i++) {
                Contact c = contacts.get(i);
                contactTree.insert(c);
                contactMap.put(c.phone, c);
            }
            updateTextArea();
            JOptionPane.showMessageDialog(this, "Contacts loaded successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading contacts.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sortContacts() {
        Contact[] array = contacts.toArray(new Contact[contacts.size()]);
        Arrays.sort(array);
        contacts.clear();
        for (Contact c : array) contacts.add(c);
        updateTextArea();
        JOptionPane.showMessageDialog(this, "Contacts sorted successfully!");
    }

    private void updateTextArea() {
        textArea.setText("First Name\tLast Name\tPhone\tEmail\tCompany\tJob Title\tAddress\tBirthday\tNotes\n");
        for (int i = 0; i < contacts.size(); i++) {
            textArea.append(contacts.get(i).toString() + "\n");
        }
    }

    private void clearFields() {
        firstNameField.setText(""); lastNameField.setText(""); phoneField.setText(""); emailField.setText("");
        companyField.setText(""); jobTitleField.setText(""); addressField.setText(""); birthdayField.setText("");
        notesField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeScreen().setVisible(true));
    }
}