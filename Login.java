import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;

public class Login {
	private static ArrayList<User> db = new ArrayList<User>();

	@SuppressWarnings("serial")
	static class GUI extends JFrame {
		private static JButton loginBtn = new JButton("Login");
		private static JTextField usernameField = new JTextField("Username");
		private static JPasswordField passwordField = new JPasswordField("Password");

		public GUI() {
			// Configure JFrame
			setTitle("Login Prompt Screen");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setSize(700, 260);
			setLocationRelativeTo(null);
			setLayout(null);
			setResizable(false);

			// Build components
			loginBtn.setBounds(260, 170, 160, 30);
			loginBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
			loginBtn.setFocusPainted(false);

			usernameField.setBounds(130, 30, 420, 55);
			usernameField.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
			usernameField.setForeground(Color.GRAY);

			passwordField.setBounds(130, 100, 420, 55);
			passwordField.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
			passwordField.setForeground(Color.GRAY);
			passwordField.setEchoChar('\0');

			// Add components to content pane.
			add(loginBtn);
			add(usernameField);
			add(passwordField);

			// Attach Listeners
			loginBtn.addActionListener(new ActionListener() {
				class PopUp extends JFrame {				
					public PopUp(String title, String text) {
						// Configure JFrame
						setTitle(title);
						setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
						setLocationRelativeTo(null);
						setBounds(getX() - 150, getY() - 150, 300, 200);
						setLayout(null);
						setResizable(false);
						setAlwaysOnTop(true);

						// Build components
						JLabel textField = new JLabel("<html>" + text + "</html>"); // html tags auto wrap the text
						textField.setBounds(65, -30, 200, 200);
						textField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

						// Add components to content pane.
						add(textField);

						// Pack and display the window.
						setVisible(true);

						// Attach Listeners
						addWindowListener(new WindowAdapter() {
							@Override
			                public void windowClosing(WindowEvent e) {
								usernameField.setEditable(true);
								usernameField.setFocusable(true);
								passwordField.setEditable(true);
								passwordField.setFocusable(true);
								loginBtn.setEnabled(true);
			                }
			            });
					}
				}
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String username = usernameField.getText();
					String password = new String(passwordField.getPassword());
					String status = "";
					
					usernameField.setEditable(false);
					usernameField.setFocusable(false);
					passwordField.setEditable(false);
					passwordField.setFocusable(false);
					loginBtn.setEnabled(false);
					
					if (isUsernamePlaceholder() && isPasswordPlaceholder()) {
		            	new PopUp("Invalid Login", "You must enter a username and a password");
		            	username = "";
		            	password = "";
		            	status = "Invalid Username & Password";
		            }	else if (isUsernamePlaceholder()) {
		            	new PopUp("Invalid Username", "You must enter a username");
		            	username = "";
		            	status = "Invalid Username";
		            } 	else if (isPasswordPlaceholder() ) {
		            	new PopUp("Invalid Password", "You must enter a password");
		            	password = "";
		            	status = "Invalid Password";
		            }	else if (isValidLogin(new User(username, password))) {
		            	new PopUp("Successfull Login", "Successfully Logged In");
		            	usernameField.setText("");
		            	passwordField.setText("");
		            	status = "Successfull Login";
		            }	else {
		            	new PopUp("Unsuccessfull Login", "Invalid Login Credentials");
		            	status = "Invalid Login Credentials";
		            }
		            
		            System.out.println(String.format("User(\"%s\", \"%s\") --> %s", username, password, status));
		         }
			});

			usernameField.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					if (isUsernamePlaceholder()) {
						usernameField.setText("");
						usernameField.setForeground(Color.BLACK);
					}
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (usernameField.getText().isEmpty()) {
						usernameField.setText("Username");
						usernameField.setForeground(Color.GRAY);
					}
				}
			});
			
			passwordField.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					if (isPasswordPlaceholder()) {
						passwordField.setText("");
						passwordField.setForeground(Color.BLACK);
						passwordField.setEchoChar('•');
					}
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (String.valueOf(passwordField.getPassword()).isEmpty()) {
						passwordField.setText("Password");
						passwordField.setForeground(Color.GRAY);
						passwordField.setEchoChar('\0');
					}
				}
				
			});

			// Pack and display the window.
			// pack(); Automatically sizes to components on content pane.
			setVisible(true);

			// Set Focus to JFrame, allowing for usernameField and passwordField Focus EventListeners to function as intended
			while(!hasFocus()) {
				requestFocus();
			}
		}

		private boolean isUsernamePlaceholder() {
			if (usernameField.getText().equals("Username") && usernameField.getForeground() == Color.GRAY) {
				return true;
			}
			
			return false;
		}

		private boolean isPasswordPlaceholder() {
			if (String.valueOf(passwordField.getPassword()).equals("Password") && passwordField.getForeground() == Color.GRAY) {
				return true;
			}
			
			return false;
		}
	}
	
	public static void main(String[] args) {
		db.add(new User("security", "sec"));
		db.add(new User("engineer", "eng"));
		db.add(new User("scientist", "sci"));
		
		write();
		
		new GUI();
	}
	
	public static void write() {
		FileWriter outf;
		
		try {
			outf = new FileWriter("output.txt");
			outf.write("Username\t\tPassword\n");
			
			for (User user: db) {
				outf.write(user.getUsername() + "\t\t" + user.getPassword() + "\n");
			}
			
			outf.close();
		} 	catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean isValidLogin(User login) {
		if (login.getUsername() == "" || login.getPassword() == "") {
			return false;
		}
		
		for (User user: db) {
			if (user.getUsername().equals(login.getUsername()) && user.getPassword().equals(login.getPassword())) {
				return true;
			}
		}
		
		return false;
	}
}