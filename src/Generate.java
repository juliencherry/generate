import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

public class Generate extends Application {
  static int width = 0;
  static int height = 0;
  static String extension = "";
  static String filename = "";
  static ImageView imageView = new ImageView();

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage mainStage) {
    mainStage.setTitle("Generate Image");
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(25, 25, 25, 25));

    // labels, text fields, etc.

    Label widthLabel = new Label("width: ");
    TextField widthText = new TextField();
    Label heightLabel = new Label("height: ");
    TextField heightText = new TextField();
    Label filenameLabel = new Label("filename: ");
    TextField filenameText = new TextField();
    ChoiceBox extensionChoice = new ChoiceBox();

    Label xLabel = new Label("x function: ");
    ChoiceBox xChoice = new ChoiceBox();
    Label yLabel = new Label("y function: ");
    ChoiceBox yChoice = new ChoiceBox();
    Label hueLabel = new Label("hue range: ");
    TextField startHue = new TextField();
    TextField endHue = new TextField();
    Label saturationLabel = new Label("saturation range: ");
    TextField startSaturation = new TextField();
    TextField endSaturation = new TextField();
    Label brightnessLabel = new Label("brightness range: ");
    TextField startBrightness = new TextField();
    TextField endBrightness = new TextField();
    Label offset = new Label("offset (x, y): ");
    TextField xOffset = new TextField();
    TextField yOffset = new TextField();

    Label brushLabel = new Label("brush: ");
    ChoiceBox brushChoice = new ChoiceBox();

    Button button = new Button("Generate!");

    widthText.setText("512");
    heightText.setText("512");
    filenameText.setText("image");

    extensionChoice.getItems().addAll(".bmp", ".gif", ".jpg", ".png");
    extensionChoice.setValue(".gif");

    xChoice.getItems().addAll("none", "identity", "sine", "cosine", "tangent", "square", "cube",
            "logarithm", "square root", "cube root", "random", "custom");
    xChoice.setValue("identity");
    yChoice.getItems().addAll("none", "identity", "sine", "cosine", "tangent", "square", "cube",
            "logarithm", "square root", "cube root", "random", "custom");
    yChoice.setValue("identity");

    try (Stream<Path> paths = Files.walk(Paths.get("brush"))) {
      paths.forEach(filename -> {
        if (Files.isRegularFile(filename)) {
          brushChoice.getItems().add(filename.toString().substring(6));
        }
      });
    } catch (Exception e) {
		// do nothing :(
    }

    startHue.setText("0");
    endHue.setText("360");

    startSaturation.setText("0");
    endSaturation.setText("100");

    startBrightness.setText("0");
    endBrightness.setText("100");

    xOffset.setText("0");
    yOffset.setText("0");

    brushChoice.setValue("pixel.png");

    grid.add(widthLabel, 0, 1);
    grid.add(widthText, 1, 1);
    grid.add(heightLabel, 0, 2);
    grid.add(heightText, 1, 2);
    grid.add(filenameLabel, 0, 3);
    grid.add(filenameText, 1, 3);
    grid.add(extensionChoice, 2, 3);
    grid.add(xLabel, 0, 4);
    grid.add(xChoice, 1, 4);
    grid.add(yLabel, 0, 5);
    grid.add(yChoice, 1, 5);
    grid.add(hueLabel, 0, 6);
    grid.add(startHue, 1, 6);
    grid.add(endHue, 2, 6);
    grid.add(saturationLabel, 0, 7);
    grid.add(startSaturation, 1, 7);
    grid.add(endSaturation, 2, 7);
    grid.add(brightnessLabel, 0, 8);
    grid.add(startBrightness, 1, 8);
    grid.add(endBrightness, 2, 8);
    grid.add(offset, 0, 9);
    grid.add(xOffset, 1, 9);
    grid.add(yOffset, 2, 9);

    grid.add(brushLabel, 0, 10);
    grid.add(brushChoice, 1, 10);

    grid.add(button, 1, 11);

    button.setOnAction((ActionEvent ae) -> {

        width = Integer.parseInt("" + widthText.getCharacters());
        height = Integer.parseInt("" + heightText.getCharacters());
        extension = "" + extensionChoice.getValue();
        extension = extension.substring(1, extension.length());
        filename = filenameText.getCharacters() + "." + extension;
        File output = new File("img/" + filename);
        RandomImage image = new RandomImage(width, height);

        image.setXFunction(xChoice.getValue().toString());
        image.setYFunction(yChoice.getValue().toString());
        image.setHue(Float.parseFloat(startHue.getText()), Float.parseFloat(endHue.getText()));
        image.setSaturation(Float.parseFloat(startSaturation.getText()), Float.parseFloat(endSaturation.getText()));
        image.setBrightness(Float.parseFloat(startBrightness.getText()), Float.parseFloat(endBrightness.getText()));
        image.setOffset(Integer.parseInt(xOffset.getText()), Integer.parseInt(yOffset.getText()));
        image.setBrush(brushChoice.getValue().toString());
        // image.setBackground(Color.WHITE);
        image.randomize();

        try {
          ImageIO.write(image, extension, output);
          Image load = new Image("file:" + output);
          imageView.setImage(load);
          imageView.setPreserveRatio(true);

          GridPane imageGrid = new GridPane();
          imageGrid.setAlignment(Pos.CENTER);
          imageGrid.add(imageView, 0, 0);
          imageView.fitWidthProperty().bind(imageGrid.widthProperty());

          Stage imageStage = new Stage();
          Scene imageScene = new Scene(imageGrid, width, height);
          imageStage.setScene(imageScene);
          imageStage.show();
        } catch (IOException ex) {
          // do nothing :(
        }
    });

    Scene mainScene = new Scene(grid, 512, 512);
    mainStage.setScene(mainScene);
    mainStage.show();
  }
}