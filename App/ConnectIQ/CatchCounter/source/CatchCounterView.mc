using Toybox.WatchUi;
using Toybox.Graphics;
using Toybox.Communications;
using Toybox.System;  // maybe I don't actually need this
using Toybox.Timer;
using Toybox.Sensor;


class CatchCounterView extends WatchUi.View {
    var dataTimer;
    var dps;
    var sending;
    var data;
    var listener;
    var tlog;

    function initialize() {
        View.initialize();
    }

    // Load your resources here
    function onLayout(dc) {
        //setLayout(Rez.Layouts.MainLayout(dc));
        dataTimer = new Timer.Timer();
        tlog = new TransmissionLog();
        listener = new CommListener(tlog);
        dps = 0;  // counts the current number of datapoints
        sending = false;
        data = "";
    }

    function beginSending() {
        sending = true;
        page = 1;
        WatchUi.requestUpdate();
        dataTimer.start(method(:receiveInfo), 100, true);
    }

    function endSending() {
        sending = false;
        page = 0;
        WatchUi.requestUpdate();
        dataTimer.stop();
    }

    function receiveInfo() {
        var info = Sensor.getInfo();
        if (info has :accel && info.accel != null) {
            data += info.accel[2] + ",";  // only the z axis is interesting
        }
        dps += 1;
        if (dps == 20) {
            Communications.transmit(data, null, listener);
            data = "";
            dps = 0;
        }
    }

    // Called when this View is brought to the foreground. Restore
    // the state of this View and prepare it to be shown. This includes
    // loading resources into memory.
    function onShow() {
    }

    // Update the view
    function onUpdate(dc) {
        // Call the parent onUpdate function to redraw the layout
        //View.onUpdate(dc);

        dc.setColor(Graphics.COLOR_TRANSPARENT, Graphics.COLOR_BLACK);
        dc.clear();
        dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_TRANSPARENT);

        if (page == 0) {
            dc.drawText(dc.getWidth() / 2, 20, Graphics.FONT_MEDIUM, "Idle", Graphics.TEXT_JUSTIFY_CENTER);
        } else {
            dc.drawText(dc.getWidth() / 2, 20, Graphics.FONT_MEDIUM, "Sending", Graphics.TEXT_JUSTIFY_CENTER);
        }
        dc.drawText(dc.getWidth() / 2, 50, Graphics.FONT_MEDIUM, "OK: " + tlog.twin, Graphics.TEXT_JUSTIFY_CENTER);
        dc.drawText(dc.getWidth() / 2, 80, Graphics.FONT_MEDIUM, "FAIL: " + tlog.twin, Graphics.TEXT_JUSTIFY_CENTER);
    }

    // Called when this View is removed from the screen. Save the
    // state of this View here. This includes freeing resources from
    // memory.
    function onHide() {
    }

}
