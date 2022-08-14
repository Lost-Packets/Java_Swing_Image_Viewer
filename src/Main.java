import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.geom.AffineTransform;

public class Main{
    // Mouse View Module
    public static class MouseViews extends JPanel{
        protected JTextArea Red, Green, Blue, Alpha, X, Y, iWidth, iHeight;
        public void setRed(String str){
            this.Red.setText("Red: " + str);
        }
        public void setGreen(String str){
            this.Green.setText("Green: " + str);
        }
        public void setBlue(String str){
            this.Blue.setText("Blue: " + str);
        }
        public void setAlpha(String str){
            this.Alpha.setText("Alpha: " + str);
        }
        public void setX(String str){
            this.X.setText("X: " + str);
        }
        public void setY(String str){
            this.Y.setText("Y: " + str);
        }
        public void setIWidth(String str){
            this.iWidth.setText("Image Width: " + str);
        }
        public void setIHeight(String str){
            this.iHeight.setText("Image Height: " + str);
        }
        public JPanel getPanel(){
            JPanel panel = new JPanel();
            panel.setBounds(320,10,300,100);
            panel.setBackground(Color.lightGray);

            Red = new JTextArea("Red: ");
            Green = new JTextArea("Green: ");
            Blue = new JTextArea("Blue: ");
            Alpha = new JTextArea("Alpha: ");
            X = new JTextArea("X: ");
            Y = new JTextArea("Y: ");
            iHeight = new JTextArea("Image Height: ");
            iWidth = new JTextArea("Image Width: ");

            Red.setBackground(Color.lightGray);
            Green.setBackground(Color.lightGray);
            Blue.setBackground(Color.lightGray);
            Alpha.setBackground(Color.lightGray);
            X.setBackground(Color.lightGray);
            Y.setBackground(Color.lightGray);
            iWidth.setBackground(Color.lightGray);
            iHeight.setBackground(Color.lightGray);

            panel.add(Red); panel.add(Green);
            panel.add(Blue); panel.add(Alpha);
            panel.add(X); panel.add(Y);
            panel.add(iWidth); panel.add(iHeight);
            return panel;
        }

    }
    // Image View
    public static class ImageView extends JPanel implements ChangeListener{
        // Vars
        protected BufferedImage image;
        protected JSlider imageSlider;
        public BufferedImage getBufImage(){
            return this.image;
        }
        public void getImage(File imageFile) {
            try {
                image = ImageIO.read(imageFile);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g.create();

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            int x = (getWidth() - image.getWidth()) / 2;
            int y = (getHeight() - image.getHeight()) / 2;

            AffineTransform imageAT = new AffineTransform();
            imageAT.setToRotation(this.getAngle(), x + (imageWidth / 2.0), y + (imageHeight / 2.0));

            imageAT.translate(x, y);
            g2.drawRenderedImage(image, imageAT);

            g2.dispose();
        }

        public ImageView(){
            this.setLayout(new BorderLayout());

            // Init Slider
            imageSlider = new JSlider();
            imageSlider.setMinimum(0);
            imageSlider.setMaximum(360);
            imageSlider.setMinorTickSpacing(5);
            imageSlider.setMajorTickSpacing(20);
            imageSlider.setPaintTicks(true);
            imageSlider.setValue(0);
            imageSlider.addChangeListener(this);
            this.add(imageSlider, BorderLayout.SOUTH);
        }

        @Override
        public void stateChanged(ChangeEvent event){
            JComponent source = (JComponent) event.getSource();
            if(source == imageSlider){
                this.repaint(); // Repaint JPanel
            }
        }
        public double getAngle() {
            // Angle needs to be radians
            return Math.toRadians(imageSlider.getValue());
        }
    }

    // Frame
    public static class Frame extends JFrame implements ActionListener, MouseMotionListener{
        protected File imageFile;
        protected JMenuItem openImage, quitItem, helpItem;
        protected Container content = getContentPane();
        protected static final int DISPLAY_WIDTH = 650, DISPLAY_HEIGHT = 400;

        // Class Implementations
        MouseViews mouse = new MouseViews();
        ImageView ImagePanel = new ImageView();

        @Override
        public void actionPerformed(ActionEvent event){
            JComponent source = (JComponent) event.getSource();
            // JMenu
            if (source == openImage){
                JFileChooser chooser = new JFileChooser("./");
                int retVal = chooser.showOpenDialog(this);
                if (retVal == JFileChooser.APPROVE_OPTION){
                    imageFile = chooser.getSelectedFile();
                    content.add(ImagePanel, BorderLayout.CENTER);
                    ImagePanel.getImage(imageFile);
                    this.setSize((ImagePanel.getBufImage().getWidth())+14, (ImagePanel.getBufImage().getHeight())+81);
                    this.revalidate(); // Revalidate Panel So Image Shows
                }
            }

            if(source == helpItem){
                JOptionPane.showMessageDialog(null, """
                        (Open an image file from the file menu.)\s
                        (Once the image has been opened a slider will appear allowing image rotation.)\s
                        (Window will resize to image dimensions.)\s
                        Warning: (RGB only shows correct values when image is in original position either 0 or 360 degrees.)\s
                        Have Fun!""");
            }

            if(source == quitItem){
                System.out.println("Quitting ...");
                System.exit(0);
            }
        }
        public Frame(){
            super("Image Viewer");
            // Menu
            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");

            this.setJMenuBar(menuBar);
            menuBar.add(fileMenu);

            openImage = new JMenuItem("Open Image");
            quitItem = new JMenuItem("Quit Program");
            helpItem = new JMenuItem("Help");

            openImage.addActionListener(this);
            helpItem.addActionListener(this);
            quitItem.addActionListener(this);

            fileMenu.add(openImage);
            fileMenu.add(helpItem);
            fileMenu.add(quitItem);

            // Mouse View
            content.add(mouse.getPanel(), BorderLayout.SOUTH);
            this.addMouseMotionListener(this);

            // Frame Settings
            this.setResizable(false);
            this.setSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            this.setVisible(true);
        }

        @Override
        public void mouseDragged(MouseEvent event){}
        @Override
        public void mouseMoved(MouseEvent event){
            int x = event.getX();
            int y = event.getY();
            mouse.setX(Integer.toString(x));
            mouse.setY(Integer.toString(y));
            if(ImagePanel.getBufImage() != null){
                mouse.setIHeight(Integer.toString(ImagePanel.getBufImage().getHeight()));
                mouse.setIWidth(Integer.toString(ImagePanel.getBufImage().getWidth()));
                try{
                    int rgba = ImagePanel.getBufImage().getRGB(event.getX(), event.getY());
                    Color color = new Color(rgba, true);
                    mouse.setRed(Integer.toString(color.getRed()));
                    mouse.setGreen(Integer.toString(color.getGreen()));
                    mouse.setBlue(Integer.toString(color.getBlue()));
                    mouse.setAlpha(Integer.toString(color.getAlpha()));
                    mouse.repaint();
                }catch(Throwable e){
                    mouse.setRed("0");
                    mouse.setGreen("0");
                    mouse.setBlue("0");
                    mouse.setAlpha("0");
                }
            }
        }
    }

    // Main
    public static void main(String[] args) {
        new Frame();
    }
}
