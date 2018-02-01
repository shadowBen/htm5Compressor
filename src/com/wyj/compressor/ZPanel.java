package com.wyj.compressor;

import java.awt.Graphics;  
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;  
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import sun.misc.BASE64Decoder;  
  
public class ZPanel extends javax.swing.JPanel {
    private static final long serialVersionUID = 1L;  
    private Image image;  
    private int imgWidth;  
    private int imgHeight;  
  
    public int getImgWidth() {  
        return imgWidth;  
    }  
  
    public void setImgWidth(int imgWidth) {  
        this.imgWidth = imgWidth;  
    }  
  
    public int getImgHeight() {  
        return imgHeight;  
    }  
  
    public void setImgHeight(int imgHeight) {  
        this.imgHeight = imgHeight;  
    }  
  
    public ZPanel() {
    }  
  
    public void setImagePath(String imgPath) {  
        // 该方法不推荐使用，该方法是懒加载，图像并不加载到内存，当拿图像的宽和高时会返回-1；  
        // image = Toolkit.getDefaultToolkit().getImage(imgPath);  
        try {  
            // 该方法会将图像加载到内存，从而拿到图像的详细信息。  
        	//String noPicPath = System.getProperty("user.dir")
					//+ "/bin/com/wyj/util/";
            //image = ImageIO.read(new FileInputStream(noPicPath+imgPath));
            String base64Img = "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";
            BASE64Decoder decoder = new sun.misc.BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(base64Img);
            InputStream bais = new ByteArrayInputStream(bytes);
            image = ImageIO.read(bais);
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        setImgWidth(image.getWidth(this));  
        setImgHeight(image.getHeight(this));  
    }  
  
    @Override  
    public void paintComponent(Graphics g1) {  
        int x = 0;  
        int y = 0;  
        Graphics g = (Graphics) g1;  
        if (null == image) {  
            return;  
        }  
 
        g.drawImage(image, x, y, image.getWidth(this), image.getHeight(this),  
                this);  
        g = null;  
    }  
}  