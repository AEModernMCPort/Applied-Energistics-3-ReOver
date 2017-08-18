import code.elix_x.excomms.color.RGBA;
import hall.collin.christopher.math.noise.DefaultFractalNoiseGenerator2D;
import hall.collin.christopher.math.noise.FractalNoiseGenerator2D;
import hall.collin.christopher.math.random.DefaultRandomNumberGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PerlinNoiseTesting {

	static final int IWIDTH = 512;
	static final int IHEIGHT = 256;
	static final double DWIDTH = IWIDTH;
	static final double DHEIGHT = IHEIGHT;
	static final double PRECISION = 0.1d;

	public static void main(String[] args){
		JFrame frame = new JFrame("PerlinNoiseTesting");
		frame.setContentPane(new PerlinNoiseTesting().root);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private JPanel imagePanel;
	private JSlider initScale;
	private JSlider initMag;
	private JSlider scaleMul;
	private JSlider magMul;
	private JCheckBox twoD;
	private JSpinner xPS;
	private JSlider yExp;
	private JPanel root;
	private JTextPane yExpVal;
	private JTextPane initScaleVal;
	private JTextPane initMagVal;
	private JTextPane scaleMulVal;
	private JTextPane magMulVal;

	private long lastUpdate = System.currentTimeMillis();
	private double time;

	private void createUIComponents(){
		BufferedImage image = new BufferedImage(IWIDTH, IHEIGHT, BufferedImage.TYPE_INT_ARGB);
		imagePanel/*.add(*/=new JPanel(){

			@Override
			protected void paintComponent(Graphics g){
				updateImage(image);
				g.clearRect(0, 0, IWIDTH, IHEIGHT);
				g.drawImage(image, 0, 0, this);
			}

		}/*)*/;
		imagePanel.setSize(IWIDTH, IHEIGHT);
		new Timer(100, e -> imagePanel.repaint()).start();
		new Timer(2000, e -> {
			if(yExpVal != null){
				yExpVal.setText(String.valueOf(yExp.getValue()));

				initScaleVal.setText(String.valueOf(initScale.getValue()));
				initMagVal.setText(String.valueOf(initMag.getValue()));
				scaleMulVal.setText(String.valueOf(scaleMul.getValue()));
				magMulVal.setText(String.valueOf(magMul.getValue()));
			}
		}).start();
	}

	private void updateImage(BufferedImage image){
		FractalNoiseGenerator2D noise = new DefaultFractalNoiseGenerator2D(initScale.getValue() / 1000d, initMag.getValue() / 1000d, scaleMul.getValue() / 1000d, magMul.getValue() / 1000d, new DefaultRandomNumberGenerator(256));
		BiFunction<Double, Double, Double> noiseF = (x, y) -> Math.pow(noise.valueAt(PRECISION, x, y), yExp.getValue() / 1000d);
		long tdelta = System.currentTimeMillis() - lastUpdate;
		lastUpdate += tdelta;
		time += (int) xPS.getValue() * (tdelta / 1000d);
		for(int x = 0; x < IWIDTH; x++)
			for(int y = 0; y < IHEIGHT; y++)
				image.setRGB(x, y, getColor(x, y, noiseF).argb());
	}

	private RGBA getColor(int x, int y, BiFunction<Double, Double, Double> noise){
		double lx = x / DWIDTH;
		double ly = 1 - y / DHEIGHT;
		double xCoord = x + time;
		if(!twoD.isSelected()){
			double res = noise.apply(xCoord, 0d);
			if(ly <= res) return new RGBA(0, (float) ly, 1 - (float) ly);
			else return new RGBA(0, 0, 0, 0);
		} else {
			double res = noise.apply(xCoord, (double) y);
			return new RGBA(0, (float) res, 1f - (float) res);
		}
	}

}
