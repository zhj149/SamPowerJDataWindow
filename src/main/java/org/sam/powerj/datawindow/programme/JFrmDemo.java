package org.sam.powerj.datawindow.programme;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import org.sam.powerj.datawindow.externs.JDataWindowDecorator;
import org.sam.powerj.datawindow.externs.JTransactionManager;
import org.sam.powerj.datawindow.resource.ResourceLoader;

import powersoft.datawindow.JDataWindowControl;

/**
 * demo
 * @author sam
 *
 */
public class JFrmDemo extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8716731991672081454L;
	private JPanel contentPane;
	
	/**
	 * 数据窗口对象
	 */
	private JDataWindowControl dwMain;
	
	/**
	 * 数据窗口装饰器对象
	 */
	private JDataWindowDecorator dwDecorator;

	/**
	 *  demo
	 */
	public JFrmDemo() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JToolBar toolBar = new JToolBar();
		add(toolBar, BorderLayout.NORTH);
		
		JButton btnModify = new JButton("编辑",new ImageIcon(ResourceLoader.getResource(ResourceLoader.IMAGE_MODIFY)));
		btnModify.addActionListener(new ActionListener() {
			
			//新增操作
			@Override
			public void actionPerformed(ActionEvent e) {
				try
				{				
					dwDecorator.readOnly();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		toolBar.add(btnModify);
		
		JButton btnAdd = new JButton("新增",new ImageIcon(ResourceLoader.getResource(ResourceLoader.IMAGE_NEW)));
		btnAdd.addActionListener(new ActionListener() {
			
			//新增操作
			@Override
			public void actionPerformed(ActionEvent e) {
				try
				{				
					//datawindow控件有个bug，新增行后，第一个可编辑单元格无法获取焦点（无特殊样式，仅仅是输入框）
					//所以需要将焦点先移入dddw，ddlb，checkbox，radiobuttons，editmask等空间后，在移动回去，才行
					//dwDecorator里面的大多数方法都是位了解决datawindow本身的bug而提供的
					dwDecorator.insertRowAuto((short)5,(short)1);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		toolBar.add(btnAdd);
		
		//删除按钮 
		JButton btnDel = new JButton("删除",new ImageIcon(ResourceLoader.getResource(ResourceLoader.IMAGE_DELETE)));
		btnDel.addActionListener(new ActionListener() {
			
			/**
			 * 删除操作
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				try
				{
					dwDecorator.deleteRow();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		toolBar.add(btnDel);
		
		//删除按钮 
		JButton btnSave = new JButton("保存",new ImageIcon(ResourceLoader.getResource(ResourceLoader.IMAGE_SAVE)));
		btnSave.addActionListener(new ActionListener() {
			
			/**
			 * 删除操作
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				try
				{
					dwDecorator.update();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		toolBar.add(btnSave);
		
		dwMain = JTransactionManager.getInstance().createDataWindowConnected( 
				ResourceLoader.PBL, "dw_employee" , "mysql");
		
		//对数据窗口的一些扩展方法
		dwDecorator = new JDataWindowDecorator(dwMain, "mysql");
        this.add(dwMain, BorderLayout.CENTER);
       
		try
		{
			dwDecorator.retrieve();	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
