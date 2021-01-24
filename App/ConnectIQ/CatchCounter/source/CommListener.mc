using Toybox.WatchUi;
using Toybox.System;
using Toybox.Communications;


class CommListener extends Communications.ConnectionListener {
    
    var transmissionLog;

    function initialize(tlog) {
        Communications.ConnectionListener.initialize();
        transmissionLog = tlog;
    }

    function onComplete() {
        transmissionLog.twin += 1;
        WatchUi.requestUpdate();
        System.println("Transmit Complete");
    }

    function onError() {
        transmissionLog.tfail += 1;
        WatchUi.requestUpdate();
        System.println("Transmit Failed");
    }
}