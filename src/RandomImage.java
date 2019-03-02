import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.function.Function;

import javax.imageio.ImageIO;


class RandomImage extends BufferedImage {
	private BufferedImage brush;

	private Function<Integer, Integer> xFunction;
	private Function<Integer, Integer> yFunction;

	private float startHue;
	private float endHue;
	private float startSaturation;
	private float endSaturation;
	private float startBrightness;
	private float endBrightness;
	private float startOpacity;
	private float endOpacity;

	private int xOffset;
	private int yOffset;

	public RandomImage(int width, int height) {
		super(width, height, BufferedImage.TYPE_INT_RGB);
	}

	public void setBrush(String filename) {
		try {
			brush = ImageIO.read(new File("brush/" + filename));
		} catch (IOException ex) {
			// do nothing :(
		}
	}

	public void setXFunction(String filter) {
		this.xFunction = findFilter(filter);
	}

	public void setYFunction(String filter) {
		this.yFunction = findFilter(filter);
	}

	public void setHue(float startHue, float endHue) {
		this.startHue = startHue;
		this.endHue = endHue;
	}

	public void setSaturation(float startSaturation, float endSaturation) {
		this.startSaturation = startSaturation;
		this.endSaturation = endSaturation;
	}

	public void setBrightness(float startBrightness, float endBrightness) {
		this.startBrightness = startBrightness;
		this.endBrightness = endBrightness;
	}

	public void setOpacity(float startOpacity, float endOpacity) {
		this.startOpacity = startOpacity;
		this.endOpacity = endOpacity;
	}

	public void setOffset(int x, int y) {
		this.xOffset = x;
		this.yOffset = y;
	}

	private Function<Integer, Integer> findFilter(String filter) {
		switch (filter) {
			case "identity":
			return x -> x;
			case "sine":
			return x -> (int)Math.floor(Math.sin((x / (this.getWidth() / 2.0)) * Math.PI) * this.getWidth());
			case "cosine":
			return x -> (int)Math.floor(Math.cos(x));
			case "tangent":
			return x -> (int)Math.floor(Math.tan(x));
			case "square":
			return x -> (int)Math.floor(Math.pow(x, 2));
			case "cube":
			return x -> (int)Math.floor(Math.pow(x, 3));
			case "square root":
			return x -> (int)Math.floor(Math.sqrt(x));
			case "cube root":
			return x -> (int)Math.floor(Math.cbrt(x));
			case "logarithm":
			return x -> (int)Math.floor(Math.log(x));
			case "random":
			return x -> (int)(Math.random() * getHeight());
			case "custom":
			return x -> (x % 7 == 0) ? x : -1;
			default:
			return x -> x;
		}
	}

	public void randomize() {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				int applyX = xFunction.apply(x + xOffset) - xOffset;
				int applyY = yFunction.apply(y + yOffset) - yOffset;
				if (applyX < getWidth() && applyX > 0 && applyY < getHeight() && applyY > 0) {
					this.drawAtPoint(applyX, applyY, getRandomColor());
				}
			}
		}
	}

	private void drawAtPoint(int x, int y, Color color) {
		int brushWidth = this.brush.getWidth();
		int brushHeight = this.brush.getHeight();

		int xCenter = brushWidth / 2;
		int yCenter = brushHeight / 2;

		for (int i = 0; i < brushWidth; i++) {
			for (int j = 0; j < brushHeight; j++) {

				int relativeX = x + (i - xCenter);
				int relativeY = y + (j - yCenter);

				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();
				int alpha = (this.brush.getRGB(i, j)>>24) & 0xff;

				alpha = (int)(255 * (alpha / 255.0) * (color.getAlpha() / 255.0));

				try {
					Color pixelColor = new Color(red, green, blue, alpha);
					Color existingColor = new Color(this.getRGB(relativeX, relativeY));
					Color newColor = blend(pixelColor, existingColor);

					this.setRGB(relativeX, relativeY, newColor.getRGB());
				} catch (ArrayIndexOutOfBoundsException ex) {
					// do nothing :(
				}
			}
		}
	}

	static Color blend(Color above, Color below) {
		double alpha = above.getAlpha() / 255.0;
		int red = mix(above.getRed(), below.getRed(), alpha);
		int green = mix(above.getGreen(), below.getGreen(), alpha);
		int blue = mix(above.getBlue(), below.getBlue(), alpha);

		return new Color(red, green, blue);
	}

	static int mix(int topComponent, int bottomComponent, double opacity) {
		return (int)(topComponent * opacity + bottomComponent * (1 - opacity));
	}

	private Color getRandomColor() {
		float h[] = {startHue, endHue};
		float s[] = {startSaturation, endSaturation};
		float b[] = {startBrightness, endBrightness};
		float a[] = {startOpacity, endOpacity};
		return getRandomColor(h, s, b, a);
	}

	static Color getRandomColor(float h[], float s[], float b[], float a[]) {
		Random r = new Random();

		h[0] /= 360;
		h[1] /= 360;
		s[0] /= 100;
		s[1] /= 100;
		b[0] /= 100;
		b[1] /= 100;
		a[0] /= 100;
		a[1] /= 100;

		float hue = h[0] + r.nextFloat() * (h[1] - h[0]);
		float saturation = s[0] + r.nextFloat() * (s[1] - s[0]);
		float brightness = b[0] + r.nextFloat() * (b[1] - b[0]);
		float alpha = a[0] + r.nextFloat() * (a[1] - a[0]);

		Color hsbColor = Color.getHSBColor(hue, saturation, brightness);

		int red = hsbColor.getRed();
		int green = hsbColor.getGreen();
		int blue = hsbColor.getBlue();

		return new Color(red, green, blue, (int)(alpha * 255.0));
	}

	// TODO: Implement setting background color
	public void setBackground(Color color) {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				this.setRGB(x, y, color.getRGB());
			}
		}
	}
}
