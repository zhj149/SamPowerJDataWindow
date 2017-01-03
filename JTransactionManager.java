package org.sam.powerj.datawindow.externs;

import java.util.HashMap;
import java.util.Map;

import org.sam.powerj.datawindow.resource.ResourceLoader;

import powersoft.datawindow.JDataWindowControl;
import powersoft.powerj.db.java_sql.Transaction;

/**
 * 采用单例模式的sybase对象管理工具
 * @author sam
 *
 */
public class JTransactionManager {

	//begnin Singlton
	
	/**
	 * 单例模式，隐藏构造函数
	 */
	private JTransactionManager()
	{

	}
	
	/**
	 * 采用jvm保障单例
	 * 并且延迟加载
	 * @author sam
	 *
	 */
	private static class SybaseDWManagerHolder
	{
		private static JTransactionManager instance = new JTransactionManager();
	}
	
	/**
	 * 获取当前的实例
	 * @return
	 */
	public static JTransactionManager getInstance()
	{
		return SybaseDWManagerHolder.instance;
	}
	
	//end 
	
	//begin flyweight 
	
	/**
	 * 当前的所有数据库连接
	 */
	private Map<String , Transaction> connections = new HashMap<>();
	
	/**
	 * 根据名称获取数据库连接
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public Transaction getTransaction(String name) throws Exception
	{
		Transaction conn = null;
		if (this.connections.containsKey(name))
		{
			conn = connections.get(name);
		}
		else
		{
			synchronized (this) 
			{
				try
				{
					conn = this.createConnection(name);
					this.connections.put(name, conn);
				}
				catch(Exception ex)
				{
					throw ex;
				}
			}
			
		}
		
		return conn;
	}
	
	/**
	 * 根据数据库名，创建一个连接
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public Transaction createConnection(String name) throws Exception
	{
		Transaction conn = new Transaction();
		
		try
		{
//			String configFile = ApplicationContext.getConfigFile();
//			Properties prop = new Properties(); 
//			prop.load(this.getClass().getResourceAsStream(configFile));
			
			String oldUrl = "jdbc:sybase:Tds:localhost:2638";
//			String dataSource = this.reNameUrl(oldUrl, name);
			String dataSource = oldUrl;
			
//			String userid = prop.getProperty("username"); 
//			String password = prop.getProperty("password");
//			this.driver = prop.getProperty("driverClass");
			
			String userid = "dba"; 
			String password = "sql";
			
			conn.setAutoCommit(this.isAutoCommit());
			conn.create(name);
			conn.registerDriver(this.getDriver());
			conn.setDriverName(this.getDriver());
			conn.setDataSource(dataSource);
			conn.setUserID(userid);
			conn.setPassword(password);
			conn.setLoginTimeout(this.getTimeout());
			conn.connect();
		}
		catch(Exception ex)
		{
			throw ex;
		}
		
		return conn;
	}
	
	/**
	 * 根据原始的字符串重新拼接mysql数据库连接串
	 * @param oldUrl 原始字符串连接
	 * @param dbName 新的数据库名称
 	 * @return
	 */
	public String reNameUrl(String oldUrl , String dbName)
	{
		if (oldUrl == null || oldUrl.length() <= 0)
			return oldUrl;
		
		int iPos = oldUrl.lastIndexOf("/");
		int iPos2 = oldUrl.indexOf("?" , iPos);
		String params = "";
		if (iPos2 > 0)
			params = oldUrl.substring(iPos2);
			
		String result = oldUrl.substring(0, iPos + 1) + dbName + params;
		return result;
	}
	
	/**
	 * 重新创建连接
	 * @param name
	 */
	public void reConnection(String name)
	{
		if (this.connections.containsKey(name))
		{
			Transaction transaction = this.connections.get(name);
			
			if (transaction.isConnected())
				transaction.disconnect();
			
			transaction.connect();
		}
	}
	
	/**
	 * 重新连接全部数据
	 */
	public void reConnectionAll()
	{
		try
		{
			for(String name : this.connections.keySet())
			{
				reConnection(name);
			}
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}
	
	//end
		
	//begin 属性对象部分
	
	/**
	 * 使用的驱动
	 */
	private String driver = "com.sybase.jdbc.SybDriver"; //"com.mysql.jdbc.Driver";
	
	/**
	 * 超时时间
	 */
	private int timeout = 5;
	
	/**
	 * 自动提交事务
	 */
	private boolean autoCommit = false;
	
	/**
	 * 自动提交事务
	 * @return
	 */
	public boolean isAutoCommit() {
		return autoCommit;
	}

	/**
	 * 自动提交事务
	 * @param autoCommit
	 */
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	/**
	 * 超时时间
	 * @return
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * 超时时间
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * 使用的驱动
	 * @return
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * 使用的驱动
	 * @param driver
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}
	
	//end
	
	//begin 方法部分
	
	/**
	 * 断开全局连接
	 */
	public void disconnect(String name)
	{
		try
		{
			if (this.connections.containsKey(name))
				this.connections.get(name).disconnect();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * 断开全局连接
	 */
	public void disconnect()
	{
		try
		{
			for(String name : this.connections.keySet())
			{
				this.connections.get(name).disconnect();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * 创建一个数据窗口对象
	 * @param pbLibrary 数据窗口对象所在的
	 * @param dwName
	 * @return
	 */
	public JDataWindowControl createDataWindow(String pbLibrary , String dwName)
	{
		JDataWindowControl dw = new JDataWindowControl();
		try
		{
			dw.setTransport( 1 );
			dw.setSourceFileType( (short)0 );
			dw.setUseExtendedNames( false );
			dw.setConnectionSource( 2 );
			dw.setLiveScroll( false );
			dw.setDataWindowObjectName( dwName );
			dw.setAutoEdit( true );
			dw.setSplitHScroll( true );
			dw.setHScroll( true );
			dw.setCharPolicy( 0 );
			dw.setHorizontalPrinterMargin( 36 );
			dw.setEncoding( "GBK" );
			dw.setSourceStreamClassName( "" );
			dw.setBorderStyle( (short)0 );
			dw.setRowChanged( false );
			dw.setDoubleBuffering( true );
			dw.setTraceToLog( false );
			dw.setSourceStream(ResourceLoader.getResourceStream(pbLibrary));
			dw.setSourceStreamMethodName( "" );
			dw.setWindowStyle( 3145729 );
			dw.setRmiURL( "" );
			dw.setTransactionName( "" );
			dw.setScrollbarActivationDelay( 2500 );
			dw.setVerticalPrinterMargin( 36 );
			dw.setVScroll( true );
			dw.setOpaque( false );
			dw.setJaguarURL( "" );
			dw.setFont( new java.awt.Font( "Dialog", java.awt.Font.PLAIN, 12 ) );
			dw.setBackground( new java.awt.Color( 204, 204, 204 ) );
			dw.setForeground( java.awt.Color.black );
			dw.setEnabled(true);
			dw.setVisible(true);
			dw.modify("DataWindow.ReadOnly=Yes");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return dw;
	}
	
	/**
	 * 直接连接好数据库的datawindow
	 * @param pbLibrary 数据窗口所在的pbl库
	 * @param dwName 数据窗口名称
	 * @param dbName 数据库名称
	 * @return
	 * @throws Exception 
	 */
	public JDataWindowControl createDataWindowConnected(String pbLibrary , String dwName , String dbName)
	{
		
		JDataWindowControl dw = this.createDataWindow(pbLibrary, dwName);
		Transaction transaction = null;
		try {
			transaction = getTransaction(dbName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dw.setTransObject(transaction);
		return dw;
	}
		
	//end
}
