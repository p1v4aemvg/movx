package com.by.movx.ui;

import com.by.movx.Common;
import com.by.movx.entity.Actor;
import com.by.movx.entity.Actors;
import com.by.movx.entity.FilmActor;
import com.by.movx.event.PickUpdateEvent;
import com.by.movx.repository.ActorRepository;
import com.by.movx.repository.FilmActorRepository;
import com.by.movx.ui.common.ActorFilmsPanel;
import com.by.movx.ui.common.PickActorPanel;
import com.by.movx.utils.MovableRect;
import com.by.movx.utils.URLFetcher;
import com.google.common.eventbus.Subscribe;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by movx
 * on 10.02.2016.
 */

public class ActorController {

    @FXML
    private TableView<Actor> actors;

    @FXML
    AnchorPane pane, paneL, pickActor, imgPane;

    @FXML
    Label markLabel;

    @Inject
    FilmActorRepository filmActorRepository;

    @Inject
    ActorRepository actorRepository;

    @FXML
    Slider part;

    @FXML
    TextField actor, partName, textUrl, born;

    @FXML
    ImageView img;

    @FXML
    private CheckBox specBox;

    private Actor current;

    private List<Actor> dataAll;

    private MovableRect r;

    @FXML
    private Button plBtn, minBtn, captureBtn, getBtn, rand, spec;

    @FXML
    ToggleButton union, intersection;

    @FXML
    CheckBox edit;

    private boolean isEdit = false;

    Actors actorsToPick = new Actors();

    @Inject
    URLFetcher fetcher;

    private ActorFilmsPanel.Mode mode = ActorFilmsPanel.Mode.UNION;

    @PostConstruct
    public void post() {
        r = new MovableRect(45);
        r.setVisible(false);
        imgPane.getChildren().add(r);
    }

    public void init() {
        Common.getInstance().getEventBus().register(this);
        actors.setRowFactory(new Callback<TableView<Actor>, TableRow<Actor>>() {
            @Override
            public TableRow<Actor> call(TableView<Actor> tableView) {
                final TableRow<Actor> row = new TableRow<Actor>() {
                    @Override
                    protected void updateItem(Actor actor, boolean empty) {
                        super.updateItem(actor, empty);
                    }
                };
                row.setOnMouseClicked(event -> {
                    Actor a = actors.getSelectionModel().getSelectedItem();
                    selectActor(a);
                });
                return row;
            }
        });

        if(current != null) {
            actors.getSelectionModel().select(current);
            selectActor(current);
        }

        dataAll = ((List<Actor>) actorRepository.findAll()).stream()
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(Collectors.toList());

        TableColumn<Actor, String> nameColumn = new TableColumn<>("Имя");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        actors.getColumns().setAll(nameColumn);
        setData(dataAll);

        char chars[] = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЫЭЮЯ".toCharArray();
        List<Hyperlink> letters = new ArrayList<>();

        int k = 0;
        for (final char c : chars) {
            Hyperlink h = new Hyperlink(String.valueOf(c));
            h.setLayoutY(18 * k);
            k++;
            h.setOnMouseClicked(event -> actors.setItems(FXCollections.observableArrayList(
                    dataAll.stream().filter(a -> a.getName().codePointAt(0) == c)
                            .collect(Collectors.toList()))));
            letters.add(h);
        }

        paneL.getChildren().addAll(letters);

        born.clear();
        born.textProperty().addListener((observable, oldValue, newValue) -> {
            if (current == null) return;
            if (StringUtils.isBlank(newValue)) return;
            try {
                Integer year = Integer.valueOf(newValue);
                current.setBorn(year);
                actorRepository.save(current);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        ToggleGroup tg = new ToggleGroup();
        union.setToggleGroup(tg);
        intersection.setToggleGroup(tg);
        union.setUserData(ActorFilmsPanel.Mode.UNION);
        intersection.setUserData(ActorFilmsPanel.Mode.INTERSECT);

        tg.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle toggle, Toggle newToggle) {
                if (newToggle == null)
                    mode = ActorFilmsPanel.Mode.UNION;
                else
                    mode = (ActorFilmsPanel.Mode) newToggle.getUserData();
                createLinks();
            }
        });
        union.setSelected(true);
    }

    private void selectActor(Actor a) {
        setImg(a);
        current = a;
        specBox.setSelected(current.isSpecial());
        actorsToPick.clear();
        actorsToPick.add(current);

        createLinks();
    }

    private void createLinks() {
        ActorFilmsPanel actorFilmsPanel = new ActorFilmsPanel(pane, actorsToPick, filmActorRepository, mode);
        actorFilmsPanel.createLinks();
        new PickActorPanel(pickActor, actorsToPick).createLinks();

        List<FilmActor> fff = actorFilmsPanel.getItems(actorsToPick);

        if(!fff.isEmpty()) {
            double x = fff.stream()
                    .map(fa -> fa.getFilm().getMark() * (4 - fa.getPart().getId()))
                    .reduce((i1, i2) -> i1 + i2).get();

            double y = fff.stream()
                    .map(fa -> (4 - fa.getPart().getId()))
                    .reduce((i1, i2) -> i1 + i2).get();

            double mark = x / y;

            markLabel.setText(String.format("%.3f", mark));
        }
    }

    private void setImg(Actor a) {
        setImg(a.getImg());
    }

    private void setImg(byte content[]) {
        if (content == null) {
            img.setImage(null);
            return;
        }

        Image i = new Image(new ByteArrayInputStream(content));
        img.setImage(i);
        img.setFitWidth(i.getWidth());
        img.setFitHeight(i.getHeight());
    }

    @FXML
    public void onPlus() {
        r.radius(r.getRadius() + 5);
    }

    @FXML
    public void onMinus() {
        r.radius(r.getRadius() - 5);
    }

    @FXML
    public void onGet() throws Exception {
        Image i = new Image(new URL(textUrl.getText()).openStream());
        double maxRadius = Math.min(i.getWidth(), i.getHeight()) / 2;
        img.setImage(i);
        img.setFitWidth(i.getWidth());
        img.setFitHeight(i.getHeight());

        r.setBx(i.getWidth());
        r.setBy(i.getHeight());
        r.setMaxRadius(maxRadius);
    }

    @FXML
    public void capture() throws Exception {

        URL url = new URL(textUrl.getText());

        int x = (int) (r.getCenterX() - r.getRadius() - img.getLayoutX());
        int y = (int) (r.getCenterY() - r.getRadius() - img.getLayoutY());
        int w = (int) r.getRadius() * 2;

        BufferedImage bi = ImageIO.read(url).getSubimage(x, y, w, w);
        bi = resizeImage(bi, 90, 90);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bi, "jpg", bos);

        byte content[] = bos.toByteArray();
        setImg(content);

        reverse();

        if (current == null) return;
        current.setImg(content);
        actorRepository.save(current);
    }

    public BufferedImage resizeImage(BufferedImage image, int width, int height) {
        int type = 0;
        type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    @FXML
    public void edit() {
        reverse();
    }

    private void reverse() {
        isEdit = !isEdit;
        r.setVisible(isEdit);
        plBtn.setVisible(isEdit);
        minBtn.setVisible(isEdit);
        getBtn.setVisible(isEdit);
        captureBtn.setVisible(isEdit);
        textUrl.setVisible(isEdit);
        textUrl.setText("");
        edit.setSelected(isEdit);
        r.setRadius(45);
        r.setCenterX(45);
        r.setCenterY(45);
    }

    @FXML
    public void onAll() {
        setData(dataAll);
    }

    @FXML
    public void onRand() {
        List<Actor> dbData = actorRepository.findRand10();
        setData(dbData);
        rand.setText("♘" + actorRepository.getNoImgCount());
    }

    @FXML
    public void special() {
        List<Actor> filtered = dataAll.stream().filter(Actor::isSpecial).collect(Collectors.toList());
        setData(filtered);
        spec.setText("☀" + filtered.size());
    }

    @FXML
    public void onSpec() {
        if(current == null) return;
        current.setSpecial(specBox.isSelected());
        actorRepository.save(current);
    }

    private void setData(List<Actor> dbData) {
        ObservableList<Actor> data = FXCollections.observableArrayList(dbData);
        actors.setItems(data);
    }

    public void setCurrent(Actor current) {
        this.current = current;
    }

    @FXML
    public void onYear() {
        List<Actor> filtered = actorRepository.find10NoYear();
        setData(filtered);
    }

    @FXML
    public void google() {
        if (current == null) return;
        try {
            new ProcessBuilder("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
                    fetcher.googleQ(current.getName())).start();
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void onDragDetected(MouseEvent event) { //drag
        Actor selected = actors.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Dragboard db = actors.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(selected.getName());
            db.setContent(content);
            event.consume();
        }
    }

    @FXML
    private void onDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    @FXML
    private void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (event.getDragboard().hasString()) {

            Actor a = actors.getSelectionModel().getSelectedItem();
            if (a != null) {
                actorsToPick.add(a);
                createLinks();
            }

            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }

    @Subscribe
    public void pickUpdateClicked(PickUpdateEvent e) {
        createLinks();
    }
}
