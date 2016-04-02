package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

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

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
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
    
    protected void exitWindow()
    {
    	Object[] options = {"��", "���"};
    	int sel = JOptionPane.showOptionDialog(null, "�� �������, ��� ������ �����?", "�����", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
    	if (sel == 0)
    	{
    		System.exit(0);
    	}
    }
    
    protected void writeInFile(String inf)
    {
    	File f = new File("C:/Users/windowsLocation.txt");
    	DataOutputStream winlocation;
		try {
			winlocation = new DataOutputStream(new FileOutputStream(f));
			winlocation.writeChars(inf);
		} catch (Exception e) {
			System.out.println("Your file is not correct");			
		}
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
