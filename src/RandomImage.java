import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.function.Function;

import javax.imageio.ImageIO;


class RandomImage extends BufferedImage {
  private Function<Integer, Integer> xFunction;
  private Function<Integer, Integer> yFunction;
  private int xOffset;
  private int yOffset;

  private float startHue;
  private float endHue;
  private float startSaturation;
  private float endSaturation;
  private float startBrightness;
  private float endBrightness;

  private BufferedImage brush;

  public RandomImage(int width, int height) {
    super(width, height, BufferedImage.TYPE_INT_RGB);
  }

  public void setBrush(String filename) {
    try {
      brush = ImageIO.read(new File("brush/" + filename));
    } catch (IOException ex) {
      // Do nothing. :(
    }
  }

  public void setXFunction(String filter) {
    this.xFunction = findFilter(filter);
  }

  public void setYFunction(String filter) {
    this.yFunction = findFilter(filter);
  }

  public void setOffset(int x, int y) {
    this.xOffset = x;
    this.yOffset = y;
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
        // return x -> (int)(Math.random() * getHeight() * Math.sin(x));
        return x -> (x % 7 == 0) ? x : -1;
      default:
        return x -> x;
    }
  }

  private double toRadians(int point) {
    return 0.0;
  }

  public void setBackground(Color color) {
    for (int x = 0; x < getWidth(); x++) {
      for (int y = 0; y < getHeight(); y++) {
        this.setRGB(x, y, color.getRGB());
      }
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

  private Color getRandomColor() {
    float h[] = {startHue, endHue};
    float s[] = {startSaturation, endSaturation};
    float b[] = {startBrightness, endBrightness};
    return getRandomColor(h, s, b);
  }

    static Color getRandomColor(float h[], float s[], float b[]) {
      Random r = new Random();

      h[0] /= 360;
      h[1] /= 360;
      s[0] /= 100;
      s[1] /= 100;
      b[0] /= 100;
      b[1] /= 100;

      float hue = h[0] + r.nextFloat() * (h[1] - h[0]);
      float saturation = s[0] + r.nextFloat() * (s[1] - s[0]);
      float brightness = b[0] + r.nextFloat() * (b[1] - b[0]);

      return Color.getHSBColor(hue, saturation, brightness);
    }

    private void drawAtPoint(int x, int y, Color color) {
      int brushWidth = this.brush.getWidth();
      int brushHeight = this.brush.getHeight();

      int xCenter = brushWidth / 2;
      int yCenter = brushHeight / 2;

      for (int i = 0; i < brushWidth; i++) {
        for (int j = 0; j < brushHeight; j++) {

          int red = color.getRed();
          int green = color.getGreen();
          int blue = color.getBlue();
          int alpha = (this.brush.getRGB(i, j)>>24) & 0xff;
          Color pixelColor = new Color(red, green, blue, alpha); // todo: alpha not working?!

          try {
            if (alpha == 255) { // temporary fix for above todo
              this.setRGB(x + (i - xCenter), y + (j - yCenter), pixelColor.getRGB());
            }

          } catch (ArrayIndexOutOfBoundsException ex) {
            // do nothing
          }
        }
      }
    }
}