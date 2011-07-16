import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.List;


import net.java.frej.*;


public class FrejDemo extends JApplet implements ActionListener {

    
    private JButton btnExact, btnStart, btnSubstr;
    private JTextArea txtRegex;
    private JTextArea txtInput;
    private JLabel labelAnswer;
    private List<Pos> poses = new java.util.LinkedList<Pos>();
    private Border border = new LineBorder(Color.BLACK);

    private static class Pos {
        public Component c;
        public float x, y, w, h;
        public Pos(Component c, double x, double y, double w, double h) {
            this.c = c;
            this.x = (float) x; this.y = (float) y;
            this.w = (float) w; this.h = (float) h;
        } // Pos
    } // class Pos
    
    
    public FrejDemo() {
        super();

        getContentPane().setLayout(null);

        addComponentListener(new ComponentAdapter(){
            @Override    
            public void componentResized(ComponentEvent e) { FrejDemo.this.componentResized(); }
        }); // addComponentListener
        
        poses.add(new Pos(new JLabel("Pattern:"), 0.1, 0.05, 0.8, 0.1));
        poses.add(new Pos(txtRegex = new JTextArea(), 0.1, 0.15, 0.8, 0.2));
        poses.add(new Pos(new JLabel("Input text:"), 0.1, 0.35, 0.8, 0.1));
        poses.add(new Pos(txtInput = new JTextArea(), 0.1, 0.45, 0.8, 0.15));
        poses.add(new Pos(btnExact = new JButton("Exact"), 0.1, 0.65, 0.2, 0.1));
        poses.add(new Pos(btnStart = new JButton("Start"), 0.4, 0.65, 0.2, 0.1));
        poses.add(new Pos(btnSubstr = new JButton("SubStr"), 0.7, 0.65, 0.2, 0.1));
        poses.add(new Pos(new JLabel("Answer:"), 0.1, 0.75, 0.8, 0.1));
        poses.add(new Pos(labelAnswer = new JLabel(), 0.1, 0.85, 0.8, 0.1));
        
        for (Pos p : poses) {
            getContentPane().add(p.c);
        } // for
        
        txtRegex.setBorder(border);
        txtInput.setBorder(border);
        labelAnswer.setBorder(border);
        btnExact.addActionListener(this);
        btnStart.addActionListener(this);
        btnSubstr.addActionListener(this);


    } // Applet


    public void actionPerformed(ActionEvent evt) {
        
        try {
            boolean b;
            Regex r = new Regex(txtRegex.getText());

            if (evt.getSource() == btnExact) {
                b = r.match(txtInput.getText());
            } else if (evt.getSource() == btnStart) {
                b = r.matchFromStart(txtInput.getText());
            } else {
                b = r.presentInSequence(txtInput.getText()) >= 0;
            } // else

            if (b) {
                labelAnswer.setText(r.getReplacement());
            } else {
                labelAnswer.setText("// Not matched!");
            } // else
        } catch (Exception e) {
            labelAnswer.setText("// Exception!");
            return;
        } // catch

    } // actionPerformed


    public void componentResized() {
        int w = getWidth();
        int h = getHeight();

        for (Pos p : poses) {
            p.c.setLocation((int) (w * p.x), (int) (h * p.y));
            p.c.setSize((int) (w * p.w), (int) (h * p.h));
        } // for

    } // componentResized


} // Applet
