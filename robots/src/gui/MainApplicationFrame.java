package gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import log.Logger;

/**
 * ��� ��������� �������:
 * 1. ����� �������� ���� ���������� ������������ � ������ ��������. 
 * ������� ��������� ��� �� ����� ����� ������� ������� (��� ������ �������� ��������� �����).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    protected LogWindow logW;
    protected GameWindow gameW;
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);
        
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);
        this.logW = logWindow;

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow); 
        this.gameW = gameWindow;
        
        addWindowListener(new WindowAdapter() 
        {
        	 public void windowClosing(WindowEvent e) 
        	 {
        		 exitWindow();
        	 }
        });
        
        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        
        if (new File(System.getProperty("user.home"), "winlocation.txt").exists()){
        	deserealisation();
        }
        
        JMenuBar menuBar = generateMenuBar();
        menuBar.add(createMenuBar());
        setJMenuBar(menuBar);
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("�������� ��������");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
 
        //Set up the lone menu.
        JMenu menu = new JMenu("Document");
        menu.setMnemonic(KeyEvent.VK_D);
        menuBar.add(menu);
 
        //Set up the first menu item.
        JMenuItem menuItem = new JMenuItem("New");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("new");
        menuItem.addActionListener((event)-> {});
        menu.add(menuItem);
 
        //Set up the second menu item.
        menuItem = new JMenuItem("Quit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener((event)-> {exitWindow();});
        menu.add(menuItem);
 
        return menuBar;
    }
    
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu lookAndFeelMenu = new JMenu("����� �����������");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
        		"���������� ������� ����������� ����������");
        
        JMenuItem systemLookAndFeel = createLookAndFeelMenuItem("��������� �����", "UIManager.getSystemLookAndFeelClassName()");
        lookAndFeelMenu.add(systemLookAndFeel);
        
        JMenuItem crossplatformLookAndFeel = createLookAndFeelMenuItem("������������� �����", "UIManager.getCrossPlatformLookAndFeelClassName()");
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        JMenu testMenu = createTestMenu();

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }
    
    private JMenu createTestMenu()
    {
    	JMenu testMenu = new JMenu("�����");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("�������� �������");
        
        JMenuItem addLogMessageItem = new JMenuItem("��������� � ���", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
        	Logger.debug("����� ������");
        });
        testMenu.add(addLogMessageItem);

        return testMenu;
    }
    
    private JMenuItem createLookAndFeelMenuItem(String text, String lookAndFeelClassName)
    {
    	 JMenuItem systemLookAndFeel = new JMenuItem(text, KeyEvent.VK_S);
    	 systemLookAndFeel.addActionListener((event) -> {
    		 setLookAndFeel(lookAndFeelClassName);
    		 this.invalidate();
    	 });
    	 return systemLookAndFeel;
    }
    protected void serealisation(){
    	int logCl = 0;
    	int gameCl = 0;
    	logW.getBounds();
    	gameW.getBounds();
    	if (logW.isClosed()){
    		logCl = 1;
    	}
    	if (gameW.isClosed()){
    		gameCl = 1;
    	}
    	String info = Integer.toString(logW.getX())+' '+Integer.toString(logW.getY())+' '+Integer.toString(logW.getWidth())+' '+Integer.toString(logW.getHeight())+' '+ Integer.toString(logCl)+' '+Integer.toString(gameW.getX())+' '+Integer.toString(gameW.getY())+' '+Integer.toString(gameW.getWidth())+' '+Integer.toString(gameW.getHeight())+' '+Integer.toString(gameCl);;
    	writeInFile(info);  
    }
    
    protected void exitWindow()
    {
    	Object[] options = {"��", "���"};
    	int sel = JOptionPane.showOptionDialog(null, "�� �������, ��� ������ �����?", "�����", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
    	if (sel == JOptionPane.YES_OPTION)
    	{
    		serealisation();
    		dispose();
    	}
    }
    
    protected void writeInFile(String inf){
    	File f = new File(System.getProperty("user.home"), "winlocation.txt");
    	try(FileWriter writer = new FileWriter(f, false))
         {
             writer.write(inf);
         }
    	catch(IOException ex){            
            System.out.println(ex.getMessage());
        } 
    }
    
    protected String readFromFile(){
    	File f = new File(System.getProperty("user.home"), "winlocation.txt");
    	try(FileReader reader = new FileReader(f))
    	{
    	    char[] buffer = new char[(int)f.length()];
    	    reader.read(buffer);
    	    return (new String(buffer));
    	}
    	catch(IOException ex){
    	    return ex.getMessage();
    	}   
    }
    
    protected void deserealisation(){
    	String inf = readFromFile(); 
    	String[] coor = inf.split(" ");
    	Rectangle logPosition = new Rectangle(Integer.parseInt(coor[0]), Integer.parseInt(coor[1]), Integer.parseInt(coor[2]), Integer.parseInt(coor[3]));
    	if (Integer.parseInt(coor[4]) == 1){
    		try {
    			logW.setIcon(true);
    		}
    		catch (PropertyVetoException e) {}
    	}
    	Rectangle gamePosition = new Rectangle(Integer.parseInt(coor[5]), Integer.parseInt(coor[6]), Integer.parseInt(coor[7]), Integer.parseInt(coor[8]));
    	if (Integer.parseInt(coor[9]) == 1){
    		try {
    			gameW.setIcon(true);
    		}
    		catch (PropertyVetoException e) {}
    	}
    	logW.setBounds(logPosition);
    	gameW.setBounds(gamePosition);    	
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
