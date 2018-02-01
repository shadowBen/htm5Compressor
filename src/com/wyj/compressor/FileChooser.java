package com.wyj.compressor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
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
	String[] quantitys = {"10%","20%","30%","40%","50%","60%","70%","80%","90%"};
	//js��������
	static int linebreakpos = -1;  //����λ��  -1��ʾ������
	static boolean munge = true;   //�Ƿ���� 
	static boolean verbose = false;//��ʾ��ϸ��Ϣ�;�����Ϣ
	static boolean preserveAllSemiColons = false;//�����ֺ�
	static boolean disableOptimizations = false;//�����Դ��������Ż���ʩ
	static float quantity = 0.1f;//ͼƬѹ������
	
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
		this.setTitle("\u524d\u7aef\u6587\u4ef6\u538b\u7f29");
		this.setLayout(new BorderLayout(5, 5));
		this.setFont(new Font("Helvetica", Font.PLAIN, 14));

		buttons = new JPanel();
		buttons.setLayout(new GridLayout(3,1));
		open = new JButton("\u9009\u62e9\u6587\u4ef6\u5939");

		// �ļ�����м������֣������� ���ٷֱ�
		fileRefer = new JPanel();
		fileReferPercentage = new JPanel();
		fileRefer.setLayout(new GridLayout(3, 1));
		fileReferPercentage.setLayout(new GridLayout(3, 1));
		// ����
		fileRefer.add(new JLabel("JavaScript"));
		fileRefer.add(new JLabel("Css"));
		fileRefer.add(new JLabel("Image"));
		// ������
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
		
		//��־��;
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(zPanel.getImgWidth(), zPanel.getImgHeight()));
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane)
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                    .addContainerGap())
        );
        txtLogInfo = new JTextArea();
        txtLogInfo.setEditable(false);
        txtLogInfo.setLineWrap(true);
        scrollPane.setViewportView(txtLogInfo);
		
		// ��ťpanel�м��������ť
        jb =new JCheckBox("\u004a\u0073\u6df7\u6dc6");
        jb.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JCheckBox jcb = (JCheckBox) e.getItem();
				if(jcb.isSelected()){
					munge = true;
				}else{
					munge = false;
				}
			}
		});
        jb.setSelected(true);
        
        //ͼƬѹ������ѡ��
        jcb = new JComboBox<>(quantitys);
        jcb.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
	                 String percantage=(String)jcb.getSelectedItem();
	                 quantity = (float) (Float.parseFloat(percantage.replaceAll("%", ""))/100);
	             }
			}
		});
        jcb.setSelectedIndex(4);
		buttons.add(open);
		buttons.add(jb);
		buttons.add(jcb);
		getContentPane().add("North", zPanel); // ����ť��ӵ�������
//		getContentPane().add("South", open);
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
		new Thread(new ReadFiles()).start();// ��ȡ�ļ�
		timer = new Timer(true);
		
	}

	class ReadFiles implements Runnable {
		@Override
		public void run() {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			jfc.showDialog(new JLabel(), "ѡ��");

			File file = jfc.getSelectedFile();
			if (null != file) {
				if (file.isDirectory()) {
					//��ʼ��
					JsNum = 0;
					CssNum = 0;
					ImgsNum = 0;
					JsFile.clear();
					CssFile.clear();
					ImgFile.clear();
					CssFinishMin = false;
					JsFinishMin = false;
					ImgFinishMin = false;
					Log("---------------ѡ���ļ�---------------");
					//ѡ�����ļ��к�ʼ����
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							CssPBar.setString(CssNum + "/" + CssFile.size());
							JsPBar.setString(JsNum + "/" + JsFile.size());
							ImgsPBar.setString(ImgsNum + "/" + ImgFile.size());
							int jsPer = 0;
							int cssPer = 0;
							int imgPer =0;
							if(JsFile.size() != 0){
								jsPer = 100*JsNum/JsFile.size();
							}
							if(CssFile.size() != 0){
								cssPer = 100*CssNum/CssFile.size();
							}
							if(ImgFile.size() != 0){
								imgPer = 100*ImgsNum/ImgFile.size();
							}
							JsPBar.setValue(jsPer);
							CssPBar.setValue(cssPer);
							ImgsPBar.setValue(imgPer);
							if(CssFinishMin && JsFinishMin && ImgFinishMin){
								Log("---------------ѹ������---------------");
								timer.cancel();
							}
						}
					}, 0, 300);
					traverseFolder2(file);
				} else if (file.isFile()) {
				}
				Log("---------------ѹ����ʼ---------------");
				new Thread(new MinCss()).start();//Cssѹ��
				new Thread(new MinJs()).start();//Jsѹ��
				new Thread(new MinImg()).start();//����ͼƬѹ��
			} else {
				Log("---------------�ļ�Ϊ��---------------");
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
			}else{
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
			}else{
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
						int index = fPath.lastIndexOf("\\");
						Thumbnails.of(fPath) 
						.scale(1f) 
						.outputQuality(quantity) 
						.toFile(fPath.substring(0, index+1)+"small-"+fPath.substring(index+1));
					} catch (IOException e) {
						e.printStackTrace();
					}
					ImgsNum++;
				}
				ImgFinishMin = true;
			}else{
				ImgFinishMin = true;
			}
		}
	}
	

	public boolean minifyJsAndCss(File file2) {// ��Ҫѹ����file
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
//								System.err.println("\n[WARNING] " + message);
								Log("\n[WARNING]"+message);
							} else {
//								System.err.println("\n[WARNING] " + fileName + line + ':' + lineOffset + ':' + message);
								Log("\n[WARNING] " + fileName + line + ':' + lineOffset + ':' + message);
							}
						}
						public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
							if (line < 0) {
								//System.err.println("\n[ERROR] " + message);
								Log("\n[ERROR]"+message);
							} else {
								//System.err.println("\n[ERROR] " + fileName + line + ':' + lineOffset + ':' + message);
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
//					System.out.println(e.getMessage());
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
			// ����js��css
		}
		return true;
	}

	public void traverseFolder2(File file) {
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				// System.out.println("�ļ����ǿյ�!");
				return;
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						// System.out.println("�ļ���:" + file2.getAbsolutePath());
						traverseFolder2(file2);
					} else {
						// minifyJsAndCss(file2);
						String fileName = file2.getName();
						if (fileName.endsWith(".css") && !fileName.endsWith(".min.css")) {
							CssFile.add(file2.getAbsolutePath());
							Log("��ȡCss��"+file2.getName());
						} else if (fileName.endsWith(".js") && !fileName.endsWith(".min.js")) {
							JsFile.add(file2.getAbsolutePath());
							Log("��ȡ Js��"+file2.getName());
						} else {
//							System.out.println(file2.getName());
							try {  
								BufferedImage image = ImageIO.read(file2);  
							    if (image == null) {
							        Log("�����ļ���"+file2.getName());
							    }else{
									ImgFile.add(file2.getAbsolutePath());
									Log("��ȡͼƬ��"+file2.getName());
							    }
							} catch(IOException ex) {  
								Log("�����ļ���"+file2.getName());
							}  
						}
					}
				}
			}
		} else {
//			System.out.println("�ļ�������!");
		}
	}
	
	private void Log(String logs) {
        LogMonitor.addLog(logs);
    }
}