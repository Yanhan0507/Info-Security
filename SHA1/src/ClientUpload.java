import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/*
 * 
 * author: Yanhan, Xianshu
 * 
 */
class ClientUpload extends JFrame implements ActionListener {

    JFileChooser fc;
    JButton browse1, browse2, b1;
    JTextField fileView1, fileView2, result, similarity, exeTime;
    
    JTextField code1, code2;
    
    
    FileInputStream in;
    Socket s;
    DataOutputStream dout;
    DataInputStream din;
    String f1Path;
    String f2Path;
    int i;

    ClientUpload() {
        super("SHA1 Comparator Yanhan&Xianshu");
        fileView1 = new JTextField();
        fileView1.setBounds(20, 50, 190, 30);
        add(fileView1);
        
        browse1 = new JButton("Browse1");
        browse1.setBounds(250, 50, 80, 30);
        add( browse1);
        browse1.addActionListener(this);
        
        fileView2 = new JTextField();
        fileView2.setBounds(20, 100, 190, 30);
        add(fileView2);
        
        browse2 = new JButton("Browse2");
        browse2.setBounds(250, 100, 80, 30);
        add(browse2);
        browse2.addActionListener(this);
        
        b1 = new JButton("Compare two files");
        b1.setBounds(140, 150, 150, 30);
        add(b1);
        
        result = new JTextField();
        result.setBounds(20, 200, 190, 30);
        add(result);
        
        exeTime = new JTextField();
        exeTime.setBounds(250, 200, 190, 30);
        add(exeTime);
        
        code1 = new JTextField();
        code1.setBounds(20, 250, 420, 30);
        add(code1);
        
        code2 = new JTextField();
        code2.setBounds(20, 300, 420, 30);
        add(code2);
        
        similarity = new JTextField();
        similarity.setBounds(20, 350, 420, 30);
        add(similarity);
        
        b1.addActionListener(this);
        fc = new JFileChooser();
        setLayout(null);
        setSize(550, 450);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        try {
        	
            if (e.getSource() ==  browse1 || e.getSource() == browse2) {
                int x = fc.showOpenDialog(null);
                if (x == JFileChooser.APPROVE_OPTION) {
                    if(e.getSource() ==  browse1){
                        File f1 = fc.getSelectedFile();
                        fileView1.setText(f1.getAbsolutePath());
                        f1Path = f1.getAbsolutePath();
                        
                    }
                    else{
                        File f2 = fc.getSelectedFile();
                        f2Path = f2.getAbsolutePath();
                        fileView2.setText(f2.getAbsolutePath());   
                    }
                }
            }
            if (e.getSource() == b1) {

               long[] exeT = {0};
               String[] rets = SHA1.compare(f1Path, f2Path, exeT);
               
               
               boolean ret = rets[0].equals(rets[1]);
               code1.setText("hashcode for file1: " + rets[0]);
               code2.setText("hashcode for file2: " + rets[1]);
               double sim = getSimilarity(rets[0], rets[1]);
               int percentage = (int)(sim*100);
               similarity.setText("Similarity (the number of same bits/128): " + percentage + "%");
               exeTime.setText("Execution Time: " + exeT[0] + "µs");
               if(ret){
            	   result.setText("They are same!");
               }
               else{
            	   result.setText("They are different!");;
               }
               
            }
        } catch (Exception ex) {
        }
    }

    private static double getSimilarity(String str1, String str2){
    	double same = 0;
    	double length = str1.length();
    	for(int i = 0; i < Math.min(str1.length(), str2.length()); i++){
    		if(str1.charAt(i) == str2.charAt(i)){
    			same++;
    		}
    	}
    	return same/length;
    }

    public static void main(String... d) {
        new ClientUpload();
    }
}