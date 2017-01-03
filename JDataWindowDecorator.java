package org.sam.powerj.datawindow.externs;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

import org.sam.powerj.datawindow.po.DataWindowAttribute;

import powersoft.datawindow.DWRetrieveArgs;
import powersoft.datawindow.DataWindowInterface;
import powersoft.datawindow.JDataWindowControl;
import powersoft.datawindow.event.ItemEvent;
import powersoft.datawindow.event.ItemListener;
import powersoft.datawindow.event.KeyEvent;
import powersoft.datawindow.event.KeyListener;
import powersoft.datawindow.event.MouseEvent;
import powersoft.datawindow.event.MouseListener;
import powersoft.powerj.db.Query;
import powersoft.powerj.db.StatementResults;
import powersoft.powerj.db.java_sql.Transaction;

/**
 * 对datawindow的装饰器
 * 
 * @author sam
 *
 */
public class JDataWindowDecorator implements MouseListener, KeyListener , ItemListener , MouseWheelListener{

	// begin 属性相关

	/**
	 * 当前操作的datawindow控件
	 */
	private JDataWindowControl adw;

	/**
	 * 双击排序
	 */
	private boolean doubleclickedSort = true;

	/**
	 * 启用复制粘贴功能
	 */
	private boolean usePasted = true;

	/**
	 * 多行选择
	 */
	private boolean mutliSelecte = true;

	/**
	 * 单行选择，会压抑多行选择
	 */
	private boolean singleSelect = true;
	
	/**
	 * 是否执行查找
	 */
	private boolean search = true;
	
	/**
	 * 加载默认
	 */
	private boolean defaultItemEvent = false;

	/**
	 * 当前的数据库连接
	 */
	private String dbName = "";
	
	/**
	 * 当前查找选择的字段名称
	 * 可以用,分割多个字段
	 */
	private String searchColumn = "";
	
	/**
	 * 最后一次查找的内容
	 */
	private String lastSearch = "";
	
	/**
	 * 可见的列表
	 */
	private List<DataWindowAttribute> colVisibles;
	
	/**
	 * 当前查找选择的字段名称
	 * 可以用,分割
	 * @return
	 */
	public String getSearchColumn() {
		return searchColumn;
	}

	/**
	 * 当前查找选择的字段名称
	 * 可以用,分割
	 * @param searchColumn
	 */
	public void setSearchColumn(String searchColumn) {
		this.searchColumn = searchColumn;
	}

	/**
	 * 当前的日期时间格式格式化字符串
	 */
	private DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 是否执行查找
	 * @return
	 */
	public boolean isSearch() {
		return search;
	}

	/**
	 * 是否执行查找
	 * @param search
	 */
	public void setSearch(boolean search) {
		this.search = search;
	}

	/**
	 * 加载默认
	 * @return
	 */
	public boolean isDefaultItemEvent() {
		return defaultItemEvent;
	}

	/**
	 * 加载默认
	 * @param defaultItemEvent
	 */
	public void setDefaultItemEvent(boolean defaultItemEvent) {
		this.defaultItemEvent = defaultItemEvent;
	}

	/**
	 * 单行选择，会压抑多行选择
	 * 
	 * @return
	 */
	public boolean isSingleSelect() {
		return singleSelect;
	}

	/**
	 * 单行选择，会压抑多行选择
	 * 
	 * @param singleSelect
	 */
	public void setSingleSelect(boolean singleSelect) {
		this.singleSelect = singleSelect;
	}

	/**
	 * 当前的数据库连接名
	 * 
	 * @return
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * 当前的数据库连接名
	 * 
	 * @param dbName
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * 是否允许编辑状态
	 * 
	 * @return
	 */
	public boolean isReadOnly() {
		return this.adw.describe("DataWindow.ReadOnly").toLowerCase().equals("yes");
	}

	/**
	 * 是否允许编辑状态
	 * 
	 * @param readOnly
	 */
	public void setReadOnly(boolean readOnly) {
		if (readOnly) {
			this.adw.modify("DataWindow.ReadOnly = Yes");
		} else {
			this.adw.modify("DataWindow.ReadOnly = No");
		}
	}

	/**
	 * 双击排序
	 */
	public boolean isDoubleclickedSort() {
		return doubleclickedSort;
	}

	/**
	 * 双击排序
	 */
	public void setDoubleclickedSort(boolean doubleclickedSort) {
		this.doubleclickedSort = doubleclickedSort;
	}

	/**
	 * 复制粘贴功能
	 * 
	 * @return
	 */
	public boolean isUsePasted() {
		return usePasted;
	}

	/**
	 * 复制粘贴功能
	 * 
	 * @param usePasted
	 */
	public void setUsePasted(boolean usePasted) {
		this.usePasted = usePasted;
	}

	/**
	 * 多行选择
	 * 
	 * @return
	 */
	public boolean isMutliSelecte() {
		return mutliSelecte;
	}

	/**
	 * 多行选择
	 * 
	 * @param mutliSelecte
	 */
	public void setMutliSelecte(boolean mutliSelecte) {
		this.mutliSelecte = mutliSelecte;
	}

	// end

	// begin 功能相关

	/**
	 * 对当前操作的datawindow做个装饰功能
	 * 
	 * @param dw
	 * @param dbName
	 *            数据库连接名
	 */
	public JDataWindowDecorator(JDataWindowControl dw, String dbName) {
		this.adw = dw;

		if (this.doubleclickedSort || this.mutliSelecte)
			this.adw.addMouseListener(this);

		if (this.usePasted)
			this.adw.addKeyListener(this);
		
		this.adw.addMouseWheelListener(this);

		this.dbName = dbName;
	}

	/**
	 * 获取主键列表
	 * 
	 * @return
	 */
	public Vector<String> getPrimaryCols() {
		Vector<String> result = new Vector<>();
		try {
			int iCount = Integer.parseInt(this.adw.describe("DataWindow.Column.Count"));
			for (int i = 1; i <= iCount; i++) {
				String name = this.adw.describe("#" + i + ".name");
				if (this.adw.describe(name + ".key").equals("yes")) {
					result.add(name);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	/**
	 * 自动翻转readonly状态
	 */
	public void readOnly() {
		this.setReadOnly(!this.isReadOnly());
	}

	/**
	 * 获取主键字段和主键类型
	 * 
	 * @return
	 */
	public Map<String, String> getPrimaryColsMap() {
		Map<String, String> result = new HashMap<>();
		try {
			int iCount = this.adw.getColumnCount();
			for (int i = 1; i <= iCount; i++) {
				String name = this.adw.describe("#" + i + ".name");
				if (this.adw.describe(name + ".key").equals("yes")) {
					result.put(name, this.adw.describe(name + ".Coltype"));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	/**
	 * 获取素有列的名称和类型 使用LinkedHashMap保证位置
	 * 
	 * @return
	 */
	public Map<String, String> getColumns() {
		Map<String, String> result = new LinkedHashMap<>();

		try {
			int iCount = Integer.parseInt(this.adw.describe("DataWindow.Column.Count"));
			for (int i = 1; i <= iCount; i++) {
				String name = this.adw.describe("#" + i + ".name");
				result.put(name, this.adw.describe(name + ".Coltype"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	/**
	 * 获取素有列的名称和类型 使用LinkedHashMap保证位置
	 * 
	 * @return
	 */
	public Map<String, String> getUpdateColumns() {
		Map<String, String> result = new LinkedHashMap<>();

		try {
			int iCount = Integer.parseInt(this.adw.describe("DataWindow.Column.Count"));
			for (int i = 1; i <= iCount; i++) {
				String name = this.adw.describe("#" + i + ".name");
				if (this.adw.describe(name + ".Update").equals("yes")) {
					result.put(name, this.adw.describe(name + ".Coltype"));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	/**
	 * 插入一行，并且自动定位到那行的方法
	 * 
	 * @param iBefore
	 * @return
	 */
	public int insertRow(int iBefore) {
		
		if (this.isReadOnly())
			this.readOnly();
		
		int iRow = this.adw.insertRow(iBefore);
		
		this.adw.setFocus();
		this.adw.scrollToRow(iBefore);
		this.adw.setRow(iRow);
		this.adw.setColumn((short)2);
		this.adw.selectRow(0, false);
		this.adw.selectRow(iRow, true);
		
		return iRow;
	}

	/**
	 * 插入数据，并且初始化自增字段
	 * @param canEditCol 可以编辑的列
	 */
	public int insertRowAuto(short canEditCol,short editCol) {
		try {
			String table = this.adw.describe("DataWindow.Table.UpdateTable");
			Map<String, String> keys = this.getPrimaryColsMap();
			if (keys.size() <= 0)
				return -1;
			
			if (this.isReadOnly())
				this.readOnly();

			JTransactionManager manager = JTransactionManager.getInstance();
			int iRow = this.adw.insertRow(0);

			for (String name : keys.keySet()) {
				String colType = keys.get(name);
				if (colType.toLowerCase().startsWith("int") || colType.toLowerCase().startsWith("long")) 
				{
					Query q = manager.getTransaction(this.dbName).createQuery("idQuery");

					q.setSQL(String.format("select max(%s) + 1 from %s", name, table));
					StatementResults rs = q.execute();
					int id = 1;
					int maxid = 1;
					if (this.adw.getRowCount() > 1) {
						maxid = Integer.parseInt(this.adw.describe(String.format("Evaluate('max(%s)',0)", name))) + 1;
					}

					if (rs != null && rs.succeeded()) {
						id = rs.getResultSetValueInt(0);
					}
					id = Integer.max(id, maxid);
					this.adw.setItem(iRow, name, id);
				}

				this.adw.setRow(iRow);
				this.adw.scrollToRow(iRow);
				this.adw.setColumn(canEditCol);
				this.adw.setColumn(editCol);
				this.adw.selectRow(0, false);
				this.adw.selectRow(iRow, true);
				this.adw.setFocus();
				
				return iRow;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return 0;
	}
	
	public int insertRowAuto()
	{
		return insertRowAuto((short)6 , (short)2);
	}
	
	public int insertRowAuto(short canEditCol)
	{
		return insertRowAuto(canEditCol , (short)2);
	}
	
	/**
	 * 删除数据行的操作
	 * @param iRow 选择的数据行
	 * @return
	 */
	public int deleteRow()
	{
		int iRow = this.adw.getSelectedRow(0);
		if (iRow <= 0)
		{
			JOptionPane.showMessageDialog(null, "请选择您要删除的数据行");
			return -1;
		}
		return this.adw.deleteRow(iRow);
	}
	
	/**
	 * 更新数据的操作
	 * @return
	 * @throws Exception 
	 * @throws NullPointerException 
	 * @throws HeadlessException 
	 */
	public int update() throws HeadlessException, NullPointerException, Exception
	{
		int iResult = this.adw.acceptText();
		if (iResult <= 0)
		{
			JOptionPane.showMessageDialog(null, "更新数据缓冲区失败");
			return -1;
		}
		
		Transaction sqlca = JTransactionManager.getInstance().getTransaction(this.getDbName());
		
		if (adw.update(true, false))
		{
			JOptionPane.showMessageDialog(null, "保存成功");
			sqlca.commit();
			adw.resetUpdate();
			if (!this.isReadOnly())
				this.readOnly();
			
			return 1;
		}
		else
		{
			JOptionPane.showMessageDialog(null, "保存失败");
			sqlca.rollback();
			return -1;
		}
	}

	/**
	 * 是否有数据行选中
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return this.adw.getSelectedRow(0) > 0;
	}

	/**
	 * 获取全部选中的数据行
	 * 
	 * @return
	 */
	public List<Integer> getSelectList() {
		LinkedList<Integer> result = new LinkedList<>();
		int iRow = this.adw.getSelectedRow(0);
		while (iRow > 0) {
			result.add(iRow);
			iRow = this.adw.getSelectedRow(iRow);
		}

		if (result.size() <= 0) {
			for (int i = 1; i <= this.adw.rowCount(); i++) {
				result.add(i);
			}
		}

		return result;
	}

	/**
	 * 获取数据窗口对象的列表
	 * 
	 * @return
	 */
	public List<DataWindowAttribute> getDataWindowObjects() {
		List<DataWindowAttribute> result = new LinkedList<>();
		List<DataWindowAttribute> sorted = new LinkedList<>();

		try {
			for (int i = 1; i <= this.adw.getColumnCount(); i++) {
				
						
				DataWindowAttribute item = new DataWindowAttribute();
				item.setId(i);
				item.setName(this.adw.describe("#" + i + ".name").toLowerCase());
				
				String X = this.adw.describe(item.getName() + ".X");
				if ("?".equals(X) || "!".equals(X))
					X = "0";
				
				item.setColType(this.adw.describe(item.getName() + ".ColType").toLowerCase());
				item.setType(this.adw.describe(item.getName() + ".Edit.Style").toLowerCase());
				item.setVisible(this.adw.describe(item.getName() + ".Visible").toLowerCase().equals("true"));
				item.setX(Integer.parseInt(X));
				item.setTitle(this.adw.describe(item.getName() + "_t.Text"));
				item.setTag(this.adw.describe(item.getName() + ".tag"));
				if (item.getType().equals("dddw")) {
					item.setDataCol(this.adw.describe(item.getName() + ".DDDW.DataColumn"));
					item.setTextCol(this.adw.describe(item.getName() + ".DDDW.DisplayColumn"));
				}
				result.add(item);
			}

			// 直接排序输出
			Stream<DataWindowAttribute> stream = result.parallelStream()
					.sorted((s1, s2) -> Integer.compare(s1.getX(), s2.getX()));

			Iterator<DataWindowAttribute> iter = stream.iterator();
			while (iter.hasNext()) {
				DataWindowAttribute item = iter.next();
				sorted.add(item);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return sorted;
	}
	
	/**
	 * 获取所有可见对象
	 * @return
	 */
	public List<DataWindowAttribute> getDataWindowObjectsVisible()
	{
		if (this.colVisibles != null && !this.colVisibles.isEmpty())
			return this.colVisibles;
			
		List<DataWindowAttribute> result = new LinkedList<>();
		List<DataWindowAttribute> sorted = new LinkedList<>();

		try {
			for (int i = 1; i <= this.adw.getColumnCount(); i++) {
				
				String colName = this.adw.describe("#" + i + ".name").toLowerCase();
				if (this.adw.describe(colName + ".visible").equals("0"))
				{
					continue;
				}
				
				//以下做了特殊处理，如果height <= 10 ，表示这个字段是为了避开datawindow的问题而加的
				if (Integer.parseInt( this.adw.describe(colName + ".height")) <= 10)
				{
					continue;
				}
				
				DataWindowAttribute item = new DataWindowAttribute();
				item.setId(i);
				item.setName(this.adw.describe("#" + i + ".name").toLowerCase());
				
				String X = this.adw.describe(item.getName() + ".X");
				if ("?".equals(X) || "!".equals(X))
					X = "0";
				
				item.setColType(this.adw.describe(item.getName() + ".ColType").toLowerCase());
				item.setType(this.adw.describe(item.getName() + ".Edit.Style").toLowerCase());
				item.setVisible(this.adw.describe(item.getName() + ".Visible").toLowerCase().equals("true"));
				item.setX(Integer.parseInt(X));
				item.setTitle(this.adw.describe(item.getName() + "_t.Text"));
				item.setTag(this.adw.describe(item.getName() + ".tag"));
				if (item.getType().equals("dddw")) {
					item.setDataCol(this.adw.describe(item.getName() + ".DDDW.DataColumn"));
					item.setTextCol(this.adw.describe(item.getName() + ".DDDW.DisplayColumn"));
				}
				result.add(item);
			}

			// 直接排序输出
			Stream<DataWindowAttribute> stream = result.parallelStream()
					.sorted((s1, s2) -> Integer.compare(s1.getX(), s2.getX()));

			Iterator<DataWindowAttribute> iter = stream.iterator();
			while (iter.hasNext()) {
				DataWindowAttribute item = iter.next();
				sorted.add(item);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		this.colVisibles = sorted;
		return colVisibles;
	}

	/**
	 * 执行计算公司的操作
	 * 
	 * @param expression
	 *            计算公式
	 * @param row
	 *            数据行，从1开始，0代表全部
	 * @return 计算的结果
	 */
	public String eval(String expression, int row) {
		return this.adw.describe(String.format("Evaluate('%s',%d)", expression, row));
	}

	/**
	 * 获取字段对应的显示值
	 * 
	 * @param colName
	 *            字段名称
	 * @param row
	 *            数据行号，从1开始，0代表全部
	 * @return 显示值
	 */
	public String getItemDisplay(String colName, int row) {
		return this.eval(String.format("lookupdisplay(%s)", colName), row);
	}

	/**
	 * 获取所有选中的数据行
	 * 
	 * @param selectRows
	 * @return
	 */
	public String getSelectedValues(List<Integer> selectRows) {
		StringBuilder sb = new StringBuilder();
		try {
			List<DataWindowAttribute> colList = this.getDataWindowObjectsVisible();

			for (int iRow : selectRows) {
				for (int i = 0; i < colList.size(); i++) {
					sb.append(this.getItemValue(iRow, colList.get(i)));
					if (i < colList.size() - 1)
						sb.append("\t");
				}

				sb.append("\r\n");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return sb.toString();
	}

	/**
	 * 检验数据并且将数据粘贴回单元格
	 * 
	 * @param text
	 *            未处理过的文本
	 * @return
	 */
	public boolean setSelectValues(String text) {
		try {
			String[] lines = text.split("\r\n");
			List<Integer> selected = this.getSelectList();

			if (lines == null || lines.length <= 0) {
				System.out.println("无粘贴数据");
				return false;
			}

			if (lines.length != selected.size()) {
				JOptionPane.showMessageDialog(null, "您所选择的数据和要粘贴的数据行数不同，操作终止");
				return false;
			}

			List<DataWindowAttribute> cols = this.getDataWindowObjectsVisible();

			int line = 0;
			int iIndex = 1;
			boolean isChecked = true;

			for (int iRow : selected) {
				if (!this.checkValue(iIndex, iRow, lines[line].split("\t"), cols))
					isChecked = false;

				iIndex++;
				line++;
			}

			line = 0;

			if (isChecked) {
				for (int iRow : selected) {
					this.setValue(iRow, lines[line].split("\t"), cols);
					line++;
				}
			} else {
				return false;
			}

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取字段对应的显示值
	 * 
	 * @param row
	 *            行
	 * @param attribute
	 *            属性设置
	 * @return 数据
	 */
	public String getItemValue(int row, DataWindowAttribute attribute) {
		try {
			if (this.adw.isItemNull(row, attribute.getName())) {
				return "";
			} 
			else if (attribute.getType().equals("edit")) 
			{
				return this.adw.getItemString(row, attribute.getName());
			} 
			else if (attribute.getType().equals("dddw")) 
			{
				DataWindowInterface[] dwcs = new DataWindowInterface[1];
				if (this.adw.getChild(attribute.getName(), dwcs) != 1)
					return "";

				if (dwcs[0].rowCount() <= 0) {
					dwcs[0].setTransaction(JTransactionManager.getInstance().getTransaction(this.dbName));
					dwcs[0].retrieve();
				}

				// 因为dddw绑定的都是long的类型，所以就不判断类型了，如果将来有其他类型，还得判断
				// 偷懒了
				String colType = dwcs[0].describe(attribute.getDataCol() + ".ColType");
				String findSql = "";
				if ( colType.startsWith("char") || colType.startsWith("varchar") || colType.startsWith("nchar") || 
						colType.startsWith("nvarchar") || colType.startsWith("date") || colType.startsWith("time"))
				{
					findSql = attribute.getDataCol() + " = '" + this.adw.getItemString(row, attribute.getName()) + "'";
				}
				else
				{
					findSql = attribute.getDataCol() + " = " + this.adw.getItemString(row, attribute.getName());
				}
				int iFind = dwcs[0].find(findSql , 1, dwcs[0].rowCount());

				if (iFind > 0)
					return dwcs[0].getItemString(iFind, attribute.getTextCol());
				else
					return "";

			}
			else if (attribute.getType().equals("editmask"))
			{
				if (attribute.getColType().startsWith("date"))
				{
					return this.getItemDisplay(attribute.getName(), row);
				}
				return this.adw.getItemString(row, attribute.getName());
			}
			else if (attribute.getType().equals("checkbox"))
			{
				return this.adw.getItemString(row, attribute.getName());
			}
			else 
			{
				return this.getItemDisplay(attribute.getName(), row);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return "";
	}

	/**
	 * 检查一行数据是否符合输入格式要求
	 * 
	 * @param iIndex
	 *            粘贴的第几行
	 * @param row
	 *            当前操作的数据行
	 * @param values
	 *            要粘贴的数据
	 * @param cols
	 *            字段属性列表
	 * @return true检查通过，false检查未通过
	 */
	public boolean checkValue(int iIndex, int row, String[] values, List<DataWindowAttribute> cols) {
		try {

			for (int i = 0; i < cols.size(); i++) {
				
				if (i >= values.length - 1 )
					continue;
				
				DataWindowAttribute attribute = cols.get(i);

				if (cols.get(i).getType().equals("dddw")) {
					
					if (values[i] == null || values[i].length() <= 0)
						continue;
					
					DataWindowInterface[] dwcs = new DataWindowInterface[1];
					if (this.adw.getChild(attribute.getName(), dwcs) != 1)
						return false;

					if (dwcs[0].rowCount() <= 0) {
						dwcs[0].setTransaction(JTransactionManager.getInstance().getTransaction(this.dbName));
						dwcs[0].retrieve();
					}

					// 因为dddw绑定的都是long的类型，所以就不判断类型了，如果将来有其他类型，还得判断
					// 显示的都是string类型的
					// 偷懒了

					int iFind = 0;

					if (values[i] != null && values[i].length() > 0) {
						iFind = dwcs[0].find(attribute.getTextCol() + " = '" + values[i] + "'", 1, dwcs[0].rowCount());

						if (iFind <= 0) {
							JOptionPane.showMessageDialog(null,
									String.format("第【%d】行【%s】输入的数据和字典表中的数据不匹配", iIndex, attribute.getTitle()));
							return false;
						}
					}

				} 
				else if (attribute.getType().equals("ddlb") || attribute.getType().equals("radiobuttons")) {
					String strValues = this.adw.describe(cols.get(i).getName() + ".values");
					String[] datas = strValues.split("/");
					boolean isPass = false;
					for (int j = 0; j < datas.length; j++) {
						String[] keyvalues = datas[j].split("\t");
						if (keyvalues[0].equals(values[i])) {
							isPass = true;
							break;
						}
					}

					if (!isPass) {
						JOptionPane.showMessageDialog(null,
								String.format("第【%d】行【%s】输入的数据和字典表中的数据不匹配", iIndex, attribute.getTitle()));
						return false;
					}
				} 
				else if (attribute.getType().equals("checkbox"))
				{
					String strValues = this.adw.describe(cols.get(i).getName() + ".values");
					String[] datas = strValues.split("/");
					boolean isPass = false;
					for (int j = 0; j < datas.length; j++) {
						if (datas[j].trim().equals(values[i])) {
							isPass = true;
							break;
						}
					}

					if (!isPass) {
						JOptionPane.showMessageDialog(null,
								String.format("第【%d】行【%s】输入的数据和字典表中的数据不匹配", iIndex, attribute.getTitle()));
						return false;
					}
				}
				else 
				{
					if (attribute.getColType().startsWith("long") || attribute.getColType().startsWith("int")
							|| attribute.getColType().startsWith("dec") || attribute.getColType().startsWith("float")
							|| attribute.getColType().startsWith("double") || attribute.getColType().startsWith("money")
							|| attribute.getColType().startsWith("small")) 
					{

					}
				}
			}

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 检查一行数据是否符合输入格式要求
	 * 
	 * @param row
	 *            当前操作的数据行
	 * @param values
	 *            要粘贴的数据
	 * @param cols
	 *            字段属性列表
	 * @return true检查通过，false检查未通过
	 */
	public boolean setValue(int row, String[] values, List<DataWindowAttribute> cols) {
		try {

			for (int i = 0; i < values.length; i++) {
				DataWindowAttribute attribute = cols.get(i);

				if (cols.get(i).getType().equals("dddw")) {
					DataWindowInterface[] dwcs = new DataWindowInterface[1];
					if (this.adw.getChild(attribute.getName(), dwcs) != 1)
						return false;

					if (dwcs[0].rowCount() <= 0) {
						dwcs[0].setTransaction(JTransactionManager.getInstance().getTransaction(this.dbName));
						dwcs[0].retrieve();
					}

					// 因为dddw绑定的都是long的类型，所以就不判断类型了，如果将来有其他类型，还得判断
					// 显示的都是string类型的
					// 偷懒了

					int iFind = 0;

					if (values[i] != null && values[i].length() > 0) {
						iFind = dwcs[0].find(attribute.getTextCol() + " = '" + values[i] + "'", 1, dwcs[0].rowCount());

						this.adw.setItem(row, attribute.getName(),
								dwcs[0].getItemNumber(iFind, attribute.getDataCol()));
					} else {
						this.adw.setItem(row, attribute.getName(), "");
					}

				} 
				else if (attribute.getType().equals("ddlb") || attribute.getType().equals("radiobuttons"))
				{
					String strValues = this.adw.describe(cols.get(i).getName() + ".values");
					String[] datas = strValues.split("/");
					for (int j = 0; j < datas.length; j++) {
						String[] keyvalues = datas[j].split("\t");
						if (keyvalues[0].equals(values[i])) {
							this.adw.setItem(row, attribute.getName(), keyvalues[1]);
							break;
						}

					}
				} 
				else 
				{
					if (attribute.getColType().startsWith("long") || attribute.getColType().startsWith("int"))
					{
						try
						{
							Integer iValue = 0;
							iValue = Integer.parseInt(values[i]);
							this.adw.setItem(row, attribute.getName(), iValue);
						}
						catch(Exception ex)
						{
							
						}
					}
					else if (attribute.getColType().startsWith("dec") || attribute.getColType().startsWith("float")
							|| attribute.getColType().startsWith("double") || attribute.getColType().startsWith("money")
							|| attribute.getColType().startsWith("small"))
					{
						try
						{
							Float iValue = 0f;
							iValue = Float.parseFloat(values[i]);
							this.adw.setItem(row, attribute.getName(), iValue);
						}
						catch(Exception ex)
						{
							
						}
					}
					else if (attribute.getColType().startsWith("datetime"))
					{
						try
						{
							Date dt = dfDate.parse(values[i]);
							this.adw.setItem(row, attribute.getName(), dt);
						}
						catch(Exception ex)
						{
							
						}
					}
					else
					{
						this.adw.setItem(row, attribute.getName(), values[i]);
					}
				}
			}

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 自动加载子类数据窗口中的数据
	 */
	public void autoRetrieveDataWindowChild() {
		try {
			List<DataWindowAttribute> cols = this.getDataWindowObjects();
			for (DataWindowAttribute col : cols) {
				if (col.getType().equals("dddw")) {
					String[] split = col.getTag().split(":");
					DataWindowInterface[] dwcs = new DataWindowInterface[1];
					if (this.adw.getChild(col.getName(), dwcs) != 1)
						return;

					if (split != null && split.length == 2 && split[0].equals("dbname")) {
						dwcs[0].setTransaction(JTransactionManager.getInstance().getTransaction(split[1]));
					} else {
						dwcs[0].setTransaction(JTransactionManager.getInstance().getTransaction(this.dbName));
					}
					dwcs[0].retrieve();
					dwcs[0].insertRow(1);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 加载所有数据，同时加载所有下拉数据数据
	 */
	public void retrieve() {
		try {
			JTransactionManager.getInstance().reConnection(this.getDbName());
			this.adw.retrieve();
			autoRetrieveDataWindowChild();
		} catch (Exception ex) {
			throw ex;
		}
	}
	
	/**
	 * 代一个参数的检索方法
	 * @param arg
	 */
	public void retrieve(DWRetrieveArgs arg)
	{
		try {
			JTransactionManager.getInstance().reConnection(this.getDbName());
			this.adw.retrieve(arg);
			autoRetrieveDataWindowChild();
		} catch (Exception ex) {
			throw ex;
		}
	}
	
	/**
	 * 根据字段名称，
	 * @param name 下拉数据窗口的名称
	 * @param value 下拉数据窗口的值
	 * @exception Exception
	 */
	public void setDDDWItem(String name , Object value) throws Exception
	{
		DataWindowInterface[] dwcs = new DataWindowInterface[1];
		if (this.adw.getChild(name, dwcs) != 1)
			return;

		if (value.getClass().equals(String.class))
		{
			
		}
		else if (value.getClass().equals(Date.class))
		{
			
		}
		else if (value.getClass().equals(BigDecimal.class))
		{
			
		}
		else if( value.getClass().equals(Float.class))
		{
			
		}
		dwcs[0].find("", 1 , dwcs[0].rowCount() );
		
		dwcs[0].retrieve();
	}

	// end

	// begin 鼠标键盘事件相关

	/**
	 * 最后一次排序的列
	 */
	private String lastSortCol = "";

	/**
	 * 最后一次选择行
	 */
	private int lastSelectRow = 0;

	/**
	 * 排序类型 true asc false desc
	 */
	private volatile boolean colSort = true;

	@Override
	public void key(KeyEvent arg0) {
				
		// ctrl-a
		if (arg0.getFlags() == 2 && arg0.getKeyCode() == 1) {
			this.adw.selectRow(0, true);
		}

		// ctrl-v
		if (arg0.getFlags() == 2 && arg0.getKeyCode() == 22) {
			try {
				Clipboard sysClb = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable t = sysClb.getContents(null);

				if (null != t && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					String text = (String) t.getTransferData(DataFlavor.stringFlavor);
					if (this.setSelectValues(text))
					{
						// 如果成功，直接保存数据库
						if (JOptionPane.showConfirmDialog(null, "粘贴成功，您是否将数据保存到数据库?", "信息提示", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
							int iResult = this.adw.acceptText();
							if (iResult <= 0) {
								JOptionPane.showMessageDialog(null, "更新数据缓冲区失败");
								return;
							}

							Transaction sqlca = JTransactionManager.getInstance().getTransaction(this.dbName);

							if (this.adw.update(true, false)) {
								JOptionPane.showMessageDialog(null, "保存成功");
								sqlca.commit();
								this.adw.resetUpdate();
							} else {
								JOptionPane.showMessageDialog(null, "保存失败");
								sqlca.rollback();
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// ctrl-c
		if (arg0.getFlags() == 2 && arg0.getKeyCode() == 3) {
			try {

				Clipboard sysClb = Toolkit.getDefaultToolkit().getSystemClipboard();

				LinkedList<Integer> selected = (LinkedList<Integer>) this.getSelectList();
				String values = this.getSelectedValues(selected);
				StringSelection ss = new StringSelection(values);
				sysClb.setContents(ss, null);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		// ctrl-f
		if (arg0.getFlags() == 2 && arg0.getKeyCode() == 6 && this.search) {
			try {

				lastSearch = JOptionPane.showInputDialog(null, "请输入您要检索的内容" , lastSearch);
				
				if (lastSearch == null || lastSearch.trim().length() <= 0)
					return;
				
				if( this.searchColumn == null || this.searchColumn.trim().length() <= 0)
				{
					//查找第一个字符的字段
					List<DataWindowAttribute> visibles = this.getDataWindowObjectsVisible();
					for(DataWindowAttribute item : visibles)
					{
						if (item.getColType().startsWith("char") || item.getColType().startsWith("varchar")
								|| item.getColType().startsWith("nchar") || item.getColType().startsWith("nvarchar"))
						{
							searchColumn = item.getName();
							break;
						}
					}
				}
				
				String find = "";
				int iFind = 0;
				
				if (this.searchColumn.indexOf(",") >= 0)
				{
					String[] cols = this.searchColumn.split(",");
				
					for(int i = 0 ; i < cols.length ;i++)
					{
						if (i == 0)
							find = " pos(" + cols[i] +", '" + lastSearch + "') > 0 ";
						else
							find = find + " or pos(" + cols[i] +", '" + lastSearch + "') > 0 ";
					}
				}
				else
				{
					find =  " pos(" + searchColumn +", '" + lastSearch + "') > 0 ";
				}
				
				//从当前行位置往后找
				int iBegin = this.adw.getSelectedRow(0);
				if (iBegin <= 0 || iBegin >= this.adw.getRowCount())
					iBegin = 1;
				else 
					iBegin += 1;
				
				iFind = this.adw.find(find , iBegin, this.adw.getRowCount());
				
				if(iFind <= 0 && iBegin > 1)  
				{
					iBegin = 1;
					//如果往下找没找到，则从第一行重新找一次，没有的话，则退出
					iFind = this.adw.find(find , iBegin, this.adw.getRowCount());
				}
				
				if (iFind > 0)
				{
					this.adw.setFocus();
					this.adw.setRow(iFind);
					this.adw.selectRow(0, false);
					this.adw.selectRow(iFind, true);
					this.adw.scrollToRow(iFind);
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	/**
	 * 多行选择操作
	 */
	@Override
	public void leftButtonClick(MouseEvent arg0) {

		try {
			if (arg0.getRow() > 0)
			{
				if (this.singleSelect && !(arg0.getFlags() == MouseEvent.CONTROL_PRESSED || arg0.getFlags() == MouseEvent.SHIFT_PRESSED))
				{
					this.adw.selectRow(0, false);
					this.adw.selectRow(arg0.getRow(), true);
					this.adw.setRow(arg0.getRow());
					lastSelectRow = arg0.getRow();
				}
				
				if (this.mutliSelecte && (arg0.getFlags() == MouseEvent.CONTROL_PRESSED || arg0.getFlags() == MouseEvent.SHIFT_PRESSED)) {
					// 按下ctrl的时候执行的多行选择操作
					if (arg0.getFlags() == MouseEvent.CONTROL_PRESSED) {
						this.adw.selectRow(arg0.getRow(), !this.adw.isSelected(arg0.getRow()));
						lastSelectRow = arg0.getRow();
					} else if (arg0.getFlags() == MouseEvent.SHIFT_PRESSED && lastSelectRow <= 0) {
						lastSelectRow = arg0.getRow();
						this.adw.selectRow(lastSelectRow, true);
					} else if (arg0.getFlags() == MouseEvent.SHIFT_PRESSED && lastSelectRow > 0) {
						int iBegin = Math.min(lastSelectRow, arg0.getRow());
						int iEnd = Math.max(lastSelectRow, arg0.getRow());
	
						for (int i = iBegin; i <= iEnd; i++) {
							this.adw.selectRow(i, true);
						}
	
						lastSelectRow = arg0.getRow();
	
					} else {
						this.adw.selectRow(0, false);
						lastSelectRow = 0;
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public void leftButtonDoubleClick(MouseEvent arg0) {
		// 双击操作
		if (this.isDoubleclickedSort()) {
			try {
				if (this.adw == null)
					return;

				//getBandAtPointer()这个函数放回的是null，应该是未实现该方法
				String clickObject = this.adw.getObjectAtPointer();
				String[] object = clickObject.split("\t");
				if (object == null || object.length <= 0)
					return;

				// 如果点中的不是标签
				if (!object[0].endsWith("_t"))
					return;

				String col = clickObject.substring(0, object[0].length() - 2);
				if (col.equals(lastSortCol)) {
					if (colSort) {
						this.adw.setSort(col + " a");
					} else {
						this.adw.setSort(col + " d");
					}
					colSort = !colSort;
				} else {
					this.adw.setSort(col + " a");
					colSort = false;
				}
				lastSortCol = col;
				this.adw.sort();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	@Override
	public void leftButtonDown(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void leftButtonUp(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void middleButtonClick(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void middleButtonDoubleClick(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMove(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rightButtonClick(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rightButtonDoubleClick(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rightButtonDown(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rightButtonUp(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	// end 鼠标事件相关
	
	//begin item相关事件
	
	@Override
	public void itemChanged(ItemEvent paramItemEvent) {
		
		
	}

	@Override
	public void itemChangeAccepted(ItemEvent paramItemEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void itemFocusChanged(ItemEvent paramItemEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void itemError(ItemEvent paramItemEvent) {
		// TODO Auto-generated method stub
		
	}
	
	//end
	
	//begin 鼠标滚轮事件
	
	/**
	 * 鼠标滚轮操作
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getWheelRotation() == -1)
		{
			//向上滚动
			try {
				int iRow = this.adw.getRow();
				
				if (iRow <= e.getScrollAmount())
					return;
				
				iRow -= e.getScrollAmount();
				
				if (iRow >= this.adw.getRowCount())
					return;
				
				this.adw.scrollToRow(iRow);
				
			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
			}
		}
		else if (e.getWheelRotation() == 1)
		{
			//向下滚动
			try {
				int iRow = this.adw.getRow() + e.getScrollAmount();
				
				if (iRow >= this.adw.getRowCount())
					return;
				
				this.adw.scrollToRow(iRow);
				
			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
			}
		}
	}
	
	//end
}
