package gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
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
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
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
        
        JMenuBar menuBar = generateMenuBar();
        menuBar.add(createMenuBar());
        setJMenuBar(menuBar);
    }
    
    protected void setCoordinatesLogWindow(String[] cor){
    	Rectangle logPosition = new Rectangle(Integer.parseInt(cor[0]), Integer.parseInt(cor[1]), Integer.parseInt(cor[2]), Integer.parseInt(cor[3])) ;
    }
    
    protected void setCoordinatesGameWindow(String[] cor){
    	Rectangle gamePosition = new Rectangle(Integer.parseInt(cor[0]), Integer.parseInt(cor[1]), Integer.parseInt(cor[2]), Integer.parseInt(cor[3]));
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
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
        
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
        		"Управление режимом отображения приложения");
        
        JMenuItem systemLookAndFeel = createLookAndFeelMenuItem("Системная схема", "UIManager.getSystemLookAndFeelClassName()");
        lookAndFeelMenu.add(systemLookAndFeel);
        
        JMenuItem crossplatformLookAndFeel = createLookAndFeelMenuItem("Универсальная схема", "UIManager.getCrossPlatformLookAndFeelClassName()");
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        JMenu testMenu = createTestMenu();

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }
    
    private JMenu createTestMenu()
    {
    	JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");
        
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
        	Logger.debug("Новая строка");
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
    	logW.getBounds();
    	gameW.getBounds();
    	String info = Integer.toString(logW.getX())+' '+Integer.toString(logW.getY())+' '+Integer.toString(logW.getWidth())+' '+Integer.toString(logW.getHeight())+' '+Integer.toString(gameW.getX())+' '+Integer.toString(gameW.getY())+' '+Integer.toString(gameW.getWidth())+' '+Integer.toString(gameW.getHeight());
    	writeInFile(info);  
    }
    
    protected void exitWindow()
    {
    	Object[] options = {"Да", "Нет"};
    	int sel = JOptionPane.showOptionDialog(null, "Вы уверены, что хотите выйти?", "Выход", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
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
