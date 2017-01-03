package org.sam.powerj.datawindow.resource;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 资源加载对象
 * 只加载一次
 * @author sam
 *
 */
public class ResourceLoader
{
	
	/**
	 * 新增操作按钮
	 */
	public static final String IMAGE_NEW = "org/sam/powerj/datawindow/resource/new.png";
	
	/**
	 * 修改按钮
	 */
	public static final String IMAGE_MODIFY = "org/sam/powerj/datawindow/resource/modify.png";
	
	/**
	 * 删除按钮
	 */
	public static final String IMAGE_DELETE = "org/sam/powerj/datawindow/resource/delete.png";
	
	/**
	 * 保存按钮
	 */
	public static final String IMAGE_SAVE = "org/sam/powerj/datawindow/resource/save.png";
	
	/**
	 * 数据窗口引用
	 */
	public static final String PBL = "org/sam/powerj/datawindow/resource/dws.pbl";
	
	/**
	 * 已加载的资源文件列表
	 */
	protected static Map<String , URL> resources = new HashMap<>();
	
	/**
	 * 获取资源文件
	 * @param name 资源名称
	 * @return 资源链接地址
	 */
	public static URL getResource(String name)
	{
		if (!resources.containsKey(name))
		{
			synchronized (resources) 
			{
				resources.put(name, ResourceLoader.class.getClassLoader().getResource(name));
			}
		}
		
		return resources.get(name);
	}
	
	/**
	 * 以流的方式获取资源
	 * @param name 资源名称 
	 * @return 
	 */
	public static InputStream getResourceStream(String name)
	{	
		return ResourceLoader.class.getClassLoader().getResourceAsStream(name);
	}
}
