package com.wyj.compressor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Alignment;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import net.coobird.thumbnailator.Thumbnails;

public class FileChooser extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String[] quantitys = { "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%" };
	// js混淆参数
	static int linebreakpos = -1; // 换行位置 -1表示不换行
	static boolean munge = true; // 是否混淆
	static boolean verbose = false;// 显示详细信息和警告信息
	static boolean preserveAllSemiColons = false;// 保留分号
	static boolean disableOptimizations = false;// 禁用自带的所有优化措施
	static float quantity = 0.1f;// 图片压缩质量

	private JButton open = null;
	private ZPanel zPanel = null;
	private ArrayList<String> JsFile = null;
	private ArrayList<String> CssFile = null;
	private ArrayList<String> ImgFile = null;
	private JProgressBar JsPBar = null;
	private JProgressBar CssPBar = null;
	private JProgressBar ImgsPBar = null;
	private JTextArea txtLogInfo = null;
	private JCheckBox jb = null;
	@SuppressWarnings("rawtypes")
	private JComboBox jcb = null;
	private JPanel buttons = null;
	private JPanel fileRefer = null;
	private JPanel fileReferPercentage = null;
	private int JsNum = 0;
	private int CssNum = 0;
	private int ImgsNum = 0;
	private boolean JsFinishMin = false;
	private boolean CssFinishMin = false;
	private boolean ImgFinishMin = false;
	private Timer timer = null;

	public FileChooser() {
		JsFile = new ArrayList<>();
		CssFile = new ArrayList<>();
		ImgFile = new ArrayList<>();
		this.setResizable(false);
		this.setTitle("\u524d\u7aef\u4ee3\u7801\u538b\u7f29");
		this.setLayout(new BorderLayout(5, 5));
		this.setFont(new Font("Helvetica", Font.PLAIN, 14));

		buttons = new JPanel();
		buttons.setLayout(new GridLayout(3, 1));
		open = new JButton("\u9009\u62e9\u6587\u4ef6\u5939");

		// 文件相关中加入文字，进度条 ，百分比
		fileRefer = new JPanel();
		fileReferPercentage = new JPanel();
		fileRefer.setLayout(new GridLayout(3, 1));
		fileReferPercentage.setLayout(new GridLayout(3, 1));
		// 文字
		fileRefer.add(new JLabel("JavaScript"));
		fileRefer.add(new JLabel("Css"));
		fileRefer.add(new JLabel("Image"));
		// 进度条
		JsPBar = new JProgressBar();
		CssPBar = new JProgressBar();
		ImgsPBar = new JProgressBar();
		JsPBar.setStringPainted(true);
		CssPBar.setStringPainted(true);
		ImgsPBar.setStringPainted(true);
		fileReferPercentage.add(JsPBar);
		fileReferPercentage.add(CssPBar);
		fileReferPercentage.add(ImgsPBar);

		zPanel = new ZPanel();
		zPanel.setImagePath("logo.png"); // 400*200
		zPanel.setPreferredSize(new Dimension(zPanel.getImgWidth(), zPanel.getImgHeight()));

		// 日志框;
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(zPanel.getImgWidth(), zPanel.getImgHeight()));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout.createSequentialGroup().addContainerGap().addComponent(scrollPane).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE).addContainerGap()));
		txtLogInfo = new JTextArea();
		txtLogInfo.setEditable(false);
		txtLogInfo.setLineWrap(true);
		scrollPane.setViewportView(txtLogInfo);

		// 按钮panel中加入各个按钮
		jb = new JCheckBox("\u004a\u0073\u6df7\u6dc6");
		jb.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JCheckBox jcb = (JCheckBox) e.getItem();
				if (jcb.isSelected()) {
					munge = true;
				} else {
					munge = false;
				}
			}
		});
		jb.setSelected(true);

		// 图片压缩质量选择
		jcb = new JComboBox<>(quantitys);
		jcb.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String percantage = (String) jcb.getSelectedItem();
					quantity = (float) (Float.parseFloat(percantage.replaceAll("%", "")) / 100);
				}
			}
		});
		jcb.setSelectedIndex(1);
		buttons.add(open);
		buttons.add(jb);
		buttons.add(jcb);
		getContentPane().add("North", zPanel); // 将按钮添加到窗口中
		getContentPane().add("South", scrollPane);
		getContentPane().add("East", buttons);
		getContentPane().add("West", fileRefer);
		getContentPane().add("Center", fileReferPercentage);

		this.setBounds(400, 200, 100, 100);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		open.addActionListener(this);

		LogMonitor.addLogChangedListener(new LogChangedListener() {
			@Override
			public void EventActivated(LogChangedEvent me) {
				txtLogInfo.setText(LogMonitor.getLogs().toString());
				txtLogInfo.setCaretPosition(txtLogInfo.getText().length());
				txtLogInfo.paintImmediately(txtLogInfo.getBounds());
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread(new ReadFiles()).start();// 读取文件
		timer = new Timer(true);

	}

	class ReadFiles implements Runnable {
		@Override
		public void run() {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int choosed = jfc.showDialog(new JLabel(), "选择");
			//System.out.println(choosed);//会返回选择结果  JFileChooser.APPROVE_OPTION=确定  JFileChooser. CANCEL_OPTION= 取消
			if (choosed == JFileChooser.APPROVE_OPTION) {
				File file = jfc.getSelectedFile();
				if (null != file) {
					// 初始化
					JsNum = 0;
					CssNum = 0;
					ImgsNum = 0;
					JsFile.clear();
					CssFile.clear();
					ImgFile.clear();
					CssFinishMin = false;
					JsFinishMin = false;
					ImgFinishMin = false;
					Log("---------------选择文件---------------");
					// 选择了文件夹后开始计数
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							CssPBar.setString(CssNum + "/" + CssFile.size());
							JsPBar.setString(JsNum + "/" + JsFile.size());
							ImgsPBar.setString(ImgsNum + "/" + ImgFile.size());
							int jsPer = 0;
							int cssPer = 0;
							int imgPer = 0;
							if (JsFile.size() != 0) {
								jsPer = 100 * JsNum / JsFile.size();
							}
							if (CssFile.size() != 0) {
								cssPer = 100 * CssNum / CssFile.size();
							}
							if (ImgFile.size() != 0) {
								imgPer = 100 * ImgsNum / ImgFile.size();
							}
							JsPBar.setValue(jsPer);
							CssPBar.setValue(cssPer);
							ImgsPBar.setValue(imgPer);
							if (CssFinishMin && JsFinishMin && ImgFinishMin) {
								Log("---------------压缩结束---------------");
								timer.cancel();

								System.gc();
							}
						}
					}, 0, 300);
					traverseFolder2(file);
					if (file.listFiles().length == 0) {
						Log("---------------空文件夹---------------");
						CssFinishMin = true;
						JsFinishMin = true;
						ImgFinishMin = true;
					} else {
						Log("---------------压缩开始---------------");
						new Thread(new MinCss()).start();// Css压缩
						new Thread(new MinJs()).start();// Js压缩
						new Thread(new MinImg()).start();// 后期图片压缩
					}
				} else {
					Log("---------------取消选择---------------");
				}
			} else {
				Log("---------------取消选择---------------");
			}
		}
	}

	class MinCss implements Runnable {
		@Override
		public void run() {
			if (CssFile.size() > 0) {
				for (String fPath : CssFile) {
					File f = new File(fPath);
					minifyJsAndCss(f);
				}
				CssFinishMin = true;
			} else {
				CssFinishMin = true;
			}
		}
	}

	class MinJs implements Runnable {
		@Override
		public void run() {
			if (JsFile.size() > 0) {
				for (String fPath : JsFile) {
					File f = new File(fPath);
					minifyJsAndCss(f);
				}
				JsFinishMin = true;
			} else {
				JsFinishMin = true;
			}
		}
	}

	class MinImg implements Runnable {
		@Override
		public void run() {
			if (ImgFile.size() > 0) {
				for (String fPath : ImgFile) {
					try {
						File f = new File(fPath);
						BufferedImage img = ImageIO.read(f);
						Raster ra = img.getData();
						Rectangle rect = ra.getBounds();
						if (rect.width > 5000 || rect.height > 5000) {
							Log(fPath + "文件分率高过大，已自动忽略");
							continue;
						}
						Thumbnails.of(f).scale(1f).outputQuality(quantity).toFile(fPath);
						f = null;
					} catch (IOException e) {
						e.printStackTrace();
						Log(e.getMessage());
					}
					ImgsNum++;
					System.gc();
				}
				ImgFinishMin = true;
			} else {
				ImgFinishMin = true;
			}
		}
	}

	public boolean minifyJsAndCss(File file2) {// 需要压缩的file
		String fileName = file2.getName();
		Reader rd = null;
		Writer fw = null;
		if (fileName.endsWith(".css") && !fileName.endsWith(".min.css")) {
			try {
				rd = new InputStreamReader(new FileInputStream(file2), "utf-8");
				fw = new OutputStreamWriter(new FileOutputStream(file2.getAbsolutePath().replace(".css", ".min.css")),
						"utf-8");
				CssCompressor csscompressor = new CssCompressor(rd);
				csscompressor.compress(fw, linebreakpos);
				CssNum++;
				if (CssNum > 0 && CssNum > CssFile.size()) {
					CssNum = 0;
				} else {
				}
			} catch (IOException e) {
				return false;
			} finally {
				try {
					fw.close();
					rd.close();
				} catch (IOException e) {
					return false;
				}
			}
		} else if (fileName.endsWith(".js") && !fileName.endsWith(".min.js")) {
			try {
				rd = new InputStreamReader(new FileInputStream(file2), "utf-8");
				fw = new OutputStreamWriter(new FileOutputStream(file2.getAbsolutePath().replace(".js", ".min.js")),
						"utf-8");

				try {
					JavaScriptCompressor jscompressor = new JavaScriptCompressor(rd, new ErrorReporter() {
						public void warning(String message, String sourceName, int line, String lineSource,
								int lineOffset) {
							if (line < 0) {
								// System.err.println("\n[WARNING] " + message);
								Log("\n[WARNING]" + message);
							} else {
								// System.err.println("\n[WARNING] " + fileName + line + ':' + lineOffset + ':'
								// + message);
								Log("\n[WARNING] " + fileName + line + ':' + lineOffset + ':' + message);
							}
						}

						public void error(String message, String sourceName, int line, String lineSource,
								int lineOffset) {
							if (line < 0) {
								// System.err.println("\n[ERROR] " + message);
								Log("\n[ERROR]" + message);
							} else {
								// System.err.println("\n[ERROR] " + fileName + line + ':' + lineOffset + ':' +
								// message);
								Log("\n[ERROR] " + fileName + line + ':' + lineOffset + ':' + message);
							}
						}

						public EvaluatorException runtimeError(String message, String sourceName, int line,
								String lineSource, int lineOffset) {
							error(message, sourceName, line, lineSource, lineOffset);
							return new EvaluatorException(message);
						}
					});
					jscompressor.compress(fw, linebreakpos, munge, verbose, preserveAllSemiColons,
							disableOptimizations);
					JsNum++;
					if (JsNum > 0 && JsNum > JsFile.size()) {
						JsNum = 0;
					} else {
					}
				} catch (Exception e) {
					// System.out.println(e.getMessage());
					return false;
				}
			} catch (IOException e) {
				return false;
			} finally {
				try {
					fw.close();
					rd.close();
				} catch (IOException e) {
					return false;
				}
			}
		} else {
			// 不是js和css
		}
		return true;
	}

	public void traverseFolder2(File file) {
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				// System.out.println("文件夹是空的!");
				return;
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						// System.out.println("文件夹:" + file2.getAbsolutePath());
						traverseFolder2(file2);
					} else {
						// minifyJsAndCss(file2);
						String fileName = file2.getName();
						if (fileName.endsWith(".css") && !fileName.endsWith(".min.css")) {
							CssFile.add(file2.getAbsolutePath());
							Log("读取Css：" + file2.getName());
						} else if (fileName.endsWith(".js") && !fileName.endsWith(".min.js")) {
							JsFile.add(file2.getAbsolutePath());
							Log("读取 Js：" + file2.getName());
						} else {
							// System.out.println(file2.getName());
							try {
								BufferedImage image = ImageIO.read(file2);
								if (image == null) {
									Log("忽略文件：" + file2.getName());
								} else {
									ImgFile.add(file2.getAbsolutePath());
									Log("读取图片：" + file2.getName());
								}
							} catch (IOException ex) {
								Log("忽略文件：" + file2.getName()+"  图片格式有可能为cmyk格式，请将图片保存为rpg格式");
							}
						}
					}
				}
			}
		} else {
			// System.out.println("文件不存在!");
		}
	}

	private void Log(String logs) {
		LogMonitor.addLog(logs);
	}
}