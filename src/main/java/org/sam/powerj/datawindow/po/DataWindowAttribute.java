package org.sam.powerj.datawindow.po;

/**
 * 数据窗口对应属性的实体
 * @author sam
 *
 */
public class DataWindowAttribute {

	/**
	 * 列对应的id
	 */
	private int id = 0;
	
	/**
	 * 字段名
	 */
	private String name = "";
	
	/**
	 * 字段类型
	 */
	private String colType = "";
	
	/**
	 * 编辑对象类型
	 */
	private String type = "";
	
	/**
	 * 标题
	 */
	private String title = "";

	/**
	 * 所在的x值
	 */
	private int x = 0;
	
	/**
	 * 可见性
	 */
	private boolean visible = true;
	
	/**
	 * 如果是dddw，它所绑定的data col name
	 */
	private String dataCol = "";
	
	/**
	 * 如果是dddw，它所绑定的display col name
	 */
	private String textCol = "";
	
	/**
	 * 字段上绑定的tag值，用来作为特殊处理
	 */
	private String tag = "";
	
	/**
	 * 如果是dddw，它所绑定的data col name
	 * @return
	 */
	public String getDataCol() {
		return dataCol;
	}

	/**
	 * 如果是dddw，它所绑定的data col name
	 * @param dataCol
	 */
	public void setDataCol(String dataCol) {
		this.dataCol = dataCol;
	}

	/**
	 * 如果是dddw，它所绑定的display col name
	 * @return
	 */
	public String getTextCol() {
		return textCol;
	}

	/**
	 * 如果是dddw，它所绑定的display col name
	 * @param textCol
	 */
	public void setTextCol(String textCol) {
		this.textCol = textCol;
	}

	/**
	 * 列对应的id
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * 列对应的id
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 字段名
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 字段名
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 字段类型
	 * @return
	 */
	public String getColType() {
		return colType;
	}

	/**
	 * 字段类型
	 * @param colType
	 */
	public void setColType(String colType) {
		this.colType = colType;
	}
	
	/**
	 * 编辑对象类型
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * 编辑对象类型
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * 标题
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 标题
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 所在的x值
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * 所在的x值
	 * @param x
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * 可见性
	 * @return
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * 可见性
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * 字段上绑定的tag值，用来作为特殊处理
	 * @return
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * 字段上绑定的tag值，用来作为特殊处理
	 * @param tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
}
