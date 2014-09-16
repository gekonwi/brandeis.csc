import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class MainView extends JFrame {

	public interface MainViewController {
		public void generateRound2BtnPressed();
	}

	private final MainViewController controller;

	private JTextArea round1TextArea;
	private JTextArea round2TextArea;

	public MainView(MainViewController c) {
		this.controller = c;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setTitle("Shlomo's Map Drawing Group Assigner");
		setBounds(100, 100, 629, 525);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 1.0,
				Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JPanel round1Panel = new JPanel();
		round1Panel.setBorder(new TitledBorder(null, "Round 1",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_round1Panel = new GridBagConstraints();
		gbc_round1Panel.insets = new Insets(0, 0, 5, 0);
		gbc_round1Panel.fill = GridBagConstraints.BOTH;
		gbc_round1Panel.gridx = 0;
		gbc_round1Panel.gridy = 0;
		getContentPane().add(round1Panel, gbc_round1Panel);
		round1Panel.setLayout(new BorderLayout(0, 0));

		JLabel lblPasteTheWhole = new JLabel(
				"Paste the whole Google Spreadsheet Round 1 here and press Generate Round 2");
		round1Panel.add(lblPasteTheWhole, BorderLayout.NORTH);

		JScrollPane round1ScrollPane = new JScrollPane();
		round1Panel.add(round1ScrollPane, BorderLayout.CENTER);

		round1TextArea = new JTextArea();
		round1ScrollPane.setViewportView(round1TextArea);

		JButton btnGenerateRound = new JButton("Generate Round 2");
		btnGenerateRound.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.generateRound2BtnPressed();
			}
		});
		GridBagConstraints gbc_btnGenerateRound = new GridBagConstraints();
		gbc_btnGenerateRound.insets = new Insets(0, 0, 5, 0);
		gbc_btnGenerateRound.gridx = 0;
		gbc_btnGenerateRound.gridy = 1;
		getContentPane().add(btnGenerateRound, gbc_btnGenerateRound);

		JPanel round2Panel = new JPanel();
		round2Panel.setBorder(new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED, null, null), "Round 2",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_round2Panel = new GridBagConstraints();
		gbc_round2Panel.fill = GridBagConstraints.BOTH;
		gbc_round2Panel.gridx = 0;
		gbc_round2Panel.gridy = 2;
		getContentPane().add(round2Panel, gbc_round2Panel);
		round2Panel.setLayout(new BorderLayout(0, 0));

		JLabel lblCopyThisInto = new JLabel(
				"Copy this into the Google Spreadsheet");
		round2Panel.add(lblCopyThisInto, BorderLayout.NORTH);

		JScrollPane round2ScrollPane = new JScrollPane();
		round2Panel.add(round2ScrollPane, BorderLayout.CENTER);

		round2TextArea = new JTextArea();
		round2ScrollPane.setViewportView(round2TextArea);
		round2TextArea.setEditable(false);
	}

	public String getRound1Text() {
		return round1TextArea.getText();
	}

	public void setRound2Text(String text) {
		round2TextArea.setText(text);
	}
}
