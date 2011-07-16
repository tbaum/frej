import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


import net.java.frej.*;


public class FrejDemo extends JApplet implements ActionListener {

    
    private JButton btnTry;
    private JTextField txtRegex;
    private JTextField txtInput;
    private JLabel txtAnswer;
    
    
    public FrejDemo() {
        super();

        txtRegex = new JTextField(30);
        txtInput = new JTextField(30);
        btnTry = new JButton("Try!");
        txtAnswer = new JLabel("Answer");

        getContentPane().setLayout(new FlowLayout());

        getContentPane().add(txtRegex);
        getContentPane().add(txtInput);
        getContentPane().add(btnTry);
        getContentPane().add(txtAnswer);
        btnTry.addActionListener(this);

    } // Applet


    public void actionPerformed(ActionEvent evt) {
        Regex r;
        
        try {
            r = new Regex(txtRegex.getText());
            if (r.match(txtInput.getText())) {
                txtAnswer.setText(r.getReplacement());
            } else {
                txtAnswer.setText("// Not matched!");
            } // else
        } catch (Exception e) {
            txtAnswer.setText("// Exception!");
            return;
        } // catch

    } // actionPerformed


} // Applet
