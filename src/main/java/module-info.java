module cfbastian.fluidsim2d.fluidsim2d {
    requires javafx.controls;
    requires javafx.fxml;


    opens cfbastian.fluidsim2d.fluidsim2d to javafx.fxml;
    exports cfbastian.fluidsim2d.fluidsim2d;
}